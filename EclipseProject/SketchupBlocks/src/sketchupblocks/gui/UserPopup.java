package sketchupblocks.gui;

import processing.core.PApplet;

public class UserPopup extends GenericUserPopup 
{
	public UserPopup(PApplet _window, String userMessage)
	{
		super(_window);
		super.userMessage = userMessage;
	}
}
