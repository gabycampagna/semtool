package com.ostrichemulators.semtool.ui.components;

import com.ostrichemulators.semtool.ui.components.renderers.PlaySheetEnumRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameListener;

import org.apache.log4j.Logger;

//import aurelienribon.ui.css.Style;
import com.ostrichemulators.semtool.om.Insight;
import com.ostrichemulators.semtool.om.InsightOutputType;
import com.ostrichemulators.semtool.rdf.engine.api.IEngine;
import com.ostrichemulators.semtool.rdf.engine.api.QueryExecutor;
import com.ostrichemulators.semtool.rdf.query.util.AbstractBindable;
import com.ostrichemulators.semtool.rdf.query.util.impl.ListOfValueArraysQueryAdapter;
import com.ostrichemulators.semtool.ui.components.api.IPlaySheet;
import com.ostrichemulators.semtool.util.DIHelper;
import com.ostrichemulators.semtool.util.GuiUtility;
import com.ostrichemulators.semtool.ui.components.tabbedqueries.TabbedQueries;
import com.ostrichemulators.semtool.ui.components.playsheets.PlaySheetCentralComponent;

import com.ostrichemulators.semtool.ui.components.tabbedqueries.SparqlTextArea;
import com.ostrichemulators.semtool.util.MultiMap;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JDesktopPane;
import javax.swing.event.InternalFrameEvent;

import org.apache.commons.io.FileUtils;
import org.openrdf.model.Value;

/**
 * Class to move the "Custom Sparql Query" window and related controls out of
 * "PlayPane.java".
 *
 * @author Thomas
 *
 */
public class CustomSparqlPanel extends JPanel {

	private static final Logger logger = Logger.getLogger( CustomSparqlPanel.class );

	private JComboBox<InsightOutputType> playSheetComboBox;
	private final JButton btnGetQuestionSparql;
	private final JButton btnShowHint;
	private final JButton btnSubmitSparqlQuery;
	private final JCheckBox appendSparqlQueryChkBox;
	private JCheckBox mainTabOverlayChkBox;
	private JComboBox<Insight> insights;
	private TabbedQueries sparqlArea;
	private final SubmitAction submitter = new SubmitAction();

	public CustomSparqlPanel() {
		setLayout( new GridBagLayout() );

//		Style.registerTargetClassName( btnGetQuestionSparql, ".standardButton" );
//		Style.registerTargetClassName( btnShowHint, ".standardButton" );
//		Style.registerTargetClassName( btnSubmitSparqlQuery, ".standardButton" );
		JLabel lblSectionCCustomize = new JLabel( "Query Panel" );
		lblSectionCCustomize.setForeground( Color.DARK_GRAY );
		lblSectionCCustomize.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
		GridBagConstraints gbc_lblSectionCCustomize = new GridBagConstraints();
		gbc_lblSectionCCustomize.anchor = GridBagConstraints.WEST;
		gbc_lblSectionCCustomize.gridwidth = 5;
		gbc_lblSectionCCustomize.insets = new Insets( 5, 8, 5, 5 );
		gbc_lblSectionCCustomize.gridx = 0;
		gbc_lblSectionCCustomize.gridy = 0;
		add( lblSectionCCustomize, gbc_lblSectionCCustomize );

		playSheetComboBox = new JComboBox<>();
		playSheetComboBox.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent ae ) {
				InsightOutputType type
						= playSheetComboBox.getItemAt( playSheetComboBox.getSelectedIndex() );
				//Store the currently selected playsheet with the currently
				//selected query tab:
				sparqlArea.setTagOfSelectedTab( null == type 
						? "Update Query" : type.toString() );
			}
		} );
		final PlaySheetEnumRenderer pr
				= new PlaySheetEnumRenderer( DIHelper.getInstance().getOutputTypeRegistry() );
		playSheetComboBox.setRenderer( pr );
		playSheetComboBox.setToolTipText( "Display response formats for custom query" );
		playSheetComboBox.setFont( new Font( "Tahoma", Font.PLAIN, 11 ) );
		playSheetComboBox.setBackground( new Color( 119, 136, 153 ) );
		playSheetComboBox.setMinimumSize( new Dimension( 125, 25 ) );
		playSheetComboBox.setPreferredSize( new Dimension( 140, 25 ) );
		// entries in combobox specified in question listener
		GridBagConstraints gbc_playSheetComboBox = new GridBagConstraints();
		gbc_playSheetComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_playSheetComboBox.gridwidth = 5;
		gbc_playSheetComboBox.anchor = GridBagConstraints.EAST;
		gbc_playSheetComboBox.insets = new Insets( 5, 5, 5, 5 );
		gbc_playSheetComboBox.gridx = 0;
		gbc_playSheetComboBox.gridy = 2;
		add( playSheetComboBox, gbc_playSheetComboBox );

		btnShowHint = new JButton();
		btnShowHint.setToolTipText( "Display Hint for PlaySheet" );
		Image img3 = GuiUtility.loadImage( "questionMark.png" );
		if ( null != img3 ) {
			Image newimg = img3.getScaledInstance( 15, 15, java.awt.Image.SCALE_SMOOTH );
			btnShowHint.setIcon( new ImageIcon( newimg ) );
		}
		GridBagConstraints gbc_btnShowHint = new GridBagConstraints();
		gbc_btnShowHint.weightx = 1.0;
		gbc_btnShowHint.anchor = GridBagConstraints.EAST;
		btnShowHint.setFont( new Font( "Tahoma", Font.BOLD, 11 ) );
		gbc_btnShowHint.fill = GridBagConstraints.NONE;
		gbc_btnShowHint.insets = new Insets( 5, 5, 5, 5 );
		gbc_btnShowHint.gridx = 3;
		gbc_btnShowHint.gridy = 3;
		add( btnShowHint, gbc_btnShowHint );

		btnGetQuestionSparql = new JButton();
		btnGetQuestionSparql.setToolTipText( "Display SPARQL Query for Current Question" );
		Image img2 = GuiUtility.loadImage( "download.png" );
		if ( null != img2 ) {
			Image newimg = img2.getScaledInstance( 15, 15, java.awt.Image.SCALE_SMOOTH );
			btnGetQuestionSparql.setIcon( new ImageIcon( newimg ) );
		}
		GridBagConstraints gbc_btnGetQuestionSparql = new GridBagConstraints();
		gbc_btnGetQuestionSparql.anchor = GridBagConstraints.EAST;
		btnGetQuestionSparql.setFont( new Font( "Tahoma", Font.BOLD, 11 ) );
		gbc_btnGetQuestionSparql.fill = GridBagConstraints.NONE;
		gbc_btnGetQuestionSparql.insets = new Insets( 5, 5, 5, 5 );
		gbc_btnGetQuestionSparql.gridx = 4;
		gbc_btnGetQuestionSparql.gridy = 3;
		add( btnGetQuestionSparql, gbc_btnGetQuestionSparql );

		btnSubmitSparqlQuery = new JButton( submitter );
		btnSubmitSparqlQuery.setEnabled( false );
		btnSubmitSparqlQuery.setFont( new Font( "Tahoma", Font.BOLD, 12 ) );
		btnSubmitSparqlQuery.setText( "Submit Query" );
		btnSubmitSparqlQuery.setToolTipText( "Execute SPARQL query for selected question and display results in Display Pane" );
		GridBagConstraints gbc_btnSubmitSparqlQuery = new GridBagConstraints();
		gbc_btnSubmitSparqlQuery.fill = GridBagConstraints.NONE;
		gbc_btnSubmitSparqlQuery.insets = new Insets( 5, 5, 5, 5 );
		gbc_btnSubmitSparqlQuery.gridwidth = 5;
		gbc_btnSubmitSparqlQuery.gridx = 0;
		gbc_btnSubmitSparqlQuery.gridy = 4;
		gbc_btnSubmitSparqlQuery.anchor = GridBagConstraints.EAST;
		add( btnSubmitSparqlQuery, gbc_btnSubmitSparqlQuery );

		appendSparqlQueryChkBox = new JCheckBox( "Overlay" );
		appendSparqlQueryChkBox.setToolTipText( "Add results to currently selected grid or graph" );
		appendSparqlQueryChkBox.setHorizontalTextPosition( SwingConstants.LEFT );
		appendSparqlQueryChkBox.setEnabled( false );
		appendSparqlQueryChkBox.setFont( new Font( "Tahoma", Font.BOLD, 11 ) );
		GridBagConstraints gbc_appendSparqlQueryChkBox = new GridBagConstraints();
		gbc_appendSparqlQueryChkBox.fill = GridBagConstraints.NONE;
		gbc_appendSparqlQueryChkBox.insets = new Insets( 5, 50, 5, 5 );
		gbc_appendSparqlQueryChkBox.gridwidth = 5;
		gbc_appendSparqlQueryChkBox.gridx = 0;
		gbc_appendSparqlQueryChkBox.gridy = 5;
		gbc_appendSparqlQueryChkBox.anchor = GridBagConstraints.EAST;
		add( appendSparqlQueryChkBox, gbc_appendSparqlQueryChkBox );

		sparqlArea = new TabbedQueries();
		sparqlArea.setTagOfSelectedTab( InsightOutputType.GRID.toString() );

		/**
		 * Handles the assignment of keyboard shortcuts when changing the tab in
		 * TabbedQueries .
		 */
		sparqlArea.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged( ChangeEvent arg0 ) {
				//Pre-select the "playSheetComboBox" with the Tag
				//of the selected query tab:
				String strPlaySheet = sparqlArea.getTagOfSelectedTab();
				if ( strPlaySheet != null ) {
					
					playSheetComboBox.setSelectedItem( "Update Query".equals( strPlaySheet ) 
							? null : InsightOutputType.valueOf( strPlaySheet ) );
				}
				
				//When the tab changes, check the contents of the displayed tab.
				//Only enable the "Submit Query" button if characters are shown:
				btnSubmitSparqlQuery.setEnabled( !sparqlArea.getTextOfSelectedTab().isEmpty() );

				//Submit Sparql query via the keystrokes <Ctrl><Enter>, from any selected component in the tool,
				//and show status in the status-bar:
				try {
					if ( sparqlArea.getEditorOfSelectedTab() != null ) {
						sparqlArea.getEditorOfSelectedTab().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK ), "handleQueryKeys" );
						sparqlArea.getEditorOfSelectedTab().getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK ), "handleQueryKeys" );
						sparqlArea.getEditorOfSelectedTab().getActionMap().put( "handleQueryKeys", submitter );
						//Only enables the "Submit Query" button if text (including spaces) 
						//has been entered into the currently selected editor:
						sparqlArea.getEditorOfSelectedTab().getDocument().addDocumentListener( new DocumentListener() {
							@Override
							public void changedUpdate( DocumentEvent cu ) {
								if ( sparqlArea.getTextOfSelectedTab().length() > 0 ) {
									btnSubmitSparqlQuery.setEnabled( true );
								}
								else {
									btnSubmitSparqlQuery.setEnabled( false );
								}
							}

							@Override
							public void insertUpdate( DocumentEvent iu ) {
							}

							@Override
							public void removeUpdate( DocumentEvent ru ) {
							}
						} );
					}
				}
				catch ( NullPointerException e ) {
					logger.error( e, e );
				}
			}
		} );
		//Initialize the keyboard listener:
		sparqlArea.getChangeListeners()[0].stateChanged( new ChangeEvent( sparqlArea ) );

		GridBagConstraints gbc_sparqlArea = new GridBagConstraints();
		gbc_sparqlArea.weightx = 50.0;
		gbc_sparqlArea.weighty = 5.0;
		gbc_sparqlArea.insets = new Insets( 5, 0, 0, 5 );
		gbc_sparqlArea.fill = GridBagConstraints.BOTH;
		gbc_sparqlArea.gridwidth = 10;
		gbc_sparqlArea.gridheight = 15;
		gbc_sparqlArea.anchor = GridBagConstraints.PAGE_START;
		gbc_sparqlArea.gridx = 5;
		gbc_sparqlArea.gridy = 0;
		add( sparqlArea, gbc_sparqlArea );
		sparqlArea.setPreferredSize( new Dimension( 200, 80 ) );

		btnGetQuestionSparql.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				Insight selected = insights.getItemAt( insights.getSelectedIndex() );

				SelectDatabasePanel sdp
						= DIHelper.getInstance().getPlayPane().getDatabasePanel();

				Map<String, Value> binds = sdp.getBindings();
				String sparql = AbstractBindable.getBoundSparql( selected.getSparql(),
						binds );
				sparqlArea.setTextOfSelectedTab( sparql );

				//Pre-select the Playsheet of the Insight copied down:
				playSheetComboBox.setSelectedItem( selected.getOutput() );
			}
		} );

		btnShowHint.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				InsightOutputType pse
						= playSheetComboBox.getItemAt( playSheetComboBox.getSelectedIndex() );
				OutputTypeRegistry registry = DIHelper.getInstance().getOutputTypeRegistry();
				String hint = ( null == pse
						? "UpdateQuery Hint: Try a SPARQL query that INSERTs data, DELETEs data, or does both."
						: registry.getSheetHint( pse ) );

				//Set the sparql area with the hint:
				if ( sparqlArea.getTabCount() == 1 ) {
					sparqlArea.setSelectedIndex( 0 );
				}
				sparqlArea.setTextOfSelectedTab( hint );
			}
		} );
	}

	public void setOutputTypeRegistry( OutputTypeRegistry reg ) {
		List<InsightOutputType> types = new ArrayList<>( reg.getRegisterKeys() );
		
		
		types.add( null );
		playSheetComboBox.setModel( new DefaultComboBoxModel( types.toArray() ) );
	}

	public void setOverlayCheckBox( JCheckBox box ) {
		mainTabOverlayChkBox = box;
	}

	public void setInsightsComboBox( JComboBox<Insight> ins ) {
		assert ( insights == null );
		insights = ins;

		insights.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				int idx = insights.getSelectedIndex();
				if ( idx >= 0 ) {
					Insight selected = insights.getItemAt( insights.getSelectedIndex() );
					
					//If a question has no Sparql associated with it (as pulled from an external source),
					//then disable the Copy-Down button, in the "Custom Sparql Query" window:
					InsightOutputType type = ( null == selected.getOutput() 
							? InsightOutputType.GRID : selected.getOutput() );
					btnGetQuestionSparql.setEnabled( type.needsSparql );
				}
				else {
					btnGetQuestionSparql.setEnabled( false );
				}
			}
		} );
	}

	public void enableAppend( boolean bb ) {
		appendSparqlQueryChkBox.setEnabled( bb );
	}

	public SparqlTextArea getOpenEditor() {
		return sparqlArea.getEditorOfSelectedTab();
	}

	public void loadFileToEmptyEditor( File file ) {
		try {
			String sparql = FileUtils.readFileToString( file );
			sparqlArea.loadToEmptyTab( sparql );
		}
		catch ( IOException ioe ) {
			logger.error( ioe, ioe );
			GuiUtility.showError( ioe.getLocalizedMessage() );
		}
	}

	public InternalFrameListener makeDesktopListener() {
		return new InternalFrameListener() {

			@Override
			public void internalFrameOpened( InternalFrameEvent e ) {
			}

			@Override
			public void internalFrameClosing( InternalFrameEvent e ) {
			}

			@Override
			public void internalFrameClosed( InternalFrameEvent e ) {
			}

			@Override
			public void internalFrameIconified( InternalFrameEvent e ) {
			}

			@Override
			public void internalFrameDeiconified( InternalFrameEvent e ) {
			}

			@Override
			public void internalFrameActivated( InternalFrameEvent e ) {
				Insight insight = insights.getItemAt( insights.getSelectedIndex() );
				if ( null == insight ) {
					return;
				}

				//Determine whether to enable/disable the "Overlay" CheckBox, based upon
				//how the renderer of the selected visualization compares with that of the 
				//currently selected question:
				JDesktopPane pane = DIHelper.getInstance().getDesktop();
				PlaySheetFrame psf = PlaySheetFrame.class.cast( pane.getSelectedFrame() );

				InsightOutputType output = insight.getOutput();

				// the frame will be activated before there's a playsheet attached to it
				// make sure we have a playsheet before continuing
				PlaySheetCentralComponent pscc = psf.getActivePlaySheet();
				OutputTypeRegistry registry = DIHelper.getInstance().getOutputTypeRegistry();
				Map<Class<? extends IPlaySheet>, InsightOutputType> psccMap
						= MultiMap.lossyflip( registry.getPlaySheetMap() );

				if ( null != pscc && output == psccMap.get( pscc.getClass() ) ) {
					mainTabOverlayChkBox.setEnabled( true );
				}
				else {
					mainTabOverlayChkBox.setEnabled( false );
				}
			}

			@Override
			public void internalFrameDeactivated( InternalFrameEvent e ) {
				mainTabOverlayChkBox.setEnabled( false );
			}
		};
	}

	private class SubmitAction extends ExecuteQueryProcessor {

		public SubmitAction() {
			super( "Submit Query" );
		}

		@Override
		protected void prepare( ActionEvent ae ) {
			InsightOutputType outputtype
					= playSheetComboBox.getItemAt( playSheetComboBox.getSelectedIndex() );
			sparqlArea.setTagOfSelectedTab( outputtype.toString() );
		}

		@Override
		protected String getTitle() {
			return sparqlArea.getTitleAt( sparqlArea.getSelectedIndex() );
		}

		@Override
		protected QueryExecutor<?> getQuery() {
			return new ListOfValueArraysQueryAdapter( sparqlArea.getTextOfSelectedTab() );
		}

		@Override
		protected InsightOutputType getOutputType() {
			InsightOutputType outputtype
					= playSheetComboBox.getItemAt( playSheetComboBox.getSelectedIndex() );
			return outputtype;
		}

		@Override
		protected IEngine getEngine() {
			return DIHelper.getInstance().getRdfEngine();
		}

		@Override
		protected boolean isAppending() {
			return appendSparqlQueryChkBox.isSelected();
		}
	}
}
