package sketchupblocks.menu;

import java.sql.Date;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;

public class Menu 
{
	private MenuState state;
	private SessionManager sessMan;
	private PApplet window;
	
	private boolean splashShown = false;
	private long splashStart = -1;
	
	private PImage splashImage;
	private PShape splashBase;
	
	public Menu(SessionManager _sessMan, PApplet _window)
	{
		sessMan = _sessMan;
		window = _window;
		state = null;
		
		splashImage = window.loadImage("./images/SplashImage.png");
		splashBase = window.createShape(PConstants.RECT, 0, 0, window.width, window.height);
	}
	
	public void handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		if (state == null)
		{
			//Create correct state
			
		}
		else
		{
			state.handleInput(cBlock, cEvent);
		}
	}
	
	public void drawMenuOverlay()
	{
		window.camera();
		window.noLights();
		window.hint(PConstants.DISABLE_DEPTH_TEST);
		
		if(Settings.showSplash && !splashShown)
		{
			if(!splashShown && splashStart == -1)
				splashStart = System.currentTimeMillis();
			
			if(System.currentTimeMillis() - splashStart < Settings.splashTTL)
			{
				splashBase.setTexture(splashImage);
				splashBase.setTextureMode(PConstants.NORMAL);
				splashBase.setTextureUV(0, window.width, window.height);
				window.shape(splashBase);
			}
			else
				splashShown = true;
		}
		
		window.hint(PConstants.ENABLE_DEPTH_TEST);
	}
}
