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

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import gov.va.semoss.om.SEMOSSEdge;
import gov.va.semoss.om.SEMOSSVertex;
import gov.va.semoss.search.SearchController;
import gov.va.semoss.ui.components.api.GraphListener;
import gov.va.semoss.ui.components.playsheets.GraphPlaySheet;
import gov.va.semoss.ui.main.listener.impl.GraphTransformerResetListener;
import gov.va.semoss.ui.main.listener.impl.GraphVertexSizeListener;
import gov.va.semoss.ui.main.listener.impl.RedoListener;
import gov.va.semoss.ui.main.listener.impl.RingsButtonListener;
import gov.va.semoss.ui.main.listener.impl.TreeConverterListener;
import gov.va.semoss.ui.main.listener.impl.UndoListener;
import gov.va.semoss.util.Constants;
import gov.va.semoss.util.Utility;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 * Icons used in this search panel contributed from gentleface.com.
 *
 * @author karverma
 * @version $Revision: 1.0 $
 */
public class ControlPanel extends JPanel implements GraphListener {

	private static final long serialVersionUID = 3128479498547975776L;

	private final JButton resetBtn;
	private final JButton decreaseVertSizeButton;
	private final JButton increaseVertSizeButton;
	private final JButton undoButton;
	private final JButton redoButton;
	private final JButton treeButton;
	private final JToggleButton highlightButton;
	private final JToggleButton ringsButton;
	private final WeightDropDownButton weightButton;
	private final JTextField searchText = new JTextField();

	private final GraphTransformerResetListener resetTransListener
			= new GraphTransformerResetListener();
	private final GraphVertexSizeListener vertSizeListener = new GraphVertexSizeListener();
	private final TreeConverterListener treeListener = new TreeConverterListener();
	private final RingsButtonListener ringsListener = new RingsButtonListener();
	private final SearchController searchController = new SearchController();
	private final RedoListener redoListener = new RedoListener();
	private final UndoListener undoListener = new UndoListener();
	
	/**
	 * Create the panel.
	 *
	 * @param search Boolean
	 */
	public ControlPanel() {
		searchText.setMaximumSize( new Dimension( 300, 20 ) );
		searchText.setBorder( new BevelBorder( BevelBorder.LOWERED, null, null, null, null ) );
		searchText.setText( Constants.ENTER_TEXT );

		searchText.setColumns( 9 );
		searchController.setText( searchText );
		searchText.addFocusListener( searchController );
		searchText.addKeyListener( searchController );

		highlightButton = new JToggleButton( Utility.loadImageIcon( "search.png" ) );
		highlightButton.setToolTipText( "<html><b>Search</b><br>Depress to see your results on the graph,<br>keep it depressed to see results as you type (slow)</html>" );
		highlightButton.addActionListener( searchController );

		resetBtn = new JButton( Utility.loadImageIcon( "refresh.png" ) );
		resetBtn.addActionListener( resetTransListener );
		resetBtn.setToolTipText( "<html><b>Reset (F5)</b><br>Reset Graph Transformers</html>" );
		addKeyListener( resetBtn, resetTransListener,
				"F5", KeyStroke.getKeyStroke( "F5" ) );

		undoButton = new JButton( Utility.loadImageIcon( "undo.png" ) );
		undoButton.setToolTipText( "<html><b>Undo (CRTL+Z)</b><br>Undo the last graph action</html>" );
		undoButton.addActionListener( undoListener );
		addKeyListener( undoButton, undoListener,
				"control Z", KeyStroke.getKeyStroke( KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK ) );
		undoButton.setEnabled( false );

		redoButton = new JButton( Utility.loadImageIcon( "redo.png" ) );
		redoButton.setToolTipText( "<html><b>Redo (CRTL+Y)</b><br>Redo the previous action</html>" );
		redoButton.addActionListener( redoListener );
		addKeyListener( redoButton, redoListener,
				"control Y", KeyStroke.getKeyStroke( KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK ) );
		redoButton.setEnabled( false );

		treeButton = new JButton( treeListener );
		treeButton.setText( "" );
		treeButton.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				if ( ringsButton.isSelected() ) {
					ringsButton.doClick();
				}
			}
		} );

		ringsButton = new JToggleButton( Utility.loadImageIcon( "ring.png" ) );
		ringsButton.setToolTipText( "<html><b>Show Radial Rings</b><br>Only available with Balloon and Radial Tree layouts</html>" );
		ringsButton.addActionListener( ringsListener );

		weightButton = new WeightDropDownButton( Utility.loadImageIcon( "width.png" ) );
		weightButton.setToolTipText( "<html><b>Edge Weight</b><br>Convert edge thickness corresponding to properties that exist on the edges</html>" );
		weightButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				weightButton.showPopup();
			}
		} );

		decreaseVertSizeButton = new JButton( Utility.loadImageIcon( "decreaseNodeSize.png" ) );
		decreaseVertSizeButton.setName( "Decrease" );
		decreaseVertSizeButton.addActionListener( vertSizeListener );
		decreaseVertSizeButton.setToolTipText( "<html><b>Decrease Node Size (CTRL+[)</b><br>Decrease the node size of selected nodes or all nodes</html>" );
		addKeyListener( decreaseVertSizeButton, vertSizeListener,
				"control [", KeyStroke.getKeyStroke( KeyEvent.VK_OPEN_BRACKET, InputEvent.CTRL_DOWN_MASK ) );

		increaseVertSizeButton = new JButton( Utility.loadImageIcon( "increaseNodeSize.png" ) );
		increaseVertSizeButton.setName( "Increase" );
		increaseVertSizeButton.addActionListener( vertSizeListener );
		increaseVertSizeButton.setToolTipText( "<html><b>Increase Node Size (CTRL+])</b><br>Increase the node size of selected nodes or all nodes</html>" );
		addKeyListener( increaseVertSizeButton, vertSizeListener,
				"control ]", KeyStroke.getKeyStroke( KeyEvent.VK_CLOSE_BRACKET, InputEvent.CTRL_DOWN_MASK ) );

		buildLayout();
		setForTree( false );
	}

	@Override
	public void graphUpdated( DirectedGraph<SEMOSSVertex, SEMOSSEdge> graph, GraphPlaySheet gps ) {
		searchController.indexGraph( graph, gps.getEngine() );
	}

	@Override
	public void layoutChanged( DirectedGraph<SEMOSSVertex, SEMOSSEdge> graph,
			String oldlayout, Layout<SEMOSSVertex, SEMOSSEdge> newlayout, GraphPlaySheet gps ) {
		if ( newlayout instanceof BalloonLayout || newlayout instanceof RadialTreeLayout ) {
			ringsListener.setGraph( Forest.class.cast( graph ) );
			ringsListener.setEnabled( true );
		}

		ringsListener.setLayout( newlayout );
	}

	public void setForTree( boolean b ) {
		ringsListener.setEnabled( b );
		ringsButton.setVisible( b );

		treeListener.setEnabled( !b );
		treeButton.setVisible( !b );
	}

	private void buildLayout() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{ 0, 0 };
		gridBagLayout.rowHeights = new int[]{ 0, 0 };
		gridBagLayout.columnWeights = new double[]{ 1.0, 0.0 };
		gridBagLayout.rowWeights = new double[]{ 0.0, 0.0 };
		setLayout( gridBagLayout );

		add( searchText, getGBC( GridBagConstraints.HORIZONTAL ) );
		add( highlightButton, getGBC() );
		add( getJSeparator(), getGBC() );

		add( resetBtn, getGBC() );
		add( undoButton, getGBC() );
		add( redoButton, getGBC() );
		add( getJSeparator(), getGBC() );

		add( treeButton, getGBC() );
		add( ringsButton, getGBC() );
		add( weightButton, getGBC() );
		add( getJSeparator(), getGBC() );

		add( decreaseVertSizeButton, getGBC() );
		add( increaseVertSizeButton, getGBC() );
	}

	private void addKeyListener( JButton button, Action action, String keyStrokeString,
			KeyStroke keyStroke ) {
		button.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( keyStroke, keyStrokeString );
		button.getActionMap().put( keyStrokeString, action );
	}

	private GridBagConstraints getGBC() {
		return getGBC( GridBagConstraints.VERTICAL );
	}

	private int currentX = 0;

	private GridBagConstraints getGBC( int orientation ) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = orientation;
		gbc.insets = new Insets( 0, 0, 0, 5 );
		gbc.gridx = currentX++;
		gbc.gridy = 0;

		return gbc;
	}

	private JSeparator getJSeparator() {
		JSeparator separator = new JSeparator();
		separator.setOrientation( SwingConstants.VERTICAL );
		return separator;
	}

	/**
	 * Sets the playsheet for all necessary listeners.
	 *
	 * @param _gps GraphPlaySheet
	 */
	public void setPlaySheet( GraphPlaySheet gps ) {
		treeListener.setPlaySheet( gps );
		resetTransListener.setPlaySheet( gps );
		redoListener.setPlaySheet( gps );
		undoListener.setPlaySheet( gps );
		searchController.setGPS( gps );
		weightButton.setPlaySheet( gps );

		VisualizationViewer<SEMOSSVertex, SEMOSSEdge> viewer = gps.getView();
		vertSizeListener.setViewer( viewer );
		ringsListener.setViewer( viewer );
	}

	public void setUndoButtonEnabled( boolean enabled ) {
		undoButton.setEnabled( enabled );
	}

	public void setRedoButtonEnabled( boolean enabled ) {
		redoButton.setEnabled( enabled );
	}

	public boolean isHighlightButtonSelected() {
		return highlightButton.isSelected();
	}

	public void clickTreeButton( String layout ) {
		treeListener.putValue( TreeConverterListener.LAYOUT_NAME, layout );
		treeButton.doClick();
		treeListener.putValue( TreeConverterListener.LAYOUT_NAME, Constants.TREE_LAYOUT );
	}
}
