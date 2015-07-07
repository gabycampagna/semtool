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
package gov.va.semoss.om;

import gov.va.semoss.util.UriBuilder;
import java.awt.Color;

import java.util.Objects;
import org.openrdf.model.URI;

/**
 *
 * @author pkapaleeswaran Something that expresses the edge
 * @version $Revision: 1.0 $
 */
public class SEMOSSEdge extends AbstractNodeEdgeBase implements Comparable<SEMOSSEdge> {

	private SEMOSSVertex inVertex;
	private SEMOSSVertex outVertex;

	/**
	 * @param _outVertex
	 * @param _inVertex
	 * @param _uri Vertex1 (OutVertex) -------> Vertex2 (InVertex) (OutEdge)
	 * (InEdge)
	 */
	public SEMOSSEdge( SEMOSSVertex _outVertex, SEMOSSVertex _inVertex, URI _uri ) {
		super( _uri, null, _uri.getLocalName() );
		inVertex = _inVertex;
		outVertex = _outVertex;

		if ( null != inVertex ) {
			inVertex.addInEdge( this );
		}
		if ( null != outVertex ) {
			outVertex.addOutEdge( this );
		}
		setColor( Color.DARK_GRAY );
	}

	public SEMOSSEdge( URI _uri ) {
		super( _uri, null, _uri.getLocalName() );
		setColor( Color.DARK_GRAY );
		inVertex = null;
		outVertex = null;
	}

	public SEMOSSVertex getInVertex() {
		return inVertex;
	}

	public SEMOSSVertex getOutVertex() {
		return outVertex;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + Objects.hashCode( this.inVertex );
		hash = 97 * hash + Objects.hashCode( this.outVertex );
		hash = 97 * hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		final SEMOSSEdge other = (SEMOSSEdge) obj;
		if ( !Objects.equals( this.getURI(), other.getURI() ) ) {
			return false;
		}
		if ( !Objects.equals( this.inVertex, other.inVertex ) ) {
			return false;
		}
		if ( !Objects.equals( this.outVertex, other.outVertex ) ) {
			return false;
		}
		if ( !Objects.equals( this.outVertex, other.outVertex ) ) {
			return false;
		}
		
		return true;
	}

	@Override
	public int compareTo( SEMOSSEdge t ) {
		return toString().compareTo( t.toString() );
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if ( !( null == outVertex || null == outVertex.getURI() ) ) {
			sb.append( outVertex.getURI() );
		}

		if ( null != getURI() ) {
			sb.append( "->" ).append( getURI() );
		}

		if ( !( null == inVertex || null == inVertex.getURI() ) ) {
			sb.append( "->" ).append( inVertex.getURI() );
		}

		return sb.toString();
	}
}
