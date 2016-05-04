/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.ui.components.renderers;

import com.ostrichemulators.semtool.ui.helpers.GraphShapeRepository;
import com.ostrichemulators.semtool.util.Constants;
import com.ostrichemulators.semtool.util.DIHelper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

/**
 *
 * @author ryan
 */
public class ShapeRenderer extends DefaultListCellRenderer {

	private static final ImageIcon EMPTY;
	private static final Map<Shape, Icon> icons = new HashMap<>();

	static {
		BufferedImage img = new BufferedImage( 16, 16, BufferedImage.TYPE_INT_ARGB );
		EMPTY = new ImageIcon( img );
	}

	@Override
	public Component getListCellRendererComponent( JList<?> list, Object val,
			int index, boolean sel, boolean focus ) {

		if( null == val ){
			return super.getListCellRendererComponent( list, val, index, sel, focus );
		}
		
		String s = GraphShapeRepository.instance().getShapeName( Shape.class.cast( val ) );
		return super.getListCellRendererComponent( list, s, index, sel, focus );
	}
}