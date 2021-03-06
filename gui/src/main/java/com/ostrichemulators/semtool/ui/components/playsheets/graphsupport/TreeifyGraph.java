/**
 * *****************************************************************************
 * Copyright 2013 SEMOSS.ORG
 *
 * This file is part of SEMOSS.
 *
 * SEMOSS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * SEMOSS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * SEMOSS. If not, see <http://www.gnu.org/licenses/>.
 * ****************************************************************************
 */
package com.ostrichemulators.semtool.ui.components.playsheets.graphsupport;

import java.awt.event.ActionEvent;

import com.ostrichemulators.semtool.om.SEMOSSVertex;
import com.ostrichemulators.semtool.ui.components.playsheets.GraphPlaySheet;
import com.ostrichemulators.semtool.ui.components.playsheets.TreeGraphPlaySheet;
import com.ostrichemulators.semtool.util.GuiUtility;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Controls converting the graph to a tree layout.
 */
public class TreeifyGraph extends AbstractAction {

	public static final String LAYOUT_NAME = "layout";
	private GraphPlaySheet gps;

	public TreeifyGraph() {
		super( "Convert to Tree", GuiUtility.loadImageIcon( "tree.png" ) );

		putValue( Action.SHORT_DESCRIPTION,
				"Convert graph to tree by duplicating nodes with multiple in-edges (in new tab)" );
	}

	/**
	 * Method setPlaySheet. Sets the play sheet that the listener will access.
	 *
	 * @param ps GraphPlaySheet
	 */
	public void setPlaySheet( GraphPlaySheet ps ) {
		gps = ps;
		setEnabled( false );

		gps.getView().getPickedVertexState().addItemListener( new ItemListener() {

			@Override
			public void itemStateChanged( ItemEvent e ) {
				Collection<? extends SEMOSSVertex> picks
						= gps.getView().getPickedVertexState().getPicked();

				if ( !isEnabled() ) {
					setEnabled( !picks.isEmpty() );
				}
			}
		} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		TreeGraphPlaySheet tgps = new TreeGraphPlaySheet( gps.getGraphData().getGraph(),
				gps.getView().getPickedVertexState().getPicked(), getLayoutClass() );
		gps.addSibling( tgps );
	}

	private Class<? extends Layout> getLayoutClass() {
		Object obj = getValue( LAYOUT_NAME );
		return (Class<? extends Layout>) ( null == obj ? TreeLayout.class : obj );
	}
}
