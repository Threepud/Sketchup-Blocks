package sketchupblocks.gui;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public class GenericWarningPopup implements Popup 
{
	public boolean active;
	
	private PApplet window;
	
	protected String warningMessage = "GENERIC";
	
	//core
	private long ttl = 2000;
	private long poisonStamp;
	public boolean died = false;
	
	//popup base
	private PFont headingFont;
	private PFont subFont;
	private int popupBaseWidth = 350;
	private int popupBaseHeight = 270;
	private float moveSpeed = 0.5f;
	
	//cogs
	private int reach = 50;
	private int min; 
	private int max;
	private int thingyCount = 15;
	private float[] topPositions;
	private float[] botPositions;
	
	public GenericWarningPopup(PApplet _window)
	{
		window = _window;
		
		headingFont = window.createFont("Arial", 40, true);
		subFont = window.createFont("Arial", 30);
		
		min = (window.width / 2) - (popupBaseWidth / 2);
		max = (window.width / 2) + (popupBaseWidth / 2) - 25;
		
		topPositions = new float[thingyCount];
		for(int x = 0; x < thingyCount; ++x)
			topPositions[x] = (min - 25) + (30 * x);
		botPositions = new float[thingyCount];
		for(int x = 0; x < thingyCount; ++x)
			botPositions[x] = max - (30 * x);
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
			if(System.currentTimeMillis() - poisonStamp > ttl)
			{
				died = true;
				return;
			}
			
			drawPopupBase();
			drawPopupHeader("WARNING");
			drawPopupSubHeader(warningMessage);
			drawWarningBars();
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
		window.text(message, window.width / 2, (window.height / 2) - 20);
	}
	
	private void drawPopupSubHeader(String message)
	{
		window.fill(255);
		window.textFont(subFont);
		window.textAlign(PConstants.CENTER);
		window.text(message, window.width / 2, (window.height / 2) + 40);
	}
	
	private void drawWarningBars()
	{
		drawBar1();
		drawBar2();
	}
	
	private void drawBar1()
	{
		window.fill(247, 254, 0);
		window.noStroke();
		window.rectMode(PConstants.CENTER);
		window.rect(window.width / 2, (window.height / 2) - (popupBaseHeight / 2) + 30, popupBaseWidth, 20);
		
		for(int x = 0; x < thingyCount; ++x)
		{	
			topPositions[x] += moveSpeed;
			if(topPositions[x] > min + 425)
				topPositions[x] = min - 25;
				
			drawThingy((int)topPositions[x], (window.height / 2) - (popupBaseHeight / 2));
		}
	}
	
	private void drawBar2()
	{
		window.fill(247, 254, 0);
		window.noStroke();
		window.rectMode(PConstants.CENTER);
		window.rect(window.width / 2, (window.height / 2) + (popupBaseHeight / 2) - 30, popupBaseWidth, 20);
		
		for(int x = 0; x < thingyCount; ++x)
		{	
			botPositions[x] -= moveSpeed;
			if(botPositions[x] < max - 425)
				botPositions[x] = max + 25;
				
			drawThingy((int)botPositions[x], (window.height / 2) + (popupBaseHeight / 2) - 20);
		}
	}
	
	private void drawThingy(int x, int y)
	{
		//check transparency
		int ratio = 0;
		if(x < min)
			ratio = 0;
		else if(x > min + reach && x < max - reach)
			ratio = 255;
		else if(x > max)
			ratio = 0;
		else if(x >= min && x <= min + reach)
		{
			int temp = x - min;
			ratio = Math.round(255.0f * ((float)temp / (float)reach));
		}
		else if(x <= max && x >= max - reach)
		{
			int temp = x - (max - reach);
			ratio = Math.round(255.0f * (1.0f - ((float)temp / (float)reach)));
		}
		
		window.fill(0, 0, 0, ratio);
		
		window.beginShape();
		
		window.vertex(x + 0, y + 20);
		window.vertex(x + 5, y + 0);
		window.vertex(x + 20, y + 0);
		window.vertex(x + 15, y + 20);
		
		window.endShape(PConstants.CLOSE);
	}
}
