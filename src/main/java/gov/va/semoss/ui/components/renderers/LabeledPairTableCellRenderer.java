/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.components.renderers;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;

/**
 *
 * @author ryan
 * @param <T> type of entity we'll store labels for
 */
public class LabeledPairTableCellRenderer<T> extends DefaultTableCellRenderer {

	private static final Logger log = Logger.getLogger( LabeledPairTableCellRenderer.class );
	private final Map<T, String> labelCache = new HashMap<>();

	public Map<T, String> getCachedLabels() {
		return new HashMap<>( labelCache );
	}

	public void clearCache(){
		labelCache.clear();
	}
	
	public void cache( Map<T, String> map ) {
		labelCache.putAll( map );
	}

	public void cache( T u, String label ) {
		labelCache.put( u, label );
	}

	@Override
	public Component getTableCellRendererComponent( JTable table, Object value,
			boolean sel, boolean foc, int r, int c ) {

		if ( value instanceof String ) {
			// not sure how we're getting here
			return super.getTableCellRendererComponent( table, value, sel, foc, r, c );
		}

		String text = "";
		if ( null != value ) {
			T val = (T) value;

			if ( !labelCache.containsKey( val ) ) {
				cache( val, getLabelForCacheMiss( val ) );
			}

			text = labelCache.get( val );
		}

		return super.getTableCellRendererComponent( table, text, sel, foc, r, c );
	}

	protected String getLabelForCacheMiss( T val ) {
		return val.toString();
	}

	public static LabeledPairTableCellRenderer<URI> getUriPairRenderer() {
		return new LabeledPairTableCellRenderer<URI>() {
			@Override
			protected String getLabelForCacheMiss( URI val ) {
				return val.getLocalName();
			}
		};
	}
}
