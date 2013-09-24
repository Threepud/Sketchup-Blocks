package sketchupblocks.gui;

import processing.core.PApplet;

public class WarningPopup extends GenericWarningPopup 
{
	public WarningPopup(PApplet window, String warningMessage)
	{
		super(window);
		super.warningMessage = warningMessage;
	}
}