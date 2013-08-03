package sketchupblocks.base;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import processing.data.XML;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Vec3;

public class ColladaLoader 
{
	public ColladaLoader()
	{
		
	}
	
	public static SmartBlock getSmartBlock(String fileName)
	{
		XML xml = null;
		try 
		{
			 xml = new XML(new File(fileName));
			
		}
		catch (IOException | ParserConfigurationException | SAXException e) 
		{
			e.printStackTrace();
			return null;
		}
		
		//get unit meter
		XML asset = xml.getChild("asset");
		XML unit = asset.getChild("unit");
		Double unitMeter = Double.parseDouble(unit.getString("meter"));
		
		XML libraryGeometries = xml.getChild("library_geometries");
		XML geometry = libraryGeometries.getChild("geometry");
		XML mesh = geometry.getChild("mesh");
		XML[] sources = mesh.getChildren("source");
		
		SmartBlock result = new SmartBlock();
		
		//get model vertices
		String[] stringVertices = sources[0].getChild("float_array").getContent().split(" ");
		Vec3[] vertices = new Vec3[stringVertices.length / 3];
		
		//TODO: get unit measurement from file.
		for(int x = 0; x < vertices.length; ++x)
		{
			int index = 3 * x;
			try
			{
				/**
				 * Here we multiply the vertices coordinates with 0.0254 because
				 * Google Sketchup exports its Collada files with inches as its measuring unit.
				 */
				vertices[x] = new Vec3();
				vertices[x].x = Double.parseDouble(stringVertices[index + 1]) * unitMeter;
				vertices[x].y = -Double.parseDouble(stringVertices[index + 2]) * unitMeter;
				vertices[x].z = Double.parseDouble(stringVertices[index]) * unitMeter;
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		//get model indices
		XML triangles = mesh.getChild("triangles");
		XML poly = triangles.getChild("p");
		String[] stringIndices = poly.getContent().split(" ");
		int[] indices = new int[stringIndices.length];
		
		for(int x = 0; x < indices.length; ++x)
		{
			try
			{
				indices[x] = Integer.parseInt(stringIndices[x]);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		
		result.vertices = vertices;
		result.indices = indices;
		
		return result;
	}
	
	public static void makeCollada(ArrayList<ModelBlock> blocks)
	{
		//create file structure
		XML root = new XML("COLLADA");
		root.addChild("asset");
		root.addChild("library_visual_scenes");
		root.addChild("library_geometries");
		root.addChild("library_materials");
		root.addChild("library_effects");
		root.addChild("scene");
		
		root.setString("xmlns", "http://www.collada.org/2005/11/COLLADASchema");
		root.setString("version", "1.4.1");
		
		//asset
		XML asset = root.getChild("asset");
		asset.addChild("contributor");
		asset.addChild("created");
		asset.addChild("modified");
		asset.addChild("unit");
		asset.addChild("up_axis");
		
		XML contributor = asset.getChild("contributor");
		contributor.addChild("authoring_tool");
		
		XML authoringTool = contributor.getChild("authoring_tool");
		authoringTool.setContent("Sketchup Blocks " + Settings.versionNr);
		
		XML created = asset.getChild("created");
		TimeZone tz = TimeZone.getTimeZone("UCT");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		String timeStamp = df.format(new Date());
		created.setContent(timeStamp);
		
		XML modified = asset.getChild("modified");
		modified.setContent(timeStamp);
		
		XML unit = asset.getChild("unit");
		unit.setString("meter", "0.02539999969303608");
		unit.setString("name", "inch");
		
		XML upAxis = asset.getChild("up_axis");
		upAxis.setContent("Z_UP");
		
		//library visual scenes
		XML libraryVisualScenes = root.getChild("library_visual_scenes");
		libraryVisualScenes.addChild("visual_scene");
		
		XML visualScene = libraryVisualScenes.getChild("visual_scene");
		visualScene.setString("id", "visualScene0");
		visualScene.addChild("node");
		
		XML node = visualScene.getChild("node");
		node.setString("name", "Sketchup");
		
		XML libraryGeometries = root.getChild("library_geometries");
		for(int x = 0; x < blocks.size(); ++x)
		{
			node.addChild("instance_geometry");
			XML instanceGeometry = node.getChild(x);
			instanceGeometry.addChild("bind_material");
			
			instanceGeometry.setString("url", "#block" + x);
			
			XML bindMaterial = instanceGeometry.getChild("bind_material");
			bindMaterial.addChild("technique_common");
			
			XML techniqueCommon = bindMaterial.getChild("technique_common");
			techniqueCommon.addChild("instance_material");
			
			XML instanceMaterial = techniqueCommon.getChild("instance_material");
			instanceMaterial.addChild("bind_vertex_input");
			
			instanceMaterial.setString("symbol", "Material2");
			instanceMaterial.setString("target", "#material0");
			
			XML bindVertexInput = instanceMaterial.getChild("bind_vertex_input");
			bindVertexInput.setString("semantic", "UVSET0");
			bindVertexInput.setString("input_semantic", "TEXCOORD");
			bindVertexInput.setString("input_set", "0");
			
			//library geometries
			libraryGeometries.addChild("geometry");
			XML geometry = libraryGeometries.getChild(x);
			geometry.setString("id", "block" + x);
			geometry.addChild("mesh");
			
			XML mesh = geometry.getChild("mesh");
			mesh.addChild("source");
			mesh.addChild("source");
			mesh.addChild("vertices");
			mesh.addChild("triangles");
			
			//vertices
			XML source = mesh.getChild(0);
			source.addChild("float_array");
			source.addChild("technique_common");
			
			source.setString("id", "vertex" + x);
			
			XML floatArray = source.getChild("float_array");
			floatArray.setString("id", "vertexData" + x);
			floatArray.setString("count", Integer.toString(blocks.get(x).smartBlock.vertices.length * 3));
			String vectorString = "";
			for(Vec3 vector: blocks.get(x).smartBlock.vertices)
			{
				vectorString += vector.z / 0.02539999969303608 + " ";
				vectorString += vector.x / 0.02539999969303608 + " ";
				vectorString += (-vector.y / 0.02539999969303608) + " ";
			}
			floatArray.setContent(vectorString.substring(0, vectorString.length() - 1));
			
			techniqueCommon = source.getChild("technique_common");
			techniqueCommon.addChild("accessor");
			
			XML accessor = techniqueCommon.getChild("accessor");
			accessor.addChild("param");
			accessor.addChild("param");
			accessor.addChild("param");
			
			accessor.setString("count", Integer.toString(blocks.get(x).smartBlock.vertices.length));
			accessor.setString("source", "#vertexData" + x);
			accessor.setString("stride", "3");
			
			XML param = accessor.getChild(0);
			param.setString("name", "X");
			param.setString("type", "float");
			
			param = accessor.getChild(1);
			param.setString("name", "Y");
			param.setString("type", "float");
			
			param = accessor.getChild(2);
			param.setString("name", "Z");
			param.setString("type", "float");
			
			//surface normals
			source = mesh.getChild(1);
			source.addChild("float_array");
			source.addChild("technique_common");
			
			source.setString("id", "normal" + x);
			
			floatArray = source.getChild("float_array");
			floatArray.setString("id", "normalData" + x);
			int count = blocks.get(x).smartBlock.vertices.length;
			floatArray.setString("count", Integer.toString(count * 3));
			String normalString = "";
			Vec3[] vectors = blocks.get(x).smartBlock.vertices;
			for(int i = 0; i < count; i += 3)
			{
				Vec3 A = Vec3.subtract(vectors[i], vectors[i + 2]);
				Vec3 B = Vec3.subtract(vectors[i + 1], vectors[i + 2]);
				Vec3 normal = Vec3.cross(A, B);
				normal.normalize();

				for(int k = 0; k < 3; ++k)
				{
					normalString += normal.z + " ";
					normalString += normal.x + " ";
					normalString += -normal.y + " ";
				}
			}
			floatArray.setContent(normalString.substring(0, normalString.length() - 1));
			
			techniqueCommon = source.getChild("technique_common");
			techniqueCommon.addChild("accessor");
			
			accessor = techniqueCommon.getChild("accessor");
			accessor.addChild("param");
			accessor.addChild("param");
			accessor.addChild("param");
			
			accessor.setString("count", Integer.toString(blocks.get(x).smartBlock.vertices.length));
			accessor.setString("source", "#normalData" + x);
			accessor.setString("stride", "3");
			
			param = accessor.getChild(0);
			param.setString("name", "X");
			param.setString("type", "float");
			
			param = accessor.getChild(1);
			param.setString("name", "Y");
			param.setString("type", "float");
			
			param = accessor.getChild(2);
			param.setString("name", "Z");
			param.setString("type", "float");
			
			XML vertices = mesh.getChild("vertices");
			vertices.addChild("input");
			vertices.addChild("input");
			
			vertices.setString("id", "vertices" + x);
			
			XML input = vertices.getChild(0);
			input.setString("semantic", "POSITION");
			input.setString("source", "#vertex" + x);
			
			input = vertices.getChild(1);
			input.setString("semantic", "NORMAL");
			input.setString("source", "#normal" + x);
			
			XML triangles = mesh.getChild("triangles");
			triangles.addChild("input");
			triangles.addChild("p");
			
			triangles.setString("count", Integer.toString(blocks.get(x).smartBlock.indices.length / 3));
			triangles.setString("material", "Material2");
			
			input = triangles.getChild("input");
			input.setString("offset", "0");
			input.setString("semantic", "VERTEX");
			input.setString("source", "#vertices" + x);
			
			XML p = triangles.getChild("p");
			String indicesString = "";
			for(int i = 0; i < blocks.get(x).smartBlock.indices.length; ++i)
			{
				indicesString += blocks.get(x).smartBlock.indices[i] + " ";
			}
			p.setContent(indicesString.substring(0, indicesString.length() - 1));
		}
		
		//library materials
		XML libraryMaterials = root.getChild("library_materials");
		libraryMaterials.addChild("material");
		
		XML material = libraryMaterials.getChild("material");
		material.addChild("instance_effect");
		
		material.setString("id", "material0");
		material.setString("name", "material");
		
		XML instanceEffect = material.getChild("instance_effect");
		instanceEffect.setString("url", "#effect0");
		
		//library effects
		XML libraryEffects = root.getChild("library_effects");
		libraryEffects.addChild("effect");
		
		XML effect = libraryEffects.getChild("effect");
		effect.addChild("profile_COMMON");
		
		effect.setString("id", "effect0");
		
		XML profileCommon = effect.getChild("profile_COMMON");
		profileCommon.addChild("technique");
		
		XML technique = profileCommon.getChild("technique");
		technique.addChild("lambert");
		
		technique.setString("sid", "COMMON");
		
		XML lambert = technique.getChild("lambert");
		lambert.addChild("diffuse");
		
		XML diffuse = lambert.getChild("diffuse");
		diffuse.addChild("color");
		
		XML color = diffuse.getChild("color");
		color.setContent("1 1 1 1");
		
		//scene
		XML scene = root.getChild("scene");
		scene.addChild("instance_visual_scene");
		
		XML instanceVisualScene = scene.getChild("instance_visual_scene");
		instanceVisualScene.setString("url", "#visualScene0");
		
		//save collada to file
		//String fileName = "SketchupBlocks" + new Timestamp(new Date().getTime()) + ".dae";
		String path = "./export/";
		String fileName = path + "SUBlocks.dae";
		fileName = fileName.replaceAll(":", "_");
		root.save(new File(fileName), "");
	}
}
