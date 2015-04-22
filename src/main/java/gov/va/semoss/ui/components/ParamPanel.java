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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.openrdf.model.URI;

import gov.va.semoss.om.Insight;
import gov.va.semoss.om.Parameter;
import gov.va.semoss.rdf.engine.api.IEngine;
import gov.va.semoss.ui.helpers.EntityFiller;
import gov.va.semoss.util.DIHelper;
import gov.va.semoss.util.Utility;

/**
 * This provides a panel to interact with various parameters.
 */
public class ParamPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -82808301245106832L;
	private static final Logger logger = Logger.getLogger( ParamPanel.class );


  Map<String, String> knownValues = new HashMap<>();
  List<ParamComboBox> dependentBoxes = new ArrayList<>();
  List<IEngine> selectedEngines;
  Insight insight = null;

  /**
   * Constructor for ParamPanel.
   */
  public ParamPanel() {
    setBackground( Color.WHITE );
  }

  /**
   * Paints the parameter panel.
   */
  public void paintParam() {
    //Remove all components (combo boxes) before repainting
    this.removeAll();
    
    //Set up layout values for panel
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{ 0, 0 };
    gridBagLayout.rowHeights = new int[]{ 0, 0, 0 };
    gridBagLayout.columnWeights = new double[]{ 0.0, 1.0 };
    gridBagLayout.rowWeights = new double[]{ 0.0, 0.0 };
    this.setLayout( gridBagLayout );

    List<ParamComboBox> fields = new ArrayList<>();
    List<GridBagConstraints> gbcs1 = new ArrayList<>();
    List<GridBagConstraints> gbcs2 = new ArrayList<>();
    List<JLabel> labels = new ArrayList<>();
    GridBagConstraints gbc_element;

    int elementInt = 0;
    List<String> setParams = new ArrayList<>();

    // Iterate over params retrieved from query
    
    for ( String parameterName: insight.getParametersKeySet() ) {
      JLabel label = new JLabel( parameterName );
      label.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
      label.setForeground( Color.DARK_GRAY );

      //Execute the logic for filling the information here
      final String parameterType = insight.getParameterType( parameterName ).stringValue();
      final ParamComboBox field = new ParamComboBox( Arrays.asList( ParamComboBox.FETCHING ) );
      // final LabeledUriPairRenderer renderer = new LabeledUriPairRenderer();
      // field.setRenderer( renderer );
      field.setFont( new Font( "Tahoma", Font.PLAIN, 11 ) );
      field.setParamName( parameterName );
      field.setEditable( false );
      field.setPreferredSize( new Dimension( 100, 25 ) );
      field.setMinimumSize( new Dimension( 100, 25 ) );
      //field.setMaximumSize(new Dimension(200, 32767));
      field.setBackground( new Color( 119, 136, 153 ) ); //Dropdown background color

      // get the list
      RepositoryList list = DIHelper.getInstance().getRepoList();
      // get the selected repository
      selectedEngines = list.getSelectedValuesList();

      String query = "";
      if( insight.getDefaultValueIsQuery() ) {
    	  query = insight.getParameterDefaultValue( parameterName ).stringValue();
      }
      
    

	  //see if the query needs to be filled
      //if it does, set it as a dependent box
      if ( checkIfFullQuery( query ) ) {
        for ( IEngine engine : selectedEngines ) {
          EntityFiller filler = new EntityFiller();
          if ( query != null && !query.isEmpty() ) {
            filler.setExternalQuery( query );
          }
          filler.fill( field, engine, parameterType );
          
          // renderer.addMapToCache( field.getLabelMap() );

          final Preferences prefs = Preferences.userNodeForPackage( getClass() );
          String lastoption = prefs.get( parameterType, "" );
          field.setSelectedItem( lastoption );
          field.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
              prefs.put( parameterType, field.getSelectedItem().toString() );
            }
          } );
        }
        setSelected( field );
        setParams.add( field.getParamName() );
        field.addActionListener( this );
      }
      else {
        setDependencies( field, query );
        field.setType( parameterType );
      }

      gbc_element = new GridBagConstraints();
      gbc_element.anchor = GridBagConstraints.WEST;
      gbc_element.insets = new Insets( 0, 5, 5, 5 );
      gbc_element.gridx = 0;
      gbc_element.gridy = elementInt;
      labels.add( label );
      gbcs1.add( gbc_element );

      gbc_element = new GridBagConstraints();
      gbc_element.anchor = GridBagConstraints.NORTH;
      gbc_element.fill = GridBagConstraints.HORIZONTAL;
      gbc_element.insets = new Insets( 0, 5, 5, 5 );
      gbc_element.gridx = 1;
      gbc_element.gridy = elementInt;

      fields.add( field );
      gbcs2.add( gbc_element );
      elementInt++;
    }

    int index = 0;
    int begAlph;
    while ( fields.size() > 1 ) {
      begAlph = 0;
      for ( int i = 1; i < fields.size(); i++ ) {
        if ( fields.get( begAlph ).getParamName().compareTo( fields.get( i ).getParamName() ) > 0 ) {
          begAlph = i;
        }
      }
      gbcs1.get( begAlph ).gridy = index;
      gbcs2.get( begAlph ).gridy = index;
      index++;
      this.add( labels.get( begAlph ), gbcs1.get( begAlph ) );
      this.add( fields.get( begAlph ), gbcs2.get( begAlph ) );
      fields.remove( begAlph );
      gbcs1.remove( begAlph );
      labels.remove( begAlph );
      gbcs2.remove( begAlph );
    }
    if ( !gbcs1.isEmpty() ) {
      gbcs1.get( 0 ).gridy = index;
      gbcs2.get( 0 ).gridy = index;
      this.add( labels.get( 0 ), gbcs1.get( 0 ) );
      this.add( fields.get( 0 ), gbcs2.get( 0 ) );
    }
    fillParams( setParams );
  }

  /**
   * Fills the list of parameters with newly changed parameters based on whether
   * the list is full from a previous query.
   *
   * @param changedParams List of changed parameters to be added.
   */
  private void fillParams( List<String> changedParams ) {
    List<String> newChangedParams = new ArrayList<>();
    for ( ParamComboBox field : dependentBoxes ) {
      //check if the box is dependent on the changed param
      Set<String> overlap = new HashSet<>( field.getDependencies() );
      overlap.retainAll( changedParams );
      if ( !overlap.isEmpty() ) {
        String query = field.getQuery();
        query = Utility.fillParam( query, knownValues );

        if ( checkIfFullQuery( query ) ) {
          String entityType = field.getType();
          for ( IEngine engine : selectedEngines ) {
            EntityFiller filler = new EntityFiller();
            if ( query != null && !query.isEmpty() ) {
              filler.setExternalQuery( query );
            }
            filler.fill( field, engine, entityType );
          }
          setSelected( field );
          newChangedParams.add( field.getParamName() );
          field.addActionListener( this );
        }
      }
    }
    if ( !newChangedParams.isEmpty() ) {
      fillParams( newChangedParams );
    }
  }

  /**
   * Invoked when an action occurs.
   *
   * @param arg0 ActionEvent
   */
  @Override
  public void actionPerformed( ActionEvent arg0 ) {
    ParamComboBox source = (ParamComboBox) arg0.getSource();
    setSelected( source );
    
    ArrayList<String> list = new ArrayList<String>();
    list.add( source.getParamName() );
    fillParams( list );
  }

  /**
   * Sets selected value into the parameter combo box.
   *
   * @param source
   */
  private void setSelected( ParamComboBox source ) {
    int selidx = source.getSelectedIndex();
    if( selidx >=0 ){    
      URI uri = source.getItemAt( selidx );
      knownValues.put( source.getParamName(), uri.stringValue() );
    }
  }

  
  public void setInsight(Insight insight) {
	  this.insight = insight;
  }

  /**
   * Sets a new question.
   *
   * @param newQuestion If true, clears the known values hashtable.
   */
  public void setNewQuestion( boolean newQuestion ) {
    if ( newQuestion ) {
      knownValues.clear();
    }
  }

  /**
   * Gets data from the queries and adds to the list of dependencies.
   *
   * @param field Parameters combo box where dependencies and queries are set.
   * @param query Query.
   */
  private void setDependencies( ParamComboBox field, String query ) {
    List<String> dependencies = new ArrayList<>();
    Pattern pattern = Pattern.compile( "[@]\\w+[@]" );
    Matcher matcher = pattern.matcher( query );
    while ( matcher.find() ) {
      String data = matcher.group();
      data = data.substring( 1, data.length() - 1 );
      logger.debug( "dependency: "+data );
      dependencies.add( data );
    }
    field.setDependency( dependencies );
    field.setQuery( query );
    this.dependentBoxes.add( field );
  }

  /**
   * Checks if the query is in the list.
   *
   * @param query Query to be checked.
   *
   * @return boolean	True if the pattern is not found in the matcher.s
   */
  private boolean checkIfFullQuery( String query ) {
    Pattern pattern = Pattern.compile( "[@]\\w+[@]" );
    Matcher matcher = pattern.matcher( query );
    return !matcher.find();
  }
}