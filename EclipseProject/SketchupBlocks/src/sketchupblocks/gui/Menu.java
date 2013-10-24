package sketchupblocks.gui;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;
import sketchupblocks.database.Block;
import sketchupblocks.database.CommandBlock;
import sketchupblocks.database.UserBlock;

/**
 * @author Jacques Coetzee
 * The menu class provides system feedback to the user
 * by displaying a 2D graphic overlay. This overlay is
 * drawn onto the 3D model viewer.
 */
public class Menu 
{
	public enum UserTypes
	{
		EXPORT,
		SPECTATE
	}
	
	private SessionManager sessMan;
	private PApplet window;
	
	//Network
	private boolean networkUpdate = false;
	private boolean networkStatus = false;
	
	//Calibration
	private boolean[] calibratedCams;
	private boolean calibrated = false;
	
	private ArrayList<Popup> displayList;
	
	/**
	 * This constructor initializes member variables and adds a
	 * calibration popup in the display list to be shown to the user.
	 * @param _sessMan Session Manager.
	 * @param _window PApplet window.
	 * @param startupStatus Boolean startup status.
	 */
	public Menu(SessionManager _sessMan, PApplet _window, boolean startupStatus)
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
		
		if(!startupStatus)
			displayList.add(new WarningPopup(window, "Check Settings"));
		if(Settings.numCameras < 1)
			displayList.add(new WarningPopup(window, "No Cameras Set"));
		else
			displayList.add(new CalibratePopup(window));
	}
	
	/**
	 * This function updates the calibrated system cameras.
	 */
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
	
	/**
	 * This function handles any user or command blocks and the appropriate
	 * actions are then taken.
	 * @param block Block object.
	 * @param cEvent Camera event.
	 */
	public void handleInput(Block block, CameraEvent cEvent)
	{
		if(calibrated)
		{
			if(block instanceof CommandBlock)
			{
				CommandBlock cBlock = (CommandBlock)block;
				if(cBlock.type == CommandBlock.CommandType.EXPORT)
				{
					if(cEvent.type == CameraEvent.EVENT_TYPE.ADD)
					{
						if(sessMan.checkModelExists())
						{
							if(!displayList.isEmpty())
							{
								if(displayList.get(displayList.size() - 1) instanceof UserPopup)
								{
									if(((UserPopup)displayList.get(displayList.size() - 1)).userType == UserTypes.EXPORT)
									{
										return;
									}
								}
							}
							
							displayList.add(new UserPopup(window, "Export", "Collada File", UserTypes.EXPORT));
						}
						else
						{
							if(!displayList.isEmpty())
							{
								if(displayList.get(displayList.size() - 1) instanceof WarningPopup)
								{
									if(((WarningPopup)displayList.get(displayList.size() - 1)).warningMessage.equals("No Model Found"))
									{
										return;
									}
								}
							}
							
							displayList.add(new WarningPopup(window, "No Model Found"));
						}
					}
					else if(cEvent.type == CameraEvent.EVENT_TYPE.REMOVE)
					{
						if(!displayList.isEmpty())
						{
							if(displayList.get(displayList.size() - 1) instanceof UserPopup)
							{
								if(((UserPopup)displayList.get(displayList.size() - 1)).userType == UserTypes.EXPORT)
								{
									displayList.remove(displayList.size() - 1);
								}
							}
						}
					}
				}
			}
		}
		if(block instanceof UserBlock)
		{
			UserBlock uBlock = (UserBlock)block;
			if(cEvent.type == CameraEvent.EVENT_TYPE.ADD)
			{
				if(!displayList.isEmpty())
				{
					if(displayList.get(displayList.size() - 1) instanceof UserPopup)
					{
						if(((UserPopup)displayList.get(displayList.size() - 1)).userType == UserTypes.SPECTATE)
						{
							return;
						}
					}
					else if(displayList.get(displayList.size() - 1) instanceof CalibratePopup)
					{
						displayList.remove(displayList.size() - 1);
					}
				}
				
				if(sessMan.isSpectating())
					displayList.add(new UserPopup(window, "Disconnect", "", UserTypes.SPECTATE, uBlock));
				else
					displayList.add(new UserPopup(window, "Spectate", uBlock.name, UserTypes.SPECTATE, uBlock));
			}
			else if(cEvent.type == CameraEvent.EVENT_TYPE.REMOVE)
			{
				if(!displayList.isEmpty())
				{
					if(displayList.get(displayList.size() - 1) instanceof UserPopup)
					{
						if(((UserPopup)displayList.get(displayList.size() - 1)).userType == UserTypes.SPECTATE)
						{
							displayList.remove(displayList.size() - 1);
							if(sessMan.isSpectating())
								return;
							checkCalibrated();
						}
					}
				}
			}
		}
	}
	
	/**
	 * This function checks the calibration status of the system. 
	 * If the system is not calibrated then the menu will display another
	 * calibration popup.
	 */
	public void checkCalibrated()
	{
		if(Settings.numCameras < 1)
			displayList.add(new WarningPopup(window, "No Cameras Set"));
		else if(!RuntimeData.isSystemCalibrated())
		{
			if(!displayList.isEmpty())
				if(displayList.get(displayList.size() - 1) instanceof CalibratePopup)
					return;
			displayList.add(new CalibratePopup(window));
		}
	}
	
	/**
	 * This function forces the removal of a connection
	 * popup given that there exists one at the front of
	 * the display list.
	 */
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
	
	/**
	 * This function creates a connection popup and adds it to
	 * the display list.
	 */
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
	
	/**
	 * This function creates a reconnection popup and adds it to
	 * the display list.
	 */
	public void createReconnectPopup()
	{
		displayList.add(new ReconnectPopup(window));
	}
	
	/**
	 * This function updates the current network connection
	 * status.
	 * @param status Boolean status.
	 */
	public void updateNetworkStatus(boolean status)
	{
		networkUpdate = true;
		networkStatus = status;
	}
	
	/**
	 * This function draws all the 2D graphical 
	 * components.
	 */
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
					{
						displayList.remove(0);
						if(c.userType == UserTypes.EXPORT)
							sessMan.exportToFile();
						else if(c.userType == UserTypes.SPECTATE)
							sessMan.spectate((UserBlock)c.holdBlock);
					}
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