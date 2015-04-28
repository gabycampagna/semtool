/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import gov.va.semoss.rdf.engine.api.IEngine;
import gov.va.semoss.util.Constants;
import gov.va.semoss.util.DIHelper;
import gov.va.semoss.ui.components.ProgressTask;
import gov.va.semoss.ui.main.SemossPreferences;

/**
 *
 * @author ryan
 */
public class PinAction extends DbAction {

  private static final Logger log = Logger.getLogger( PinAction.class );
  private boolean ispinned = false;

  public PinAction( String optg ) {
    super( optg, PIN, "dbpin" );
    putValue( AbstractAction.SHORT_DESCRIPTION, "Pin/Unpin the database" );
	putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_P);
  }

  @Override
  public void setEngine( IEngine eng ) {
    super.setEngine( eng );

    if ( null != eng ) {
      ispinned
          = Boolean.parseBoolean( eng.getProperty( Constants.PIN_KEY ) );
      putValue( AbstractAction.NAME, ( ispinned ? "Unpin " : "Pin " )
          + getEngineName() );
    }
  }

  @Override
  protected ProgressTask getTask( ActionEvent ae ) {
    ProgressTask pt = new ProgressTask( ( ispinned ? "Unpinning " : "Pinning " )
        + getEngineName(), new Runnable() {
          @Override
          public void run() {
            SemossPreferences.getInstance().togglePin( getEngine() );
            setEngine( getEngine() );
						
						DIHelper.getInstance().getRepoList().repaint();
          }
        } );

    return pt;
  }

}
