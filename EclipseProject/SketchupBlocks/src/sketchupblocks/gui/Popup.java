package sketchupblocks.gui;

/**
 * @author Jacques Coetzee
 * Popup interface implemented by all the
 * popup objects.
 */
public interface Popup 
{
	/**
	 * This function activates the popup.
	 * When a popup is activated then it is 
	 * functional and all related graphical components
	 * of the popup are drawn.
	 */
	public void activate();
	
	/**
	 * This function starts a countdown until the
	 * popup expires. When the popup expires then 
	 * it will be removed from the drawList and 
	 * will no longer be drawn. 
	 */
	public void feedPoison();
	
	/**
	 * This function is responsible for drawing all
	 * relevant graphical componenets of the popup.
	 */
	public void draw();
}
