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
package com.ostrichemulators.semtool.ui.components.playsheets;

import com.ostrichemulators.semtool.rdf.engine.api.IEngine;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.scene.web.WebEngine;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;

/**
 * The Play Sheet for the United States geo-location data heatmap. Visualizes a
 * world heat map that can show any numeric property on a node.
 */
public class USHeatMapPlaySheet extends BrowserPlaySheet2 {

	private static final long serialVersionUID = 150592881428916712L;
	private final static String LOCATION_ID = "locationId";
	private final static String HEAT_VALUE = "heatValue";
	private final static String PARAM_MAP = "paramMap";

	/**
	 * Constructor for USHeatMapPlaySheet.
	 */
	public USHeatMapPlaySheet() {
		super( "/html/RDFSemossCharts/app/heatmapus.html" );
	}

	@Override
	public void create( List<Value[]> newdata, List<String> headers, IEngine engine ) {
		setHeaders( headers );
		convertUrisToLabels( newdata, engine );

		Set<Map<String, Object>> data = new HashSet<>();

		for ( Value[] listElement : newdata ) {
			Literal location = Literal.class.cast( listElement[0] );
			Literal heatValue = Literal.class.cast( listElement[1] );
			if ( location == null || heatValue == null ) {
				continue;
			}

			LinkedHashMap<String, Object> elementHash = new LinkedHashMap<>();

			try {
				elementHash.put( HEAT_VALUE, heatValue.doubleValue() );
			}
			catch ( Exception e ) {
				continue;
			}

			elementHash.put( LOCATION_ID, location.stringValue() );

			HashMap<String, String> tooltipParams = new HashMap<>();
			insideLoop:
			for ( int i = 2; i < listElement.length; i++ ) {
				Literal thisParam = Literal.class.cast( listElement[i] );
				if ( thisParam == null ) {
					continue;
				}
				tooltipParams.put( headers.get( i ), thisParam.stringValue() );
			}
			elementHash.put( PARAM_MAP, tooltipParams );

			data.add( elementHash );
		}

		Map<String, Object> allHash = new HashMap<>();
		allHash.put( "dataSeries", data );
		allHash.put( "heatDataName", headers.get( 1 ) );
		addDataHash( allHash );

		createView();
	}

	@Override
	protected void postExecute( WebEngine eng ){
		eng.executeScript( "zoom( 1.01 )");
	}


	@Override
	protected BufferedImage getExportImage() throws IOException {
		return getExportImageFromSVGBlock();
	}
}
