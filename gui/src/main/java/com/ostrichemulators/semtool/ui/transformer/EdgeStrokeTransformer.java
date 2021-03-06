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
package com.ostrichemulators.semtool.ui.transformer;

import com.ostrichemulators.semtool.om.GraphElement;
import com.ostrichemulators.semtool.om.SEMOSSEdge;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class EdgeStrokeTransformer<T extends GraphElement> extends SelectingTransformer<T, Stroke> {

	private Map<SEMOSSEdge, Double> edges = new HashMap<>();
	public static final float DEFAULT_SIZE = 1f;
	public static final float UNSELECTED_SIZE = 0.3f;
	public static final double SELECTED_SIZE = 2f;

	private Stroke normal;
	private Stroke selected;
	private Stroke unselected;
	private double n;
	private double s;
	private double u;

	public EdgeStrokeTransformer() {
		this( DEFAULT_SIZE, SELECTED_SIZE, UNSELECTED_SIZE );
	}

	public EdgeStrokeTransformer( double norm, double sel, double unsel ) {
		reset( norm, sel, unsel );
	}

	public void setNormalSize( double norm ) {
		normal = new BasicStroke( (float) norm, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND );
		n = norm;
	}

	public void setSelectedSize( double m ) {
		selected = new BasicStroke( (float) m, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f );
		s = m;
	}

	public void setUnselectedSize( double m ) {
		unselected = new BasicStroke( (float) m );
		u = m;
	}

	public double getNormalSize() {
		return n;
	}

	public double getSelectedSize() {
		return s;
	}

	public double getUnselectedSize() {
		return u;
	}

	public void reset() {
		reset( DEFAULT_SIZE, SELECTED_SIZE, UNSELECTED_SIZE );
	}

	/**
	 * Resets the strokes to the given sizes, and clears the selected edges. If a
	 * size &lt; 0, it will not be changed
	 *
	 * @param norm
	 * @param sel
	 * @param unsel
	 */
	public void reset( double norm, double sel, double unsel ) {
		if ( norm > 0 ) {
			setNormalSize( norm );
		}
		if ( sel > 0 ) {
			setSelectedSize( sel );
		}
		if ( unsel > 0 ) {
			setUnselectedSize( unsel );
		}

		clearSelected();
	}

	@Override
	protected Stroke transformNormal( GraphElement t ) {
		return normal;
	}

	@Override
	protected Stroke transformSelected( GraphElement t ) {
		return selected;
	}

	@Override
	protected Stroke transformNotSelected( GraphElement t, boolean skel ) {
		return ( skel ? unselected : normal );
	}

	/**
	 * Method setEdges.
	 *
	 * @param edges HashMap
	 */
	public void setEdges( Map<SEMOSSEdge, Double> edges ) {
		this.edges = edges;
	}

	/**
	 * Method getEdges.
	 *
	 * @return HashMap
	 */
	public Map<SEMOSSEdge, Double> getEdges() {
		return edges;
	}
}
