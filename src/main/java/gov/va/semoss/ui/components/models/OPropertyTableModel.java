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
package gov.va.semoss.ui.components.models;

import gov.va.semoss.ui.components.PropertySpecData;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import gov.va.semoss.util.Constants;
import gov.va.semoss.util.DIHelper;


/**
 * This class is used to create a table model for property specific data.
 */
public class OPropertyTableModel extends AbstractTableModel {
	
	PropertySpecData data = null;
	Logger logger = Logger.getLogger(getClass());
	String uriVal = Constants.PREDICATE_URI;

	/**
	 * Constructor for OPropertyTableModel.
	 * @param data PropertySpecData
	 */
	public OPropertyTableModel(PropertySpecData data)
	{
		this.data = data;
	}
	
	/**
	 * Returns the column count.
	
	 * @return int 	Column count. */
	@Override
	public int getColumnCount() {
		return data.columnNames.length;
	}
	
	/**
	 * Sets the control data.
	 * @param data PropertySpecData
	 */
	public void setControlData(PropertySpecData data)
	{
		this.data = data;
	}

	/**
	 * Gets the column name at a particular index.
	 * @param index 	Column index.
	
	 * @return String 	Column name. */
	public String getColumnName(int index)
	{
		return this.data.columnNames2[index];
	}

	/**
	 * Returns the row count.
	
	 * @return int 	Row count. */
	@Override
	public int getRowCount() {
		//return data.getPredicateRows();
		return 0;
	}

	/**
	 * Gets the cell value at a particular row and column index.
	 * @param arg0 		Row index.
	 * @param arg1 		Column index.
	
	 * @return Object 	Cell value. */
	@Override
	public Object getValueAt(int arg0, int arg1) {
		// get the value first
		//return data.getValueAt2(arg0, arg1);
		return null;
	}
	
	/**
	 * Gets the column class at a particular index.
	 * @param column 	Column index.
	
	 * @return Class 	Column class. */
	public Class getColumnClass(int column)
	{
		//logger.debug("Getting clolumn " + column);
		Object val = data.dataList2[0][column];
		return val.getClass();
	}

	/**
	 * Checks whether the cell at a particular row and column index is editable.
	 * @param row 		Row index.
	 * @param column 	Column index.
	
	 * @return boolean 	True if cell is editable. */
	public boolean isCellEditable(int row, int column)
	{
		if(column == 1 || column == 2)
			return true;
		else
			return false;
	}
		
	/**
	 * Sets the label value at a particular row and column index.
	 * @param value 	Label value.
	 * @param row 		Row index.
	 * @param column 	Column index.
	 */
	public void setValueAt(Object value, int row, int column)
	{
		logger.debug("Calling the edge filter set value at");
		//data.setValueAt2(uriVal, value, row, column);
		fireTableDataChanged();
		
		// need to figure out a better way to do this
		JTextField field = (JTextField)DIHelper.getInstance().getLocalProp(Constants.OBJECT_PROP_STRING);
		field.setText(DIHelper.getInstance().getProperty(Constants.PREDICATE_URI));

	}
	
}