package de.noobisoft.powerpong.domain.scene;

import java.util.Random;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.audio.AudioManager;
import de.noobisoft.powerpong.audio.ESoundEffects;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.ResourceLoader;

/**
 * This class represents the ghost train in the game. It will appaer from time
 * to time and drives from one portal to the other.
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class Train extends AbstractSceneObject implements MotionState
{
	private RigidBody		rigidBody						= null;
	private boolean			moving							= false;
	private boolean			trainSoundPlayed1				= false;
	private boolean			trainSoundPlayed2				= false;
	private SceneNode		node;
	private double			lastTrainStart					= 0;
	private static boolean	trainActive						= true;

	// minimum time between twoo trains, a random value will always be added to
	// this one
	private double			minTimeBetweenTrains			= 60;
	private int				randomRangeTimeBetweenTrains	= 60;

	/**
	 * 
	 */
	public Train()
	{
		try
		{
			float mass = 50;

			BoxShape shape = new BoxShape(new Vector3f(197f / 2f, 8f / 2f,
					6.5f / 2f));

			this.rigidBody = PhysicManager.getInstance().createRigidBody(this,
					shape,
					mass);
			this.rigidBody.setGravity(new Vector3f(0, 0, 0));
			this.rigidBody.setRestitution(0.5f);
			this.rigidBody.setDamping(0.5f, 0.5f);
			this.rigidBody.setFriction(0.5f);
			this.rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

			SceneNode trainNode = ResourceLoader.loadCollada("meshes/stock_train.DAE");

			trainNode.setTransform(de.bht.jvr.core.Transform.translate(new Vector3(
					0f, 0f, 0f)).mul(de.bht.jvr.core.Transform.rotateX(-(float) (Math.PI / 2))));

			node = new GroupNode();
			((GroupNode) node).addChildNode(trainNode);

			resetToStartPosition();

			// set the time
			lastTrainStart = System.currentTimeMillis() / 1000.0;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * resets the train to its start position
	 */
	private void resetToStartPosition()
	{
		this.rigidBody.proceedToTransform(new Transform(new Matrix4f(
				Matrix4.translate(new Vector3(300.0f, 2.5f, 0.0f)).getData())));

		setPosition(new Vector3(300f, 2.5f, 0));
	}

	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		if(!trainActive)
			return;
		
		if (inServerMode)
		{
			// move calculation is done on the server
			if (moving)
			{
				Vector3 position = getPosition().add(new Vector3(
						-(float) elapsedTime * 20, 0, 0));

				setPosition(new Vector3(position.x(), position.y(),
						position.z()));

				this.rigidBody.proceedToTransform(new Transform(new Matrix4f(
						Matrix4.translate(new Vector3(position.x(),
								position.y(), position.z())).getData())));

				// if the train is through the portal and beyond
				if (getPosition().x() < -300)
				{
					moving = false;
					lastTrainStart = System.currentTimeMillis() / 1000.0;

					lastTrainStart += new Random().nextInt(randomRangeTimeBetweenTrains); // add
					// a
					// random
					// time value
					resetToStartPosition();
				}
			}
			else
			{
				// set the train after a time interval
				if (System.currentTimeMillis() / 1000.0 > lastTrainStart
						+ minTimeBetweenTrains)
				{
					moving = true;
				}
			}
		}
		else
		{
			Vector3 position = getPosition();

			// if he was marked as not moving but his coords are not the default
			// one
			if (!moving && position.x() < 300)
			{
				moving = true;
			}
			// if he was marked as moving but is in its rest postiion
			else if (moving && position.x() == 300)
			{
				moving = false;
				trainSoundPlayed1 = false;
				trainSoundPlayed2 = false;
			}

			// check if we should play the trainbell sound (train starts moving)
			if (moving && !trainSoundPlayed1 && position.x() < 299)
			{
				AudioManager.getInstance().playSoundEffect(ESoundEffects.Trainbell);
				trainSoundPlayed1 = true;
			}

			// check if we should play the train moving sound (train is between
			// the portals)
			if (moving && !trainSoundPlayed2 && position.x() < 210)
			{
				AudioManager.getInstance().playSoundEffect(ESoundEffects.Trainmoving);
				trainSoundPlayed2 = true;
			}

			// update the rigid body for correct colission detection
			this.rigidBody.proceedToTransform(new Transform(new Matrix4f(
					Matrix4.translate(new Vector3(position.x(), position.y(),
							position.z())).getData())));

			super.update(elapsedTime, game, inServerMode);
		}
	}

	@Override
	public SceneNode getNode()
	{
		return node;
	}

	@Override
	public Transform getWorldTransform(Transform out)
	{
		if (node != null)
		{
			float[] t = node.getTransform().getMatrix().getData();
			out.set(new Matrix4f(t));
		}
		return out;
	}

	@Override
	public void setWorldTransform(Transform worldTrans)
	{
		if (node != null)
		{
			Matrix4f m = new Matrix4f();
			worldTrans.getMatrix(m);
			float[] newTrans = new float[] { m.m00, m.m01, m.m02, m.m03, m.m10,
					m.m11, m.m12, m.m13, m.m20, m.m21, m.m22, m.m23, m.m30,
					m.m31, m.m32, m.m33 };

			node.setTransform(new de.bht.jvr.core.Transform(new Matrix4(
					newTrans)));
		}
	}

}
