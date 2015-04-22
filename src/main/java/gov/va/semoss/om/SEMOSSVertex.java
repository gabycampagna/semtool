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

import gov.va.semoss.ui.components.models.ValueTableModel;
import gov.va.semoss.ui.helpers.TypeColorShapeTable;
import gov.va.semoss.util.Constants;
import gov.va.semoss.util.Utility;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.impl.URIImpl;

/**
 * Variables are transient because this tells the json writer to ignore them
 */
public class SEMOSSVertex {
	private transient static Logger logger = Logger.getLogger(SEMOSSVertex.class);

	private transient Map<String, String> uriHash = new HashMap<>();
	private transient Map<String, SEMOSSVertex> edgeHash = new HashMap<>();
	private Map<String, Object> propHash = new HashMap<>();

	private transient List<SEMOSSEdge> inEdge = new ArrayList<>();
	private transient List<SEMOSSEdge> outEdge = new ArrayList<>();

	private transient Color color;
	private transient Shape shape, shapeLegend;
	private String colorString, shapeString;
	
	/**
	 * Constructor for SEMOSSVertex.
	 *
	 * @param _uri
	 *            String
	 */
	public SEMOSSVertex(String uri) {
		String instanceName = uri;
		try {
			instanceName = new URIImpl(uri).getLocalName();
		} catch (Exception ignored) {}

		String className = Utility.getClassName(uri);
		if (className == null) {
			className = instanceName;
		}

		initVertex(uri, instanceName, className);
	}

	/**
	 * Constructor for SEMOSSVertex. This constructor is for when the vertex is a
	 * Literal
	 *
	 * @param type
	 *            String
	 * @param vert
	 *            Object
	 */
	public SEMOSSVertex(String type, Object vert) {
		initVertex(type + "/" + vert, vert + "", vert + "");
		propHash.put(type, vert + "");
	}

	private void initVertex(String uriString, String instanceName,
			String className) {
		logger.debug("Initializing Vertex (Name, Type, URI): (" + 
			instanceName + ", " + className + ", " + uriString + ")");

		putProperty(Constants.URI_KEY, uriString);
		putProperty(Constants.VERTEX_NAME, instanceName);
		putProperty(Constants.VERTEX_TYPE, className);

		TypeColorShapeTable.getInstance().initializeColor(this);
		TypeColorShapeTable.getInstance().initializeShape(this);
	}

	// this is the out vertex
	public void addInEdge(SEMOSSEdge edge) {
		inEdge.add(edge);
		propHash.put(Constants.INEDGE_COUNT, inEdge.size());

		edgeHash.put(
				edge.getInVertex().getProperty(Constants.VERTEX_NAME) + "",
				edge.getInVertex());
		addVertexCounter(edge.getOutVertex());
	}

	/**
	 * Method addVertexCounter.
	 *
	 * @param outVertex
	 *            SEMOSSVertex
	 */
	private void addVertexCounter(SEMOSSVertex outVertex) {
		Integer vertTypeCount = 0;
		try {
			if (propHash.containsKey(outVertex.getType())) {
				vertTypeCount = (Integer) propHash.get(outVertex.getType());
			}
			vertTypeCount++;
			propHash.put(outVertex.getType(), vertTypeCount);
		} catch (Exception ignored) {}
	}

	// this is the invertex
	public void addOutEdge(SEMOSSEdge edge) {
		outEdge.add(edge);
		propHash.put(Constants.OUTEDGE_COUNT, outEdge.size());

		edgeHash.put(edge.getOutVertex().getProperty(Constants.VERTEX_NAME)
				+ "", edge.getOutVertex());
		addVertexCounter(edge.getInVertex());
	}

	/**
	 * Method setProperty.
	 *
	 * @param propNameURI
	 *            String
	 * @param propValue
	 *            Object
	 */
	public void setProperty(String propNameURI, Object propValue) {
		String instanceName = propNameURI;
		try {
			instanceName = new URIImpl(propNameURI).getLocalName();
		} catch (Exception e) {
			logger.warn("Could not parse " + propNameURI + " into a URI: " + e, e);
		}
		
		uriHash.put(instanceName, propNameURI);
		
		if (propValue instanceof Literal) {
			propHash.put(instanceName, ValueTableModel.getValueFromLiteral((Literal)propValue));
		} else {
			propHash.put(instanceName, ValueTableModel.parseXMLDatatype(propValue.toString()));
		}
	}
	
	/**
	 * Sets a new label for this vertex. This function is really a convenience
	 * to {@link #putProperty(java.lang.String, java.lang.String)}, but doesn't
	 * go through the same error-checking. Any name is acceptable. We can always
	 * rename a label.
	 * 
	 * @param label
	 *            the new label to set
	 */
	public void setLabel(String label) {
		putProperty(Constants.VERTEX_NAME, label);
	}

	public final void setColor(Color _color) {
		color = _color;
	}

	public Color getColor() {
		return color;
	}

	public void setColorString(String _colorString) {
		colorString = _colorString;
	}

	public String getColorString() {
		return colorString;
	}

	public void setShape(Shape _shape) {
		shape = _shape;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShapeString(String _shapeString) {
		shapeString = _shapeString;
	}

	public String getShapeString() {
		return shapeString;
	}

	public void setShapeLegend(Shape _shapeLegend) {
		shapeLegend = _shapeLegend;
	}

	public Shape getShapeLegend() {
		return shapeLegend;
	}

	public Map<String, Object> getProperties() {
		return propHash;
	}

	public Object getProperty(String arg0) {
		return propHash.get(arg0);
	}

	public final void putProperty(String propName, String propValue) {
		propHash.put(propName, propValue);
	}

	public Collection<SEMOSSEdge> getInEdges() {
		return inEdge;
	}

	public Collection<SEMOSSEdge> getOutEdges() {
		return outEdge;
	}

	public String getURI() {
		return getProperty(Constants.URI_KEY) + "";
	}

	public String getType() {
		return getProperty(Constants.VERTEX_TYPE) + "";
	}

	public String getLabel() {
		return getProperty(Constants.VERTEX_NAME) + "";
	}
}