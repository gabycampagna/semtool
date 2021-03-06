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
import java.awt.Font;

import com.ostrichemulators.semtool.util.Constants;

/**
 * Transforms the font label on a node vertex in the graph.
 */
public class LabelFontTransformer<T extends GraphElement> extends SizedSelectingTransformer<T, Font> {

	private static final int DEFAULT_SIZE = Constants.INITIAL_GRAPH_FONT_SIZE;
	private static final int MAXSIZE = 55;
	private static final int MINSIZE = 0;
	private static final Font hidden = new Font( "Plain", Font.PLAIN, MINSIZE );

	private Font normal = new Font( "Plain", Font.PLAIN, DEFAULT_SIZE );
	private Font unsel = new Font( "Plain", Font.PLAIN, DEFAULT_SIZE );

	public LabelFontTransformer() {
		super( DEFAULT_SIZE, MAXSIZE, MINSIZE, 1.0 );
	}

	@Override
	public void setUnselectedSize( double sz ) {
		super.setUnselectedSize( sz );
		unsel = new Font( "Plain", Font.PLAIN, (int) sz );
	}

	@Override
	public void setDefaultSize( double n ) {
		super.setDefaultSize( n );
		normal = new Font( "Plain", Font.PLAIN, (int) n );
	}

	@Override
	protected Font transformNotSelected( T t, boolean skel ) {
		return ( skel ? hidden : unsel );
	}

	@Override
	protected Font getNormal( T t, Double sz, double defaultSize ) {
		if ( null == sz ) {
			sz = defaultSize;
		}

		return ( sz == DEFAULT_SIZE ? normal
				: new Font( "Plain", Font.PLAIN, sz.intValue() ) );
	}

	@Override
	protected Font getSelected( T t, Double sz, double defaultSize ) {
		return getNormal( t, sz, defaultSize );
	}
}
