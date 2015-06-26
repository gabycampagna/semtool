/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.poi.main;

import gov.va.semoss.poi.main.ImportValidationException.ErrorType;
import gov.va.semoss.rdf.engine.util.EngineLoader;
import gov.va.semoss.util.MultiMap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A skeletal implementation of the Workbook interface. This class does almost
 * nothing except read bare-bones data and throw exceptions, though it also
 * swallows lots of function calls that it doesn't support
 *
 * @author ryan
 */
public class LowMemXlsWorkbook implements Workbook {

	private static final Logger log = Logger.getLogger( LowMemXlsWorkbook.class );

	private final LinkedHashMap<String, String> sheetNameIdLkp;
	private final ArrayList<String> sharedStrings;
	private final XSSFReader reader;
	private final OPCPackage pkg;
	private final StylesTable styles;
	private Row.MissingCellPolicy policy;

	public LowMemXlsWorkbook( File filename ) throws IOException {
		this( new BufferedInputStream( new FileInputStream( filename ) ) );
	}

	public LowMemXlsWorkbook( String filename ) throws IOException {
		this( new File( filename ) );
	}

	public LowMemXlsWorkbook( InputStream stream ) throws IOException {
		try {
			pkg = OPCPackage.open( stream );
			reader = new XSSFReader( pkg );

			styles = reader.getStylesTable();

			sheetNameIdLkp = readSheetInfo( reader );
			sharedStrings = readSharedStrings( reader );
		}
		catch ( OpenXML4JException e ) {
			throw new IOException( "unexpected error" + e.getLocalizedMessage(), e );
		}
	}

	/**
	 * Releases resources used by this reader
	 */
	public void release() {
		try {
			pkg.close();
			sheetNameIdLkp.clear();
			sharedStrings.clear();
		}
		catch ( Exception e ) {
			log.error( e, e );
		}
	}

	@Override
	public Sheet getSheet( String sheetname ) {
		return null;
	}

	/**
	 * Gets the sheet types. If a loader tab exists, only those tabs will be
	 * checked (and the metadata tab will be verified against what the loader tab
	 * says).
	 *
	 *
	 * @return
	 * @throws ImportValidationException
	 */
	public Map<String, SheetType> getSheetTypes() throws ImportValidationException {
		Map<String, SheetType> types = new HashMap<>();
		Set<String> tabsToCheck = new HashSet<>();
		boolean checktypes = false;

		try {
			XMLReader parser = XMLReaderFactory.createXMLReader();

			if ( sheetNameIdLkp.containsKey( "Loader" ) ) {
				checktypes = true;
				try ( InputStream is = reader.getSheet( sheetNameIdLkp.get( "Loader" ) ) ) {

					LoaderTabXmlHandler handler = new LoaderTabXmlHandler( sharedStrings );
					parser.setContentHandler( handler );

					InputSource sheetSource = new InputSource( is );
					parser.parse( sheetSource );

					types.putAll( handler.getSheetTypes() );
					tabsToCheck.addAll( types.keySet() );

					if ( tabsToCheck.isEmpty() ) {
						throw new ImportValidationException( ErrorType.MISSING_DATA,
								"No data to process" );
					}
				}
			}
			else {
				tabsToCheck.addAll( sheetNameIdLkp.keySet() );
			}

			// now check the actual sheets
			SheetTypeXmlHandler handler = new SheetTypeXmlHandler( sharedStrings );
			parser.setContentHandler( handler );

			boolean seenMetadata = false; // we can only have 1 metadata tab
			for ( String sheetname : tabsToCheck ) {
				if ( !sheetNameIdLkp.containsKey( sheetname ) ) {
					throw new ImportValidationException( ErrorType.MISSING_DATA,
							"Missing sheet: " + sheetname );
				}

				try ( InputStream is = reader.getSheet( sheetNameIdLkp.get( sheetname ) ) ) {
					InputSource sheetSource = new InputSource( is );
					parser.parse( sheetSource );

					SheetType sheettype = handler.getSheetType();
					boolean sheetsaysM = ( SheetType.METADATA == sheettype );

					if ( sheetsaysM ) {
						if ( seenMetadata ) {
							throw new ImportValidationException( ErrorType.TOO_MUCH_DATA,
									"Too many metadata tabs in loading file" );
						}
						seenMetadata = true;
					}

					SheetType loadertype = types.get( sheetname );
					if ( checktypes ) {
						if ( ( SheetType.USUAL == loadertype && sheetsaysM )
								|| SheetType.METADATA == loadertype && !sheetsaysM ) {
							// if the loader or the sheet itself says its a metadata sheet,
							// then both types must agree
							throw new ImportValidationException( ErrorType.WRONG_TABTYPE,
									"Loader Sheet data type for " + sheetname
									+ " conflicts with sheet type" );
						}
					}

					types.put( sheetname, sheettype );
				}
			}
		}
		catch ( SAXException | IOException | InvalidFormatException e ) {
			log.error( e, e );
		}

		return types;
	}

	public ImportData getData() throws ImportValidationException {
		ImportData id = new ImportData();
		EngineLoader.initNamespaces( id );

		try {
			XMLReader parser = XMLReaderFactory.createXMLReader();

			Map<String, SheetType> types = getSheetTypes();
			MultiMap<SheetType, String> mm = MultiMap.flip( types );

			// load the metadata sheet first, if we have one
			for ( String metasheet : mm.getNN( SheetType.METADATA ) ) {
				try ( InputStream is = reader.getSheet( sheetNameIdLkp.get( metasheet ) ) ) {
					MetadataTabXmlHandler handler
							= new MetadataTabXmlHandler( sharedStrings, id.getMetadata() );
					parser.setContentHandler( handler );

					InputSource sheetSource = new InputSource( is );
					parser.parse( sheetSource );

					id.setMetadata( handler.getMetadata() );
				}

				types.remove( metasheet ); // don't reprocess in the next loop								
			}

			for ( Map.Entry<String, SheetType> typeen : types.entrySet() ) {
				String sheetname = typeen.getKey();
				String sheetid = sheetNameIdLkp.get( sheetname );
				SheetType sheettype = typeen.getValue();

				try ( InputStream is = reader.getSheet( sheetid ) ) {
					if ( SheetType.NODE == sheettype || SheetType.RELATION == sheettype ) {
						LoadingSheetXmlHandler handler
								= new LoadingSheetXmlHandler( sharedStrings, styles, sheetname,
										id.getMetadata().getNamespaces() );
						parser.setContentHandler( handler );

						InputSource sheetSource = new InputSource( is );
						parser.parse( sheetSource );

						LoadingSheetData lsd = handler.getSheet();
						if ( lsd.isEmpty() ) {
							throw new ImportValidationException( ErrorType.NOT_A_LOADING_SHEET,
									"Sheet " + sheetname + " contains no loadable data" );
						}
						id.add( lsd );
					}
				}
			}
		}
		catch ( SAXException | InvalidFormatException | IOException ife ) {
			log.error( ife, ife );
		}

		if ( id.isEmpty() ) {
			throw new ImportValidationException( ErrorType.MISSING_DATA,
					"No data to process" );
		}

		return id;
	}

	public Collection<String> getSheetNames() {
		return sheetNameIdLkp.keySet();
	}

	/**
	 * Gets sheet name-to-id mapping
	 *
	 * @param r
	 * @return
	 */
	private LinkedHashMap<String, String> readSheetInfo( XSSFReader r ) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();

		try ( InputStream is = r.getWorkbookData() ) {
			SAXReader sax = new SAXReader();
			Document doc = sax.read( is );

			Namespace ns = new Namespace( "r",
					"http://schemas.openxmlformats.org/officeDocument/2006/relationships" );

			Element sheets = doc.getRootElement().element( "sheets" );
			for ( Object sheet : sheets.elements( "sheet" ) ) {
				Element e = Element.class.cast( sheet );
				String name = e.attributeValue( "name" );
				String id = e.attributeValue( new QName( "id", ns ) );
				map.put( name, id );
			}
		}
		catch ( Exception e ) {
			log.error( e, e );
		}

		return map;
	}

	private ArrayList<String> readSharedStrings( XSSFReader r ) {
		ArrayList<String> map = new ArrayList<>();

		try ( InputStream is = r.getSharedStringsData() ) {
			XMLReader parser = XMLReaderFactory.createXMLReader();
			ContentHandler handler = new DefaultHandler() {
				String content;
				boolean reading = false;

				@Override
				public void startElement( String uri, String localName, String qName, Attributes atts ) throws SAXException {
					reading = ( "t".equals( localName ) );
					content = "";
				}

				@Override
				public void endElement( String uri, String localName, String qName ) throws SAXException {
					if ( reading ) {
						map.add( content );
						reading = false;
					}
				}

				@Override
				public void characters( char[] ch, int start, int length ) throws SAXException {
					if ( reading ) {
						content += new String( ch, start, length );
					}
				}
			};
			parser.setContentHandler( handler );
			parser.parse( new InputSource( is ) );
		}
		catch ( Exception e ) {
			log.error( e, e );
		}

		return map;
	}

	@Override
	public int getActiveSheetIndex() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setActiveSheet( int sheetIndex ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getFirstVisibleTab() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setFirstVisibleTab( int sheetIndex ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setSheetOrder( String sheetname, int pos ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setSelectedTab( int index ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setSheetName( int sheet, String name ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getSheetName( int sheet ) {
		return new ArrayList<>( sheetNameIdLkp.keySet() ).get( sheet );
	}

	@Override
	public int getSheetIndex( String name ) {
		int c = 0;
		for ( String n : sheetNameIdLkp.keySet() ) {
			c++;
			if ( n.equals( name ) ) {
				return c;
			}
		}
		return -1;
	}

	@Override
	public int getSheetIndex( Sheet sheet ) {
		return getSheetIndex( sheet.getSheetName() );
	}

	@Override
	public Sheet createSheet() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Sheet createSheet( String sheetname ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Sheet cloneSheet( int sheetNum ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getNumberOfSheets() {
		return sheetNameIdLkp.size();
	}

	@Override
	public Sheet getSheetAt( int index ) {
		return getSheet( this.getSheetName( index ) );
	}

	@Override
	public void removeSheetAt( int index ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setRepeatingRowsAndColumns( int sheetIndex, int startColumn, int endColumn, int startRow, int endRow ) {
	}

	@Override
	public Font createFont() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Font findFont( short boldWeight, short color, short fontHeight, String name, boolean italic, boolean strikeout, short typeOffset, byte underline ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public short getNumberOfFonts() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Font getFontAt( short idx ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CellStyle createCellStyle() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public short getNumCellStyles() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CellStyle getCellStyleAt( short idx ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void write( OutputStream stream ) throws IOException {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getNumberOfNames() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Name getName( String name ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Name getNameAt( int nameIndex ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Name createName() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getNameIndex( String name ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void removeName( int index ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void removeName( String name ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setPrintArea( int sheetIndex, String reference ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setPrintArea( int sheetIndex, int startColumn, int endColumn, int startRow, int endRow ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getPrintArea( int sheetIndex ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void removePrintArea( int sheetIndex ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Row.MissingCellPolicy getMissingCellPolicy() {
		return policy;
	}

	@Override
	public void setMissingCellPolicy( Row.MissingCellPolicy missingCellPolicy ) {
		policy = missingCellPolicy;
	}

	@Override
	public DataFormat createDataFormat() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int addPicture( byte[] pictureData, int format ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<? extends PictureData> getAllPictures() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CreationHelper getCreationHelper() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isHidden() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setHidden( boolean hiddenFlag ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isSheetHidden( int sheetIx ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isSheetVeryHidden( int sheetIx ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setSheetHidden( int sheetIx, boolean hidden ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setSheetHidden( int sheetIx, int hidden ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void addToolPack( UDFFinder toopack ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setForceFormulaRecalculation( boolean value ) {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean getForceFormulaRecalculation() {
		throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
	}

	private static class SheetHandler extends DefaultHandler {

		private static final Map<String, Integer> formats = new HashMap<>();
		//private final Map<Integer, Value> currentrowdata = new HashMap<>();
		private final ValueFactory vf = new ValueFactoryImpl();
		private final ArrayList<String> sst;
		private final LowMemXlsSheet sheet;
		//private final Map<Integer, Cell> currentrowdata = new LinkedHashMap<>();
		private final StylesTable styles;
		private String lastContents;
		private boolean reading = false;
		private LowMemXlsRow currentrow;
		private LowMemXlsCell currentcell;
		private boolean isdate = false;

		static {
			formats.put( "s", Cell.CELL_TYPE_STRING );
			formats.put( "n", Cell.CELL_TYPE_NUMERIC );
			formats.put( "b", Cell.CELL_TYPE_BOOLEAN );
		}

		private SheetHandler( ArrayList<String> sst, StylesTable styles,
				String sheetname, LowMemXlsWorkbook wb ) {
			this.sst = sst;
			this.styles = styles;
			sheet = new LowMemXlsSheet( sheetname, wb );
		}

		public Sheet getSheet() {
			return sheet;
		}

		public static int getColNum( String colname ) {
			int sum = 0;

			for ( int i = 0; i < colname.length(); i++ ) {
				sum *= 26;
				sum += ( colname.charAt( i ) - 'A' );
			}

			return sum;
		}

		@Override
		public void startElement( String uri, String localName, String name,
				Attributes attributes ) throws SAXException {
			if ( null != name ) {
				switch ( name ) {
					case "c": // c is a new cell
						String celltypestr = attributes.getValue( "t" );
						int celltype = ( formats.containsKey( celltypestr )
								? formats.get( celltypestr ) : Cell.CELL_TYPE_BLANK );
						// dates don't always have a type attribute
						if ( Cell.CELL_TYPE_NUMERIC == celltype || null == celltypestr ) {
							// check if it's a date
							String styleidstr = attributes.getValue( "s" );
							int styleid = ( null == styleidstr ? 0
									: Integer.parseInt( styleidstr ) );

							XSSFCellStyle style = styles.getStyleAt( styleid );
							int formatIndex = style.getDataFormat();
							String formatString = style.getDataFormatString();
							isdate = DateUtil.isADateFormat( formatIndex, formatString );

							if ( isdate ) {
								celltype = Cell.CELL_TYPE_NUMERIC;
								currentcell.setCellStyle( style );
							}
						}
						String colname = attributes.getValue( "r" );
						int currentcol = getColNum( colname.substring( 0,
								colname.lastIndexOf( Integer.toString( currentrow.getRowNum() + 1 ) ) ) );
						currentcell = currentrow.createCell( currentcol, celltype );
						break;
					case "row":
						int rownum = Integer.parseInt( attributes.getValue( "r" ) );
						currentrow = sheet.createRow( rownum - 1 );
						break;
					case "v": // new value for a cell
						reading = true;
						lastContents = "";
						break;
				}
			}
		}

		@Override
		public void endElement( String uri, String localName, String name )
				throws SAXException {

//			log.debug( "end: " + localName + "..." + name + "..." + uri );
			//		log.debug( "I am " + ( reading ? "" : "NOT" ) + " reading" );
			if ( reading ) {
				// If we've fully read the data, add it to our row mapping
				switch ( currentcell.getCellType() ) {
					case Cell.CELL_TYPE_STRING:
						currentcell.setCellValue( sst.get( Integer.parseInt( lastContents ) ) );
						if ( currentcell.getStringCellValue().isEmpty() ) {
							currentrow.removeCell( currentcell );
							currentcell = null;
						}
						break;
					case Cell.CELL_TYPE_BLANK:
						currentrow.removeCell( currentcell );
						currentcell = null;
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						currentcell.setCellValue( "1".equals( lastContents ) );
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if ( isdate ) {
							Date date = DateUtil.getJavaDate( Double.parseDouble( lastContents ) );
							currentcell.setCellValue( date );
						}
						else {
							currentcell.setCellValue( Double.parseDouble( lastContents ) );
						}
						break;
					case Cell.CELL_TYPE_ERROR:
						log.warn( "unhandled cell type: CELL_TYPE_ERROR" );
						break;
					case Cell.CELL_TYPE_FORMULA:
						log.warn( "unhandled cell type: CELL_TYPE_FORMULA" );
						break;
					default:
						log.warn( "unhandled cell type: " + currentcell.getCellType() );
				}

				if ( null != currentcell ) {
					log.debug( sheet.getSheetName() + "(" + currentrow.getRowNum() + ","
							+ currentcell.getColumnIndex() + ") " + currentcell.getStringCellValue() );
				}
				reading = false;
			}
		}

		@Override
		public void characters( char[] ch, int start, int length )
				throws SAXException {
			if ( reading ) {
				lastContents += new String( ch, start, length );
			}
		}
	}
}
