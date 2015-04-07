/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.components;

import gov.va.semoss.poi.main.ImportData;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import gov.va.semoss.rdf.engine.api.IEngine;
import gov.va.semoss.rdf.engine.util.EngineLoader;
import gov.va.semoss.util.DIHelper;
import gov.va.semoss.util.Utility;
import gov.va.semoss.rdf.query.util.MetadataQuery;
import gov.va.semoss.util.Constants;
import gov.va.semoss.ui.main.SemossPreferences;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

/**
 *
 * @author ryan
 */
public class ImportExistingDbPanel extends JPanel {
	
	private static final Logger log = Logger.getLogger( ImportExistingDbPanel.class );
	private IEngine engine;

	/**
	 * Creates new form ExistingDbPanel
	 */
	public ImportExistingDbPanel() {
		this( null );
	}
	
	public ImportExistingDbPanel( IEngine eng ) {
		initComponents();
		
		Preferences prefs = Preferences.userNodeForPackage( getClass() );
		file.setPreferencesKeys( prefs, "lastpath" );
		file.setMultipleFilesOk( true );
		JFileChooser chsr = file.getChooser();
		chsr.addChoosableFileFilter( FileBrowsePanel.getLoadingSheetsFilter( true ) );
		chsr.addChoosableFileFilter( new FileBrowsePanel.CustomFileFilter( "Turtle Files", "ttl" ) );
		chsr.addChoosableFileFilter( new FileBrowsePanel.CustomFileFilter( "RDF/XML Files", "rdf" ) );
		chsr.addChoosableFileFilter( new FileBrowsePanel.CustomFileFilter( "N-Triples Files", "nt" ) );
		chsr.addChoosableFileFilter( new FileBrowsePanel.CustomFileFilter( "N3 Files", "n3" ) );
		chsr.setFileFilter( FileBrowsePanel.getAllImportTypesFilter() );
		
		setEngine( eng );
		
		SemossPreferences vc = SemossPreferences.getInstance();
		calcInfers.setSelected( PlayPane.getProp( vc, Constants.CALC_INFERENCES_PREF ) );
	}
	
	public void setFiles( Collection<File> files ) {
		file.setFileText( files );
	}
	
	public static void showDialog( Frame frame, IEngine eng, Collection<File> files ) {
		ImportExistingDbPanel iedp = new ImportExistingDbPanel( eng );
		iedp.setFiles( files );
		
		String ename = null;
		MetadataQuery mq = new MetadataQuery( RDFS.LABEL );
		try {
			eng.query( mq );
			ename = mq.getOne();
		}
		catch ( RepositoryException | MalformedQueryException | QueryEvaluationException e ) {
			// don't care
			log.debug( e, e );
		}
		
		if ( null == ename ) {
			ename = eng.getEngineName();
		}
		
		Object options[] = { "Import to " + ename, "Cancel" };
		
		int opt = JOptionPane.showOptionDialog( frame, iedp,
				"Import External File(s)", JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0] );
		if ( 0 == opt ) {
			iedp.doImport();
		}
	}

	/**
	 * The engine to use
	 *
	 * @param eng the engine. if null, the currently-selected engine will be used
	 */
	public final void setEngine( IEngine eng ) {
		engine = eng;
		// refreshLabels();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings( "unchecked" )
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    buttonGroup1 = new javax.swing.ButtonGroup();
    jPanel2 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    file = new gov.va.semoss.ui.components.FileBrowsePanel();
    jPanel1 = new javax.swing.JPanel();
    diskStaging = new javax.swing.JRadioButton();
    memoryStaging = new javax.swing.JRadioButton();
    togridbtn = new javax.swing.JRadioButton();
    jPanel3 = new javax.swing.JPanel();
    replaceCheck = new javax.swing.JCheckBox();
    calcInfers = new javax.swing.JCheckBox();
    metamodel = new javax.swing.JCheckBox();
    conformer = new javax.swing.JCheckBox();

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 100, Short.MAX_VALUE)
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 100, Short.MAX_VALUE)
    );

    jLabel2.setLabelFor(file);
    jLabel2.setText("Select File(s) to Import");

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(java.awt.Color.gray, 1, true), "Load Intermediate Data", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 0, 12))); // NOI18N
    jPanel1.setToolTipText("Where should the raw data be loaded before importing?");

    buttonGroup1.add(diskStaging);
    diskStaging.setText("On Disk");
    diskStaging.setToolTipText("Loading on disk can be slower, but uses less memory");

    buttonGroup1.add(memoryStaging);
    memoryStaging.setText("In Memory");
    memoryStaging.setToolTipText("Loading in memory can be faster, but uses more memory");

    buttonGroup1.add(togridbtn);
    togridbtn.setSelected(true);
    togridbtn.setText("To Grid PlaySheet");
    togridbtn.setToolTipText("Same as In-Memory, but with a chance to view the data before committing it");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(diskStaging)
          .addComponent(memoryStaging)
          .addComponent(togridbtn))
        .addGap(0, 86, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(diskStaging)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(memoryStaging)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(togridbtn)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.PAGE_AXIS));

    replaceCheck.setText("Replace Existing Data");
    replaceCheck.setToolTipText("Clear the database before running this load");
    replaceCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        replaceCheckActionPerformed(evt);
      }
    });
    jPanel3.add(replaceCheck);

    calcInfers.setSelected(true);
    calcInfers.setText("Compute Dependent Relationships");
    jPanel3.add(calcInfers);

    metamodel.setSelected(true);
    metamodel.setText("Create Metamodel");
    jPanel3.add(metamodel);

    conformer.setText("Check Quality");
    jPanel3.add(conformer);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(file, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(0, 0, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(0, 0, 0)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(file, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

	public void doImport() {
		final boolean replace = replaceCheck.isSelected();
		final boolean stageInMemory = memoryStaging.isSelected();
		final boolean calc = calcInfers.isSelected();
		final boolean dometamodel = metamodel.isSelected();
		final boolean conformance = conformer.isSelected();
		final boolean gridy = togridbtn.isSelected();
		
		if ( gridy || ( ( replace && runOverrideCheck( file.getFiles() ) )
				|| ( !replace && runCheck() ) ) ) {
			
			final IEngine eng
					= ( null == engine ? DIHelper.getInstance().getRdfEngine() : engine );
			
			ProgressTask pt;
			if ( gridy ) {
				LoadingPlaySheetFrame psf = new LoadingPlaySheetFrame( engine,
						file.getFiles(), calc, dometamodel, conformance, replace );
				DIHelper.getInstance().getDesktop().add( psf );
				pt = psf.getLoadingTask();
			}
			else {
				final boolean successfulImport[] = { false };
				
				String t = ( replace ? "Replacing data in " : "Adding data to " )
						+ eng.getEngineName() + " from " + file.getDelimitedPaths();
				pt = new ProgressTask( t, new Runnable() {
					
					@Override
					public void run() {
						if ( replace ) {
							ImportDataProcessor.clearEngine( engine, file.getFiles() );
						}
						try {
							ImportData errs = ( conformance ? ImportData.forEngine( eng )
									: null );
							EngineLoader el = new EngineLoader( stageInMemory );
							el.loadToEngine( file.getFiles(), engine, dometamodel, errs );
							el.release();
							
							if ( !( null == errs || errs.isEmpty() ) ) {
								LoadingPlaySheetFrame psf = new LoadingPlaySheetFrame( eng, errs );
								psf.setTitle( "Quality Check Errors" );
								DIHelper.getInstance().getDesktop().add( psf );
							}
							// if we get here, no exceptions have been thrown, so we're good
							successfulImport[0] = true;
						}
						catch ( RepositoryException | IOException ioe ) {
							log.error( ioe, ioe );
						}
					}
				} ) {
					
					@Override
					public void done() {
						super.done();
						//finally, show whether or not successful
						if ( successfulImport[0] ) {
							Utility.showMessage( "Your database has been successfully updated!" );
						}
						else {
							Utility.showError( "Import has failed." );
						}
					}
				};
			}
			OperationsProgress.getInstance( PlayPane.UIPROGRESS ).add( pt );
		}
	}
	

  private void replaceCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceCheckActionPerformed
  }//GEN-LAST:event_replaceCheckActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.JCheckBox calcInfers;
  private javax.swing.JCheckBox conformer;
  private javax.swing.JRadioButton diskStaging;
  private gov.va.semoss.ui.components.FileBrowsePanel file;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JRadioButton memoryStaging;
  private javax.swing.JCheckBox metamodel;
  private javax.swing.JCheckBox replaceCheck;
  private javax.swing.JRadioButton togridbtn;
  // End of variables declaration//GEN-END:variables

	private boolean runCheck() {
		Object[] buttons = { "Cancel Loading", "Continue With Loading" };
		int response = JOptionPane.showOptionDialog( null,
				"This move cannot be undone. Please make sure the excel file is "
				+ "formatted correctly \nand make a back up jnl file before "
				+ "continuing. Would you still like to continue?", "Warning",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
				buttons, buttons[1] );
		return response == 1;
	}
	
	private boolean runOverrideCheck( Collection<File> fileNames ) {
		
		StringBuilder replacedString = new StringBuilder();
		
		for ( File onefile : fileNames ) {
			try ( FileInputStream fis = new FileInputStream( onefile ) ) {
				XSSFWorkbook book = new XSSFWorkbook( fis );
				XSSFSheet lSheet = book.getSheet( "Loader" );
				int lastRow = lSheet.getLastRowNum();
				
				List<String> nodes = new ArrayList<>();
				List<String[]> relationships = new ArrayList<>();
				for ( int rIndex = 1; rIndex <= lastRow; rIndex++ ) {
					XSSFRow sheetNameRow = lSheet.getRow( rIndex );
					XSSFCell cell = sheetNameRow.getCell( 0 );
					XSSFSheet sheet = book.getSheet( cell.getStringCellValue() );
					
					XSSFRow row = sheet.getRow( 0 );
					String sheetType = "";
					if ( row.getCell( 0 ) != null ) {
						sheetType = row.getCell( 0 ).getStringCellValue();
					}
					if ( "Node".equalsIgnoreCase( sheetType ) ) {
						if ( row.getCell( 1 ) != null ) {
							nodes.add( row.getCell( 1 ).getStringCellValue() );
						}
					}
					if ( "Relation".equalsIgnoreCase( sheetType ) ) {
						String subject;
						String object;
						String relationship = "";
						if ( row.getCell( 1 ) != null && row.getCell( 2 ) != null ) {
							subject = row.getCell( 1 ).getStringCellValue();
							object = row.getCell( 2 ).getStringCellValue();
							
							row = sheet.getRow( 1 );
							if ( row.getCell( 0 ) != null ) {
								relationship = row.getCell( 0 ).getStringCellValue();
							}
							
							relationships.add( new String[]{ subject, relationship, object } );
						}
					}
				}
				for ( String node : nodes ) {
					replacedString.append( node ).append( "\n" );
				}
				for ( String[] rel : relationships ) {
					replacedString.append( rel[0] ).append( " " ).append( rel[1] );
					replacedString.append( " " ).append( rel[2] ).append( "\n" );
				}
			}
			catch ( Exception e ) {
				log.error( e );
			}
		}
		
		Object[] buttons = { "Cancel", "Continue" };
		int response = JOptionPane.showOptionDialog( null,
				"This move cannot be undone.\nPlease make sure the excel file is formatted "
				+ "correctly and make a back up jnl file before continuing.\n\nThe "
				+ "following data will be replaced:\n\n" + replacedString + "\n"
				+ "Would you still like to continue?",
				"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
				buttons, buttons[1] );
		return response == 1;
	}
	
}
