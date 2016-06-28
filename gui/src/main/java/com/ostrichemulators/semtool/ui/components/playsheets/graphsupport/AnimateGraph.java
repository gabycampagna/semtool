/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.ui.components.playsheets.graphsupport;

import com.ostrichemulators.semtool.om.GraphElement;
import edu.uci.ics.jung.graph.DirectedGraph;
import com.ostrichemulators.semtool.om.SEMOSSEdge;
import com.ostrichemulators.semtool.om.SEMOSSVertex;
import com.ostrichemulators.semtool.ui.components.GraphAnimationPanel;
import com.ostrichemulators.semtool.ui.components.api.GraphListener;
import com.ostrichemulators.semtool.ui.components.playsheets.GraphPlaySheet;
import com.ostrichemulators.semtool.util.MultiMap;
import com.ostrichemulators.semtool.util.RDFDatatypeTools;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author ryan
 */
public class AnimateGraph extends AbstractAction {

	private static final Logger log = Logger.getLogger( AnimateGraph.class );
	private final GraphPlaySheet gps;

	public AnimateGraph( GraphPlaySheet ps ) {
		super( "Animate Graph" );
		putValue( Action.SHORT_DESCRIPTION,
				"Animate the graph using an edge property" );
		gps = ps;

		gps.addGraphListener( new GraphListener() {

			@Override
			public void graphUpdated( DirectedGraph<SEMOSSVertex, SEMOSSEdge> graph,
					GraphPlaySheet gps ) {
				Collection<? extends GraphElement> edges = gps.getVisibleGraph().getEdges();
				setEnabled( GraphAnimationPanel.hasAnimationCandidates( edges ) );
			}

			@Override
			public void layoutChanged( DirectedGraph<SEMOSSVertex, SEMOSSEdge> graph,
					String oldlayout, Layout<SEMOSSVertex, SEMOSSEdge> newlayout, GraphPlaySheet gps ) {
				// don't care
			}
		} );

		DirectedGraph<SEMOSSVertex, SEMOSSEdge> graph = gps.getVisibleGraph();
		Collection<SEMOSSEdge> edges = graph.getEdges();
		setEnabled( GraphAnimationPanel.hasAnimationCandidates( edges ) );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		DirectedGraph<SEMOSSVertex, SEMOSSEdge> graph = gps.getVisibleGraph();
		Collection<SEMOSSEdge> edges = graph.getEdges();

		final Map<URI, URI> map = GraphAnimationPanel.getAnimationInput(
				JOptionPane.getRootFrame(), gps.getLabelCache(),
				gps.getEngine(), edges );

		final Set<SEMOSSEdge> animatedEdges = new HashSet<>();

		final MultiMap<Value, SEMOSSEdge> iterations = new MultiMap<>();
		// we only support one element in this map (for now, at least)
		URI pred = null;
		for ( Map.Entry<URI, URI> ee : map.entrySet() ) {
			pred = ee.getValue();
			for ( SEMOSSEdge edge : edges ) {
				if ( edge.getType().equals( ee.getKey() ) && edge.hasProperty( pred ) ) {
					Value v = edge.getValue( pred );
					iterations.add( v, edge );
					animatedEdges.add( edge );
				}
			}
		}

		final List<Value> vals = RDFDatatypeTools.sortValues( iterations.keySet() );
		AnimationPaintable ap = new AnimationPaintable( gps.getLabelCache().get( pred ) );
		gps.getView().addPostRenderPaintable( ap );
		gps.getView().setGraphLayout( CircleLayout.class );
		Timer timer = new Timer( 3000, new ActionListener() {
			int listpos = -1;

			@Override
			public void actionPerformed( ActionEvent e ) {
				Map<SEMOSSEdge, Boolean> hidden = new HashMap<>();
				if ( listpos < 0 ) {
					gps.getView().hide( animatedEdges, false );
					ap.text = "All";
				}
				else {
					Value val = vals.get( listpos );

					for ( SEMOSSEdge edge : iterations.getNN( val ) ) {
						hidden.put( edge, false );
					}
					for ( SEMOSSEdge edge : animatedEdges ) {
						hidden.putIfAbsent( edge, true );
					}

					gps.getView().hide( hidden );
					ap.text = val.stringValue();
				}

				listpos++;
				if ( listpos >= iterations.size() ) {
					listpos = -1;
				}
			}
		} );

		timer.start();
	}

	private class AnimationPaintable implements Paintable {

		public String text = "All";
		public final String field;

		public AnimationPaintable( String field ) {
			this.field = field;
		}

		@Override
		public void paint( Graphics g ) {
			g.setColor( Color.BLACK );
			FontMetrics fm = g.getFontMetrics();
			g.drawString( field + ": " + text, fm.stringWidth( " " ), fm.getHeight() );
		}

		@Override
		public boolean useTransform() {
			return false;
		}
	}
}
