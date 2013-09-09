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

	//sidebar
	private boolean slideDone = false;
	private boolean calibrated = false;
	private int sidebarWidth;
	private int sidebarHeight;
	private int slide;
	private PShape[] camBases;
	private boolean[] calibratedCams;
	
	public Menu(SessionManager _sessMan, PApplet _window)
	{
		sessMan = _sessMan;
		window = _window;
		
		splashImage = window.loadImage("./images/SplashImage.png");
		splashBase = window.createShape(PConstants.RECT, 0, 0, window.width, window.height);
		
		headingFont = window.createFont("Arial", 40, true);
		subFont = window.createFont("Arial", 30);
		
		int offset = 10;
		int quadSize = 40;
		sidebarWidth = (offset * 3) + quadSize;
		sidebarHeight = (offset * 2) + (Settings.numCameras * quadSize) + (Settings.numCameras * offset);
		camBases = new PShape[Settings.numCameras];
		for(int x = 0; x < camBases.length; ++x)
		{
			camBases[x] = window.createShape
			(
				PConstants.RECT, 
				offset, 
				offset + (x * quadSize) + (x * offset), 
				quadSize, 
				quadSize
			);
		}
	}
	
	public void updateCalibratedCameras(boolean[] _calibrated)
	{
		calibratedCams = _calibrated;
		
		for(boolean bool: calibratedCams)
		{
			if(!bool)
				return;
		}
		calibrated = true;
	}
	
	public void handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		switch(cBlock.type)
		{
			case EXPORT:
				break;
			case SPECTATE:
				break;
			default:
				System.err.println("Command not yet supported.");
		}
	}
	
	public void drawMenuOverlay()
	{
		window.camera();
		window.noLights();
		window.hint(PConstants.DISABLE_DEPTH_TEST);
		
		drawSidebar();
		drawPopup();
		drawSplash();
		
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
		
			//draw popup base
			window.fill(255);
			window.noStroke();
			window.rectMode(PConstants.CENTER);
			window.rect(window.width / 2, window.height / 2, 400, 280, 5);
			
			//draw popup outline
			window.noFill();;
			window.stroke(100);
			window.rectMode(PConstants.CENTER);
			window.rect(window.width / 2, window.height / 2, 390, 270, 5);
			
			//draw text
			window.stroke(200);
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
	
	public void drawSidebar()
	{
		if(!slideDone)
		{
			if(calibrated)
			{
				if(-slide > sidebarWidth)
					slideDone = true;
				else
					slide--;
			}
			
			//draw sidebar base
			window.fill(255);
			window.noStroke();
			window.rectMode(PConstants.CENTER);
			window.rect(slide + (sidebarWidth / 2) - 10, (sidebarHeight / 2) - 10, sidebarWidth, sidebarHeight, 5);
			
			if(!calibrated)
			{
				for(int x = 0; x < camBases.length; ++x)
				{
					PShape quad = camBases[x];
					
					//find out which cameras have been calibrated (calibratedCams)
					quad.setFill(window.color(255));
					quad.setStroke(window.color(200));
					window.shape(quad);
					
					window.stroke(100);
					window.fill(0);
					window.text((x + 1), quad.getVertexX(0), quad.getVertexY(0) + 10);
				}
			}
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