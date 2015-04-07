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

/**
 * This class is used to adjust the edge values in a table.
 */
public class EdgeAdjusterTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 8890192606196740124L;

	private VertexFilterData data;

	/**
	 * Constructor for EdgeAdjusterTableModel.
	 * 
	 * @param data
	 *            VertexFilterData
	 */
	public EdgeAdjusterTableModel(VertexFilterData _data) {
		data = _data;
	}

	/**
	 * Gets the column count.
	 * 
	 * @return int Column count.
	 */
	@Override
	public int getColumnCount() {
		return 2;
	}

	/**
	 * Sets the vertex filter data.
	 * 
	 * @param data
	 *            Data to be set.
	 */
	public void setVertexFilterData(VertexFilterData data) {
		this.data = data;
	}

	/**
	 * Gets the column name of a particular column index.
	 * 
	 * @param index
	 *            Column index.
	 * 
	 * @return String Column name.
	 */
	public String getColumnName(int index) {
		return data.getEdgeTypeNames()[index];
	}

	/**
	 * Counts the number of rows.
	 * 
	 * @return int Row count.
	 */
	@Override
	public int getRowCount() {
		return data.getEdgeTypes().length;
	}

	/**
	 * Returns the value for the cell at a specified row and column index.
	 * 
	 * @param arg0
	 *            Row index.
	 * @param arg1
	 *            Column index.
	 * 
	 * @return Object Edge adjust value.
	 */
	@Override
	public Object getValueAt(int arg0, int arg1) {
		// get the value first
		return data.getEdgeAdjustValueAt(arg0, arg1);
	}

	/**
	 * Get column class.
	 * 
	 * @param column
	 *            Column.
	 * 
	 * @return Class Column class.
	 */
	public Class<?> getColumnClass(int column) {
		if (column == 0)
			return String.class;
		else
			return Double.class;
	}

	/**
	 * Checks whether a cell can be edited.
	 * 
	 * @param row
	 *            Row number.
	 * @param column
	 *            Column number.
	 * 
	 * @return boolean True if the cell
	 */
	public boolean isCellEditable(int row, int column) {
		if (column == 0)
			return false;
		else
			return true;
	}

	/**
	 * Sets the edge value at a particular row and column location.
	 * 
	 * @param value
	 *            Edge value.
	 * @param row
	 *            Row number.
	 * @param column
	 *            Column number.
	 */
	public void setValueAt(Object value, int row, int column) {
		data.setEdgeAdjustValueAt(value, row, column);
		fireTableDataChanged();
	}
}
