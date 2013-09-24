package sketchupblocks.gui;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import sketchupblocks.base.Settings;

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
	
	public SplashPopup(PApplet _window)
	{
		window = _window;
		
		splashImage = window.loadImage("./images/SplashImage.png");
		splashBase = window.createShape(PConstants.RECT, 0, 0, window.width, window.height);
	}
	
	@Override
	public void activate() 
	{
		active = true;
	}

	@Override
	public void feedPoison() 
	{
		poisonStamp = System.currentTimeMillis();
	}

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
	
	private void drawSplash()
	{	
		splashBase.setTexture(splashImage);
		splashBase.setTextureMode(PConstants.NORMAL);
		splashBase.setTextureUV(0, window.width, window.height);
		window.shape(splashBase);
	}
}
