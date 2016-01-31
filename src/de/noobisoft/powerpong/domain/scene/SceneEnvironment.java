package de.noobisoft.powerpong.domain.scene;

import de.bht.jvr.core.SceneNode;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.util.ResourceLoader;

public class SceneEnvironment extends AbstractSceneObject
{

	protected SceneNode node;
	
	public SceneEnvironment(String file, Vector3 pos)
	{
		try
		{
			node = ResourceLoader.getCollada(file);
			setPosition(pos);
			setRotationX(-(float)Math.PI/2);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public SceneNode getNode()
	{
		return node;
	}

}
