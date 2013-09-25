package sketchupblocks.gui;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.base.Logger;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;

public class Menu 
{
	private SessionManager sessMan;
	private PApplet window;
	
	//Network
	private boolean networkUpdate = false;
	private boolean networkStatus = false;
	
	//Calibration
	private boolean[] calibratedCams;
	private boolean calibrated = false;
	
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
		
		if(Settings.showSplash)
			displayList.add(new SplashPopup(window));
		
		displayList.add(new CalibratePopup(window));
	}
	
	private void updateCalibratedCameras()
	{
		for(int x = 0; x < calibratedCams.length; ++x)
		{
			calibratedCams[x] = RuntimeData.isCameraCalibrated(x);
		}
		
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
							if(!displayList.isEmpty())
							{
								if(displayList.get(displayList.size() - 1) instanceof UserPopup)
								{
									if(((UserPopup)displayList.get(displayList.size() - 1)).userMessage.equals("Export"))
									{
										return;
									}
								}
							}
							
							displayList.add(new UserPopup(window, "Export"));
						}
						else
						{
							displayList.add(new WarningPopup(window, "No Model Found"));
						}
					}
					else if(cEvent.type == CameraEvent.EVENT_TYPE.REMOVE)
					{
						if(!displayList.isEmpty())
						{
							if(displayList.get(displayList.size() - 1) instanceof UserPopup)
							{
								if(((UserPopup)displayList.get(displayList.size() - 1)).userMessage.equals("Export"))
								{
									displayList.remove(displayList.size() - 1);
								}
							}
						}
					}
					break;
				case SPECTATE:
					if(cEvent.type == CameraEvent.EVENT_TYPE.ADD)
					{
						if(!displayList.isEmpty())
						{
							if(displayList.get(displayList.size() - 1) instanceof UserPopup)
							{
								if(((UserPopup)displayList.get(displayList.size() - 1)).userMessage.equals("Spectate"))
								{
									return;
								}
							}
						}
						
						displayList.add(new UserPopup(window, "Spectate"));
					}
					else if(cEvent.type == CameraEvent.EVENT_TYPE.REMOVE)
					{
						if(!displayList.isEmpty())
						{
							if(displayList.get(displayList.size() - 1) instanceof UserPopup)
							{
								if(((UserPopup)displayList.get(displayList.size() - 1)).userMessage.equals("Spectate"))
								{
									displayList.remove(displayList.size() - 1);
								}
							}
						}
					}
					break;
				default:
					Logger.log("Unsupported command", 1);
			}
		}
	}
	
	public void checkCalibrated()
	{
		if(!RuntimeData.isSystemCalibrated())
		{
			displayList.add(new CalibratePopup(window));
		}
	}
	
	public void forceStopConnectPopup()
	{
		if(!displayList.isEmpty())
		{
			if(displayList.get(0) instanceof ConnectingPopup)
			{
				displayList.remove(0);
			}
		}
	}
	
	public void createConnectPopup()
	{
		if(!displayList.isEmpty())
		{
			if(displayList.get(0) instanceof CalibratePopup)
			{
				displayList.remove(0);
			}
		}
		
		displayList.add(new ConnectingPopup(window));
	}
	
	public void createReconnectPopup()
	{
		displayList.add(new ReconnectPopup(window));
	}
	
	public void updateNetworkStatus(boolean status)
	{
		networkUpdate = true;
		networkStatus = status;
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
				CalibratePopup c = (CalibratePopup)displayList.get(0);
				if(!c.active)
				{
					c.activate();
				}
				else
				{
					if(!c.died)
					{
						updateCalibratedCameras();
						
						c.updateCalibratedCameras(calibratedCams);
						if(calibrated)
						{
							c.feedPoison();
						}
						
						c.draw();
					}
					else
						displayList.remove(0);
				}
			}
			else if(displayList.get(0) instanceof ReconnectPopup)
			{
				ReconnectPopup c = (ReconnectPopup)displayList.get(0);
				if(!c.active)
				{
					c.activate();
				}
				else
				{
					if(!c.died)
					{
						if(networkUpdate)
						{
							c.setStatus(networkStatus);
							c.feedPoison();
							
							networkUpdate = false;
						}
						
						c.draw();
					}
					else
						displayList.remove(0);
				}
			}
			else if(displayList.get(0) instanceof ConnectingPopup)
			{
				ConnectingPopup c = (ConnectingPopup)displayList.get(0);
				if(!c.active)
				{
					c.activate();
				}
				else
				{
					if(!c.died)
					{
						if(networkUpdate)
						{
							c.setStatus(networkStatus);
							c.feedPoison();
							
							networkUpdate = false;
						}
						
						c.draw();
					}
					else
						displayList.remove(0);
				}
			}
			else if(displayList.get(0) instanceof UserPopup)
			{
				UserPopup c = (UserPopup)displayList.get(0);
				if(!c.active)
				{
					c.activate();
					c.feedPoison();
				}
				else
				{
					if(!c.died)
						c.draw();	
					else
						displayList.remove(0);
				}
			}
			else if(displayList.get(0) instanceof SplashPopup)
			{
				SplashPopup c = (SplashPopup)displayList.get(0);
				if(!c.active)
				{
					c.activate();
					c.feedPoison();
				}
				else
				{
					if(!c.died)
						c.draw();
					else
						displayList.remove(0);
				}
			}
			else if(displayList.get(0) instanceof WarningPopup)
			{
				WarningPopup c = (WarningPopup)displayList.get(0);
				if(!c.active)
				{
					c.activate();
					c.feedPoison();
				}
				else
				{
					if(!c.died)
						c.draw();
					else
						displayList.remove(0);
				}
			}
		}
		
		window.hint(PConstants.ENABLE_DEPTH_TEST);
	}
}