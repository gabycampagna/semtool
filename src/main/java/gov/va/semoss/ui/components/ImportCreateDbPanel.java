/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.components;

import gov.va.semoss.poi.main.FileLoadingException;
import gov.va.semoss.poi.main.ImportData;
import gov.va.semoss.poi.main.ImportFileReader;
import gov.va.semoss.poi.main.ImportMetadata;
import gov.va.semoss.rdf.engine.api.IEngine;
import gov.va.semoss.rdf.engine.util.EngineLoader;
import gov.va.semoss.rdf.engine.util.EngineManagementException;
import gov.va.semoss.rdf.engine.util.EngineOperationAdapter;
import gov.va.semoss.rdf.engine.util.EngineOperationListener;
import gov.va.semoss.rdf.engine.util.EngineUtil;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.log4j.Logger;
import gov.va.semoss.util.Constants;
import gov.va.semoss.util.Utility;
import gov.va.semoss.ui.main.SemossPreferences;
import gov.va.semoss.util.DIHelper;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author ryan
 */
public class ImportCreateDbPanel extends javax.swing.JPanel {

	private static final Logger log = Logger.getLogger( ImportCreateDbPanel.class );
	public static final String METADATABASEURI = "Use Loading Sheet Metadata";

	private boolean loadable = false;

	/**
	 * Creates new form ExistingDbPanel
	 */
	public ImportCreateDbPanel() {
		initComponents();

		Preferences prefs = Preferences.userNodeForPackage( getClass() );
		file.setPreferencesKeys( prefs, "lastpath" );
		file.setMultipleFilesOk( true );

		questionfile.setPreferencesKeys( prefs, "lastquestionspath" );

		questionfile.getChooser().setFileFilter( FileBrowsePanel.getInsightTypesFilter() );

		dbdir.setPreferencesKeys( prefs, "lastdbcreatepath" );
		dbdir.setMultipleFilesOk( false );
		dbdir.getChooser().setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		dbdir.setFileTextFromInit();

		baseuri.addItem( METADATABASEURI );
		Set<String> seen = new HashSet<>();
		seen.add( METADATABASEURI );
		for ( String uri : prefs.get( "lastontopath", "http://va.gov/ontologies" ).split( ";" ) ) {
			if ( !seen.contains( uri ) ) {
				baseuri.addItem( uri );
				seen.add( uri );
			}
		}

		JFileChooser chsr = file.getChooser();
		chsr.
				addChoosableFileFilter( FileBrowsePanel.getLoadingSheetsFilter( true ) );
		chsr.addChoosableFileFilter( new FileBrowsePanel.CustomFileFilter(
				"Turtle Files", "ttl" ) );
		chsr.addChoosableFileFilter( new FileBrowsePanel.CustomFileFilter(
				"RDF/XML Files", "rdf" ) );
		chsr.addChoosableFileFilter( new FileBrowsePanel.CustomFileFilter(
				"N-Triples Files", "nt" ) );
		chsr.addChoosableFileFilter( new FileBrowsePanel.CustomFileFilter(
				"N3 Files", "n3" ) );
		chsr.setFileFilter( FileBrowsePanel.getAllImportTypesFilter() );

		loadable = false;
		DocumentListener dl = new DocumentListener() {

			@Override
			public void insertUpdate( DocumentEvent e ) {
				check();
			}

			@Override
			public void removeUpdate( DocumentEvent e ) {
				check();
			}

			@Override
			public void changedUpdate( DocumentEvent e ) {
				check();
			}

			private void check() {
				checkOk();
			}
		};

		dbname.getDocument().addDocumentListener( dl );
		dbdir.addDocumentListener( dl );

		SemossPreferences vc = SemossPreferences.getInstance();
		calcInfers.setSelected( PlayPane.
				getProp( vc, Constants.CALC_INFERENCES_PREF ) );
	}

	private void checkOk() {
		loadable = !( null == dbdir.getFirstFile() || dbname.getText().isEmpty() );
	}

	public boolean isLoadable() {
		return loadable;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings( "unchecked" )
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel2 = new javax.swing.JLabel();
    file = new gov.va.semoss.ui.components.FileBrowsePanel();
    urilbl = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    dbname = new javax.swing.JTextField();
    questionlbl = new javax.swing.JLabel();
    questionfile = new gov.va.semoss.ui.components.FileBrowsePanel();
    dbdir = new gov.va.semoss.ui.components.FileBrowsePanel();
    jLabel1 = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    diskStaging = new javax.swing.JRadioButton();
    memoryStaging = new javax.swing.JRadioButton();
    calcInfers = new javax.swing.JCheckBox();
    metamodel = new javax.swing.JCheckBox();
    conformer = new javax.swing.JCheckBox();
    baseuri = new javax.swing.JComboBox<String>();

    jLabel2.setLabelFor(file);
    jLabel2.setText("Select File(s) to Import");

    urilbl.setText("Designate Base URI");

    jLabel3.setText("New Database Name");

    dbname.setToolTipText("Database name cannot contain spaces");

    questionlbl.setText("Import Insights");

    questionfile.setToolTipText("Enter file path and name or browse to find custom questions sheet");

    jLabel1.setText("Database Location");

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(java.awt.Color.gray, 1, true), "Load Intermediate Data", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 0, 12))); // NOI18N
    jPanel1.setToolTipText("Where should the raw data be loaded before importing?");

    diskStaging.setText("On Disk");
    diskStaging.setToolTipText("Loading on disk can be slower, but uses less memory");

    memoryStaging.setSelected(true);
    memoryStaging.setText("In Memory");
    memoryStaging.setToolTipText("Loading in memory can be faster, but uses more memory");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(diskStaging)
          .addComponent(memoryStaging))
        .addGap(0, 83, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(diskStaging)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(memoryStaging))
    );

    calcInfers.setSelected(true);
    calcInfers.setText("Compute Dependent Relationships");

    metamodel.setSelected(true);
    metamodel.setText("Create Metamodel");

    conformer.setText("Check Quality");

    baseuri.setEditable(true);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(0, 0, 0)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(questionlbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(urilbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
              .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(file, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(dbname)
              .addComponent(dbdir, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
              .addComponent(questionfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(baseuri, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(conformer)
              .addComponent(calcInfers)
              .addComponent(metamodel)
              .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(0, 0, Short.MAX_VALUE))))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(0, 0, 0)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(dbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(urilbl)
          .addComponent(baseuri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(dbdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel1))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(questionlbl)
          .addComponent(questionfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(file, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2)
            .addGap(18, 18, 18)
            .addComponent(calcInfers)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(metamodel)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(conformer)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents

	public static void showDialog( Frame frame ) {
		Object options[] = { "Create DB", "Cancel" };
		ImportCreateDbPanel icdp = new ImportCreateDbPanel();

		boolean ok = false;
		boolean docreate = false;
		while ( !ok ) {
			int opt = JOptionPane.showOptionDialog( frame, icdp,
					"Create New Database", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0] );
			ok = icdp.isLoadable();

			if ( 0 == opt ) {
				if ( ok ) {
					docreate = true;
				}
				else {
					JOptionPane.showMessageDialog( frame,
							"You must specify a DB location and name", "Inconsistent Data",
							JOptionPane.ERROR_MESSAGE );
				}
			}
			else {
				ok = true;
			}
		}

		if ( docreate ) {
			try {
				icdp.doCreate();
			}
			catch ( IOException | FileLoadingException e ) {
				log.error( e, e );
				Utility.showError( e.getLocalizedMessage() );
			}
		}
	}

	public void doCreate() throws FileLoadingException, IOException {
		String mybase = baseuri.getSelectedItem().toString();

		final boolean stageInMemory = memoryStaging.isSelected();
		final boolean calc = calcInfers.isSelected();
		final boolean dometamodel = metamodel.isSelected();
		final boolean conformance = conformer.isSelected();

		Collection<File> files = file.getFiles();

		URI defaultBase = null;
		if ( !files.isEmpty() ) {
			if ( null == mybase || mybase.isEmpty() || METADATABASEURI.equals( mybase ) ) {
				Set<URI> uris = new HashSet<>();
				Preferences prefs = Preferences.userNodeForPackage( getClass() );
				String basepref = prefs.get( "lastontopath", "http://va.gov/ontologies/" );
				for ( String b : basepref.split( ";" ) ) {
					uris.add( new URIImpl( b ) );
				}

				defaultBase = getDefaultBaseUri( files, uris );

				// save the default base for next time
				if ( null == defaultBase ) {
					return; // user canceled
				}
				else if ( !Constants.ANYNODE.equals( defaultBase ) ) {
					// user specified something
					uris.add( defaultBase );
					StringBuilder sb = new StringBuilder();
					for ( URI u : uris ) {
						if ( 0 != sb.length() ) {
							sb.append( ";" );
						}
						sb.append( u.stringValue() );
					}
					prefs.put( "lastontopath", sb.toString() );
				}
				// else {} // every file has a base URI specified
			}
		}
		else {
			defaultBase = new URIImpl( mybase );
		}

		final URI defaultBaseUri = defaultBase;

		ProgressTask pt = new ProgressTask( "Creating Database from "
				+ file.getDelimitedPaths(), new Runnable() {
					@Override
					public void run() {
						final File smss[] = { null };
						ImportData errors = ( conformance ? new ImportData() : null );
						final EngineUtil eutil = EngineUtil.getInstance();

						EngineOperationListener eol = new EngineOperationAdapter() {

							@Override
							public void engineOpened( IEngine eng ) {
								String smssloc = eng.getProperty( Constants.SMSS_LOCATION );
								if ( null != smss[0]
								&& smssloc.equals( smss[0].getAbsolutePath() ) ) {
									eutil.removeEngineOpListener( this );
									Utility.showMessage(
											"Your database has been successfully created!" );

									if ( conformance && !errors.isEmpty() ) {
										LoadingPlaySheetFrame psf
										= new LoadingPlaySheetFrame( eng, errors );
										psf.setTitle( "Conformance Check Errors" );
										DIHelper.getInstance().getDesktop().add( psf );
									}
								}
							}
						};

						eutil.addEngineOpListener( eol );

						try {
							smss[0] = EngineUtil.createNew( dbdir.getFirstFile(),
									dbname.getText(), defaultBaseUri,
									defaultBaseUri.toString().equals( baseuri.getSelectedItem().toString() ),
									null, null, questionfile.getFirstPath(), files, stageInMemory,
									calc, dometamodel, errors );
							EngineUtil.getInstance().mount( smss[0], true );
						}
						catch ( IOException | EngineManagementException ioe ) {
							log.error( ioe, ioe );
							Utility.showError( ioe.getLocalizedMessage() );
						}
					}
				} );
		OperationsProgress.getInstance( PlayPane.UIPROGRESS ).add( pt );
	}

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox<String> baseuri;
  private javax.swing.JCheckBox calcInfers;
  private javax.swing.JCheckBox conformer;
  private gov.va.semoss.ui.components.FileBrowsePanel dbdir;
  private javax.swing.JTextField dbname;
  private javax.swing.JRadioButton diskStaging;
  private gov.va.semoss.ui.components.FileBrowsePanel file;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JRadioButton memoryStaging;
  private javax.swing.JCheckBox metamodel;
  private gov.va.semoss.ui.components.FileBrowsePanel questionfile;
  private javax.swing.JLabel questionlbl;
  private javax.swing.JLabel urilbl;
  // End of variables declaration//GEN-END:variables

	/**
	 * Checks every file to make sure it has a base uri set. If any files are
	 * missing a base uri, ask the user to specify one
	 *
	 * @param files the files to check
	 * @param choices choices for a dropdown for the user
	 * @return the URI the user chose, null if the user canceled, or
	 * {@link Constants#ANYNODE} if every file has a base URI specified
	 * @throws gov.va.semoss.poi.main.FileLoadingException
	 * @throws java.io.IOException
	 */
	public static URI getDefaultBaseUri( Collection<File> files, Collection<URI> choices )
			throws FileLoadingException, IOException {
		Set<String> bases = new HashSet<>();

		EngineLoader el = new EngineLoader();

		URI choice = null;
		boolean everyFileHasBase = true;

		for ( File f : files ) {
			ImportFileReader reader = el.getReader( f );
			ImportMetadata metadata = reader.getMetadata( f );

			URI baser = metadata.getBase();
			if ( null == baser ) {
				everyFileHasBase = false;
			}
			else {
				bases.add( metadata.getBase().stringValue() );
			}
		}

		if ( everyFileHasBase ) {
			// nothing to do here
			return Constants.ANYNODE;
		}
		else {
			if ( bases.isEmpty() ) {
				JComboBox<String> box = new JComboBox<>();
				box.addItem( "" );

				for ( URI item : choices ) {
					box.addItem( item.stringValue() );
				}
				box.setEditable( true );

				JPanel pnl = new JPanel();
				pnl.setLayout( new BoxLayout( pnl, BoxLayout.LINE_AXIS ) );
				pnl.add( new JLabel( "<html>Not all the files have Base URIs set.<br>"
						+ "Please specify a Base URI to use<br>when one is not provided." ) );
				JPanel junk = new JPanel();
				junk.add( box );
				pnl.add( junk );

				int opt = JOptionPane.showOptionDialog( null, pnl, "Specify the Base URI",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						null, null );
				if ( JOptionPane.OK_OPTION != opt ) {
					log.debug( "create canceled" );
					return null;
				}

				choice = new URIImpl( box.getSelectedItem().toString() );
			}
		}

		return choice;
	}
}
