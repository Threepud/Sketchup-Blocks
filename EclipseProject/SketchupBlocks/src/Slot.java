import java.io.File;

public class Slot 
{
	public File slot;
	Slot(String _name)
	{
		slot = new File(Settings.slotDirectory+"/"+_name);
	}
}
