package sketchupblocks.gui;

import processing.core.PApplet;

/**
 * @author Jacques Coetzee
 * This class extends the Generic User Popup
 * as a specialization of the genric user popups
 * where custom heading and sub headings can be set.
 */
public class WarningPopup extends GenericWarningPopup 
{
	/**
	 * The constructor initializes the member variables of its
	 * super class and sets the custom message.
	 * @param window PApplet window.
	 * @param warningMessage Popup message string.
	 */
	public WarningPopup(PApplet window, String warningMessage)
	{
		super(window);
		super.warningMessage = warningMessage;
	}
}