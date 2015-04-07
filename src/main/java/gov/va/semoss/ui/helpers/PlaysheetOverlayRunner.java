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
package gov.va.semoss.ui.helpers;

import gov.va.semoss.ui.components.api.IPlaySheet;
import gov.va.semoss.ui.components.playsheets.AbstractRDFPlaySheet;

/**
 * This class helps with running the overlay view method for a playsheet.
 */
public class PlaysheetOverlayRunner implements Runnable{

	IPlaySheet playSheet = null;
	
	
	/**
	 * Constructor for PlaysheetOverlayRunner.
	 * @param playSheet IPlaySheet
	 */
	public PlaysheetOverlayRunner(IPlaySheet playSheet)
	{
		this.playSheet = playSheet;
	}

	
	/**
	 * Method run. Calls the overlay view method on the local play sheet.
	 */
	@Override
	public void run() {
		if(playSheet instanceof AbstractRDFPlaySheet)
			((AbstractRDFPlaySheet)playSheet).setAppend(true);
		playSheet.createData();
		playSheet.runAnalytics();
		playSheet.overlayView();
	}
	
	/**
	 * Method setPlaySheet. Sets the playsheet to this playsheet.
	 * @param playSheet IPlaySheet
	 */
	public void setPlaySheet(IPlaySheet playSheet)
	{
		this.playSheet = playSheet;
	}

}
