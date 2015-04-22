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
package gov.va.semoss.rdf.engine.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * A way of storing each triple in RDF for SELECT SPARQL queries.
 */
public class SesameJenaSelectStatement {

	private final Map<String, Object> propHash = new HashMap<>();
	private final Map<String, Object> rawPropHash = new HashMap<>();

	/**
	 * Method setVar. Sets the variables and the values.
	 *
	 * @param var Object - The variable name.
	 * @param val Object - The value of the variable.
	 */
	public void setVar( String var, Object val ) {
		propHash.put( var, val );
	}

	/**
	 * Method getVar. Gets the value associated with a certain variable name.
	 *
	 * @param var Object - The variable name.
	 *
	 * @return Object - the value of the variable.
	 */
	public Object getVar( String var ) {
		return propHash.get( var );
	}

	/**
	 * Method setRawVar. Sets the variables and the values. Uses the full URI.
	 *
	 * @param var Object - The variable name.
	 * @param val Object - The value of the variable.
	 */
	public void setRawVar( String var, Object val ) {
		rawPropHash.put( var, val );
	}

	/**
	 * Method getVar. Gets the value associated with a certain variable name. Uses
	 * the full URI.
	 *
	 * @param var Object - The variable name.
	 *
	 * @return Object - the value of the variable.
	 */
	public Object getRawVar( String var ) {
		return rawPropHash.get( var );
	}

	public Map<String, Object> getPropHash() {
		return propHash;
	}

	public Map<String, Object> getRawPropHash() {
		return rawPropHash;
	}
}