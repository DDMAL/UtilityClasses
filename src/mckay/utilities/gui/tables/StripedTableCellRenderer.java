/*
 * StripedTableCellRenderer.java
 * Version 3.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College, McGill University and University of Waikato
 */

package mckay.utilities.gui.tables;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;


/**
 * Used to stripe alternate rows of a JTable.
 *
 * <p>Use as follows, for example:
 *
 * <p>JTable table = new JTable();
 * <br>StripedTableCellRenderer.installInTable( table,
 * <br>                                         Color.lightGray,
 * <br>                                         Color.white,
 * <br>                                         null,
 * <br>                                         null );
 *
 * <p>This class is derived from the code on pp. 607-9 of
 *
 * <p>Topley, K. 2000. Core Swing advanced programming. Upper Saddle River, NJ: Prentice Hall.
 *
 * @author Cory McKay
 */
public class StripedTableCellRenderer
     implements TableCellRenderer
{
     /* FIELDS ****************************************************************/
     
     
     protected TableCellRenderer   targetRenderer;
     protected Color               evenBack;
     protected Color               evenFore;
     protected Color               oddBack;
     protected Color               oddFore;
     
     
     /* CONSTRUCTOR ***********************************************************/
     
     
     /**
      * Instantiates the StripedTableCellRenderer and stores the given
      * parameters.
	  * 
	  * @param	targetRenderer		The TableCellRenderer in question.
	  * @param	evenBack			The evenBack color.
	  * @param	evenFore			The evenFore color.
	  * @param	oddBack				The oddBack color.
	  * @param	oddFore				The oddFore color.
	  */
     public StripedTableCellRenderer(TableCellRenderer targetRenderer,
          Color evenBack, Color evenFore,
          Color oddBack, Color oddFore)
     {
          this.targetRenderer = targetRenderer;
          this.evenBack = evenBack;
          this.evenFore = evenFore;
          this.oddBack  = oddBack;
          this.oddFore  = oddFore;
     }
     
     
     /* PUBLIC METHODS ********************************************************/
     
     
     /**
      * Implementation of TableCellRenderer interface
      */
     public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected,
          boolean hasFocus, int row, int column)
     {
          TableCellRenderer renderer = targetRenderer;
          if (renderer == null)
          {
               // Get default renderer from the table
               renderer = table.getDefaultRenderer(table.getColumnClass(column));
          }
          
          // Let the real renderer create the component
          Component comp = renderer.getTableCellRendererComponent(table, value,
               isSelected, hasFocus,
               row, column);
          
          // Apply the stripe effect
          if (isSelected == false && hasFocus == false)
          {
               if ((row & 1) == 0)
               {
                    comp.setBackground(evenBack != null ? evenBack :
                         table.getBackground());
                    comp.setForeground(evenFore != null ? evenFore :
                         table.getForeground());
               }
               else
               {
                    comp.setBackground(oddBack != null ? oddBack :
                         table.getBackground());
                    comp.setForeground(oddFore != null ? oddFore :
                         table.getForeground());
               }
          }
          
          return comp;
     }
     
     
     /**
      * Method to apply this renderer to single column.
	  * 
	  * @param	table		The JTable containing the column.
	  * @param	columnIndex	The index of the column to apply this to.
	  * @param	evenBack	The evenBack color.
	  * @param	evenFore	The evenFore color.
	  * @param	oddBack		The oddBack color.
	  * @param	oddFore		The oddFore color.
      */
     public static void installInColumn(JTable table, int columnIndex,
          Color evenBack, Color evenFore,
          Color oddBack, Color oddFore)
     {
          TableColumn tc = table.getColumnModel().getColumn(columnIndex);
          
          // Get the cell renderer for this column, if any
          TableCellRenderer targetRenderer = tc.getCellRenderer();
          
          // Create a new StripedTableCellRenderer and install it
          tc.setCellRenderer(new StripedTableCellRenderer(targetRenderer,
               evenBack, evenFore,
               oddBack, oddFore));
     }
     
     
     /**
      * Method to apply this renderer to an entire table.
 	  * 
	  * @param	table		The JTable to affect.
	  * @param	evenBack	The evenBack color.
	  * @param	evenFore	The evenFore color.
	  * @param	oddBack		The oddBack color.
	  * @param	oddFore		The oddFore color.
	  */
     public static void installInTable(JTable table,
          Color evenBack, Color evenFore,
          Color oddBack, Color oddFore)
     {
          StripedTableCellRenderer sharedInstance = null;
          int columns = table.getColumnCount();
          for (int i = 0 ; i < columns; i++)
          {
               TableColumn tc = table.getColumnModel().getColumn(i);
               TableCellRenderer targetRenderer = tc.getCellRenderer();
               if (targetRenderer != null)
               {
                    // This column has a specific renderer
                    tc.setCellRenderer(new StripedTableCellRenderer(targetRenderer,
                         evenBack, evenFore,
                         oddBack, oddFore));
               }
               else
               {
                    // This column uses a class renderer - use a shared renderer
                    if (sharedInstance == null)
                    {
                         sharedInstance = new StripedTableCellRenderer(null,
                              evenBack, evenFore,
                              oddBack, oddFore);
                    }
                    tc.setCellRenderer(sharedInstance);
               }
          }
     }
}
