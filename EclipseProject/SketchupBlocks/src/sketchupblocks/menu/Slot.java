package sketchupblocks.menu;

import java.io.File;

import sketchupblocks.base.Settings;

public class Slot 
{
	public File slot;
	public Slot(String _name)
	{
		slot = new File(Settings.slotDirectory+"/"+_name);
	}
}
