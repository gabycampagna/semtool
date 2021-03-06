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

import com.ostrichemulators.semtool.om.GraphColorShapeRepository;
import com.ostrichemulators.semtool.om.SEMOSSVertex;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * Transforms the size and shape of selected nodes.
 * @param <T>
 */
public class VertexShapeTransformer<T extends SEMOSSVertex> extends SizedSelectingTransformer<T, Shape> {

	private static final double INITIAL_SCALE = 1.0;
	private static final double MAXSIZE = 100.0;
	private static final double MINSIZE = 0.0;
	public static final double STEPSIZE = 0.5;
	private GraphColorShapeRepository repo;

	public void setColorShapeRepository( GraphColorShapeRepository repo ) {
		this.repo = repo;
	}

	public VertexShapeTransformer() {
		super( INITIAL_SCALE, MAXSIZE, MINSIZE, STEPSIZE );
	}

	@Override
	protected Shape transformNotSelected( SEMOSSVertex t, boolean inSkeletonMode ) {
		double ICONSIZE = repo.getIconSize();
		Shape shape = repo.getShape( t ).getShape( ICONSIZE );
		AffineTransform at = AffineTransform.getTranslateInstance( -ICONSIZE / 2, -ICONSIZE / 2 );
		return at.createTransformedShape( shape );
	}

	@Override
	protected Shape getNormal( SEMOSSVertex t, Double scale, double defaultScale ) {
		if ( null == scale ) {
			scale = defaultScale;
		}

		double iconsize = repo.getIconSize() * scale;
		Shape s = repo.getShape( t ).getShape( iconsize );

		AffineTransform at = AffineTransform.getTranslateInstance( -iconsize / 2, -iconsize / 2 );
		return at.createTransformedShape( s );
	}

	@Override
	protected Shape getSelected( SEMOSSVertex t, Double sz, double defaultSize ) {
		return getNormal( t, sz, defaultSize );
	}
}
