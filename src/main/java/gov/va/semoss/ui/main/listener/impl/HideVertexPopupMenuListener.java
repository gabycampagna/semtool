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
 *****************************************************************************
 */
package gov.va.semoss.ui.main.listener.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gov.va.semoss.om.SEMOSSVertex;
import gov.va.semoss.ui.components.playsheets.GraphPlaySheet;
import javax.swing.AbstractAction;

/**
 * Controls hiding the pop up menu for nodes on the graph play sheet.
 */
public class HideVertexPopupMenuListener extends AbstractAction {

	GraphPlaySheet gps;
	SEMOSSVertex[] highlightedVertices;

	public HideVertexPopupMenuListener(GraphPlaySheet gps, SEMOSSVertex[] highlightedVertices) {
		super( "Hide Nodes" );
		this.gps = gps;
		this.highlightedVertices = highlightedVertices;
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		// get the engine
		// execute the neighbor hood 
		// paint it
		// get the query from the
		for ( SEMOSSVertex vertex : highlightedVertices ) {
			// take the vertex and add it to the sheet
			gps.getFilterData().addNodeToFilter( vertex );
		}
		gps.refineView();
	}
}