package sketchupblocks.gui;

import processing.core.PApplet;

public class ReconnectPopup extends ConnectingPopup 
{
	public ReconnectPopup(PApplet _window)
	{
		super(_window);
		super.baseString = "Reconnecting";
	}
}
