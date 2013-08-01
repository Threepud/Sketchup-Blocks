package sketchupblocks.base;

import java.util.ArrayList;

import processing.core.*;
import sketchupblocks.database.SmartBlock;

public class SketchupBlocks extends PApplet 
{
	private static final long serialVersionUID = 1L;
	
	Interpreter wimpie;
	SessionManager sessMan;
	
	public void setup()
	{
		size(displayWidth, displayHeight, P3D);
		frameRate(60);
		
		if (frame != null) 
		{
			frame.setResizable(false);
		}
		
		sessMan = new SessionManager(this);
		sessMan.setModelConstructor(new ModelConstructor(sessMan));
		for (int k = 0;  k < Settings.numCameras; k++)
		{
			wimpie = new Interpreter(Settings.cameraSettings[k].port, sessMan, this,k);
		}
		
		//testing collada writer
		ArrayList<ModelBlock> blocks = new ArrayList<>();
		SmartBlock sBlock = ColladaLoader.getSmartBlock("./models/GoogleCube.dae");
		ModelBlock mBlock = new ModelBlock();
		mBlock.smartBlock = sBlock;
		blocks.add(mBlock);
		blocks.add(mBlock);
		ColladaLoader.makeCollada(blocks);
	}

	public void draw() 
	{
		sessMan.drawGUI();
	}
	
	public static void main(String args[]) 
	{
		String[] arguments = {"--present"};
		PApplet.main("sketchupblocks.base.SketchupBlocks", arguments);
	}
	
	public boolean sketchFullScreen() 
	{
		return true;
	}
}
