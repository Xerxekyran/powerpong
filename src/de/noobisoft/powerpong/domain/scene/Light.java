package de.noobisoft.powerpong.domain.scene;

import java.awt.Color;

import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;

/**
 * A light object in powerpong
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class Light extends AbstractSceneObject
{

	private SpotLightNode	light;
	private static int		count	= 0;

	/**
	 * 
	 * @param pos
	 */
	public Light(Vector3 pos, Color color)
	{
		this.light = new SpotLightNode("MySpotLight" + count);
		this.light.setColor(color);
		this.light.setShadowBias(0.5f);
		this.light.setCastShadow(true);
		this.light.setSpotCutOff(40);
		this.setPosition(pos);
		setRotationX(-(float) Math.PI / 2);
	}

	@Override
	public SceneNode getNode()
	{
		return this.light;
	}
}
