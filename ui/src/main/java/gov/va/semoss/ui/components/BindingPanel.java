/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.components;

import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Tree;
import gov.va.semoss.om.Parameter;
import gov.va.semoss.util.MultiSetMap;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * This is the eventual replacement for {@link ParamPanel}, but it's not fully
 * functional yet. In general, you should use ParamPanel until it is removed
 * from the code base.
 *
 * @author ryan
 */
public class BindingPanel extends JPanel {

	private static final Pattern NEEDVARS
			= Pattern.compile( "^\\s*(?:SELECT\\s*[^\\{]+|CONSTRUCT\\s*\\{.*)\\{(.*)\\}$",
					Pattern.CASE_INSENSITIVE );
	private static final Pattern ONEVAR = Pattern.compile( "\\?(\\w+)" );
	private final Map<Parameter, JComboBox<URI>> combos = new HashMap<>();
	private final List<JPanel> panels = new ArrayList<>();

	public BindingPanel() {
		setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );

	}

	public void setParameters( Collection<Parameter> params ) {
		for ( JPanel pnl : panels ) {
			remove( pnl );
		}

		Tree<Parameter, Integer> ordered = treeify( params );
		print( ordered, ordered.getRoot(), 0 );
		Parameter root = ordered.getRoot();

		int height = 0;
		int width = 300;
		Dimension labelsizer = new Dimension( 100, 25 );

		Deque<Parameter> todo = new ArrayDeque<>();
		todo.add( root );
		while ( !todo.isEmpty() ) {
			Parameter parent = todo.poll();

			Collection<Parameter> children = ordered.getChildren( parent );
			for ( Parameter child : children ) {
				JPanel panel = new JPanel( new BorderLayout() );
				JLabel lbl = new JLabel( child.getLabel() );
				lbl.setSize( labelsizer );
				lbl.setPreferredSize( labelsizer );
				lbl.setMinimumSize( labelsizer );

				JComboBox<URI> cmb = new JComboBox<>();
				combos.put( child, cmb );

				panel.add( lbl, BorderLayout.WEST );
				panel.add( cmb, BorderLayout.CENTER );

				add( panel );
				panels.add( panel );

				height += labelsizer.height;

				todo.add( child );
			}
		}

		this.setPreferredSize( new Dimension( width, height ) );
	}

	public Map<Parameter, Value> getBindings() {
		Map<Parameter, Value> map = new HashMap<>();
		for ( Map.Entry<Parameter, JComboBox<URI>> en : combos.entrySet() ) {
			JComboBox<URI> cmb = en.getValue();
			map.put( en.getKey(), cmb.getItemAt( cmb.getSelectedIndex() ) );
		}

		return map;
	}

	protected void print( Tree<Parameter, Integer> tree, Parameter node, int depth ) {
		Logger log = Logger.getLogger( getClass() );
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < depth; i++ ) {
			sb.append( "  " );
		}
		sb.append( tree.getRoot().equals( node ) ? "<root>" : node );
		log.debug( sb.toString() );

		for ( Parameter p : tree.getChildren( node ) ) {
			print( tree, p, depth + 1 );
		}
	}

	/**
	 * Figures out which parameters are dependent on others.
	 *
	 * @param unordered the parameters
	 * @return an list of Parameters, with
	 */
	private static Tree<Parameter, Integer> treeify( Collection<Parameter> unordered ) {
		Parameter root = new Parameter();

		// 1) run through all the parameters to see what variables they return, and 
		//     what variables the need (if any)
		// 2) go through our needs map, and if a parameter needs a variable
		//     provided by another, then it must be a child of the other
		// if a parameter doesn't have any values satisfied by other parameters, it's
		// a child of the root
		Map<String, Parameter> provides = new LinkedHashMap<>();
		MultiSetMap<Parameter, String> needs = new MultiSetMap<>();
		//Map<Parameter, Parameter> childParentLkp = new HashMap<>();
		MultiSetMap<Parameter, Parameter> parentChildren = new MultiSetMap<>();

		for ( Parameter p : unordered ) {
			String providervar = p.getVariable();
			provides.put( providervar, p );

			Matcher m = NEEDVARS.matcher( p.getDefaultQuery().replaceAll( "\n", " " ) );
			if ( m.matches() ) {
				String whereclause = m.group( 1 );

				Matcher varmatch = ONEVAR.matcher( whereclause );
				while ( varmatch.find() ) {
					String var = varmatch.group( 1 );
					if ( !var.equals( providervar ) ) {
						needs.add( p, var );
					}
				}
			}
		}

		for ( Map.Entry<Parameter, Set<String>> en : needs.entrySet() ) {
			Parameter needer = en.getKey();
			boolean isrootchild = true;

			for ( String need : en.getValue() ) {
				if ( provides.containsKey( need ) ) {
					Parameter provider = provides.get( need );
					parentChildren.add( provider, needer );
					isrootchild = false;
				}
			}

			if ( isrootchild ) {
				parentChildren.add( root, needer );
			}
		}

		// make sure we add to our tree parameters with no needs
		for ( Parameter p : unordered ) {
			if ( !needs.containsKey( p ) ) {
				parentChildren.add( root, p );
			}
		}

		DelegateTree<Parameter, Integer> ordered = new DelegateTree<>();
		ordered.addVertex( root );
		Deque<Parameter> todo = new ArrayDeque<>();
		todo.add( root );
		int edgecount = 0;
		while ( !todo.isEmpty() ) {
			Parameter parent = todo.poll();

			for ( Parameter child : parentChildren.getNN( parent ) ) {
				ordered.addChild( edgecount++, parent, child );
				todo.add( child );
			}
		}

		return ordered;
	}
}
