package sketchupblocks.gui;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.event.KeyEvent;
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
	private boolean showNoticePopup = true;
	private boolean calibratePopup = true;
	
	private boolean connectPopup = false;
	private boolean connectionEstablished = false;
	private boolean connectionFailed = false;
	
	private boolean showLoadingPopup = false;
	//TODO: implement loading rings
	private boolean exportPopup = false;
	
	private PFont headingFont;
	private PFont subFont;
	private int popupBaseWidth = 350;
	private int popupBaseHeight = 270;
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
	
	//Calibration
	private boolean[] calibratedCams;
	private boolean calibrated = false;
	private long calibratePopupTTL = 3500;
	private long noticeStartTime;

	//Particle Swarm
	private PVector[] currentLocation;
	private PVector[] targetLocation;
	private PVector[] currentDirection;
	private PVector[] targetDirection;
	private boolean[] checkpoints;
	private int[] velocity;
	int particleCount = 100 * Settings.numCameras;
	private int[] colourIndex;
	
	public Menu(SessionManager _sessMan, PApplet _window)
	{
		sessMan = _sessMan;
		window = _window;
		
		//calibration
		calibratedCams = new boolean[Settings.numCameras];
		for(int x = 0; x < calibratedCams.length; ++x)
			calibratedCams[x] = false;
		
		//splash screen
		splashImage = window.loadImage("./images/SplashImage.png");
		splashBase = window.createShape(PConstants.RECT, 0, 0, window.width, window.height);
		
		headingFont = window.createFont("Arial", 40, true);
		subFont = window.createFont("Arial", 30);
		
		//Particle Swarm
		Random ranGenny = new Random();
		currentLocation = new PVector[particleCount];
		currentDirection = new PVector[particleCount];
		targetDirection = new PVector[particleCount];
		colourIndex = new int[particleCount];
		checkpoints = new boolean[particleCount];
		for(int x = 0; x < particleCount; ++x)
		{
			currentLocation[x] = new PVector
			(
					ranGenny.nextInt(popupBaseWidth / 2) + (window.width / 2) - (popupBaseWidth / 4),
					ranGenny.nextInt(popupBaseHeight / 2) + (window.height / 2) - (popupBaseHeight / 4)
			);
			currentDirection[x] = new PVector(ranGenny.nextFloat(), ranGenny.nextFloat());
			targetDirection[x] = new PVector();
			if(Settings.numCameras < randomColours.length)
				colourIndex[x] = x % Settings.numCameras;
			else
				colourIndex[x] = x % randomColours.length;
			
			checkpoints[x] = ranGenny.nextBoolean();
		}
		targetLocation = new PVector[3];
		targetLocation[0] = new PVector(window.width / 2, window.height / 2);
		targetLocation[1] = new PVector(targetLocation[0].x - 70, window.height / 2);
		targetLocation[2] = new PVector(targetLocation[0].x + 70, window.height / 2);
		
		velocity = new int[Settings.numCameras];
		int vel = 2;
		for(int x = 0; x < velocity.length; ++x)
		{
			velocity[x] = vel;
			vel += 1;
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
		noticeStartTime = System.currentTimeMillis();
	}
	
	public void handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		if(calibrated)
		{
			switch(cBlock.type)
			{
				case EXPORT:
					if(cEvent.type == CameraEvent.EVENT_TYPE.ADD)
					{
						if(sessMan.checkModelExists())
						{
							exportPopup = true;
							showLoadingPopup = true;
						}
						else
						{
							//show warning message
						}
					}
					else if(cEvent.type == CameraEvent.EVENT_TYPE.REMOVE)
					{
						showLoadingPopup = false;
						exportPopup = false;
						popupStart = -1;
					}
					break;
				case SPECTATE:
					break;
				default:
					System.err.println("Command not yet supported.");
			}
		}
	}
	
	public void connectingPopup(boolean status)
	{
		//try connecting
		if(!connectPopup)
		{
			if(calibratePopup)
				calibratePopup = false;
			else
				showNoticePopup = true;
			
			connectPopup = true;
		}
		else
		{
			//connection unsuccessful
			if(!status)
				connectionFailed = true;
			//connection successful
			else
				connectionEstablished = true;
			
			noticeStartTime = System.currentTimeMillis();
		}
	}
	
	public void drawMenuOverlay()
	{
		window.camera();
		window.perspective();
		window.noLights();
		window.hint(PConstants.DISABLE_DEPTH_TEST);
		
		drawLoadingPopup();
		drawNoticePopup();
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
	
	private void drawNoticePopup()
	{
		if(showNoticePopup)
		{
			if(calibrated || connectionEstablished || connectionFailed)
			{
				if(System.currentTimeMillis() - noticeStartTime > calibratePopupTTL)
				{
					showNoticePopup = false;
					
					if(calibratePopup)
					{
						calibratePopup = false;
					}
					else if(connectionEstablished || connectionFailed)
					{
						connectPopup = false;
						connectionEstablished = false;
						connectionFailed = false;
					}
					
					popupStart = -1;
					return;
				}
			}
			
			drawPopupBase();
			
			if(calibratePopup)
				drawPopupHeader("Calibrating");
			else if(connectPopup && !connectionEstablished && connectionFailed)
				drawPopupHeader("Connection Failed");
			else if(connectPopup && !connectionEstablished)
				drawPopupHeader("Connecting");
			else if(connectPopup && connectionEstablished)
				drawPopupHeader("Connected");
			
			drawParticles();
		}
	}
	 
	private void drawParticles()
	{
		window.noStroke();
		
		for(int x = 0; x < particleCount; ++x)
		{
			if(calibratePopup)
			{
				window.fill
				(
						randomColours[colourIndex[x]][0],
						randomColours[colourIndex[x]][1],
						randomColours[colourIndex[x]][2]
				);
			}
			else if(connectPopup && !connectionEstablished && connectionFailed)
			{
				if(x % 2 == 0)
					window.fill(0);
				else
					window.fill(255, 0, 0);
			}
			else if(connectPopup && !connectionEstablished)
				window.fill(255);
			else if(connectPopup && connectionEstablished)
			{
				window.fill
				(
						randomColours[colourIndex[x]][0],
						randomColours[colourIndex[x]][1],
						randomColours[colourIndex[x]][2]
				);
			}
			
			//get target direction
			PVector target = null;
			if(calibratePopup)
				target = targetLocation[0];
			else if(connectPopup)
			{
				if(checkpoints[x])
					target = targetLocation[1];
				else
					target = targetLocation[2];
			}
			else
				return;
			
			//check distance if connecting
			if(connectPopup)
			{
				if(PVector.dist(target, currentLocation[x]) < 5.0f)
					checkpoints[x] = !checkpoints[x];
			}
			
			targetDirection[x] = PVector.sub(target, currentLocation[x]);
			targetDirection[x].normalize();
			
			//update current direction
			currentDirection[x] = PVector.add(currentDirection[x], targetDirection[x]);
			if(!calibratedCams[x % Settings.numCameras] || (connectPopup && !connectionEstablished))
			{
				PVector noise = PVector.random2D();
				if(connectPopup)
					noise.mult(0.5f);
				else
					noise.mult(1.4f);
				currentDirection[x].add(noise);
			}
			currentDirection[x].normalize();
			
			if(connectPopup)
				currentDirection[x].mult(2);
			else if(calibratedCams[x % Settings.numCameras])
				currentDirection[x].mult(velocity[x % Settings.numCameras]);
			else
				currentDirection[x].mult(3);
			
			currentLocation[x] = PVector.add(currentLocation[x], currentDirection[x]);
			
			window.ellipse(currentLocation[x].x, currentLocation[x].y, 5, 5);
		}
	}
	
	private void drawLoadingPopup()
	{
		//draw pop-up
		if(showLoadingPopup)
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
				showLoadingPopup = false;
				
				if(exportPopup)
				{
					exportPopup = false;
					sessMan.exportToFile();
				}
				
				popupStart = -1;
				return;
			}
		
			drawPopupBase();
			
			drawPopupHeader("Sketchup Blocks");
			
			String message = "";
			if(exportPopup)
				message = "Export";
			else if(Settings.verbose >= 3)
				System.out.println("ERROR: Popup error, no type selected.");
				
			drawPopupSubHeader(message);
			drawProgressBar();
		}
	}
	
	private void drawPopupBase()
	{
		window.fill(0, 0, 0, 200);
		window.noStroke();
		window.rectMode(PConstants.CENTER);
		window.rect(window.width / 2, window.height / 2, popupBaseWidth, popupBaseHeight);
	}
	
	private void drawPopupHeader(String message)
	{
		window.fill(255);
		window.textFont(headingFont);
		window.textAlign(PConstants.CENTER);
		window.text(message, window.width / 2, (window.height / 2) - 80);
	}
	
	private void drawPopupSubHeader(String message)
	{
		window.fill(255);
		window.textFont(subFont);
		window.textAlign(PConstants.CENTER);
		window.text(message, window.width / 2, (window.height / 2) - 20);
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
		window.strokeWeight(1);
	}
}