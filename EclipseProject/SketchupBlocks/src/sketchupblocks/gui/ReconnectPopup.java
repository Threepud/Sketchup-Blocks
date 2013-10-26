package sketchupblocks.gui;

import processing.core.PApplet;

/**
 * @author Jacques Coetzee
 * This class extends the connection popup, essentially
 * doing exactly the same thing except for the popup message
 * displayed.
 */
public class ReconnectPopup extends ConnectingPopup 
{
	/**
	 * This constructor initializes all the member variables
	 * and sets the display string to show that this is a reconnect
	 * popup.
	 * @param _window PApplet window.
	 */
	public ReconnectPopup(PApplet _window)
	{
		super(_window);
		super.baseString = "Reconnecting";
	}
}
