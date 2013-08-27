package sketchupblocks.menu;

import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;

public class SlotChooserState extends MenuState
{
	protected int slotNumber = 0; //This will be -1 if the user backs out of this menu state (i.e. cancels the slot choosing)
	private float rot;
	private boolean rotationSet = false;
	protected OverwriteState childState;
	public SlotChooserState(SessionManager _sessMan)
	{
		super(_sessMan);
		System.out.println("Currently selected slot: "+slotNumber);
	}
	
	public boolean handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		if (Settings.verbose >= 3)
			System.out.println("--SlotChooser is handling input--");
		
		//If we aren't currently forwarding input to a yes/no dialogue
		if (childState == null)
		{
			if (cBlock.type == CommandBlock.CommandType.ROTATE)
			{
				if (!rotationSet)
				{
					rotationSet = true;
					rot = cEvent.rotation;
					if (Settings.verbose >= 3)
						System.out.println("--SlotChooser has set initial rotation--");
				}
				else
				{
					float diff = cEvent.rotation - rot;
					//This may need refining.
					//int moveNum = (int)(diff/Settings.scrollTrigger);
					/*
					if (moveNum != 0)
					{
						slotNumber = (slotNumber+moveNum)%Settings.numSlots;
						rot = cEvent.rotation;
						System.out.println("Currently selected slot: "+slotNumber);
					}
					else if (Settings.verbose >= 3)
						System.out.println("--SlotChooser has not moved slots --");*/
				}
				
				return false;
			}
			else if (cBlock.type == CommandBlock.CommandType.OK)
			{
				if (sessMan.projectSlots[slotNumber].slot.exists())
				{
					if (Settings.verbose >= 3)
						System.out.println("--Existing file detected. Now entering overwrite dialogue--");
					childState = new OverwriteState(sessMan);
					return false; //Not done
				}
				else 
				{
					if (Settings.verbose >= 1)
						System.out.println("Final chosen slot: "+slotNumber);
					return true; //Done!
				}
			}
			else if (cBlock.type == CommandBlock.CommandType.CANCEL)
			{
				slotNumber = -1;
				if (Settings.verbose >= 1)
					System.out.println("Slot choosing has been cancelled");
				return true;
			}
			else
			{
				if (Settings.verbose >= 3)
					System.out.println("--SlotChooser received garbage input: "+cBlock.type.name()+"--");
				return false;
			}
		}
		else
		{
			if (Settings.verbose >= 3)
				System.out.println("--SlotChooser has forwarded input to overwrite dialogue--");
			
			boolean childDone = childState.handleInput(cBlock, cEvent);
			if (childDone && !childState.yesno) //If the dialogue exited with "no" continue waiting for slot chooser input.
			{			
				childState = null;
				if (Settings.verbose >= 3)
					System.out.println("--SlotChooser recognizes that overwrite dialogue refused overwrite--");
				return false;
			}
			
			return childDone;	//Else return true.
		}
	}
}
