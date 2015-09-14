package gov.va.semoss.ui.components;


import org.apache.log4j.Logger;

import gov.va.semoss.om.Insight;
import gov.va.semoss.rdf.engine.api.IEngine;
import gov.va.semoss.rdf.engine.api.QueryExecutor;
import gov.va.semoss.rdf.query.util.UpdateExecutorAdapter;
import gov.va.semoss.ui.components.api.IPlaySheet;
import gov.va.semoss.ui.components.playsheets.PlaySheetCentralComponent;
import gov.va.semoss.util.DIHelper;

import gov.va.semoss.util.GuiUtility;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

public abstract class ExecuteQueryProcessor extends AbstractAction {

	private static final Logger logger = Logger.getLogger( ExecuteQueryProcessor.class );

	public ExecuteQueryProcessor() {
	}

	public ExecuteQueryProcessor( String name ) {
		super( name );
	}

	public ExecuteQueryProcessor( String name, Icon icon ) {
		super( name, icon );
	}

	/**
	 * Displays a warning dialog to the user, indicating that the attempted
	 * database-update query cannot be undone by a simple keystroke, and offers an
	 * option to cancel out.
	 *
	 * @return showWarning -- (int) Corresponds to the "JOptionPane.YES_OPTION" or
	 * the "JOptionPane.NO_OPTION".
	 */
	private boolean okToUpdate() {
		Object[] buttons = { "Continue", "Cancel" };
		int response = JOptionPane.showOptionDialog( null,
				"The update query you are about to run \ncannot be undone.  Would you like to continue?",
				"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[1] );
		return ( JOptionPane.YES_OPTION == response );
	}

	protected abstract String getTitle();

	protected String getFrameTitle() {
		return getTitle();
	}

	protected abstract QueryExecutor getQuery();

	protected abstract Class<? extends IPlaySheet> getPlaySheet();

	protected abstract IEngine getEngine();

	protected abstract boolean isAppending();

	/**
	 * Gives subclasses a chance to prepare for running our query
	 *
	 * @param ae
	 */
	protected void prepare( ActionEvent ae ) {
	}

	@Override
	public void actionPerformed( ActionEvent ae ) {
		QueryExecutor query = getQuery();

		Class<? extends IPlaySheet> klass = getPlaySheet();
		IEngine engine = getEngine();

		ProgressTask pt = null;
		if ( null == klass ) {
			if ( okToUpdate() ) {
				pt = makeUpdateTask( query, engine );
			}
		}
		else {
			// run a regular query		
			JDesktopPane pane = DIHelper.getInstance().getDesktop();
			DIHelper.getInstance().getPlayPane().showDesktop();
			String title = getTitle();
			boolean appending = isAppending();

			Insight insight = new Insight( title, query.getSparql(),
					(Class<PlaySheetCentralComponent>) klass );

			if ( appending ) {
				PlaySheetFrame psf = PlaySheetFrame.class.cast( pane.getSelectedFrame() );
				pt = psf.getOverlayTask( insight, query.getBindingMap(), title );
			}
			else {
				PlaySheetFrame psf = new PlaySheetFrame( engine, getFrameTitle() );
				DIHelper.getInstance().getDesktop().add( psf );
				pt = psf.getCreateTask( insight, query.getBindingMap() );
			}

			DIHelper.getInstance().getPlayPane().showDesktop();
		}

		if ( null != pt ) {
			OperationsProgress op = OperationsProgress.getInstance( PlayPane.UIPROGRESS );
			op.add( pt );
		}
	}

	private static ProgressTask makeUpdateTask( QueryExecutor query, IEngine engine ) {
		final Exception ok[] = { null };
		// this is an update query
		return new ProgressTask( "Executing Update", new Runnable() {

			@Override
			public void run() {
				try {
					UpdateExecutorAdapter uea = new UpdateExecutorAdapter( query.getSparql() );
					uea.copyBindings( query );
					engine.update( uea );
				}
				catch ( RepositoryException | MalformedQueryException | UpdateExecutionException | SecurityException ex ) {
					logger.error( ex, ex );
					ok[0] = ex;
				}
			}
		} ) {

			@Override
			public void done() {
				super.done();
				if ( null != ok[0] ) {
					GuiUtility.showError( ok[0].getLocalizedMessage() );
				}
			}
		};
	}
}
