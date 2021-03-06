package sketchupblocks.gui;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PVector;

/**
 * @author Jacques Coetzee
 * This class implements the Popup interface.
 * This popup is displayed to show the network
 * connection status of the system
 */
public class ConnectingPopup implements Popup 
{
	public boolean active;
	
	private PApplet window;
	
	protected String baseString = "Connecting";
	
	//core
	private long ttl = 3000;
	private long poisonStamp;
	private boolean poisonFed = false;
	private boolean success = false;
	public boolean died = false;
	
	//popup base
	private PFont headingFont;
	private int popupBaseWidth = 350;
	private int popupBaseHeight = 270;
	private int[][] randomColours = 
		{
			{255, 32, 0},
			{0, 255, 64},
			{0, 217, 255},
			{159, 0, 255},
			{254, 1, 159},
			{255, 134, 0}
		};
	
	//particles
	private PVector[] currentLocation;
	private PVector[] targetLocation;
	private PVector[] currentDirection;
	private PVector[] targetDirection;
	private boolean[] checkpoints;
	private int[] velocity;
	int particleCount = 300;
	private int[] colourIndex;
	private int[] whiteOffset;
	
	/**
	 * This constructor initializes all necessary member
	 * variables like the particle swarm positions.
	 * @param _window PApplet window.
	 */
	public ConnectingPopup(PApplet _window)
	{
		active = false;
		window = _window;
		
		headingFont = window.createFont("Arial", 40, true);
		
		//Particle Swarm
		Random ranGenny = new Random();
		currentLocation = new PVector[particleCount];
		currentDirection = new PVector[particleCount];
		targetDirection = new PVector[particleCount];
		colourIndex = new int[particleCount];
		whiteOffset = new int[particleCount];
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
			if(3 < randomColours.length)
				colourIndex[x] = x % 3;
			else
				colourIndex[x] = x % randomColours.length;
			
			whiteOffset[x] = ranGenny.nextInt(50);
			
			checkpoints[x] = ranGenny.nextBoolean();
		}
		targetLocation = new PVector[3];
		targetLocation[0] = new PVector(window.width / 2, window.height / 2);
		targetLocation[1] = new PVector(targetLocation[0].x - 70, window.height / 2);
		targetLocation[2] = new PVector(targetLocation[0].x + 70, window.height / 2);
		
		velocity = new int[3];
		int vel = 2;
		for(int x = 0; x < velocity.length; ++x)
		{
			velocity[x] = vel;
			vel += 1;
		}
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
		if(!poisonFed)
		{
			poisonStamp = System.currentTimeMillis();
			poisonFed = true;
		}
	}
	
	/**
	 * This function updates the ultimate network
	 * connection status.
	 * @param status Connection status.
	 */
	public void setStatus(boolean status)
	{
		success = status;
	}
	
	/* (non-Javadoc)
	 * @see sketchupblocks.gui.Popup#draw()
	 */
	@Override
	public void draw()
	{
		if(active)
		{
			if(poisonFed)
			{
				if(System.currentTimeMillis() - poisonStamp > ttl)
				{
					died = true;
					return;
				}
			}
			
			drawPopupBase();
			
			if(poisonFed && success)
				drawPopupHeader("Connected");
			else if(poisonFed && !success)
			{
				if(headingFont.getSize() == 40)
					headingFont = window.createFont("Arial", 30, true);
				drawPopupHeader("Connection Failed");
			}
			else if(!poisonFed)
				drawPopupHeader(baseString);
			
			drawParticles();
		}
	}
	
	/**
	 * This function draws the base graphical component of 
	 * the popup.
	 */
	private void drawPopupBase()
	{
		window.fill(0, 0, 0, 200);
		window.noStroke();
		window.rectMode(PConstants.CENTER);
		window.rect(window.width / 2, window.height / 2, popupBaseWidth, popupBaseHeight);
	}
	
	/**
	 * This function draws a popup heading with the given string
	 * message.
	 * @param message String message.
	 */
	private void drawPopupHeader(String message)
	{
		window.fill(255);
		window.textFont(headingFont);
		window.textAlign(PConstants.CENTER);
		window.text(message, window.width / 2, (window.height / 2) - 80);
	}
	
	/**
	 * This function draws the particle swarm graphical component.
	 */
	private void drawParticles()
	{
		window.noStroke();
		
		for(int x = 0; x < particleCount; ++x)
		{
			if(poisonFed && !success)
			{
				if(x % 2 == 0)
					window.fill(0);
				else
					window.fill(255, 0, 0);
			}
			else if(poisonFed && success)
			{
				window.fill
				(
						randomColours[colourIndex[x]][0],
						randomColours[colourIndex[x]][1],
						randomColours[colourIndex[x]][2]
				);
			}
			else if(!poisonFed)
				window.fill(255 - whiteOffset[x]);
			
			//get target direction
			PVector target = null;
			if(checkpoints[x])
				target = targetLocation[1];
			else
				target = targetLocation[2];
			
			//check distance if connecting
			if(PVector.dist(target, currentLocation[x]) < 5.0f)
				checkpoints[x] = !checkpoints[x];
			
			targetDirection[x] = PVector.sub(target, currentLocation[x]);
			targetDirection[x].normalize();
			
			//update current direction
			currentDirection[x] = PVector.add(currentDirection[x], targetDirection[x]);
			if(poisonFed && !success)
			{
				PVector noise = PVector.random2D();
				noise.mult(2.0f);
				currentDirection[x].add(noise);
			}
			else if(!poisonFed)
			{
				PVector noise = PVector.random2D();
				noise.mult(0.5f);
				currentDirection[x].add(noise);
			}
			
				
			currentDirection[x].normalize();
			currentDirection[x].mult(2);
			currentLocation[x] = PVector.add(currentLocation[x], currentDirection[x]);
			
			window.ellipse(currentLocation[x].x, currentLocation[x].y, 5, 5);
		}
	}
}
