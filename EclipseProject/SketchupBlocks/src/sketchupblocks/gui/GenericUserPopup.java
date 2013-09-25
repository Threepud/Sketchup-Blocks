package sketchupblocks.gui;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import sketchupblocks.base.Settings;

public class GenericUserPopup implements Popup 
{
	public boolean active;
	
	private PApplet window;
	
	protected String userMessage = "GENERIC";
	
	//core
	private long poisonStamp;
	public boolean died = false;
	
	//popup base
	private PFont headingFont;
	private PFont subFont;
	private int popupBaseWidth = 350;
	private int popupBaseHeight = 270;
	private float barRadius = 70;
	private int maxVel = Settings.progressBarRotationSpeed;
	private float[] currentVel;
	private int[] randomIndex;
	private int turn = 0;
	private int[][] randomColours = 
		{
			{255, 32, 0},
			{0, 255, 64},
			{0, 217, 255},
			{159, 0, 255},
			{254, 1, 159},
			{255, 134, 0}
		};
	
	public GenericUserPopup(PApplet _window)
	{
		window = _window;
		
		headingFont = window.createFont("Arial", 40, true);
		subFont = window.createFont("Arial", 30);
		
		currentVel = new float[3];
		for(int x = 0; x < 3; ++x)
			currentVel[x] = maxVel * 1.7f;
		
		Random genny = new Random();
		randomIndex = new int[3];
		randomIndex[0] = genny.nextInt(randomColours.length);
		randomIndex[1] = genny.nextInt(randomColours.length);
		while(randomIndex[1] == randomIndex[0])
			randomIndex[1] = genny.nextInt(randomColours.length);
		randomIndex[2] = genny.nextInt(randomColours.length);
		while(randomIndex[2] == randomIndex[1] || randomIndex[2] == randomIndex[0])
			randomIndex[2] = genny.nextInt(randomColours.length);
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
			if(System.currentTimeMillis() - poisonStamp > Settings.commandWaitTime)
			{
				died = true;
				return;
			}
			
			drawPopupBase();
			drawPopupHeader("Sketchup Blocks");
			drawPopupSubHeader(userMessage);
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
		
		window.strokeWeight(3);
		
		turn = (int)(System.currentTimeMillis() - poisonStamp) / 1000;
		for(int x = 0; x < 3; ++x)
		{
			window.stroke(randomColours[randomIndex[x]][0],
						  randomColours[randomIndex[x]][1], 
						  randomColours[randomIndex[x]][2]);
			
			//arcs 1
			if(x >= turn)
			{
				if(x == turn)
					currentVel[x] -= 5.0f;
				if(currentVel[x] < maxVel)
					currentVel[x] = maxVel;
				
				float angle = PConstants.PI * 2 * (float)(System.currentTimeMillis() - poisonStamp) / currentVel[x];
				float radius = (barRadius + (10 * x)) * (1 - (x == turn ? ((float)(System.currentTimeMillis() - poisonStamp) % (Settings.commandWaitTime / 3) / (float)(Settings.commandWaitTime / 3)) : 0));
				window.arc(window.width / 2, (window.height / 2) + 60, 
						   radius,
						   radius, 
					       x % 2 == 0 ? (float)((-PConstants.PI / 6.0) + angle) : (float)((-PConstants.PI / 6.0) - angle), 
						   x % 2 == 0 ? (float)((PConstants.PI / 6.0) + angle) : (float)((PConstants.PI / 6.0) - angle));
				window.arc(window.width / 2, (window.height / 2) + 60,
						   radius,
						   radius, 
						   x % 2 == 0 ? (float)((-PConstants.PI / 6.0) + angle + PConstants.PI) : (float)((-PConstants.PI / 6.0) - angle - PConstants.PI), 
						   x % 2 == 0 ? (float)((PConstants.PI / 6.0) + angle + PConstants.PI) : (float)((PConstants.PI / 6.0) - angle - PConstants.PI));
			}
		}
		
		window.strokeWeight(1);
	}
}
