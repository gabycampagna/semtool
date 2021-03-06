/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.ui.components.renderers;

import com.ostrichemulators.semtool.om.GraphColorShapeRepository;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author ryan
 */
public class ColorRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent( JList<?> list, Object val,
			int index, boolean sel, boolean focus ) {

		if ( null == val ) {
			val = GraphColorShapeRepository.TRANSPARENT;
		}

		Color color = Color.class.cast( val );
		super.getListCellRendererComponent( list, "", index, sel, focus );
		TableColorRenderer.colorify( this, color );

		return this;
	}
}
