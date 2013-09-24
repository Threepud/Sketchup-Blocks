package sketchupblocks.gui;

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
	
	public GenericUserPopup(PApplet _window)
	{
		window = _window;
		
		headingFont = window.createFont("Arial", 40, true);
		subFont = window.createFont("Arial", 30);
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
		
		//get random colour
		window.stroke(randomColours[randomIndex][0],
					  randomColours[randomIndex][1], 
					  randomColours[randomIndex][2]);
		window.strokeWeight(3);
		float angle = PConstants.PI * 2 * 
				(float)(System.currentTimeMillis() - poisonStamp) / (float)rotationSpeed;
		
		//arcs 1
		window.arc(window.width / 2, (window.height / 2) + 60, 
				   barRadius * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)),
				   barRadius * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)), 
			       (float)((-PConstants.PI / 6.0) + angle), 
				   (float)((PConstants.PI / 6.0) + angle));
		window.arc(window.width / 2, (window.height / 2) + 60,
				   barRadius * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)),
				   barRadius * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)), 
				   (float)((-PConstants.PI / 6.0) + angle + PConstants.PI), 
				   (float)((PConstants.PI / 6.0) + angle + PConstants.PI));
		
		//arcs2
		window.arc(window.width / 2, (window.height / 2) + 60, 
				   (barRadius + 1) * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)),
				   (barRadius + 1) * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)), 
			       (float)((-PConstants.PI / 6.0) - angle), 
				   (float)((PConstants.PI / 6.0) - angle));
		window.arc(window.width / 2, (window.height / 2) + 60,
				   (barRadius + 1) * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)),
				   (barRadius + 1) * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)), 
				   (float)((-PConstants.PI / 6.0) - angle + PConstants.PI), 
				   (float)((PConstants.PI / 6.0) - angle + PConstants.PI));
		
		//arcs2
		window.arc(window.width / 2, (window.height / 2) + 60, 
				   (barRadius + 2) * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)),
				   (barRadius + 2) * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)), 
			       (float)((-PConstants.PI / 6.0) + angle), 
				   (float)((PConstants.PI / 6.0) + angle));
		window.arc(window.width / 2, (window.height / 2) + 60,
				   (barRadius + 2) * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)),
				   (barRadius + 2) * (1 - ((float)(System.currentTimeMillis() - poisonStamp) / (float)Settings.commandWaitTime)), 
				   (float)((-PConstants.PI / 6.0) + angle + PConstants.PI), 
				   (float)((PConstants.PI / 6.0) + angle + PConstants.PI));
		
		window.strokeWeight(1);
	}
}
