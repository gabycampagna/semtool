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
package gov.va.semoss.ui.helpers;

import gov.va.semoss.util.Constants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

public class NodeEdgeNumberedPropertyUtility {
	private static final Map<String, URI> localNameToURIHash = new HashMap<>();
	private static final Map<String, String> displayNameMap = new HashMap<>();
	private static final Set<String> hidePropertySet = new HashSet<>();
	private static final Set<String> keepPropertySet = new HashSet<>();

	static {
		displayNameMap.put(Constants.IN_EDGE_CNT.getLocalName(),  "In-Degree");
		displayNameMap.put(Constants.OUT_EDGE_CNT.getLocalName(), "Out-Degree");
		
		hidePropertySet.add("graphing.level");
		
		keepPropertySet.add("label");
		keepPropertySet.add("type");
	}
	
	public static URI getURI(String key) {
		return localNameToURIHash.get(key);
	}
	
	public static Map<String, Object> transformProperties(Map<URI, Object> oldProperties, boolean useKeepProperties) {
		HashMap<String, Object> newProperties = new HashMap<>();
		
		for( Map.Entry<URI, Object> propEntry : oldProperties.entrySet() ) {
			String propertyName = propEntry.getKey().getLocalName();
			if (displayNameMap.keySet().contains(propertyName))
				propertyName = displayNameMap.get(propertyName);

			if (useKeepProperties && keepPropertySet.contains(propertyName)) {
				String valueAsString = propEntry.getValue() + "";
				if (propEntry.getValue() instanceof URI)
					valueAsString = ((URI) propEntry.getValue()).getLocalName();
				newProperties.put( propertyName, valueAsString );
				localNameToURIHash.put( propertyName, propEntry.getKey() );
				continue;
			}
			
			if (hidePropertySet.contains(propertyName)) 
				continue;
			
			Double valueAsDouble = getDoubleIfPossibleFrom( propEntry.getValue() );
			if (valueAsDouble <= 0) 
				continue;
			
			newProperties.put( propertyName, valueAsDouble );
			localNameToURIHash.put( propertyName, propEntry.getKey() );
		}
		
		return newProperties;
	}

	/*
	 * method 
	 */
	public static double getDoubleIfPossibleFrom( Object propertyValue ) {
		if ( propertyValue == null ) {
			return -1;
		}

		String stringval;

		if ( propertyValue instanceof URI ) {
			stringval = URI.class.cast( propertyValue ).getLocalName();
		}
		else if ( propertyValue instanceof Literal ) {
			stringval = Literal.class.cast( propertyValue ).getLabel();
		}
		else {
			stringval = propertyValue.toString();
		}

		try {
			return Double.parseDouble( stringval );
		}
		catch ( NumberFormatException e ) {
			return -1;
		}
	}
}