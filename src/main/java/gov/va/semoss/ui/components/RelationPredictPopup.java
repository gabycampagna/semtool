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
package gov.va.semoss.ui.components;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

import gov.va.semoss.om.SEMOSSVertex;
import gov.va.semoss.rdf.engine.api.IEngine;
import gov.va.semoss.rdf.engine.impl.SesameJenaSelectStatement;
import gov.va.semoss.rdf.engine.impl.SesameJenaSelectWrapper;
import gov.va.semoss.ui.components.playsheets.GraphPlaySheet;
import gov.va.semoss.util.Constants;
import gov.va.semoss.util.DIHelper;
import gov.va.semoss.util.Utility;

/**
 * This class creates a popup menu to visualize possible relationships given two
 * queries.
 */
public class RelationPredictPopup extends JMenu implements MouseListener {

	// given 2 queries it will visualize the relationships possible
	GraphPlaySheet ps = null;
	// sets the picked node list
	SEMOSSVertex[] pickedVertex = null;
	Logger logger = Logger.getLogger( getClass() );

	// both of them bind
	String mainQuery = null;

	// subject bind but not object, object as label
	String mainQuery2 = null;

	//both subject and object as label
	String mainQuery3 = null;
	String neighborQuery = null;
	String altQuery = null;
	String altMainQuery = null;
	String altQuery2 = null;

	boolean instance = false;

	public RelationPredictPopup( String name, GraphPlaySheet ps, SEMOSSVertex[] pickedVertex ) {
		super( name );
		// need to get this to read from popup menu
		this.ps = ps;
		this.pickedVertex = pickedVertex;
		this.mainQuery = Constants.NEIGHBORHOOD_PREDICATE_FINDER_QUERY;
		this.mainQuery2 = Constants.NEIGHBORHOOD_PREDICATE_ALT2_FINDER_QUERY;
		this.mainQuery3 = Constants.NEIGHBORHOOD_PREDICATE_ALT3_FINDER_QUERY;
		addMouseListener( this );
	}

	/**
	 * Executes the query and adds all the relationships.
	 */
	public void addRelations() {

		// the relationship needs to have the subject - selected vertex
		// need to add the relationship to the relationship URI
		// and the predicate selected
		// the listener should then trigger the graph play sheet possibly
		// and for each relationship add the listener
		Map<String, String> hash = new HashMap<>();
		String ignoreURI = DIHelper.getInstance().getProperty( Constants.IGNORE_URI );
		// there should exactly be 2 vertices
		// we try to find the predicate based on both subject and object
		SEMOSSVertex vert1 = pickedVertex[0];
		String uri = vert1.getURI();
		SEMOSSVertex vert2 = pickedVertex[1];
		String uri2 = vert2.getURI();

		Pattern pattern = Pattern.compile( "\\s" );
		Matcher matcher = pattern.matcher( uri );
		boolean found1 = matcher.find();
		matcher = pattern.matcher( uri2 );
		boolean found2 = matcher.find();

		String subject = uri;
		String object = uri2;
		String query = DIHelper.getInstance().getProperty( mainQuery );

		if ( found1 && uri.contains( "/" ) ) {
			subject = "\"" + Utility.getInstanceName( uri ) + "\"";
		}
		else if ( uri.contains( "/" ) ) {
			subject = "<" + uri + ">";
		}
		if ( found2 && uri2.contains( "/" ) ) {
			object = "\"" + Utility.getInstanceName( uri2 ) + "\"";
		}
		else if ( uri2.contains( "/" ) ) {
			object = "<" + uri2 + ">";
		}

		hash.put( Constants.SUBJECT, subject );
		hash.put( Constants.OBJECT, object );

		if ( found1 && uri.contains( "/" ) ) {
			query = DIHelper.getInstance().getProperty( mainQuery2 );
			if ( found2 && uri2.contains( "/" ) ) {
				query = DIHelper.getInstance().getProperty( mainQuery3 );
			}
		}

		String query1 = Utility.fillParam( query, hash );

		if ( found2 && uri.contains( "/" ) ) {
			query = DIHelper.getInstance().getProperty( mainQuery2 );
			if ( found1 && uri2.contains( "/" ) ) {
				query = DIHelper.getInstance().getProperty( mainQuery3 );
			}
		}

		hash.put( Constants.SUBJECT, object );
		hash.put( Constants.OBJECT, subject );

		String query2 = Utility.fillParam( query, hash );

		IEngine engine = DIHelper.getInstance().getRdfEngine();
		logger.debug( "Found the engine for repository   " + engine.getEngineName() );

		// run the query
		SesameJenaSelectWrapper sjw = new SesameJenaSelectWrapper();
		sjw.setEngine( engine );
		sjw.setEngineType( engine.getEngineType() );
		sjw.setQuery( query1 );
		sjw.executeQuery();

		logger.debug( "Executed Query" );
		logger.info( "Executing query " + query1 );

		String[] vars = sjw.getVariables();
		while ( sjw.hasNext() ) {
			SesameJenaSelectStatement stmt = sjw.next();
			// only one variable

			String predName = stmt.getRawVar( vars[0] ) + "";

			if ( predName.length() > 0 && !Utility.checkPatternInString( ignoreURI, predName ) ) {
				NeighborRelationMenuItem nItem 
						= new NeighborRelationMenuItem( predName, ps, predName );
				add( nItem );
			}
		}

		// do it the other way now
		sjw.setQuery( query2 );
		sjw.executeQuery();
		logger.info( "Executing query " + query2 );

		logger.debug( "Executed Query" );
		while ( sjw.hasNext() ) {
			SesameJenaSelectStatement stmt = sjw.next();
			// only one variable

			String predName = stmt.getRawVar( vars[0] ) + "";

			if ( predName.length() > 0 && !Utility.checkPatternInString( ignoreURI, predName ) ) {
				NeighborRelationMenuItem nItem 
						= new NeighborRelationMenuItem( predName, ps, predName );
				add( nItem );
			}
		}
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on a
	 * component. Adds relations once the mouse has been clicked.
	 *
	 * @param arg0 MouseEvent
	 */
	@Override
	public void mouseClicked( MouseEvent arg0 ) {
		logger.info( "Mouse Entered and Clicked" );
		addRelations();
	}

	/**
	 * Invoked when the mouse enters a component.
	 *
	 * @param arg0 MouseEvent
	 */
	@Override
	public void mouseEntered( MouseEvent arg0 ) {
		logger.info( "Mouse Entered and Clicked" );
	//	addRelations();

	}

	/**
	 * Invoked when the mouse exits a component.
	 *
	 * @param arg0 MouseEvent
	 */
	@Override
	public void mouseExited( MouseEvent arg0 ) {

	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 *
	 * @param arg0 MouseEvent
	 */
	@Override
	public void mousePressed( MouseEvent arg0 ) {

	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 *
	 * @param arg0 MouseEvent
	 */
	@Override
	public void mouseReleased( MouseEvent arg0 ) {

	}
}
