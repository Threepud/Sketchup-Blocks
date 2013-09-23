package sketchupblocks.gui;

import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;

public class Menu 
{
	private SessionManager sessMan;
	private PApplet window;
	
	//Calibration
	private boolean[] calibratedCams;
	private boolean calibrated;
	
	private ArrayList<Popup> displayList;
	
	public Menu(SessionManager _sessMan, PApplet _window)
	{
		sessMan = _sessMan;
		window = _window;
		
		//calibration
		calibratedCams = new boolean[Settings.numCameras];
		for(int x = 0; x < calibratedCams.length; ++x)
			calibratedCams[x] = false;
		
		displayList = new ArrayList<>();
		displayList.add(new CalibratePopup(window));
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
		if(calibrated)
		{
			switch(cBlock.type)
			{
				case EXPORT:
					if(cEvent.type == CameraEvent.EVENT_TYPE.ADD)
					{
						if(sessMan.checkModelExists())
						{
							//add to display list
						}
						else
						{
							//show warning message
						}
					}
					else if(cEvent.type == CameraEvent.EVENT_TYPE.REMOVE)
					{
						//remove from display list
					}
					break;
				case SPECTATE:
					break;
				default:
					System.err.println("Command not yet supported.");
			}
		}
	}
	
	public void checkCalibrated()
	{
		
	}
	
	public void reconnectingPopup(boolean status)
	{
		
	}
	
	public void connectingPopup(boolean status)
	{
		
	}
	
	public void drawMenuOverlay()
	{
		window.camera();
		window.perspective();
		window.noLights();
		window.hint(PConstants.DISABLE_DEPTH_TEST);
		
		if(!displayList.isEmpty())
		{
			if(displayList.get(0) instanceof CalibratePopup)
			{
				
			}
			else if(displayList.get(0) instanceof ReconnectPopup)
			{
				
			}
		}
		
		window.hint(PConstants.ENABLE_DEPTH_TEST);
	}
}