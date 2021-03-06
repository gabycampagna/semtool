/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.ui.components.insight.manager;

import com.ostrichemulators.semtool.om.Insight;
import com.ostrichemulators.semtool.om.Parameter;
import com.ostrichemulators.semtool.rdf.engine.util.StructureManager;
import com.ostrichemulators.semtool.rdf.engine.util.StructureManagerFactory;
import com.ostrichemulators.semtool.ui.components.UriComboBox;
import com.ostrichemulators.semtool.ui.components.renderers.LabeledPairRenderer;
import com.ostrichemulators.semtool.util.Constants;
import com.ostrichemulators.semtool.util.Utility;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.openrdf.model.URI;

/**
 *
 * @author ryan
 */
public class ParameterPanel extends DataPanel<Parameter> {

	private Insight insight;
	private final JTree tree;
	private final DefaultTreeModel model;

	public ParameterPanel( JTree tree, DefaultTreeModel model ) {
		this.tree = tree;
		this.model = model;
		initComponents();

		listenTo( parameterName );
		listenTo( parameterQuery );

		parameterQuery.getDocument().addDocumentListener( new DocumentListener() {
			@Override
			public void insertUpdate( DocumentEvent e ) {
				setVariableLabel();
			}

			@Override
			public void removeUpdate( DocumentEvent e ) {
				setVariableLabel();
			}

			@Override
			public void changedUpdate( DocumentEvent e ) {
				setVariableLabel();
			}
		} );

	}

	public ParameterPanel() {
		this( null, null );
	}

	@Override
	protected void isetElement( Parameter p, DefaultMutableTreeNode node ) {
		parameterName.setText( p.getLabel() );
		parameterQuery.setText( p.getDefaultQuery() );
	}

	public void setInsight( Insight i ) {
		insight = i;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings( "unchecked" )
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel7 = new javax.swing.JLabel();
    parameterName = new javax.swing.JTextField();
    jScrollPane5 = new javax.swing.JScrollPane();
    parameterQuery = new com.ostrichemulators.semtool.ui.components.tabbedqueries.SparqlTextArea();
    jLabel8 = new javax.swing.JLabel();
    conceptbtn = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    vartext = new javax.swing.JTextField();

    jLabel7.setText("Parameter Label");

    parameterQuery.setColumns(20);
    parameterQuery.setRows(5);
    jScrollPane5.setViewportView(parameterQuery);

    jLabel8.setText("Query");

    conceptbtn.setText("Query Builder");
    conceptbtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        conceptbtnActionPerformed(evt);
      }
    });

    jLabel1.setText("Variable Name");

    vartext.setEditable(false);
    vartext.setToolTipText("Use this variable in the Insight Sparql");
    vartext.setEnabled(false);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(conceptbtn))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel7)
              .addComponent(jLabel8)
              .addComponent(jLabel1))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(parameterName)
              .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
              .addComponent(vartext))))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel7)
          .addComponent(parameterName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(vartext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel8)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(conceptbtn)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void conceptbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conceptbtnActionPerformed
		StructureManager sm = StructureManagerFactory.getStructureManager( getEngine() );
		Set<URI> uris = sm.getTopLevelConcepts();
		Map<URI, String> labels = Utility.getInstanceLabels( uris, getEngine() );
		labels = Utility.sortUrisByLabel( labels );

		Parameter parameter = getElement();
		Map<URI, Parameter> parameters = new HashMap<>();
		// we want the user to be able to select a parent parameter,
		// but we don't have that functionality yet, so we'll simulate it here
				
		List<Parameter> currentparams = new ArrayList<>();
		Enumeration<DefaultMutableTreeNode> en = getNode().children();
		while( en.hasMoreElements() ){
			DefaultMutableTreeNode child = en.nextElement();
			currentparams.add( Parameter.class.cast( child.getUserObject() ) );
		}
		
		for ( Parameter p : currentparams ) {
			if ( !p.equals( parameter ) ) {
				parameters.put( p.getId(), p );
				uris.add( p.getId() );
				labels.put( p.getId(), "Instances of \"" + p.getLabel() + "\"" );
			}
		}

		uris.clear();
		uris.add( Constants.ANYNODE );
		uris.addAll( labels.keySet() );

		labels.put( Constants.ANYNODE, "<Any Concept>" );

		JComboBox<URI> combo = new UriComboBox( uris );
		LabeledPairRenderer<URI> renderer = LabeledPairRenderer.getUriPairRenderer();
		renderer.cache( labels );
		combo.setRenderer( renderer );
		String opts[] = { "Ok", "Cancel" };
		int ans = JOptionPane.showOptionDialog( this, combo, "Concept Type",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0] );

		if ( JOptionPane.YES_OPTION == ans ) {
			URI type = combo.getItemAt( combo.getSelectedIndex() );
			if ( Constants.ANYNODE.equals( type ) ) {
				parameterQuery.setText( "SELECT ?concept\nWHERE {\n  ?concept rdfs:subClassOf <"
						+ getEngine().getSchemaBuilder().getConceptUri().build() + ">\n}" );
			}
			else if ( parameters.containsKey( type ) ) {
				Parameter p = parameters.get( type );
				// get the first variable
				Pattern pat = Pattern.compile( "^.* (\\?\\w+).*" );
				String pquery = p.getDefaultQuery().replaceAll( "\n", " " );
				Matcher m = pat.matcher( pquery );
				if ( m.matches() ) {
					String pvar = m.group( 1 );
					parameterQuery.setText( "SELECT ?instance WHERE { ?instance a " + pvar + " }" );
				}
			}
			else {
				String label = type.getLocalName();
				parameterQuery.setText( "SELECT ?" + label + "\nWHERE {\n  ?" + label
						+ " a <" + type.toString() + ">\n}" );
			}
		}
  }//GEN-LAST:event_conceptbtnActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton conceptbtn;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JScrollPane jScrollPane5;
  private javax.swing.JTextField parameterName;
  private com.ostrichemulators.semtool.ui.components.tabbedqueries.SparqlTextArea parameterQuery;
  private javax.swing.JTextField vartext;
  // End of variables declaration//GEN-END:variables

	@Override
	protected void updateElement( Parameter p ) {
		p.setLabel( parameterName.getText() );
		p.setDefaultQuery( parameterQuery.getText() );
	}

	private void setVariableLabel() {
		vartext.setText( "?" + Parameter.getVariableFromSparql( parameterQuery.getText() ) );
	}

	@Override
	protected void clear(){
		vartext.setText( null );
		parameterQuery.setText( null );
		parameterQuery.setText( null );
	}
}
