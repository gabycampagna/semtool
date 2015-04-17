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

import gov.va.semoss.ui.components.VertexFilterData;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

/**
 * This class is used to create a table model (listeners) for edge filters.
 */
public class EdgeFilterTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 53864898863707354L;
	private static final Logger logger = Logger.getLogger(EdgeFilterTableModel.class);
	
	private static final String[] edgeColumnNames = { "Show", "Relation", "Instance" };
	private VertexFilterData data = null;

	/**
	 * Constructor for EdgeFilterTableModel.
	 * 
	 * @param data
	 *            VertexFilterData
	 */
	public EdgeFilterTableModel(VertexFilterData _data) {
		data = _data;
	}

	/**
	 * Gets the edge column count from the data.
	 * 
	 * @return int Column count.
	 */
	@Override
	public int getColumnCount() {
		return edgeColumnNames.length;
	}

	/**
	 * Sets the vertex filter data.
	 * 
	 * @param data
	 *            VertexFilterData
	 */
	public void setVertexFilterData(VertexFilterData data) {
		this.data = data;
	}

	/**
	 * Gets the column name at a particular cell.
	 * 
	 * @param index
	 *            Column index.
	 * 
	 * @return String Column name.
	 */
	@Override
	public String getColumnName(int index) {
		return edgeColumnNames[index];
	}

	/**
	 * Gets the row count from the data.
	 * 
	 * @return int Row count.
	 */
	@Override
	public int getRowCount() {
		return data.getEdgeCount();
	}

	/**
	 * Returns the value for the cell.
	 * 
	 * @param arg0
	 *            Row index.
	 * @param arg1
	 *            Column index.
	 * 
	 * @return Object Value of the cell.
	 */
	@Override
	public Object getValueAt(int arg0, int arg1) {
		return data.getEdgeValueAt(arg0, arg1);
	}

	/**
	 * Gets the class at a particular column.
	 * 
	 * @param column
	 *            Column index.
	 * 
	 * @return Class Column class.
	 */
	@Override
	public Class<?> getColumnClass(int column) {
		Object edgeVal = data.getEdgeVal(0, column);
		if (edgeVal == null)
			edgeVal = "";
		return edgeVal.getClass();
	}

	/**
	 * Checks if the cell at a particular row and column is editable.
	 * 
	 * @param row
	 *            Row index.
	 * @param column
	 *            Column index.
	 * 
	 * @return boolean True if the cell is editable.
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 0 || column == 3)
			return true;
		return false;
	}

	/**
	 * Sets the edge value at a particular row and column.
	 * 
	 * @param value
	 *            Value to assign to cell.
	 * @param row
	 *            Row that value is assigned to.
	 * @param column
	 *            Column that value is assigned to.
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {
		logger.debug("Calling the edge filter set value at");
		data.setEdgeValueAt(value, row, column);
		fireTableDataChanged();
	}

}
