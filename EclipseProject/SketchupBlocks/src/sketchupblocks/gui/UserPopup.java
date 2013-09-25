package sketchupblocks.gui;

import processing.core.PApplet;
import sketchupblocks.database.UserBlock;

public class UserPopup extends GenericUserPopup 
{
	public UserPopup(PApplet _window, String userHeader, String userMessage, Menu.UserTypes userType)
	{
		super(_window);
		super.userHeader = userHeader;
		super.userMessage = userMessage;
		super.userType = userType;
	}
	
	public UserPopup(PApplet _window, String userHeader, String userMessage, Menu.UserTypes userType, UserBlock uBlock)
	{
		super(_window);
		super.userHeader = userHeader;
		super.userMessage = userMessage;
		super.userType = userType;
		super.holdBlock = uBlock;
	}
}
