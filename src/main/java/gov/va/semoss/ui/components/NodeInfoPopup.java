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
package gov.va.semoss.ui.components;

import javax.swing.JTable;

import org.apache.log4j.Logger;

import gov.va.semoss.om.SEMOSSVertex;
import gov.va.semoss.ui.components.playsheets.GraphPlaySheet;
import gov.va.semoss.ui.components.playsheets.GridPlaySheet;
import gov.va.semoss.util.MultiMap;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * This class is used to display information about a node in a popup window.
 */
public class NodeInfoPopup extends AbstractAction {

	private final GraphPlaySheet gps;
	private final Collection<SEMOSSVertex> pickedVertex;
	private static final Logger logger = Logger.getLogger( NodeInfoPopup.class );
	GridFilterData gfd = new GridFilterData();
	JTable table = null;

	public NodeInfoPopup( GraphPlaySheet p, Collection<SEMOSSVertex> picked ) {
		super( "Show Selected Node Information" );
		this.putValue( Action.SHORT_DESCRIPTION,
				"To select nodes press Shift and click on nodes" );
		gps = p;
		pickedVertex = picked;
	}

	@Override
	public void actionPerformed( ActionEvent ae ) {
		MultiMap<URI, SEMOSSVertex> typeCounts = new MultiMap<>();
		for ( SEMOSSVertex v : pickedVertex ) {
			URI vType = v.getType();
			typeCounts.add( vType, v );
		}

		ValueFactory vf = new ValueFactoryImpl();
		List<Value[]> data = new ArrayList<>();
		int total = 0;
		for ( Map.Entry<URI, List<SEMOSSVertex>> en : typeCounts.entrySet() ) {
			Value[] row = { en.getKey(), vf.createLiteral( en.getValue().size() ) };
			data.add( row );
			total += en.getValue().size();
		}

		data.add( new Value[]{ vf.createLiteral( "Total Vertex Count" ),
			vf.createLiteral( total ) } );

		GridPlaySheet grid = new GridPlaySheet();
		grid.setTitle( "Selected Node Information" );
		grid.create( data, Arrays.asList( "Property Name", "Value" ), gps.getEngine() );
		//PlaySheetFrame psf = new PlaySheetFrame( gps.getEngine() );
		//psf.setTitle( "Selected Node Information" );
		gps.getPlaySheetFrame().addTab( grid );
	}
}
