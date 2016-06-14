/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.util;

import com.ostrichemulators.semtool.om.Perspective;
import com.ostrichemulators.semtool.rdf.engine.util.EngineUtil;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import com.ostrichemulators.semtool.rdf.engine.api.IEngine;
import com.ostrichemulators.semtool.rdf.engine.util.EngineManagementException;
import com.ostrichemulators.semtool.rdf.engine.util.EngineOperationListener;
import com.ostrichemulators.semtool.user.LocalUserImpl;
import com.ostrichemulators.semtool.ui.preferences.SemtoolPreferences;

/**
 *
 * @author ryan
 */
public class PinningEngineListener implements EngineOperationListener {

	private static final Logger log = Logger.getLogger( PinningEngineListener.class );
	private final Set<String> pinWhenOpen = new HashSet<>();

	private void repin( File file ) {
		try {
			EngineUtil.getInstance().mount( file, true, true, new LocalUserImpl() );
		}
		catch ( EngineManagementException ioe ) {
			log.error( "could not load database: " + file, ioe );
		}
	}

	public synchronized void reopenPinned() {
		log.debug( "reopening pinned databases" );

		Set<String> pinned = SemtoolPreferences.getPinnedLocations();

		// open everything that has been pinned
		for ( String smss : pinned ) {
			log.debug( "found pinned database: " + smss );
			pinWhenOpen.add( smss );
			File smssfile = new File( smss );
			if ( smssfile.exists() ) {
				repin( smssfile );
			}
			else {
				log.warn( "could not find pinned database: " + smss );
				SemtoolPreferences.removePin( smss );
			}
		}
	}

	@Override
	public void engineOpened( IEngine eng ) {
		// check to see if this database should be pinned
		String smss = eng.getProperty( Constants.SMSS_LOCATION );
		if ( pinWhenOpen.contains( smss ) ) {
			SemtoolPreferences.togglePin( eng );
			pinWhenOpen.remove( smss );
		}
		else {
			Set<String> pins = SemtoolPreferences.getPinnedLocations();
			if ( pins.contains( smss ) ) {
				eng.setProperty( Constants.PIN_KEY, Boolean.toString( true ) );
			}
		}
	}

	@Override
	public void engineClosed( IEngine eng ) {
		// nothing to do here
	}

	@Override
	public void insightsModified( IEngine eng, Collection<Perspective> perspectives ) {
	}

	@Override
	public void handleError( IEngine eng, EngineManagementException eme ) {
	}
}
