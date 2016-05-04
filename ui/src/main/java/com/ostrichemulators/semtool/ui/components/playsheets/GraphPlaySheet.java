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
package com.ostrichemulators.semtool.ui.components.playsheets;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.JSplitPane;

import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleGraph;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import com.ostrichemulators.semtool.om.GraphDataModel;
import com.ostrichemulators.semtool.om.SEMOSSEdge;
import com.ostrichemulators.semtool.om.SEMOSSVertex;
import com.ostrichemulators.semtool.rdf.engine.api.IEngine;
import com.ostrichemulators.semtool.ui.components.ControlPanel;
import com.ostrichemulators.semtool.graph.functions.GraphToTreeConverter;
import com.ostrichemulators.semtool.om.GraphModelListener;
import com.ostrichemulators.semtool.search.GraphSearchTextField;
import com.ostrichemulators.semtool.ui.components.playsheets.graphsupport.GraphLegendPanel;
import com.ostrichemulators.semtool.ui.components.playsheets.graphsupport.WeightDropDownButton;
import com.ostrichemulators.semtool.ui.components.api.GraphListener;
import com.ostrichemulators.semtool.ui.components.playsheets.graphsupport.GraphNodeListener;
import com.ostrichemulators.semtool.ui.components.playsheets.graphsupport.TreeConverterListener;
import com.ostrichemulators.semtool.util.GuiUtility;
import com.ostrichemulators.semtool.util.MultiMap;
import com.ostrichemulators.semtool.util.RetrievingLabelCache;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;

/**
 */
public class GraphPlaySheet extends ImageExportingPlaySheet implements PropertyChangeListener {

	private static final String PROPERTY_PREF = "GraphPropertyLocation";
	private static final long serialVersionUID = 4699492732234656487L;
	private static final Logger log = Logger.getLogger( GraphPlaySheet.class );

	private SemossGraphVisualization view;
	private JSplitPane graphSplitPane;
	private final VisualizationControlPanel control;

	protected GraphDataModel gdm;

	protected boolean traversable = true;
	protected boolean nodesHidable = true;

	protected int overlayLevel = 0;
	protected int maxOverlayLevel = 0;

	private final List<GraphListener> listenees = new ArrayList<>();
	private boolean inGraphOp = false;
	private final RetrievingLabelCache labelcache = new RetrievingLabelCache();

	private final Action undo = new AbstractAction( "Undo", GuiUtility.loadImageIcon( "undo.png" ) ) {
		@Override
		public void actionPerformed( ActionEvent e ) {
			undoView();
		}
	};

	private final Action redo = new AbstractAction( "Redo", GuiUtility.loadImageIcon( "redo.png" ) ) {
		@Override
		public void actionPerformed( ActionEvent e ) {
			redoView();
		}
	};

	private final Action reset = new AbstractAction( "Reset", GuiUtility.loadImageIcon( "refresh.png" ) ) {
		@Override
		public void actionPerformed( ActionEvent e ) {
			if ( xray.isSelected() ) {
				xray.doClick();
			}
			view.clearHighlighting();
		}
	};

	private final JToggleButton graphprops = new JToggleButton();
	private final JToggleButton xray = new JToggleButton();
	private final TreeConverterListener tree = new TreeConverterListener();
	private final WeightDropDownButton weightButton = new WeightDropDownButton();
	private final GraphSearchTextField searcher = new GraphSearchTextField();

	/**
	 * Constructor for GraphPlaySheetFrame.
	 */
	public GraphPlaySheet() {
		this( new GraphDataModel() );
	}

	public GraphPlaySheet( GraphDataModel model ) {
		this( model, new VisualizationControlPanel() );
	}

	public GraphPlaySheet( GraphDataModel model, VisualizationControlPanel vcp ) {
		log.debug( "new graphplaysheet" );
		gdm = model;

		undo.setEnabled( false );
		redo.setEnabled( false );

		control = vcp;
		control.setLabelCache( labelcache );

		graphSplitPane = new JSplitPane();
		graphSplitPane.setOneTouchExpandable( true );
		graphSplitPane.setOrientation( JSplitPane.HORIZONTAL_SPLIT );

		setLayout( new BorderLayout() );
		add( graphSplitPane, BorderLayout.CENTER );
		GraphLegendPanel legendPanel = new GraphLegendPanel( labelcache );
		add( legendPanel, BorderLayout.SOUTH );

		view = new SemossGraphVisualization( gdm );
		initVisualizer( view );
		view.addPickingSupport();
		control.setVisualization( view );

		control.setMinimumSize( new Dimension( 0, 0 ) );
		graphSplitPane.setLeftComponent( new GraphZoomScrollPane( view ) );
		graphSplitPane.setRightComponent( control );
		graphSplitPane.setDividerLocation( 1d );
		graphSplitPane.setResizeWeight( 1d );

		graphprops.setAction( new AbstractAction( "Graph Properties" ) {
			@Override
			public void actionPerformed( ActionEvent e ) {
				Preferences prefs = Preferences.userNodeForPackage( getClass() );

				if ( graphprops.isSelected() ) {
					int pct = prefs.getInt( PROPERTY_PREF, GraphPlaySheet.this.getWidth() - 250 );
					graphSplitPane.setDividerLocation( pct );
				}
				else {
					prefs.putInt( PROPERTY_PREF, graphSplitPane.getDividerLocation() );
					graphSplitPane.setDividerLocation( 1d );
				}
			}
		} );

		xray.setAction( new AbstractAction( "",
				GuiUtility.loadImageIcon( "x-ray.png" ) ) {
					@Override
					public void actionPerformed( ActionEvent e ) {
						view.setSkeletonMode( xray.isSelected() );
					}
				} );
		xray.setToolTipText( "X-Ray Highlighting");

		reset.putValue( Action.SHORT_DESCRIPTION, "Reset graph transformers" );

		addGraphListener( legendPanel );
		addGraphListener( control );

		searcher.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
		searcher.setColumns( 15 );

		gdm.addModelListener( new GraphModelListener() {

			@Override
			public void changed( DirectedGraph<SEMOSSVertex, SEMOSSEdge> graph, GraphDataModel gdm ) {
				searcher.index( graph );
			}
		} );
	}

	@Override
	public void populateToolBar( JToolBar toolBar, String tabTitle ) {
		super.populateToolBar( toolBar, tabTitle );

		weightButton.setPlaySheet( this );
		searcher.setPlaySheet( this );
		tree.setPlaySheet( this );

		toolBar.add( graphprops );
		toolBar.add( xray );
		toolBar.add( reset );
		toolBar.add( weightButton );
		toolBar.add( undo );
		toolBar.add( redo );
		toolBar.addSeparator();
		toolBar.add( tree );
		toolBar.add( searcher );
	}

	public Forest<SEMOSSVertex, SEMOSSEdge> asForest() {
		DirectedGraph<SEMOSSVertex, SEMOSSEdge> graph = gdm.getGraph();
		Forest<SEMOSSVertex, SEMOSSEdge> forest = ( graph instanceof Forest
				? Forest.class.cast( graph )
				: new GraphToTreeConverter().convert( graph,
						view.getPickedVertexState().getPicked() ) );
		GraphToTreeConverter.printForest( forest );
		return forest;
	}

	/**
	 * Gets the visible graph as a SimpleGraph. This function differs from 
	 * {@link GraphDataModel#asSimpleGraph() } because only visible nodes are
	 * included
	 *
	 * @return
	 */
	public SimpleGraph<SEMOSSVertex, SEMOSSEdge> asSimpleGraph() {
		Graph<SEMOSSVertex, SEMOSSEdge> visible = getVisibleGraph();

		SimpleGraph<SEMOSSVertex, SEMOSSEdge> graph
				= new SimpleGraph<>( SEMOSSEdge.class );

		for ( SEMOSSVertex v : visible.getVertices() ) {
			graph.addVertex( v );
		}

		for ( SEMOSSEdge e : visible.getEdges() ) {
			SEMOSSVertex s = visible.getSource( e );
			SEMOSSVertex d = visible.getDest( e );
			graph.addEdge( s, d, e );
		}

		return graph;
	}

	/**
	 * This function differs from {@link GraphDataModel#getGraph() } because only
	 * visible nodes are included
	 *
	 * @return
	 */
	public DirectedGraph<SEMOSSVertex, SEMOSSEdge> getVisibleGraph() {
		return view.getGraph();
	}

	@Override
	protected BufferedImage getExportImage() {

		Dimension d = view.getGraphLayout().getSize();
		d.setSize( d.getWidth() * 1.2, d.getHeight() * 1.2 );

		VisualizationImageServer<SEMOSSVertex, SEMOSSEdge> vis
				= new VisualizationImageServer<>( view.getGraphLayout(), d );

		vis.setBackground( view.getBackground() );
		vis.setRenderContext( view.getRenderContext() );

		try {
			// the visualization server seems to take a little time to render
			// correctly. Wait a little before processing the image
			// (this is really just voodoo...I don't know if/why it works)
			Thread.sleep( 500 );
		}
		catch ( Exception e ) {
			log.error( e, e );
		}

		BufferedImage image = (BufferedImage) vis.getImage(
				new Point2D.Double( view.getGraphLayout().getSize().getWidth(),
						view.getGraphLayout().getSize().getHeight() ),
				new Dimension( view.getGraphLayout().getSize() ) );
		return image;
	}

	public GraphDataModel getGraphData() {
		return gdm;
	}

	public void setGraphData( GraphDataModel gdm ) {
		this.gdm = gdm;
	}

	/**
	 * Method undoView. Get the latest view and undo it.
	 */
	public void undoView() {
		if ( overlayLevel > 1 ) {
			overlayLevel--;
			setUndoRedoBtn();
			view.refresh();
			fireGraphUpdated();
		}
	}

	/**
	 * Method redoView.
	 */
	public void redoView() {
		if ( overlayLevel < maxOverlayLevel ) {
			overlayLevel++;
			setUndoRedoBtn();
			view.refresh();
			fireGraphUpdated();
		}
	}

	/**
	 * Regenerates all the data needed to display the graph
	 */
	public void updateGraph() {
		setUndoRedoBtn();
		fireGraphUpdated();
	}

	/**
	 * Method initVisualizer.
	 */
	private void initVisualizer( SemossGraphVisualization viewer ) {
		GraphNodeListener gl = new GraphNodeListener( this );
		gl.setMode( ModalGraphMouse.Mode.PICKING );
		view.setGraphMouse( gl );
		view.setLabelCache( labelcache );

		view.addPropertyChangeListener( SemossGraphVisualization.VISIBILITY_CHANGED,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						fireGraphUpdated();
					}
				} );
		view.addPropertyChangeListener( SemossGraphVisualization.LAYOUT_CHANGED,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						fireLayoutUpdated( gdm.getGraph(), "",
								(Layout<SEMOSSVertex, SEMOSSEdge>) evt.getNewValue() );
					}
				} );
	}

	/**
	 * Method setUndoRedoBtn.
	 */
	private void setUndoRedoBtn() {
		undo.setEnabled( overlayLevel > 1 );
		redo.setEnabled( maxOverlayLevel > overlayLevel );
		view.setOverlayLevel( overlayLevel );
	}

	public MultiMap<URI, SEMOSSVertex> getVerticesByType() {
		MultiMap<URI, SEMOSSVertex> typeToInstances = new MultiMap<>();
		for ( SEMOSSVertex v : getVisibleGraph().getVertices() ) {
			typeToInstances.add( v.getType(), v );
		}
		return typeToInstances;
	}

	public MultiMap<URI, SEMOSSEdge> getEdgesByType() {
		MultiMap<URI, SEMOSSEdge> typeToInstances = new MultiMap<>();
		for ( SEMOSSEdge v : getVisibleGraph().getEdges() ) {
			typeToInstances.add( v.getType(), v );
		}
		return typeToInstances;
	}

	public void fireLayoutUpdated( DirectedGraph<SEMOSSVertex, SEMOSSEdge> graph,
			String oldName, Layout<SEMOSSVertex, SEMOSSEdge> layout ) {
		for ( GraphListener gl : listenees ) {
			try {
				gl.layoutChanged( graph, oldName, layout, this );
			}
			catch ( Exception ex ) {
				log.error( "Error updating layout for GraphListener " + gl + ": " + ex, ex );
			}
		}
	}

	public void fireGraphUpdated() {
		DirectedGraph<SEMOSSVertex, SEMOSSEdge> g = getGraphData().getGraph();
		for ( GraphListener gl : listenees ) {
			try {
				gl.graphUpdated( g, this );
			}
			catch ( Exception ex ) {
				log.error( "Error updating graph for GraphListener " + gl + ": " + ex, ex );
			}
		}
	}

	public final void addGraphListener( GraphListener gl ) {
		listenees.add( gl );
	}

	public final void removeGraphListener( GraphListener gl ) {
		listenees.remove( gl );
	}

	public SemossGraphVisualization getView() {
		return view;
	}

	@Override
	public void create( Model m, IEngine engine ) {
		add( m, null, engine );
	}

	/**
	 * Creates graph nodes from the given data. If the {@code Value[]}s have
	 * length 1, the values are expected to be nodes ({@link Resource}s. If they
	 * have length 3, then they are repackaged as Statements, and forwarded on to
	 * {@link #create(org.openrdf.model.Model, com.ostrichemulators.semtool.rdf.engine.api.IEngine) }.
	 * Anything else will throw an exception
	 *
	 * @param data the data to add are expected to be
	 * @param headers ignored
	 * @param engine
	 * @throws IllegalArgumentException
	 *
	 */
	@Override
	public void create( List<Value[]> data, List<String> headers, IEngine engine ) {
		labelcache.setEngine( engine );
		List<URI> nodes = new ArrayList<>();
		Model model = new LinkedHashModel();
		for ( Value[] row : data ) {
			URI s = URI.class.cast( row[0] );
			if ( 1 == row.length ) {
				nodes.add( s );
			}
			else if ( 3 == row.length ) {
				URI p = URI.class.cast( row[1] );
				Value o = row[2];

				model.add( s, p, o );
			}
			else {
				throw new IllegalArgumentException( "Values cannot be converted for graph usage" );
			}
		}

		add( model, nodes, engine );
	}

	@Override
	public void overlay( Model m, IEngine engine ) {
		add( m, null, engine );
	}

	/**
	 * Redirects to {@link #create(java.util.List, java.util.List,
	 * com.ostrichemulators.semtool.rdf.engine.api.IEngine) }
	 *
	 * @param data
	 * @param headers
	 * @param eng
	 */
	@Override
	public void overlay( List<Value[]> data, List<String> headers, IEngine eng ) {
		create( data, headers, eng );
	}

	public void add( Model m, List<URI> nodes, IEngine engine ) {
		labelcache.setEngine( engine );
		setHeaders( Arrays.asList( "Subject", "Predicate", "Object" ) );
		if ( null == nodes ) {
			nodes = new ArrayList<>();
		}
		if ( null == m ) {
			m = new LinkedHashModel();
		}

		if ( m.isEmpty() && nodes.isEmpty() ) {
			return; // nothing to add to the graph
		}

		inGraphOp = true;
		if ( overlayLevel < maxOverlayLevel ) {
			Set<SEMOSSVertex> removedVs = new HashSet<>();
			Set<SEMOSSEdge> removedEs = new HashSet<>();
			gdm.removeElementsSinceLevel( overlayLevel, removedVs, removedEs );
			for ( SEMOSSVertex v : removedVs ) {
				v.removePropertyChangeListener( this );
			}
			for ( SEMOSSEdge v : removedEs ) {
				v.removePropertyChangeListener( this );
			}
		}

		overlayLevel++;
		view.setOverlayLevel( overlayLevel );

		Set<SEMOSSVertex> oldverts = new HashSet<>( gdm.getGraph().getVertices() );
		Set<SEMOSSEdge> oldedges = new HashSet<>( gdm.getGraph().getEdges() );
		if ( !m.isEmpty() ) {
			gdm.addGraphLevel( m, engine, overlayLevel );
		}
		if ( !nodes.isEmpty() ) {
			gdm.addGraphLevel( nodes, engine, overlayLevel );
		}

		Set<SEMOSSVertex> newverts = new HashSet<>( gdm.getGraph().getVertices() );
		Set<SEMOSSEdge> newedges = new HashSet<>( gdm.getGraph().getEdges() );
		newverts.removeAll( oldverts );
		newedges.removeAll( oldedges );

		for ( SEMOSSVertex v : newverts ) {
			v.addPropertyChangeListener( this );
		}
		for ( SEMOSSEdge v : newedges ) {
			v.addPropertyChangeListener( this );
		}

		if ( overlayLevel > maxOverlayLevel ) {
			maxOverlayLevel = overlayLevel;
		}
		inGraphOp = false;

		updateGraph();
	}

	@Override
	public boolean canAcceptDataWithHeaders( List<String> newheaders ) {
		// we can accept either a model's headers, or a single header 
		return ( Arrays.asList( "Subject", "Predicate", "Object" ).equals( newheaders )
				|| ( 1 == newheaders.size() ) );
	}

	@Override
	public void incrementFont( float incr ) {
		super.incrementFont( incr );
		view.incrementFont( incr );
	}

	public boolean isTraversable() {
		return traversable;
	}

	public boolean areNodesHidable() {
		return nodesHidable;
	}

	public ControlPanel getSearchPanel() {
		// return controlPanel;
		return null;
	}

	/**
	 * Allow subclasses to substitute a different vertex for the given one
	 *
	 * @param v
	 * @return
	 */
	public SEMOSSVertex getRealVertex( SEMOSSVertex v ) {
		return v;
	}

	public SEMOSSEdge getRealEdge( SEMOSSEdge v ) {
		return v;
	}

	protected boolean isloading() {
		return inGraphOp;
	}

	@Override
	public void propertyChange( PropertyChangeEvent evt ) {
		if ( !isloading() ) {
			view.refresh();
			fireGraphUpdated();
		}
	}
}