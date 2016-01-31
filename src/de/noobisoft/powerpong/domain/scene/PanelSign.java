package de.noobisoft.powerpong.domain.scene;

import java.util.concurrent.ConcurrentHashMap;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.util.ResourceLoader;

public class PanelSign extends AbstractSceneObject
{
	private Integer									number	= 0;
	private GroupNode								signsNode;
	private ConcurrentHashMap<Integer, SceneNode>	numbers	= new ConcurrentHashMap<Integer, SceneNode>();

	private Vector3 hideVector = new Vector3(-2,0,0);
	
	public PanelSign()
	{
		signsNode = new GroupNode();

		for (int i = 0; i < 10; i++)
		{
			SceneNode sign = ResourceLoader.getCollada("meshes/panel_signs/"
					+ i + ".DAE");
			sign.setTransform(Transform.translate(hideVector));
			numbers.put(i, sign);
			signsNode.addChildNode(sign);
		}
	}

	@Override
	public SceneNode getNode()
	{
		return signsNode;
	}

	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		super.update(elapsedTime, game, inServerMode);
	}

	/**
	 * @return the number
	 */
	public Integer getNumber()
	{
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(Integer number)
	{
		for (int i = 0; i < 10; i++)
		{
			numbers.get(i).setTransform(Transform.translate(hideVector));
		}
		this.number = number;
		SceneNode numberNode = numbers.get(number);
		if(numberNode!=null)
			numberNode.setTransform(Transform.translate(new Vector3()));
		else
			System.out.println("Number "+number+" is not available");
	}

}
