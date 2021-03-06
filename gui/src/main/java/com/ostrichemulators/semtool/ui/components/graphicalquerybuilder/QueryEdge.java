/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.ui.components.graphicalquerybuilder;

import org.openrdf.model.URI;

/**
 * An extension of SEMOSSVertex to allow multiple values for one property
 *
 * @author ryan
 */
public class QueryEdge extends AbstractQueryGraphElement implements QueryGraphElement {

	public QueryEdge( URI id ) {
		super( id );
	}

	public QueryEdge( URI id, URI type, String label ) {
		super( id, type, label );
	}

	@Override
	public boolean isNode() {
		return false;
	}

}
