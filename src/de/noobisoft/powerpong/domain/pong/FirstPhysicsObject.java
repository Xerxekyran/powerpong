package de.noobisoft.powerpong.domain.pong;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import de.bht.jvr.core.SceneNode;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.ResourceLoader;

public class FirstPhysicsObject extends AbstractSceneObject implements
		MotionState
{
	private SceneNode	node;
	private RigidBody	rigidBody;

	public FirstPhysicsObject()
	{
		try
		{
			BoxShape bs = new BoxShape(new Vector3f(3, 3, 3));

			this.rigidBody = PhysicManager.getInstance().createRigidBody(this,
					bs,
					100.0f);

			this.node = ResourceLoader.getCollada("meshes/duck.dae");

			this.node.setTransform(de.bht.jvr.core.Transform.translate(0.0f,
					5.f,
					0.0f));
			
			this.rigidBody.proceedToTransform(new Transform(new Matrix4f(
					Matrix4.translate(new Vector3(0.0f, 5.0f, -20.0f)).getData())));
			
			//this.rigidBody.setLinearVelocity(new Vector3f(0.5f, 15.0f, -4.0f));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{

	}

	@Override
	public SceneNode getNode()
	{
		return this.node;
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
