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
package gov.va.semoss.algorithm.impl;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;

import gov.va.semoss.om.SEMOSSEdge;
import gov.va.semoss.om.SEMOSSVertex;
import edu.uci.ics.jung.visualization.picking.PickedState;
import gov.va.semoss.graph.functions.IslandProcessor;
import gov.va.semoss.ui.components.playsheets.GraphPlaySheet;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;

/**
 * This class is used to identify islands in the network.
 */
public class IslandIdentifierProcessor extends AbstractAction {

	private final List<SEMOSSVertex> selectedVerts = new ArrayList<>();
	private final GraphPlaySheet gps;
	private final boolean allIslands;

	public IslandIdentifierProcessor( GraphPlaySheet gps, Collection<SEMOSSVertex> pickedV ) {
		super( "Island Identifier" );
		this.gps = gps;

		// if no nodes are selected, then all nodes are selected
		allIslands = pickedV.isEmpty();
		selectedVerts.addAll( allIslands
				? gps.getVisibleGraph().getVertices() : pickedV );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		DirectedGraph<SEMOSSVertex, SEMOSSEdge> graph = gps.getVisibleGraph();
		IslandProcessor<SEMOSSVertex, SEMOSSEdge> proc = new IslandProcessor( graph );
		Set<Graph<SEMOSSVertex, SEMOSSEdge>> islands = proc.getIslands( selectedVerts );

		if ( allIslands ) {
			Graph<SEMOSSVertex, SEMOSSEdge> biggestIsland = null;

			for ( Graph<SEMOSSVertex, SEMOSSEdge> island : islands ) {
				if ( null == biggestIsland
						|| island.getVertexCount() > biggestIsland.getVertexCount() ) {
					biggestIsland = island;
				}
			}

			islands.remove( biggestIsland );
		}

		for ( Graph<SEMOSSVertex, SEMOSSEdge> island : islands ) {
			highlightIsland( island.getVertices(), island.getEdges() );
		}
	}

	/**
	 * Sets the transformers based on valid edges and vertices for the playsheet.
	 */
	private void highlightIsland( Collection<SEMOSSVertex> islandVerts,
			Collection<SEMOSSEdge> islandEdges ) {

		if ( allIslands ) {
			gps.highlight( islandVerts, islandEdges );
		}
		else {
			PickedState state = gps.getView().getPickedVertexState();

			for ( SEMOSSVertex v : islandVerts ) {
				state.pick( v, true );
			}

			gps.skeleton( islandVerts, islandEdges );
		}
	}
}
