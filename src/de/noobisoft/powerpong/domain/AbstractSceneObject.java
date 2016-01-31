package de.noobisoft.powerpong.domain;

import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;

public abstract class AbstractSceneObject
{
	//current position
	private Vector3 position = new Vector3();
	
	//scale: 1 is original
	private float scale = 1;
	
	//rotation in rad
	private float rotationX = 0;
	private float rotationY = 0;//(float) (Math.PI/2.0);
	private float rotationZ = 0;
	
	/**
	 * updates this object by the given transformations
	 * @param elapsedTime
	 * @param game
	 */
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		getNode().setTransform(	Transform.translate(position)
								.mul(Transform.rotateZ(rotationZ))
								.mul(Transform.rotateY(rotationY))
								.mul(Transform.rotateX(rotationX))
								.mul(Transform.scale(scale)));
	}
	
	/**
	 * @return the position
	 */
	public Vector3 getPosition()
	{
		return position;
	}


	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector3 position)
	{
		this.position = position;
	}


	/**
	 * @return the scale
	 */
	public float getScale()
	{
		return scale;
	}


	/**
	 * @param scale the scale to set
	 */
	public void setScale(float scale)
	{
		this.scale = scale;
	}


	/**
	 * @return the rotationX
	 */
	public float getRotationX()
	{
		return rotationX;
	}


	/**
	 * @param rotationX the rotationX to set
	 */
	public void setRotationX(float rotationX)
	{
		this.rotationX = rotationX;
	}


	/**
	 * @return the rotationY
	 */
	public float getRotationY()
	{
		return rotationY;
	}


	/**
	 * @param rotationY the rotationY to set
	 */
	public void setRotationY(float rotationY)
	{
		this.rotationY = rotationY;
	}


	/**
	 * @return the rotationZ
	 */
	public float getRotationZ()
	{
		return rotationZ;
	}


	/**
	 * @param rotationZ the rotationZ to set
	 */
	public void setRotationZ(float rotationZ)
	{
		this.rotationZ = rotationZ;
	}

	
	/**
	 * returns the node
	 * @return
	 */
	public abstract SceneNode getNode();
}
