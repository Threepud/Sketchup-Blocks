package sketchupblocks.gui;

import processing.core.PApplet;
import sketchupblocks.database.UserBlock;

/**
 * @author Jacques Coetzee
 * This class extends the Generic User Popup
 * as a specialization of the genric user popups
 * where custom heading and sub headings can be set.
 */
public class UserPopup extends GenericUserPopup 
{
	/**
	 * The constructor initializes the member variables of its
	 * super class and sets the custom messages.
	 * @param _window PApplet window.
	 * @param userHeader Popup heading string.
	 * @param userMessage Popup sub-heading string.
	 * @param userType Popup type.
	 */
	public UserPopup(PApplet _window, String userHeader, String userMessage, Menu.UserTypes userType)
	{
		super(_window);
		super.userHeader = userHeader;
		super.userMessage = userMessage;
		super.userType = userType;
	}
	
	/**
	 * The constructor initializes the member variables of its
	 * super class and sets the custom messages.
	 * @param _window PApplet window.
	 * @param userHeader Popup heading string.
	 * @param userMessage Popup sub-heading string.
	 * @param userType Popup type.
	 * @param uBlock User block.
	 */
	public UserPopup(PApplet _window, String userHeader, String userMessage, Menu.UserTypes userType, UserBlock uBlock)
	{
		super(_window);
		super.userHeader = userHeader;
		super.userMessage = userMessage;
		super.userType = userType;
		super.holdBlock = uBlock;
	}
}
