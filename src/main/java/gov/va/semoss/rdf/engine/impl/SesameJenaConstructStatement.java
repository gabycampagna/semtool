/*******************************************************************************
 * Copyright 2013 SEMOSS.ORG
 * 
 * This file is part of SEMOSS.
 * 
 * SEMOSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SEMOSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SEMOSS.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package gov.va.semoss.rdf.engine.impl;

/**
 * A way of storing each triple in RDF for CONSTRUCT based SPARQL queries.
 */
public class SesameJenaConstructStatement {
	
	String subject = null;
	String predicate = null;
	Object object = null;
	
	/**
	 * Method getSubject. Gets the subject of the SPARQL query.	
	 * @return String - the subject of the query.*/
	public String getSubject() {
		return subject;
	}
	
	/**
	 * Method setSubject.  Sets the subject of the SPARQL query.
	 * @param subject String - the subject of the query.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * Method getPredicate. Gets the predicate of the SPARQL query.		
	 * @return String - the predicate of the query.*/
	public String getPredicate() {
		return predicate;
	}
	
	/**
	 * Method setPredicate. - Sets the predicate of the SPARQL query.	
	 * @param predicate String - the predicate of the query.
	 */
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	
	/**
	 * Method getObject. Gets the object of the SPARQL query.	
	 * @return Object - the object of the query.*/
	public Object getObject() {
		return object;
	}
	
	/**
	 * Method setObject. Sets the object of the SPARQL query.	
	 * @param object Object - the object of the query.
	 */
	public void setObject(Object object) {
		this.object = object;
	}
}