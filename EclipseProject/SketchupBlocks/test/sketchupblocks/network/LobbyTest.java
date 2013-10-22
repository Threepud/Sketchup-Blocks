package sketchupblocks.network;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import processing.core.PApplet;
import sketchupblocks.base.Model;
import sketchupblocks.base.SessionManager;
import sketchupblocks.construction.ModelBlock;
import sketchupblocks.database.Block;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.gui.Menu;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;

public class LobbyTest 
{
	private static PApplet parent;
	private static SessionManager sessMan;
	private static Menu menu;
	
	private static Lobby lobby;
	private static Server server;
	private static NetworkedLobby networkedLobby;
	
	private static ModelBlock testBlock;
	
	@BeforeClass
	public static void setup()
	{		
		parent = new PApplet();
		sessMan = new SessionManager(parent, true);
		menu = new Menu(sessMan, parent, true);
		
		lobby = new LocalLobby();
		lobby.setModel(new Model());
		
		try 
		{
			server = new Server(lobby, 5666);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			fail("Initialization failed, couldn't create server.");
		}
		server.start();
		
		try 
		{
			networkedLobby = new NetworkedLobby("localhost", 5666, menu, sessMan);
			networkedLobby.setModel(new Model());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			fail("Initialization failed, couldn't create networked lobby.");
		}
		networkedLobby.start();
		
		//create mock model block
		int[] associatedFiducials = {54, 55, 56, 57, 58, 59};
		int blockID = 9;
		Block.BlockType blockType = Block.BlockType.SMART;
		Vec3[] fiducialCoordinates =
			{
				new Vec3(0.0, 0.0, 6.5),
				new Vec3(-3.25, 0.0, 0.0),
				new Vec3(0.0, -9.75, 0.0),
				new Vec3(3.25, 0.0, 0.0),
				new Vec3(0.0, 0.0, -6.5),
				new Vec3(0.0, 9.75, 0.0)
			};
		Vec3[] fiducialOrientation =
			{
				new Vec3(0.0, 1.0, 0.0),
				new Vec3(0.0, 0.0, 1.0),
				new Vec3(0.0, 0.0, 1.0),
				new Vec3(0.0, 0.0, 1.0),
				new Vec3(0.0, -1.0, 0.0),
				new Vec3(0.0, 0.0, -1.0)
			};
		int[] indices = {0, 1, 2, 1, 0, 3, 4, 5, 6, 5, 4, 7, 8, 9, 10, 9, 8, 11, 12, 13, 14, 13, 12, 15, 16, 17, 18, 17, 16, 19, 20, 21, 22, 21, 20, 23};
		Vec3[] vertices =
			{
				new Vec3(-3.2499999214462347, -9.749999764338733, 6.499999842892485),
				new Vec3(3.2499999214462347, 9.749999764338733, 6.499999842892485),
				new Vec3(-3.24999992144625, 9.74999976433873, 6.499999842892485),
				new Vec3(3.24999992144625, -9.74999976433873, 6.499999842892485),
				new Vec3(-3.2499999214462347, -9.749999764338733, 6.499999842892485),
				new Vec3(-3.24999992144625, 9.74999976433873, -6.499999842892479),
				new Vec3(-3.2499999214462347, -9.749999764338733, -6.499999842892479),
				new Vec3(-3.24999992144625, 9.74999976433873, 6.499999842892485),
				new Vec3(3.24999992144625, -9.74999976433873, -6.499999842892479),
				new Vec3(-3.2499999214462347, -9.749999764338733, 6.499999842892485),
				new Vec3(-3.2499999214462347, -9.749999764338733, -6.499999842892479),
				new Vec3(3.24999992144625, -9.74999976433873, 6.499999842892485),
				new Vec3(3.2499999214462347, 9.749999764338733, 6.499999842892485),
				new Vec3(3.24999992144625, -9.74999976433873, -6.499999842892479),
				new Vec3(3.2499999214462347, 9.749999764338733, -6.499999842892479),
				new Vec3(3.24999992144625, -9.74999976433873, 6.499999842892485),
				new Vec3(3.2499999214462347, 9.749999764338733, 6.499999842892485),
				new Vec3(-3.24999992144625, 9.74999976433873, -6.499999842892479),
				new Vec3(-3.24999992144625, 9.74999976433873, 6.499999842892485),
				new Vec3(3.2499999214462347, 9.749999764338733, -6.499999842892479),
				new Vec3(3.24999992144625, -9.74999976433873, -6.499999842892479),
				new Vec3(-3.24999992144625, 9.74999976433873, -6.499999842892479),
				new Vec3(3.2499999214462347, 9.749999764338733, -6.499999842892479),
				new Vec3(-3.2499999214462347, -9.749999764338733, -6.499999842892479)
			};
		
		SmartBlock sBlock = new SmartBlock();
		sBlock.associatedFiducials = associatedFiducials;
		sBlock.blockId = blockID;
		sBlock.blockType = blockType;
		sBlock.fiducialCoordinates = fiducialCoordinates;
		sBlock.fiducialOrient = fiducialOrientation;
		sBlock.indices = indices;
		sBlock.name = "./models/ColladaTEST.dae";
		sBlock.vertices = vertices;
		
		testBlock = new ModelBlock();
		testBlock.smartBlock = sBlock;
		testBlock.transformationMatrix = Matrix.identity(4);
		testBlock.type = ModelBlock.ChangeType.UPDATE;
	}
	
	@AfterClass
	public static void cleanUp()
	{
		networkedLobby.stopLobby();
		server.stopServer();
		
		networkedLobby = null;
		server = null;
		lobby = null;
		menu = null;
		sessMan = null;
		parent = null;
		
		testBlock = null;
	}
	
	@Test
	public void lobbyNetworkIntegrationTest() 
	{
		lobby.updateModel(testBlock);
		Model model = null;
		try 
		{
			model = lobby.getModel();
		}
		catch (ModelNotSetException e) 
		{
			e.printStackTrace();
			fail("Lobby: Model not set");
		}
		ArrayList<ModelBlock> blocks = new ArrayList<>(model.getBlocks());
		assertTrue("Blocks not equal to 1", blocks.size() == 1);
		
		testBlockEqual(blocks.get(0));
		
		//Wait for networked lobby to receive block
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < 100){}
		
		try 
		{
			model = networkedLobby.getModel();
		}
		catch (ModelNotSetException e) 
		{
			e.printStackTrace();
			fail("Networked Lobby: Model not set");
		}
		blocks = new ArrayList<>(model.getBlocks());
		assertTrue("Blocks not equal to 1", blocks.size() == 1);
		
		testBlockEqual(blocks.get(0));
	}
	
	private void testBlockEqual(ModelBlock mBlock)
	{
		//test vertices
		for(int x = 0; x < testBlock.smartBlock.vertices.length; ++x)
		{
			assertTrue
			(
					"Vertices don't match", 
					testBlock.smartBlock.vertices[x].x == mBlock.smartBlock.vertices[x].x &&
					testBlock.smartBlock.vertices[x].y == mBlock.smartBlock.vertices[x].y &&
					testBlock.smartBlock.vertices[x].z == mBlock.smartBlock.vertices[x].z
			);
		}
		
		//test indices
		for(int x = 0; x < testBlock.smartBlock.indices.length; ++x)
		{
			assertTrue("Indices don't match", testBlock.smartBlock.indices[x] == mBlock.smartBlock.indices[x]);
		}
		
		assertTrue("Transformation matrices don't match", testBlock.transformationMatrix.equals(mBlock.transformationMatrix));
	}
}
