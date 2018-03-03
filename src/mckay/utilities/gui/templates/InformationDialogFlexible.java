package mckay.utilities.gui.templates;

import java.awt.*;
import javax.swing.*;

/**
 * A general purpose dialog box for displaying a (presumably large) amount of pre-generated text. Includes
 * scroll bars if needed.
 *
 * @author Cory McKay
 */
public class InformationDialogFlexible
	extends JDialog
{
     /**
      * Set up the dialog box and displays it.
	  * 
	  * @param text_to_display		The text to display in this dialog box.
	  * @param window_title			The title to give this dialog box.
	  * @param modal_window			Whether or not the dialog box is modal.
	  * @param lines_wrap			Whether or not the text_to_display text will be wrapped if it reaches the
	  *								dialog box's boundaries.
	  * @param editable				Whether or not the text_to_display text is editable.
	  * @param start_at_top			Whether the text should be displayed starting at the top (true), or the
	  *								bottom (false)
	  * @param number_text_columns	How big to make the dialog box horizontally. Expressed in number of text
	  *								characters.
	  * @param number_text_rows		How big to make the dialog box vertically. Expressed in number of text
	  *								lines.
	  */ 
	public InformationDialogFlexible( String text_to_display,
	                                  String window_title, 
	                                  boolean modal_window,
	                                  boolean lines_wrap,
	                                  boolean editable,
									  boolean start_at_top,
	                                  int number_text_columns,
	                                  int number_text_rows )
	 {
		// Give the dialog box its owner, its title and make it modal (or not)
		super();
		setTitle(window_title);
		setModal(modal_window);

		// Set up text_area
		JTextArea text_area = new JTextArea(number_text_rows, number_text_columns);
		text_area.setEditable(editable);
		text_area.setLineWrap(lines_wrap);
		text_area.setWrapStyleWord(true);
		text_area.setText(text_to_display);
		if (start_at_top)
			text_area.setCaretPosition(0);

		// Display the panel
		Container content_pane = getContentPane();
		content_pane.add(new JScrollPane(text_area));
		pack();
		setVisible(true);
	}
}