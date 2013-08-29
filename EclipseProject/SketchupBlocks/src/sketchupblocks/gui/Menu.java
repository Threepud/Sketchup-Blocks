package sketchupblocks.gui;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PShape;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;

public class Menu 
{
	private SessionManager sessMan;
	private PApplet window;
	
	//splash screen
	private boolean splashShown = false;
	private long splashStart = -1;
	private PImage splashImage;
	private PShape splashBase;
	
	//popup
	private boolean showPopup = false;
	private PFont headingFont;
	private PFont subFont;
	private long popupStart = -1;
	private float barRadius = 70;
	//TODO: make configurable in settings ?
	private int rotationSpeed = Settings.progressBarRotationSpeed;
	private int randomIndex = -1;
	private int[][] randomColours = 
		{
			{255, 32, 0},
			{0, 255, 64},
			{0, 217, 255},
			{159, 0, 255},
			{254, 1, 159},
			{255, 134, 0}
		};
	
	public Menu(SessionManager _sessMan, PApplet _window)
	{
		sessMan = _sessMan;
		window = _window;
		
		splashImage = window.loadImage("./images/SplashImage.png");
		splashBase = window.createShape(PConstants.RECT, 0, 0, window.width, window.height);
		
		headingFont = window.createFont("Arial", 40, true);
		subFont = window.createFont("Arial", 30);
	}
	
	public void handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		
	}
	
	public void drawMenuOverlay()
	{
		window.camera();
		window.noLights();
		window.hint(PConstants.DISABLE_DEPTH_TEST);
		
		drawSplash();
		drawPopup();
		
		window.hint(PConstants.ENABLE_DEPTH_TEST);
	}
	
	private void drawSplash()
	{
		//draw splash screen
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
	}
	
	private void drawPopup()
	{
		//draw pop-up
		if(showPopup)
		{
			if(popupStart == -1)
			{
				popupStart = System.currentTimeMillis();
				
				Random numGenny = new Random(System.currentTimeMillis());
				int temp = -1;
				while(temp == randomIndex || temp == -1)
				{
					temp = numGenny.nextInt(randomColours.length);
				}
				randomIndex = temp;
			}
			else if(System.currentTimeMillis() - popupStart > Settings.commandWaitTime)
			{
				popupStart = -1;
				//TODO: fire command
			}
		
			window.fill(255);
			window.noStroke();
			window.rectMode(PConstants.CENTER);
			window.rect(window.width / 2, window.height / 2, 400, 280, 5);
			
			window.fill(0);
			window.textFont(headingFont);
			window.textAlign(PConstants.CENTER);
			window.text("Sketchup Blocks", window.width / 2, (window.height / 2) - 80);
			
			window.textFont(subFont);
			String popupString = "Exporting";//getPopupString("Exporting");
			window.text(popupString, window.width / 2, (window.height / 2) - 20);
			
			drawProgressBar();
		}
	}
	
	private void drawProgressBar()
	{
		//draw progress bar
		window.noFill();
		
		//get random colour
		window.stroke(randomColours[randomIndex][0],
					  randomColours[randomIndex][1], 
					  randomColours[randomIndex][2]);
		window.strokeWeight(3);
		float angle = PConstants.PI * 2 * 
				(float)(System.currentTimeMillis() - popupStart) / (float)rotationSpeed;
		window.arc(window.width / 2, (window.height / 2) + 60, 
				   barRadius * (1 - ((float)(System.currentTimeMillis() - popupStart) / (float)Settings.commandWaitTime)),
				   barRadius * (1 - ((float)(System.currentTimeMillis() - popupStart) / (float)Settings.commandWaitTime)), 
			       (float)((-PConstants.PI / 6.0) + angle), 
				   (float)((PConstants.PI / 6.0) + angle));
		window.arc(window.width / 2, (window.height / 2) + 60,
				   barRadius * (1 - ((float)(System.currentTimeMillis() - popupStart) / (float)Settings.commandWaitTime)),
				   barRadius * (1 - ((float)(System.currentTimeMillis() - popupStart) / (float)Settings.commandWaitTime)), 
				   (float)((-PConstants.PI / 6.0) + angle + PConstants.PI), 
				   (float)((PConstants.PI / 6.0) + angle + PConstants.PI));
	}
}