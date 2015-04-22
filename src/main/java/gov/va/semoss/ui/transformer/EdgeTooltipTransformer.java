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
package gov.va.semoss.ui.transformer;

import gov.va.semoss.om.SEMOSSEdge;
import gov.va.semoss.ui.components.ControlData;
import gov.va.semoss.util.Constants;

import java.util.ArrayList;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

/**
 * Transforms what is displayed on the tooltip when an edge is selected on a graph.
 */
public class EdgeTooltipTransformer implements Transformer <SEMOSSEdge, String> {
	
	Logger logger = Logger.getLogger(getClass());	
	ControlData data = null;
	
	/**
	 * Constructor for EdgeTooltipTransformer.
	 * @param data ControlData
	 */
	public EdgeTooltipTransformer(ControlData data)
	{
		this.data = data;
	}
	

	/**
	 * Method transform.  Get the DI Helper to find what is needed to get for vertex
	 * @param edge DBCMEdge - The edge of which this returns the properties.
	
	 * @return String - The name of the property. */
	@Override
	public String transform(SEMOSSEdge edge) {		
		String propName = "";

		ArrayList<String> props = data.getSelectedPropertiesTT(edge.getEdgeType());
		if(props != null && props.size() > 0)
		{
			propName = propName + edge.getProperty(props.get(0)+"");
			for(int propIndex=1;propIndex < props.size();propIndex++){
				String prop = props.get(propIndex)+"";
				propName = propName + "<br>";
				
				//only add the label on the property if it is not one of the main three
				if(!prop.equals(Constants.VERTEX_NAME)&&!prop.equals(Constants.EDGE_NAME)&&!prop.equals(Constants.VERTEX_TYPE)&&!prop.equals(Constants.EDGE_TYPE)&&!prop.equals(Constants.URI_KEY))
					propName = propName + prop+": ";
				propName = propName + edge.getProperty(props.get(propIndex));
			}
		}
		
		if(propName.equals(""))
			return null;		
		
		propName = "<html><body style=\"border:0px solid white; box-shadow:1px 1px 1px #000; padding:2px; background-color:white;\">" +
				"<font size=\"3\" color=\"black\"><i>" + propName + "</i></font></body></html>";
		return propName;
	}
}