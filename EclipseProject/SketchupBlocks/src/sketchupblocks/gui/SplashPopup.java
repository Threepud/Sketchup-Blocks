package sketchupblocks.gui;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import sketchupblocks.base.Settings;

/**
 * @author Jacques Coetzee
 * This class implements the popup interface.
 * This popup is really a overridden version of the popup
 * where is actually displays a splash screen, but the splash screen
 * still has the same mechanics as the rest of the popups.
 */
public class SplashPopup implements Popup 
{
	public boolean active;
	
	private PApplet window;
	
	//core
	private long poisonStamp;
	public boolean died = false;
	
	//splash
	private PImage splashImage;
	private PShape splashBase;
	
	/**
	 * This constructor initializes the member variables
	 * of the class.
	 * @param _window PApplet window.
	 */
	public SplashPopup(PApplet _window)
	{
		window = _window;
		
		splashImage = window.loadImage("./images/SplashImage.png");
		splashBase = window.createShape(PConstants.RECT, 0, 0, window.width, window.height);
	}
	
	/* (non-Javadoc)
	 * @see sketchupblocks.gui.Popup#activate()
	 */
	@Override
	public void activate() 
	{
		active = true;
	}

	/* (non-Javadoc)
	 * @see sketchupblocks.gui.Popup#feedPoison()
	 */
	@Override
	public void feedPoison() 
	{
		poisonStamp = System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see sketchupblocks.gui.Popup#draw()
	 */
	@Override
	public void draw() 
	{
		if(active)
		{
			if(System.currentTimeMillis() - poisonStamp > Settings.splashTTL)
			{
				died = true;
				return;
			}
			
			drawSplash();
		}
	}
	
	/**
	 * This function draws the splash graphical component
	 * of the popup.
	 */
	private void drawSplash()
	{	
		splashBase.setTexture(splashImage);
		splashBase.setTextureMode(PConstants.NORMAL);
		splashBase.setTextureUV(0, window.width, window.height);
		window.shape(splashBase);
	}
}
