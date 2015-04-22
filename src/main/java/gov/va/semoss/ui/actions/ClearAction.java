/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.actions;

import gov.va.semoss.rdf.engine.util.EngineManagementException;
import gov.va.semoss.rdf.engine.util.EngineUtil;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import gov.va.semoss.rdf.query.util.ModificationExecutorAdapter;
import gov.va.semoss.util.Utility;
import gov.va.semoss.ui.components.CopyWhatPanel;
import gov.va.semoss.ui.components.ProgressTask;
import java.io.IOException;

/**
 *
 * @author ryan
 */
public class ClearAction extends DbAction {

	private static final Logger log = Logger.getLogger( ClearAction.class );
	private final Frame frame;
	private final CopyWhatPanel copywhat = new CopyWhatPanel( "Erase What Data?" );

	public ClearAction( String optg, Frame frame ) {
		super( optg, CLEAR, "erasedb" );
		this.frame = frame;
		putValue( AbstractAction.SHORT_DESCRIPTION, "Empty the data from a database" );
	}

	@Override
	public boolean preAction( ActionEvent ae ) {
		int retval = JOptionPane.showConfirmDialog( frame, copywhat,
				"Clear what data from data from " + getEngineName() + "?",
				JOptionPane.OK_CANCEL_OPTION );
		return ( JOptionPane.OK_OPTION == retval );
	}

	@Override
	protected ProgressTask getTask( ActionEvent ae ) {
		ProgressTask pt = new ProgressTask( "Erasing " + getEngineName(),
				new Runnable() {
					@Override
					public void run() {
						try {
							if ( copywhat.isDataSelected() ) {
								getEngine().execute( new ModificationExecutorAdapter() {

									@Override
									public void exec( RepositoryConnection conn ) throws RepositoryException {
										conn.clear();
									}
								} );
							}

							if ( copywhat.isInsightSelected() ) {
								EngineUtil eu = EngineUtil.getInstance();
								eu.importInsights( getEngine(), null, true );
							}
						}
						catch ( RepositoryException | IOException | EngineManagementException re ) {
							Utility.showError( re.getLocalizedMessage() );
							log.error( re, re );
						}
					}
				} ) {
					@Override
					public void done() {
						super.done();

						StringBuilder msg = new StringBuilder();
						boolean dsel = copywhat.isDataSelected();
						boolean isel = copywhat.isInsightSelected();

						if ( dsel ) {
							msg.append( "Data" );
						}
						if ( isel ) {
							if ( dsel ) {
								msg.append( " and " );
							}
							msg.append( "Insights" );
						}

						if ( !( dsel && isel ) ) {
							msg.append( " (only)" );
						}

						msg.append( " erased" );

						Utility.showMessage( msg.toString() );
					}
				};

		return pt;
	}

}