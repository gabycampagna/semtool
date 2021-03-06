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
package com.ostrichemulators.semtool.ui.main.listener.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import com.ostrichemulators.semtool.ui.components.playsheets.GraphPlaySheet;

/**
 * Controls running of the redo button for a play sheet.
 */
public class RedoListener extends AbstractAction implements ActionListener {
		
	private GraphPlaySheet gps = null;
	
	/**
	 * Method actionPerformed.  Dictates what actions to take when an Action Event is performed.
	 * @param actionevent ActionEvent - The event that triggers the actions in the method.
	 */
	@Override
	public void actionPerformed(ActionEvent actionevent) {
		gps.redoView();
	}

	/**
	 * Method setPlaySheet.  Sets the play sheet that the listener will access.
	 * @param gps GraphPlaySheet
	 */
	public void setPlaySheet(GraphPlaySheet gps)
	{
		this.gps = gps;
	}

}
