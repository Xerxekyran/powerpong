/**
 * 
 */
package de.noobisoft.powerpong.util;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.noobisoft.powerpong.config.Constants;

/**
 * @author Chris
 * 
 */
public class ResourceLoader
{
	static Logger										logger		= Logger.getLogger(ResourceLoader.class);
	private static boolean								isServer	= false;
	private static ConcurrentHashMap<String, SceneNode>	sceneNodes	= new ConcurrentHashMap<String, SceneNode>();

	/**
	 * returns the sceneNode representing this collada file in groupNode
	 * 
	 * @param fileName
	 * @return
	 */
	public static SceneNode getCollada(String fileName)
	{
		// in server mode we do not need any models
		if (isServer)
			return new GroupNode();

		SceneNode node = sceneNodes.get(fileName);
		
		if(Constants.DEBUG)
		{
			boolean found = false;
			for(String s : Constants.debugModels)
			{
				if(s.equals(fileName))
				{
					found = true;
					break;
				}
			}				
			if(!found)
				return new GroupNode();
		}

		if (node == null)
		{
			try
			{
				SplashScreen.setLoadingText(fileName);
				logger.debug("Loading Collada File [" + fileName + "]");
				node = ColladaLoader.load(new File(fileName));
				sceneNodes.put(fileName, node);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		GroupNode retVal = new GroupNode();
		return retVal.addChildNode(node);
	}

	/**
	 * returns the sceneNode representing this collada file by loading it
	 * 
	 * @param fileName
	 * @return
	 */
	public static SceneNode loadCollada(String fileName)
	{
		// in server mode we do not need any models
		if (isServer)
			return new GroupNode();
		
		if(Constants.DEBUG)
		{
			boolean found = false;
			for(String s : Constants.debugModels)
			{
				if(s.equals(fileName))
				{
					found = true;
					break;
				}
			}				
			if(!found)
				return new GroupNode();
		}

		SceneNode node = null;
		try
		{
			SplashScreen.setLoadingText(fileName);
			logger.debug("Loading Collada File [" + fileName + "]");
			node = ColladaLoader.load(new File(fileName));
			sceneNodes.put(fileName, node);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		GroupNode retVal = new GroupNode();
		return retVal.addChildNode(node);
	}
	
	/**
	 * loads certain models without using them directly. the benefit is, that
	 * loading calls will not have to parse the dae files later on
	 */
	public static void preLoadModels()
	{
		ResourceLoader.getCollada("meshes/blob/blob.DAE");
		ResourceLoader.getCollada("meshes/box.dae");
		ResourceLoader.getCollada("meshes/mod_weapon/terrain.DAE");
		ResourceLoader.getCollada("meshes/playerBlueRabbid.DAE");
		ResourceLoader.getCollada("meshes/playerRedMonkey.DAE");
	}

	/**
	 * @return the isServer
	 */
	public static boolean isServer()
	{
		return isServer;
	}

	/**
	 * @param isServer
	 *            the isServer to set
	 */
	public static void setServer(boolean isServer)
	{
		ResourceLoader.isServer = isServer;
	}
}
