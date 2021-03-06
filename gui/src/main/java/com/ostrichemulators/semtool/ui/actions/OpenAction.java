/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.ui.actions;

import com.ostrichemulators.semtool.poi.main.ImportData;
import com.ostrichemulators.semtool.poi.main.ImportFileReader;
import com.ostrichemulators.semtool.poi.main.ImportValidationException;
import com.ostrichemulators.semtool.poi.main.LoadingSheetData;
import com.ostrichemulators.semtool.poi.main.LoadingSheetData.DataIterator;
import com.ostrichemulators.semtool.poi.main.LoadingSheetData.LoadingNodeAndPropertyValues;
import com.ostrichemulators.semtool.poi.main.POIReader;
import com.ostrichemulators.semtool.rdf.engine.api.IEngine;
import com.ostrichemulators.semtool.rdf.engine.impl.InMemorySesameEngine;
import com.ostrichemulators.semtool.rdf.engine.util.EngineLoader;
import com.ostrichemulators.semtool.rdf.engine.util.EngineManagementException;
import com.ostrichemulators.semtool.rdf.engine.util.EngineUtil;
import com.ostrichemulators.semtool.ui.components.FileBrowsePanel;
import com.ostrichemulators.semtool.ui.components.LoadingPlaySheetFrame;
import com.ostrichemulators.semtool.ui.components.PlaySheetFrame;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;

import com.ostrichemulators.semtool.ui.components.ProgressTask;
import com.ostrichemulators.semtool.ui.components.SemtoolFileView;
import com.ostrichemulators.semtool.ui.components.models.ValueTableModel;
import com.ostrichemulators.semtool.ui.components.playsheets.GridRAWPlaySheet;
import com.ostrichemulators.semtool.util.Constants;
import com.ostrichemulators.semtool.util.DIHelper;
import com.ostrichemulators.semtool.util.MultiMap;

import com.ostrichemulators.semtool.util.GuiUtility;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;

/**
 *
 * @author ryan
 */
public class OpenAction extends DbAction {

	private static final Logger log = Logger.getLogger( OpenAction.class );
	private final List<File> files = new ArrayList<>();
	private final Frame frame;

	public static enum FileHandling {

		LOADINGSHEET, SPREADSHEET, JOURNAL, TRIPLES, UNKNOWN
	};

	public OpenAction( String optg, Frame frame ) {
		super( optg, "Open File", "open-file3" );
		this.frame = frame;
		putValue( SHORT_DESCRIPTION, "Open Files" );
		putValue( AbstractAction.MNEMONIC_KEY, KeyEvent.VK_O );
	}

	@Override
	public boolean preAction( ActionEvent ae ) {
		files.clear();
		Preferences prefs = Preferences.userNodeForPackage( getClass() );
		File f = FileBrowsePanel.getLocationForEmptyPref( prefs, "lastimpdir" );

		Set<File> openedDbs = new HashSet<>();
		for ( IEngine eng : DIHelper.getInstance().getEngineMap().values() ) {
			openedDbs.add( new File( eng.getProperty( Constants.SMSS_LOCATION ) ) );
		}

		JFileChooser chsr = new JFileChooser( f );
		chsr.setFileSelectionMode( JFileChooser.FILES_ONLY );
		chsr.setFileView(new SemtoolFileView() );
		chsr.setFileFilter( FileBrowsePanel.getLoadingSheetsFilter( true ) );
		chsr.addChoosableFileFilter( FileBrowsePanel.getDatabaseFilter( openedDbs ) );
		chsr.setDialogTitle( "Select File to Import" );
		chsr.setMultiSelectionEnabled( true );

		if ( JFileChooser.APPROVE_OPTION == chsr.showOpenDialog( frame ) ) {
			prefs.put( "lastimpdir", chsr.getSelectedFile().getParent() );

			for ( File file : chsr.getSelectedFiles() ) {
				if ( file.exists() ) {
					files.add( file );
				}
			}
		}

		return !files.isEmpty();
	}

	@Override
	public ProgressTask getTask( ActionEvent ae ) {
		ProgressTask pt = openFiles( DIHelper.getInstance().getDesktop(),
				new ArrayList<>( files ), getEngine() );
		files.clear(); // clear for next time
		return pt;
	}

	/**
	 * A super-inefficient way to organize the given files. This function tries to
	 * determine what kind of file it is. Hopefully, this can be done by examining
	 * the file extension. However, for cases like <code>LOADINGSHEET</code> and
	 * <code>SPREADSHEET</code>, the files must be opened and parsed to tell what
	 * we have
	 *
	 * @param files
	 * @return
	 */
	public static Map<File, FileHandling> categorizeFiles( Collection<File> files ) {
		Map<File, FileHandling> map = new HashMap<>();

		for ( File file : files ) {
			switch ( FilenameUtils.getExtension( file.getName() ).toLowerCase() ) {
				case "jnl":
					map.put( file, FileHandling.JOURNAL );
					break;
				case "csv":
					map.put( file, FileHandling.LOADINGSHEET );
					break;
				case "rdf":
				case "nt":
				case "ttl":
					map.put( file, FileHandling.TRIPLES );
					break;
				case "xlsx":
					map.put( file, checkLoadingSheetValidity( file ) );
					break;
				default:
					log.warn( "could not determine file type: " + file );
					map.put( file, FileHandling.UNKNOWN );
			}
		}

		return map;
	}

	private static FileHandling checkLoadingSheetValidity( File fileToLoad ) {
		ImportFileReader rdr = EngineLoader.getDefaultReader( fileToLoad );

		try {
			// we read the whole file to see if we throw any exceptions
			// FIXME: this ain't a great plan
			rdr.readOneFile( fileToLoad ); // see if an exception gets thrown
			return FileHandling.LOADINGSHEET;
		}
		catch ( ImportValidationException ive ) {
			if ( "xlsx".equalsIgnoreCase( FilenameUtils.getExtension( fileToLoad.getName() ) ) ) {
				return FileHandling.SPREADSHEET;
			}
		}
		catch ( IOException e ) {
			return FileHandling.UNKNOWN;
		}

		return FileHandling.UNKNOWN;
	}

	/**
	 * A convenience function to {@link #openFiles(javax.swing.JDesktopPane,
	 * java.util.Collection, com.ostrichemulators.semtool.rdf.engine.api.IEngine, boolean,
	 * boolean, boolean, boolean) openFiles( pane, toload, engine, false, false,
	 * false, false )}
	 *
	 * @param pane
	 * @param toload
	 * @param engine
	 * @return the progress task to open the files, or null if the user canceled
	 */
	public static ProgressTask openFiles( JDesktopPane pane,
			Collection<File> toload, IEngine engine ) {
		return openFiles( pane, toload, engine, false, false, false, false );
	}

	/**
	 * Opens a set of unknown files as best as possible. This function will open a
	 * {@link LoadingPlaySheetFrame} for previewable files, plus a regular
	 * grid-filled {@link PlaySheetFrame} for any previewable-but-not loading
	 * sheet files, and attempt to straight load any RDF-type files
	 *
	 * @param pane
	 * @param toload
	 * @param engine
	 * @param calc
	 * @param dometamodel
	 * @param conformance
	 * @param replace
	 * @return the progress task to open the files, or null if the user canceled
	 */
	public static ProgressTask openFiles( JDesktopPane pane,
			Collection<File> toload, IEngine engine, boolean calc,
			boolean dometamodel, boolean conformance, boolean replace ) {

		// things are easier if we figure out what we can read, and what we 
		// can't before we bother processing.
		MultiMap<FileHandling, File> map = MultiMap.flip( categorizeFiles( toload ) );
		List<File> journals = map.getNN( FileHandling.JOURNAL );
		List<File> triples = map.getNN( FileHandling.TRIPLES );

		List<File> loadingsheets = map.getNN( FileHandling.LOADINGSHEET );
		List<File> spreadsheets = map.getNN( FileHandling.SPREADSHEET );
		List<File> unknowns = map.getNN( FileHandling.UNKNOWN );

		if ( !unknowns.isEmpty() ) {
			GuiUtility.showError( "Could not read/parse " + unknowns.size()
					+ " files.\nPlease correct them before continuing.\n"
					+ Arrays.toString( unknowns.toArray() ) );
			return null;
		}

		if ( !spreadsheets.isEmpty() ) {
			ListIterator<File> li = spreadsheets.listIterator();
			while ( li.hasNext() ) {
				File spreadsheet = li.next();
				int ans = JOptionPane.showConfirmDialog( pane,
						spreadsheet + " does not appear to be a Loading Sheet\nOpen Anyway?",
						"Loading Sheet Error", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null );
				if ( JOptionPane.NO_OPTION == ans ) {
					li.remove();
				}
				else if ( JOptionPane.CANCEL_OPTION == ans ) {
					return null;
				}
			}
		}

		ProgressTask progress = new ProgressTask( "Loading Files", new Runnable() {
			@Override
			public void run() {
				if ( !loadingsheets.isEmpty() ) {
					LoadingPlaySheetFrame psf = new LoadingPlaySheetFrame( engine,
							loadingsheets, calc, dometamodel, conformance, replace );
					pane.add( psf );

					StringBuilder windowTitle = new StringBuilder();
					for ( File f : loadingsheets ) {
						if ( 0 != windowTitle.length() ) {
							windowTitle.append( ", " );
						}
						windowTitle.append( f.getName() );
					}

					psf.setTitle( windowTitle.toString() );
					psf.getLoadingTask().getOp().run();
				}

				if ( !triples.isEmpty() ) {
					addTriples( triples, engine, pane );
				}

				if ( !spreadsheets.isEmpty() ) {
					addGrids( spreadsheets, engine, pane );
				}

				if ( !journals.isEmpty() ) {
					for ( File f : journals ) {
						try {
							EngineUtil.getInstance().mount( f.toString(), true );
						}
						catch ( EngineManagementException e ) {
							Logger.getLogger( OpenAction.class ).error( e, e );
						}
					}
				}
			}
		} );

		return progress;
	}

	private static void addTriples( Collection<File> noreader, IEngine engine,
			JDesktopPane pane ) {
		PlaySheetFrame psf = new PlaySheetFrame( engine, "Triples Data" );
		pane.add( psf );
		int progressPerFile = 100 / noreader.size();

		for ( File f : noreader ) {
			psf.addProgress( "Reading " + f, progressPerFile );

			InMemorySesameEngine eng = InMemorySesameEngine.open();
			EngineLoader loader = new EngineLoader();
			try {
				loader.loadToEngine( Arrays.asList( f ), eng, false, null );
				GridRAWPlaySheet grid = new GridRAWPlaySheet();
				grid.setTitle( f.toString() );
				grid.create( eng.toModel(), eng );
				psf.add( grid );
			}
			catch ( RepositoryException | IOException | ImportValidationException e ) {
				log.error( e, e );
			}
			finally {
				loader.release();
				engine.closeDB();
			}
		}
	}

	public static void addGrids( Collection<File> xslxes, IEngine engine,
			JDesktopPane pane ) {
		int progressPerFile = 100 / xslxes.size();
		String sTitle = xslxes.toString();
		//PlaySheetFrame psf = new PlaySheetFrame( engine, "Import Data" );
		PlaySheetFrame psf = new PlaySheetFrame( engine,
				sTitle.substring( sTitle.lastIndexOf( "\\" ) + 1, sTitle.lastIndexOf( "]" ) ) );
		try {
			psf.setIcon( true );
		}
		catch ( PropertyVetoException e ) {
			// TODO Auto-generated catch block

		}
		pane.add( psf );

		for ( File fileToLoad : xslxes ) {
			psf.addProgress( "Reading " + fileToLoad, progressPerFile );
			try {
				psf.setTitle( fileToLoad.getName() );
				ValueFactory vf = new ValueFactoryImpl();
				ImportData data = POIReader.readNonloadingSheet( fileToLoad );
				for ( LoadingSheetData lsd : data.getSheets() ) {
					ValueTableModel vtm = new ValueTableModel();
					vtm.setAllowInsertsInPlace( true );
					vtm.setReadOnly( false );
					GridRAWPlaySheet grid = new GridRAWPlaySheet( vtm );

					List<Value[]> vals = new ArrayList<>();
					DataIterator di = lsd.iterator();
					while ( di.hasNext() ) {
						LoadingNodeAndPropertyValues nap = di.next();
						vals.add( nap.convertToValueArray( vf ) );
					}
					grid.create( vals, lsd.getHeaders(), engine );
					grid.setTitle( lsd.getName() );

					Action save = grid.getActions().get( LoadingPlaySheetFrame.SAVE );
					save.putValue( AbstractSavingAction.LASTSAVE, fileToLoad );
					Action saveall = grid.getActions().get( LoadingPlaySheetFrame.SAVE_ALL );
					saveall.putValue( AbstractSavingAction.LASTSAVE, fileToLoad );

					psf.addTab( grid );
					psf.hideProgress();
				}
			}
			catch ( Exception eee ) {
				log.error( eee, eee );
			}
		}
	}
}
