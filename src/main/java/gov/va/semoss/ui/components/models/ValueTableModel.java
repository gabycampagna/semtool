/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.components.models;

import static gov.va.semoss.rdf.query.util.QueryExecutorAdapter.getDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import java.util.Set;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;

/**
 *
 * @author ryan
 */
public class ValueTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 7491106662313232478L;

	private final List<Object[]> data = new ArrayList<>();
	private final List<String> headers = new ArrayList<>();
	private final List<Class<?>> columnClasses = new ArrayList<>();
	private static final Map<URI, Class<?>> TYPELOOKUP = new HashMap<>();
	private static final Map<Class<?>, URI> DATATYPELOOKUP = new HashMap<>();
	private static final ValueFactory VF = new ValueFactoryImpl();

	static {
		TYPELOOKUP.put( XMLSchema.INT, Integer.class );
		TYPELOOKUP.put( XMLSchema.INTEGER, Integer.class );
		TYPELOOKUP.put( XMLSchema.DOUBLE, Double.class );
		TYPELOOKUP.put( XMLSchema.FLOAT, Float.class );
		TYPELOOKUP.put( XMLSchema.DECIMAL, Double.class );
		TYPELOOKUP.put( XMLSchema.STRING, String.class );
		TYPELOOKUP.put( XMLSchema.DATE, Date.class );
		TYPELOOKUP.put( XMLSchema.DATETIME, Date.class );
		TYPELOOKUP.put( XMLSchema.BOOLEAN, Boolean.class );

		DATATYPELOOKUP.put( Integer.class, XMLSchema.INT );
		DATATYPELOOKUP.put( Double.class, XMLSchema.DOUBLE );
		DATATYPELOOKUP.put( Float.class, XMLSchema.FLOAT );
		DATATYPELOOKUP.put( String.class, XMLSchema.STRING );
		DATATYPELOOKUP.put( Date.class, XMLSchema.DATETIME );
		DATATYPELOOKUP.put( Boolean.class, XMLSchema.BOOLEAN );
	}

	private boolean useraw;
	private boolean readonly;
	private boolean allowInsertsInPlace = false;

	public ValueTableModel() {
		this( true );
	}

	public ValueTableModel( boolean raw ) {
		useraw = raw;
	}

	public ValueTableModel( List<Value[]> newdata, List<String> heads ) {
		setData( newdata, heads );
	}

	protected void clear() {
		int ds = data.size();
		data.clear();
		fireTableRowsDeleted( 0, ds );
	}

	/**
	 * Permits this model to allow an empty row at the bottom where the user can
	 * insert new rows of data, like old MS Access tables
	 *
	 * @param b allow?
	 */
	public void setAllowInsertsInPlace( boolean b ) {
		allowInsertsInPlace = b;
	}

	/**
	 * Does this model support in-place data adds. Note that if
	 * {@link #isReadOnly() } returns <code>false</code>, so will this function.
	 *
	 * @return if inserts are allowed
	 */
	public boolean allowsInsertsInPlace() {
		return ( isReadOnly() ? false : allowInsertsInPlace );
	}

	/**
	 * Sets the table to readonly/read-write. If
	 *
	 * @param b
	 */
	public void setReadOnly( boolean b ) {
		readonly = b;
	}

	public boolean isReadOnly() {
		return readonly;
	}

	public final void addData( List<Value[]> newdata ) {
		int sz = data.size();

		if ( !newdata.isEmpty() ) {
			data.addAll( convertValuesToClassedData( newdata, columnClasses, useraw ) );
			int newsz = data.size();
			fireTableRowsInserted( sz - 1, newsz - 1 );
		}
	}

	public void setData( List<Value[]> newdata, List<String> heads ) {
		headers.clear();
		headers.addAll( heads );
		columnClasses.clear();
		columnClasses.addAll( figureColumnClassesFromData( newdata, headers.size() ) );

		data.clear();
		data.addAll( convertValuesToClassedData( newdata, columnClasses, useraw ) );
		fireTableStructureChanged();
	}

	public void setHeaders( Map<String, URI> heads ) {
		headers.clear();
		columnClasses.clear();

		for ( Map.Entry<String, URI> en : heads.entrySet() ) {
			headers.add( en.getKey() );
			columnClasses.add( TYPELOOKUP.get( en.getValue() ) );
		}
		fireTableStructureChanged();
	}

	public void setHeaders( List<String> heads ) {
		headers.clear();
		headers.addAll( heads );
		fireTableStructureChanged();
	}

	public void setHeaders( Object[] heads ) {
		headers.clear();
		for ( Object o : heads ) {
			headers.add( o.toString() );
		}
	}

	@Override
	public int getRowCount() {
		int sz = data.size();
		return ( allowsInsertsInPlace() ? sz + 1 : sz );
	}

	/**
	 * Same as {@link #getRowCount()}, but be sure to ignore extra row if
	 * {@link #allowsInsertsInPlace()} returns <code>true</code>
	 * @return 
	 */
	public int getRealRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return headers.size();
	}

	@Override
	public Object getValueAt( int r, int c ) {
		if ( isInsertRow( r ) ) {
			return ( 0 == c ? "*" : null );
		}

		return data.get( r )[c];
	}

	public Value getRdfValueAt( int r, int c ) {
		Object val = getValueAt( r, c );
		if ( null == val ) {
			return null;
		}

		String valstr = val.toString();
		Class<?> klass = getColumnClass( c );
		URI datatype = DATATYPELOOKUP.get( klass );
		return ( null == datatype ? VF.createLiteral( valstr )
				: VF.createLiteral( valstr, datatype ) );
	}

	@Override
	public boolean isCellEditable( int row, int col ) {
		if ( isInsertRow( row ) ) {
			return ( 0 == col );
		}

		return !readonly;
	}

	/**
	 * Is this the row for inserting new values?
	 *
	 * @param row the row to check
	 * @return true, if it's the insert row
	 */
	protected final boolean isInsertRow( int row ) {
		return ( allowsInsertsInPlace() && getRowCount() - 1 == row );
	}

	@Override
	public void setValueAt( Object aValue, int r, int c ) {
		Class<?> k = getColumnClass( c );

		if ( isInsertRow( r ) ) {
			Object objs[] = new Object[getColumnCount()];
			data.add( objs );
		}

		data.get( r )[c] = k.cast( aValue );

		if ( isInsertRow( r ) ) {
			Object objs2[] = new Object[getColumnCount()];
			objs2[0] = "*";
			data.add( objs2 );

			fireTableRowsInserted( r + 1, r + 1 );
		}

		fireTableCellUpdated( r, c );
	}

	@Override
	public Class<?> getColumnClass( int c ) {
		return ( c < columnClasses.size() ? columnClasses.get( c ) : Object.class );
	}

	@Override
	public String getColumnName( int column ) {
		return headers.get( column );
	}

	public static List<Class<?>> figureColumnClassesFromData( List<Value[]> newdata,
			int columns ) {
		List<Class<?>> columnClasses = new ArrayList<>();
		if ( newdata.isEmpty() ) {
			for ( int i = 0; i < columns; i++ ) {
				columnClasses.add( Object.class );
			}
		}
		else {
			// we'd like to be able to figure out column types even
			// if a row doesn't have values for every column.
			// we used to determine the class of a column from it's first non-null Value,
			// but that's just not always true for every element of the column, so we
			// need to check until we're sure all values are the same, or we can decide
			// if the column is a String or Object.
			List<Integer> colsToFigure = new ArrayList<>();
			for ( int i = 0; i < columns; i++ ) {
				colsToFigure.add( i );
			}
			Class<?> arr[] = new Class<?>[columns];

			// now iterate as far as we have to until we have all the column classes
			Iterator<Value[]> it = newdata.iterator();
			Set<Class<?>> finalClasses
					= new HashSet<>( Arrays.asList( String.class, Object.class ) );

			while ( !colsToFigure.isEmpty() && it.hasNext() ) {
				Value[] first = it.next();

				// we have a row of data, so see if it can provide a class for 
				// any column we don't yet have a class for
				ListIterator<Integer> colit = colsToFigure.listIterator();
				while ( colit.hasNext() ) {
					int col = colit.next();
					Value v = first[col];
					Class<?> k = getClassForValue( v );

					// getClassForValue returns Object when the classtype can't be determined
					if ( !Object.class.equals( k ) ) {
						Class<?> previousK = arr[col];

						if ( null == previousK ) {
							// first time we've set a value for this column
							arr[col] = k;
						}
						else if ( previousK != k ) {
							// we have a previous column class, 
							if ( !finalClasses.contains( previousK ) ) {
								// we're not already at a "final" class, so figure out what we want
								if ( finalClasses.contains( k ) ) {
									// we're going to be final, so set it
									arr[col] = k;
								}
								else {
									// we have two different classes, 
									// assume they're irreconcilable
									arr[col] = Object.class;
								}
							}
							// else we're "final," so don't change
						}

						if ( finalClasses.contains( arr[col] ) ) {
							colit.remove();
						}
					}
				}
			}

			// remove any columns where we have a class, albeit a non-"final" one
			ListIterator<Integer> li = colsToFigure.listIterator();
			while ( li.hasNext() ) {
				int col = li.next();
				if ( null != arr[col] ) {
					li.remove();
				}
			}

			// we don't have any data for the remaining columns, so do something safe
			for ( int col : colsToFigure ) {
				arr[col] = Object.class;
			}

			columnClasses.addAll( Arrays.asList( arr ) );
		}

		return columnClasses;
	}

	private static List<Object[]> convertValuesToClassedData( Collection<Value[]> newdata,
			List<Class<?>> columnClasses, boolean userawstrings ) {

		List<Object[]> data = new ArrayList<>();
		for ( Value[] varr : newdata ) {
			try {
				Object[] arr = new Object[varr.length];
				for ( int i = 0; i < varr.length; i++ ) {
					final Class<?> cc = columnClasses.get( i );
					Value val = varr[i];
					if ( null == val ) {
						arr[i] = null;
					}
					else if ( URI.class == cc ) {
						arr[i] = URI.class.cast( val );
					}
					else if ( Object.class == cc ) {
						arr[i] = val.stringValue();
					}
					else {
						if ( Integer.class == cc ) {
							Literal l = Literal.class.cast( val );
							arr[i] = l.intValue();
						}
						else if ( Boolean.class == cc ) {
							Literal l = Literal.class.cast( val );
							arr[i] = l.booleanValue();
						}
						else if ( Date.class == cc ) {
							Literal l = Literal.class.cast( val );
							arr[i] = getDate( l.calendarValue() );
						}
						else if ( Double.class == cc ) {
							Literal l = Literal.class.cast( val );
							arr[i] = l.doubleValue();
						}
						else if ( Float.class == cc ) {
							Literal l = Literal.class.cast( val );
							arr[i] = l.floatValue();
						}
						else if ( String.class == cc ) {
							// just because our column class is "String" doesn't mean the
							// val is a String or even a literal; it just means the output
							// should be of type String

							if ( val instanceof Literal ) {
								Literal l = Literal.class.cast( val );

								if ( userawstrings ) {
									if ( null != l.getLanguage() ) {
										arr[i] = "\"" + l.stringValue() + "\"@" + l.getLanguage();
									}
									else if ( null != l.getDatatype() ) {
										arr[i] = "\"" + l.stringValue() + "\"^^" + l.getDatatype().stringValue();
									}
									else {
										arr[i] = l.stringValue();
									}
								}
								else {
									arr[i] = val.stringValue();
								}
							}
							else {
								arr[i] = val.stringValue();
							}
						}
					}
				}
				data.add( arr );
			}
			catch ( Exception e ) {
				Logger.getLogger( ValueTableModel.class ).error( e, e );
			}
		}

		return data;
	}

	public static Class<?> getClassForValue( Value v ) {

		if ( v instanceof URI ) {
			return URI.class;
		}

		if ( v instanceof Literal ) {
			Literal l = Literal.class.cast( v );
			URI dt = l.getDatatype();
			return ( TYPELOOKUP.containsKey( dt )
					? TYPELOOKUP.get( dt ) : String.class );
		}

		return Object.class;
	}

	public static Object parseXMLDatatype( String input ) {
		if ( input == null ) {
			return null;
		}

		input = input.trim();

		String[] pieces = input.split( "\"" );
		if ( pieces.length != 3 ) {
			return removeExtraneousDoubleQuotes( input );
		}

		Class<?> theClass = null;
		for ( URI datatypeUri : TYPELOOKUP.keySet() ) {
			if ( pieces[2].contains( datatypeUri.stringValue() ) ) {
				theClass = TYPELOOKUP.get( datatypeUri );
			}
		}

		String dataPiece = pieces[1];

		if ( theClass == Double.class && XMLDatatypeUtil.isValidDouble( dataPiece ) ) {
			return XMLDatatypeUtil.parseDouble( dataPiece );
		}

		if ( theClass == Float.class && XMLDatatypeUtil.isValidFloat( dataPiece ) ) {
			return XMLDatatypeUtil.parseFloat( dataPiece );
		}

		if ( theClass == Integer.class && XMLDatatypeUtil.isValidInteger( dataPiece ) ) {
			return XMLDatatypeUtil.parseInteger( dataPiece );
		}

		if ( theClass == Boolean.class && XMLDatatypeUtil.isValidBoolean( dataPiece ) ) {
			return XMLDatatypeUtil.parseBoolean( dataPiece );
		}

		if ( theClass == Date.class && XMLDatatypeUtil.isValidDate( dataPiece ) ) {
			return XMLDatatypeUtil.parseCalendar( dataPiece );
		}

		return removeExtraneousDoubleQuotes( input );
	}

	public static Object getValueFromLiteral( Literal input ) {
		if ( input == null ) {
			return null;
		}

		Class<?> theClass = getClassForValue( input );

		if ( theClass == Double.class ) {
			return input.doubleValue();
		}

		if ( theClass == Integer.class ) {
			return input.intValue();
		}

		if ( theClass == Boolean.class ) {
			return input.booleanValue();
		}

		if ( theClass == Float.class ) {
			return input.floatValue();
		}

		if ( theClass == Date.class ) {
			return input.calendarValue();
		}

		return removeExtraneousDoubleQuotes( input.stringValue() );
	}

	public static String removeExtraneousDoubleQuotes( String input ) {
		while ( input != null && input.length() > 2
				&& input.charAt( 0 ) == '\"'
				&& input.charAt( input.length() - 1 ) == '\"' ) {
			input = input.substring( 1, input.length() - 1 );
		}

		return input;
	}
}
