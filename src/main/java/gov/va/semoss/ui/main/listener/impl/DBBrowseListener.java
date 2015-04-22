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
package gov.va.semoss.ui.main.listener.impl;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import gov.va.semoss.ui.components.api.IChakraListener;
import gov.va.semoss.util.Constants;
import gov.va.semoss.util.DIHelper;

/**
 * Controls browsing files for a database.
 */
public class DBBrowseListener implements IChakraListener {

	Logger log = Logger.getLogger(getClass());
		
	/**
	 * Method setModel.  Sets the model that the listener will access.
	 * @param model JComponent
	 */
	public void setModel(JComponent model)
	{
	}
	
	/**
	 * Method actionPerformed.  Dictates what actions to take when an Action Event is performed.
	 * @param e ActionEvent - The event that triggers the actions in the method.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JTextField view = (JTextField)DIHelper.getInstance().getLocalProp(Constants.DB_NAME_FIELD);
		// I just need to show the file chooser and set the action performed to a file chooser class
		JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new java.io.File("."));
		int retVal = jfc.showOpenDialog((JComponent)e.getSource());
		 //Handle open button action.
	    if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            //This is where a real application would open the file.
            log.info("Opening: " + file.getName() + ".");
            view.setText(file.getAbsolutePath());
        } else {
            log.info("Open command cancelled by user.");
        }
	}

	/**
	 * Method setView. Sets a JComponent that the listener will access and/or modify when an action event occurs.  
	 * @param view the component that the listener will access
	 */
	@Override
	public void setView(JComponent view) {
		//this.view = (JTextField)view;		
	}

}