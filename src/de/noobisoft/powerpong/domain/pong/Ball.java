package de.noobisoft.powerpong.domain.pong;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.MaterialGenerator;
import de.noobisoft.powerpong.util.ResourceLoader;

public class Ball extends AbstractSceneObject implements MotionState
{
	private RigidBody	rigidBody			= null;
	private SceneNode	node;
	private Matrix4		matrixFromServer	= new Matrix4();

	/**
	 * 
	 */
	public Ball()
	{
		// load the model
		try
		{
			float density = 1.0f;
			float radius = 3.0f;
			float mass = density * 4.0f / 3.0f * (float) Math.PI * radius
					* radius * radius;

			mass = 2;

			SphereShape shape = new SphereShape(radius);

			this.rigidBody = PhysicManager.getInstance().createRigidBody(this,
					shape,
					mass);
			this.rigidBody.setRestitution(1f);
			this.rigidBody.setDamping(0f, 0f);
			this.rigidBody.setFriction(1f);

			SceneNode ballNode = ResourceLoader.loadCollada("meshes/sphere.dae");
			node = new GroupNode();			
			setScale(6);
			
			ShapeNode ballShape = new ShapeNode(
					"BallShape",
					Finder.findGeometry(ballNode, null),
					MaterialGenerator.makeBallMaterial());
						
			((GroupNode)node).addChildNode(ballShape);

			this.rigidBody.proceedToTransform(new Transform(new Matrix4f(
					Matrix4.translate(new Vector3(0.0f, 5f, 0.0f)).getData())));

			this.rigidBody.setLinearVelocity(new Vector3f(2.0f, 0.0f, -20.0f));
			this.rigidBody.setAngularVelocity(new Vector3f(0f, 0f, 0f));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public RigidBody getRigidBody()
	{
		return rigidBody;
	}

	public void setRigidBody(RigidBody rigidBody)
	{
		this.rigidBody = rigidBody;
	}

	public void setMatrixFromServer(Matrix4 matrixFromServer)
	{
		this.matrixFromServer = matrixFromServer;
	}

	@Override
	public SceneNode getNode()
	{
		return node;
	}

	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		// in server mode the real ballposition is calculated by jbullet
		if (inServerMode)
		{

		}
		else
		{
			getNode().setTransform(new de.bht.jvr.core.Transform(
					matrixFromServer).mul(de.bht.jvr.core.Transform.scale(getScale())));
						
			
			this.rigidBody.proceedToTransform(new Transform(new Matrix4f(
					Matrix4.translate(matrixFromServer.translation()).getData())));
		}

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

	public void setWorldTransform(float[] worldTransform)
	{
		if (node != null)
		{
			node.setTransform(new de.bht.jvr.core.Transform(new Matrix4(
					worldTransform)));
		}
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
