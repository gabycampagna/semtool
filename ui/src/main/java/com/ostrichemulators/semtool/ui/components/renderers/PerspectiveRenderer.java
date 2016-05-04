/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.ui.components.renderers;

import com.ostrichemulators.semtool.om.Perspective;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.apache.log4j.Logger;
import com.ostrichemulators.semtool.rdf.engine.api.InsightManager;

/**
 *
 * @author ryan
 */
public class PerspectiveRenderer extends DefaultListCellRenderer {

  private static final Logger log = Logger.getLogger( PerspectiveRenderer.class );
  private InsightManager engine;

  public PerspectiveRenderer() {
  }

  @Override
  public Component getListCellRendererComponent( JList list, Object val, int idx,
      boolean sel, boolean hasfocus ) {
    // figure out 
    
    if ( null == val ) {
      return super.getListCellRendererComponent( list, null, idx, sel, hasfocus );
    }

    Perspective p = Perspective.class.cast( val );
		setToolTipText( p.getDescription() );
    return super.getListCellRendererComponent( list, p.getLabel(), idx, sel, hasfocus );
  }
}