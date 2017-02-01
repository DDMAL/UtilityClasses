package mckay.utilities.gui.tables;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Static methods for use with JTable and FeatureSelectorTableModel objects.
 *
 * @author Cory McKay
 */
public class TableMethods
{
	/**
	 * Set the preferred widths of each of the columns of the given table to fit the biggest thing in each
	 * column (which may be either the column header or the contents of one of the column's cells).
	 *
	 * @param table			The table whose columns are to be resized.
	 * @param fixed_width	If this is true, then columns keep the widths assigned by this method no matter
	 *						what, with the result that oversize oversize tables become horizontally 
	 *						scrollable, and undersize tables include empty space. If it is false, then
	 *						oversize tables have their columns automatically resized, and undersize tables are
	 *						automatically expanded to fill available space.
	 */
	public static void sizeTableColumnsToFit(JTable table, boolean fixed_width)
	{
		// A buffer amount to add to column widths to make them look less cluttered
		final int extra_spacing = 10;

		// Allow a horizontal scrollbar to appear for use with oversize columns
		if (fixed_width)
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Calculate and reset the preferred width of each column one by one
		for (int column = 0; column < table.getColumnCount(); column++)
		{
			// This column
			TableColumn table_column = table.getColumnModel().getColumn(column);

			// Find the width of the column header
			TableCellRenderer column_header_renderer = table_column.getHeaderRenderer();
			if (column_header_renderer == null)
				column_header_renderer = table.getTableHeader().getDefaultRenderer();
			Component column_header_component = column_header_renderer.getTableCellRendererComponent(table, table_column.getHeaderValue(), false, false, -1, column);
			int preferred_column_header_width = column_header_component.getPreferredSize().width + table.getIntercellSpacing().width + extra_spacing;

			// Set the preferred table width to be the larger of the minimum width and the header width
			int preferred_column_width = Math.max(preferred_column_header_width, table_column.getMinWidth());

			// Find the widest row in this column, and reset preferred_column_width if it is larger than
			// the current preferred_column_width
			for (int row = 0; row < table.getRowCount(); row++)
			{
				TableCellRenderer cell_renderer = table.getCellRenderer(row, column);
				Component cell_component = table.prepareRenderer(cell_renderer, row, column);
				int cell_content_width = cell_component.getPreferredSize().width + table.getIntercellSpacing().width + extra_spacing;

				preferred_column_width = Math.max(preferred_column_width, cell_content_width);
			}

			// Set the preferred width of this column
			table_column.setPreferredWidth(preferred_column_width);
		}
	}
}