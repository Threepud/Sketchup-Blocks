package sketchupblocks.gui;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import sketchupblocks.base.Logger;
import sketchupblocks.base.Model;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.construction.ModelBlock;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.math.Face;
import sketchupblocks.math.Line;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;

/**
 * @author Jacques Coetzee
 * This class provides additional debug information
 * and displays it in the OpenGL window alongside the
 * model.
 */
public class DebugViewer extends ModelViewer 
{
	//fiducial debug lines
	private boolean showDebugLines = false;
	private int lineLength = 80;
	private int lineRate = 1;
	private boolean lineShorter = false;
	private boolean lineLonger = false;
	
	//fiducial debug points
	private boolean showDebugPoints = false;
	
	//debug - general output lines
	private boolean showOutputLines = false;
	
	//debug faces
	private boolean showDebugFaces = false;
	
	//debug ghost blocks - pre pseudo physics
	private boolean showGhostBlocks = false;
	
	//debug additional ghost blocks - pre 2 point wiggling
	private boolean showMediumRare = false;
	
	/**
	 * This constructor sets the lobby and window
	 * handler needed to send the draw calls to
	 * and the the current model.
	 * @param _lobby Current working lobby.
	 * @param _window PApplet window handler.
	 */
	public DebugViewer(Lobby _lobby, PApplet _window)
	{
		super.lobby = _lobby;
		super.window = _window;
	}
	
	/* (non-Javadoc)
	 * @see sketchupblocks.gui.ModelViewer#setKeyboardInput(processing.event.KeyEvent)
	 */
	public void setKeyboardInput(KeyEvent e)
	{
		if(e.getKey() == 'l')
		{
			if(e.getAction() == KeyEvent.RELEASE)
				showDebugLines = !showDebugLines;
		}
		else if(e.getKey() == '[')
		{
			if(e.getAction() == KeyEvent.PRESS)
				lineShorter = true;
			else if(e.getAction() == KeyEvent.RELEASE)
				lineShorter = false;
		}
		else if(e.getKey() == ']')
		{
			if(e.getAction() == KeyEvent.PRESS)
				lineLonger = true;
			else if(e.getAction() == KeyEvent.RELEASE)
				lineLonger = false;
		}
		else if(e.getKey() == 'p')
		{
			if(e.getAction() == KeyEvent.RELEASE)
				showDebugPoints = !showDebugPoints;
		}
		else if(e.getKey() == 'o')
		{
			if(e.getAction() == KeyEvent.RELEASE)
			{
				showOutputLines = !showOutputLines; 
			}
		}
		else if(e.getKey() == 'c')
		{
			if(e.getAction() == KeyEvent.RELEASE)
			{
				showDebugFaces = !showDebugFaces;
			}
		}
		else if(e.getKey() == 'g')
		{
			if(e.getAction() == KeyEvent.RELEASE)
			{
				showGhostBlocks = !showGhostBlocks; 
			}
		}
		else if(e.getKey() == 'f')
		{
			if(e.getAction() == KeyEvent.RELEASE)
			{
				showMediumRare = !showMediumRare; 
			}
		}
	}
	
	/**
	 * This is the Debug Viewer's main 
	 * draw call. This function calls all relevant
	 * sub functions to draw the rest of the debug
	 * graphical components.
	 */
	public void drawDebugInformation()
	{
		drawDebugLines();
		drawOutputLines();
		drawDebugPoints();
		drawDebugFaces();
		drawGhostBlocks();
		drawMediumRareBlocks();
	}
	
	/**
	 * This function draw the lines from all the system
	 * cameras to a fiducial center.
	 */
	private void drawDebugLines()
	{
		if(lineShorter)
			lineLength -= lineRate;
		if(lineLonger)
			lineLength += lineRate;
		
		super.window.pushMatrix();
		window.scale(1f);
		if(showDebugLines)
		{
			window.stroke(255, 0, 0);
			Model model;
			try 
			{
				model = lobby.getModel();
			} 
			catch (ModelNotSetException e) 
			{
				e.printStackTrace();
				return;
			}
			for(ModelBlock mBlock: model.getBlocks())
			{
				for(int x = 0; x < mBlock.debugLines.length; ++x)
				{
					Line line = mBlock.debugLines[x];
					
					Vec3 start = new Vec3(line.point.y, -line.point.z, line.point.x);
					Vec3 end = new Vec3(line.direction.y, -line.direction.z, line.direction.x);
					
					start = Vec3.scalar(10, start);
					end = Vec3.scalar(lineLength * 10, end);
					end = Vec3.add(start, end);
					window.line((float)start.x, (float)start.y, (float)start.z, (float)end.x, (float)end.y, (float)end.z);
				}
			}
		}
		window.popMatrix();
	}
	
	/**
	 * This function enable easy debugging by displaying 
	 * arbitrary lines given by the programming. These lines 
	 * are fetched from the RuntimeData class.
	 */
	private void drawOutputLines()
	{
		final float olLength = 10.0f;
		
		window.pushMatrix();
		window.scale(1f);
		
		if(showOutputLines)
		{
			window.stroke(222, 3, 255);
			LinkedBlockingQueue<Line> outputLines = RuntimeData.outputLines;
			if(outputLines != null)
			{
				for(Line line: outputLines)
				{
					Vec3 start = new Vec3(line.point.y, -line.point.z, line.point.x);
					Vec3 end = new Vec3(line.direction.y, -line.direction.z, line.direction.x);
					
					start = Vec3.scalar(10, start);
					end = Vec3.scalar(olLength * 10, end);
					end = Vec3.add(start, end);
					window.line((float)start.x, (float)start.y, (float)start.z, (float)end.x, (float)end.y, (float)end.z);
				}
			}
		}
		window.popMatrix();
	}
	
	/**
	 * This function draws all the fiducial 
	 * center points.
	 */
	private void drawDebugPoints()
	{
		if(showDebugPoints)
		{
			window.pushMatrix();
			window.noStroke();
			window.fill(0, 255, 0);
			
			Model model;
			try 
			{
				model = lobby.getModel();
			} 
			catch (ModelNotSetException e) 
			{
				e.printStackTrace();
				return;
			}
			for(ModelBlock mBlock: model.getBlocks())
			{
				for(int x = 0; x < mBlock.debugPoints.length; ++x)
				{
					Vec3 point = mBlock.debugPoints[x];
					
					window.pushMatrix();
					
					point = Vec3.scalar(10, point);
					window.translate((float)point.y, (float)-point.z, (float)point.x);
					window.sphere(5);
					
					window.popMatrix();
				}
			}
			window.fill(255);
			window.popMatrix();
		}
	}
	
	/**
	 * This function draws faces given in the
	 * RuntimeData class for debugging the Pseudo
	 * Physics class.
	 */
	private void drawDebugFaces()
	{
		if(showDebugFaces)
		{
			window.pushMatrix();
			window.fill(0, 162, 237);
			
			Face f = RuntimeData.topFace;
			if(f != null)
			{
				window.beginShape();
				for(int x = 0; x < f.corners.length; ++x)
				{
					window.vertex(10 * (float)f.corners[x].y, 10 * -(float)f.corners[x].z, 10 * (float)f.corners[x].x);
				}
				window.endShape(PConstants.CLOSE);
			}
			
			window.fill(206, 27, 167);
			f = RuntimeData.bottomFace;
			if(f != null)
			{
				window.beginShape();
				for(int x = 0; x < f.corners.length; ++x)
				{
					window.vertex(10 * (float)f.corners[x].y, 10 * -(float)f.corners[x].z, 10 * (float)f.corners[x].x);
				}
				window.endShape(PConstants.CLOSE);
			}
			window.popMatrix();
		}
	}
	
	/*private void drawDebugLinesIntersections()
	{
		if(showDebugLineIntersection)
		{
			window.pushMatrix();
			window.fill(255);
			
			Line debugLine = RuntimeData.debugLine;
			if(debugLine != null)
			{
				Vec3 start = new Vec3(10 * debugLine.point.y, 10 * -debugLine.point.z, 10 * debugLine.point.x);
				Vec3 end = new Vec3(10 * debugLine.direction.y, 10 * -debugLine.direction.z, 10 * debugLine.direction.x);
				window.line((float)start.x, (float)start.y, (float)start.z, (float)end.x, (float)end.y, (float)end.z);
			}
			
			window.popMatrix();
		}
	}*/
	
	/**
	 * This function draws the model blocks
	 * before they have been modified by the 
	 * pseudo physics engine.
	 */
	private void drawGhostBlocks()
	{
		if(showGhostBlocks)
		{
			window.pushMatrix();
			window.noStroke();
			window.fill(200, 255, 0, 100);
			window.scale(10, 10, 10);
			
			//draw block list
			Model model;
			try 
			{
				model = lobby.getModel();
			} 
			catch (ModelNotSetException e) 
			{
				e.printStackTrace();
				return;
			}
			for(ModelBlock block: new ArrayList<ModelBlock>(model.getBlocks()))
			{
				/*if(showDebugLineIntersection)
				{
					Line line = RuntimeData.debugLine;
					if(line != null)
					{
						if(EnvironmentAnalyzer.isIntersecting(line, block))
						{
							window.fill(0, 255, 0);
						}
						else
						{
							window.fill(255);
						}
					}
				}*/
				if (block.type == ModelBlock.ChangeType.REMOVE)
				{
					Logger.log("Drawing removed block!", 1);
					return;
				}
				
				SmartBlock smartBlock = block.smartBlock;
				window.beginShape(PConstants.TRIANGLES);
				for(int x = 0; x < smartBlock.indices.length; ++x)
				{
					Vec3 vertex = smartBlock.vertices[smartBlock.indices[x]];
					
					vertex = Matrix.multiply(block.rawMatrix, vertex.padVec3()).toVec3();
					window.vertex((float)vertex.y, -(float)vertex.z, (float)vertex.x);
				}
				
				window.endShape();
			}
			window.popMatrix();
		}
	}
	
	/**
	 * This function draws the model blocks fresh
	 * from the SVD Decomposition. This is before any
	 * correction to the orientation has been applied.
	 */
	private void drawMediumRareBlocks()
	{
		if(showMediumRare)
		{
			window.pushMatrix();
			window.noStroke();
			window.fill(255, 0, 0, 100);
			window.scale(10, 10, 10);
			
			//draw block list
			Model model;
			try 
			{
				model = lobby.getModel();
			} 
			catch (ModelNotSetException e) 
			{
				e.printStackTrace();
				return;
			}
			for(ModelBlock block: new ArrayList<ModelBlock>(model.getBlocks()))
			{
				if (block.type == ModelBlock.ChangeType.REMOVE)
				{
					Logger.log("Drawing removed block!", 1);
					return;
				}
				
				SmartBlock smartBlock = block.smartBlock;
				window.beginShape(PConstants.TRIANGLES);
				for(int x = 0; x < smartBlock.indices.length; ++x)
				{
					Vec3 vertex = smartBlock.vertices[smartBlock.indices[x]];
					Matrix trans = block.mooingMatrix;
					if(trans != null)
					{
						vertex = Matrix.multiply(trans, vertex.padVec3()).toVec3();
						window.vertex((float)vertex.y, -(float)vertex.z, (float)vertex.x);
					}
				}
				
				window.endShape();
			}
			window.popMatrix();
		}
	}
}
