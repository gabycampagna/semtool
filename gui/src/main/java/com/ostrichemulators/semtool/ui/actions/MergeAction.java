/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import com.ostrichemulators.semtool.rdf.engine.api.IEngine;
import com.ostrichemulators.semtool.util.GuiUtility;
import com.ostrichemulators.semtool.rdf.engine.util.EngineUtil;
import com.ostrichemulators.semtool.rdf.engine.util.EngineUtil2;
import com.ostrichemulators.semtool.ui.components.CopyWhatPanel;
import com.ostrichemulators.semtool.ui.components.ProgressTask;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JOptionPane;

/**
 *
 * @author ryan
 */
public class MergeAction extends DbAction {

	private static final Logger log = Logger.getLogger( MergeAction.class );
	private IEngine target = null;
	private final Frame frame;
	private final CopyWhatPanel copywhat = new CopyWhatPanel( "Copy What Data?" );

	public MergeAction( String opprog, Frame frame ) {
		super( opprog, MERGE );
		this.frame = frame;
		putValue( AbstractAction.SHORT_DESCRIPTION, "Merge two databases" );
		putValue( AbstractAction.SMALL_ICON, DbAction.getIcon( "semossjnl" ) );

	}

	public MergeAction( String opprog, IEngine from, IEngine to, Frame f ) {
		this( opprog, f );
		setEndpoints( from, to );
	}

	@Override
	public void setEngine( IEngine eng ) {
		super.setEngine( eng );
		setEnabled( !( null == getEngine() || null == target ) );
	}

	public void setTarget( IEngine to ) {
		target = to;
		if ( null != target ) {
			putValue( AbstractAction.NAME, EngineUtil2.getEngineLabel( to ) );
		}
		setEnabled( !( null == getEngine() || null == to ) );
	}

	public final void setEndpoints( IEngine from, IEngine to ) {
		setEngine( from );
		setTarget( to );
		setEnabled( !( null == from || null == to ) );
	}

	@Override
	protected boolean preAction( ActionEvent ae ) {
		copywhat.setPreferredSize( new Dimension( 250, 80 ) );
		int retval = JOptionPane.showConfirmDialog( frame, copywhat,
				"Copy what data from " + getEngineName() + "?",
				JOptionPane.OK_CANCEL_OPTION );
		return ( JOptionPane.OK_OPTION == retval );
	}

	@Override
	public ProgressTask getTask( ActionEvent ae ) {
		final String toname = target.getEngineName();
		ProgressTask pt = new ProgressTask( "Merging " + getEngineName()
				+ " into " + toname, new Runnable() {
					@Override
					public void run() {
						try {
							EngineUtil.getInstance().merge( getEngine(), target,
									copywhat.isDataSelected(), copywhat.isInsightSelected() );
						}
						catch ( Exception e ) {
							String msg = "Could not merge data";
							GuiUtility.showError( msg );
							log.error( msg, e );
						}
					}
				} );

		return pt;
	}

}
