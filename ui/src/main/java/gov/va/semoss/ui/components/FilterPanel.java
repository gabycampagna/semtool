/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.components;

import gov.va.semoss.om.GraphElement;
import gov.va.semoss.om.SEMOSSEdge;
import gov.va.semoss.om.SEMOSSVertex;
import gov.va.semoss.rdf.engine.api.IEngine;
import gov.va.semoss.ui.components.models.FilterRow;
import gov.va.semoss.ui.components.models.NodeEdgePropertyTableModel;
import gov.va.semoss.ui.components.models.VertexFilterTableModel;
import gov.va.semoss.ui.components.renderers.LabeledPairTableCellRenderer;
import gov.va.semoss.ui.components.renderers.SimpleValueEditor;
import gov.va.semoss.util.Constants;
import gov.va.semoss.util.GuiUtility;
import java.awt.Component;
import java.util.Arrays;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author ryan
 */
public class FilterPanel extends javax.swing.JPanel {

	private VertexFilterTableModel<SEMOSSVertex> nodemodel
			= new VertexFilterTableModel<>( "Node Type" );

	private VertexFilterTableModel<SEMOSSEdge> edgemodel
			= new VertexFilterTableModel<>( "Edge Type" );

	private NodeEdgePropertyTableModel propmodel;

	/**
	 * Creates new form FilterPanel2
	 */
	public FilterPanel() {
		initComponents();
		props.getColumnModel().getColumn( 1 ).setCellEditor( new SimpleValueEditor() );

		int sizes[] = { 15, 50, 50 };
		for ( int i = 0; i < sizes.length; i++ ) {
			nodes.getColumnModel().getColumn( i ).setPreferredWidth( sizes[i] );
		}
	}

	public void setModels( VertexFilterTableModel<SEMOSSVertex> nmodel,
			VertexFilterTableModel<SEMOSSEdge> emodel, NodeEdgePropertyTableModel pmodel,
			IEngine engine ) {
		nodemodel = nmodel;
		edgemodel = emodel;
		propmodel = pmodel;

		nodes.setModel( nmodel );
		edges.setModel( emodel );
		props.setModel( pmodel );

		nodemodel.fireTableDataChanged();
		edgemodel.fireTableDataChanged();
		propmodel.fireTableDataChanged();

		int sizes[] = { 15, 50, 50 };
		for ( int i = 0; i < sizes.length; i++ ) {
			nodes.getColumnModel().getColumn( i ).setPreferredWidth( sizes[i] );
			edges.getColumnModel().getColumn( i ).setPreferredWidth( sizes[i] );
		}
		setEngine( engine );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings( "unchecked" )
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    nodes = new javax.swing.JTable();
    jScrollPane2 = new javax.swing.JScrollPane();
    props = new javax.swing.JTable();
    jScrollPane3 = new javax.swing.JScrollPane();
    edges = new javax.swing.JTable();

    setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

    nodes.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Show", "Type", "Instance"
      }
    ));
    jScrollPane1.setViewportView(nodes);

    add(jScrollPane1);

    props.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Property", "Value"
      }
    ));
    jScrollPane2.setViewportView(props);

    add(jScrollPane2);

    edges.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Show", "Type", "Instance"
      }
    ));
    jScrollPane3.setViewportView(edges);

    add(jScrollPane3);
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTable edges;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JTable nodes;
  private javax.swing.JTable props;
  // End of variables declaration//GEN-END:variables

	public void setEngine( IEngine eng ) {
		LabeledPairTableCellRenderer<Value> pr
				= LabeledPairTableCellRenderer.getValuePairRenderer( eng );
		pr.cache( Constants.IN_EDGE_CNT, "In-Edges" );
		pr.cache( Constants.OUT_EDGE_CNT, "Out-Edges" );

		props.setDefaultRenderer( Value.class, pr );
		props.setDefaultRenderer( URI.class, pr );

		ShowRenderer valrend = new ShowRenderer( eng );
		for ( JTable tbl : Arrays.asList( nodes, edges ) ) {
			tbl.getColumnModel().getColumn( 1 ).setCellRenderer( valrend );
			tbl.getColumnModel().getColumn( 2 ).setCellRenderer( valrend );
		}
	}

	public void useBlankModels() {
		nodes.setModel( new DefaultTableModel( new String[]{ "Show", "Type", "Instance" }, 0 ) );
		edges.setModel( nodes.getModel() );
		props.setModel( new DefaultTableModel( new String[]{ "Property", "Value" }, 0 ) );
	}

	public NodeEdgePropertyTableModel getPropertyModel() {
		return propmodel;
	}

	public VertexFilterTableModel<SEMOSSVertex> getNodeModel() {
		return nodemodel;
	}

	public VertexFilterTableModel<SEMOSSEdge> getEdgeModel() {
		return edgemodel;
	}

	private class ShowRenderer extends LabeledPairTableCellRenderer<Value> {

		private final IEngine engine;

		public ShowRenderer( IEngine engine ) {
			this.engine = engine;
		}

		@Override
		public Component getTableCellRendererComponent( JTable table, Object value,
				boolean sel, boolean foc, int r, int c ) {

			VertexFilterTableModel<? extends GraphElement> model
					= (VertexFilterTableModel<? extends GraphElement>) table.getModel();
			FilterRow<? extends GraphElement> row = model.getRawRow( r );

			if ( row.isHeader() ) {
				return super.getTableCellRendererComponent( table,
						( 1 == c ? row.type : "Set For All" ), sel, foc, r, c );
			}
			else {
				return super.getTableCellRendererComponent( table,
						( 1 == c ? null : row.instance.getLabel() ), sel, foc, r, c );
			}
		}

		@Override
		protected String getLabelForCacheMiss( Value val ) {
			if ( null == val ) {
				return "";
			}

			String ret;
			if ( val instanceof URI ) {
				URI uri = URI.class.cast( val );
				ret = ( null == engine ? uri.getLocalName()
						: GuiUtility.getInstanceLabel( Resource.class.cast( val ), engine ) );
				cache( val, ret );
			}
			else if ( val instanceof Literal ) {
				ret = Literal.class.cast( val ).getLabel();
			}
			else {
				ret = val.stringValue();
			}
			return ret;
		}
	}
}