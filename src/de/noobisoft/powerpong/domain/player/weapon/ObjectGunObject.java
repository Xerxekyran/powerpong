package de.noobisoft.powerpong.domain.player.weapon;

import java.awt.Color;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.MaterialGenerator;
import de.noobisoft.powerpong.util.ResourceLoader;

/**
 * An ObjectGunObect is a physical object, created by the ObjectGun
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class ObjectGunObject extends AbstractSceneObject implements MotionState
{
	private SceneNode		node;
	private RigidBody		rigidBody			= null;
	private Matrix4			matrixFromServer	= new Matrix4();
	private ShaderMaterial	shaderMaterial		= null;

	private int				id;

	/**
	 * 
	 * @param position
	 * @param direction
	 * @param id
	 * @param withWholeInit
	 */
	public ObjectGunObject(Vector3 position,
			Vector3 direction,
			int id,
			boolean withWholeInit)
	{
		// set an id
		this.id = id;

		if (withWholeInit)
		{
			init(position, direction);
		}

	}

	/**
	 * 
	 * @param position
	 * @param direction
	 */
	private void init(Vector3 position, Vector3 direction)
	{
		// create the visual part
		GroupNode object = (GroupNode) ResourceLoader.getCollada("meshes/box.dae");

		shaderMaterial = MaterialGenerator.makeGhostobjectMaterial(Color.GRAY);

		ShapeNode objectShape = new ShapeNode("ObjectGunObjectShape",
				Finder.findGeometry(object, null), shaderMaterial,
				de.bht.jvr.core.Transform.scale(2f));
		GroupNode objectCorrectness = new GroupNode("Correct the damn box",
				de.bht.jvr.core.Transform.translate(0, 0, -1.0f));
		objectCorrectness.addChildNode(objectShape);

		node = new GroupNode();
		node.setTransform(de.bht.jvr.core.Transform.translate(new Vector3(0, 0,
				-0.5f)));

		((GroupNode) node).addChildNode(objectCorrectness);

		this.rigidBody = PhysicManager.getInstance().createShot(EWeapons.ObjectActivator,
				position,
				direction,
				this,
				id);
	}

	/**
	 * set the visible mode for this object
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		shaderMaterial.setUniform("AMBIENT", "isVisible", new UniformBool(
				visible));
	}

	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		// in server mode the real position is calculated by jbullet
		if (inServerMode)
		{
			// check if the object got through the portal
			Vector3f position = new Vector3f();
			this.rigidBody.getCenterOfMassPosition(position);

			if (position.z < 6.8f && position.z > -6.8f && position.x < -31.7f)
			{
				this.rigidBody.proceedToTransform(new com.bulletphysics.linearmath.Transform(
						new Matrix4f(
								Matrix4.translate(new Vector3(position.x
										+ (31.7f * 2f), position.y, position.z)).getData())));
			}
			else if (position.z < 6.8f && position.z > -6.8f
					&& position.x > 31.7f)
			{
				this.rigidBody.proceedToTransform(new com.bulletphysics.linearmath.Transform(
						new Matrix4f(
								Matrix4.translate(new Vector3(position.x
										- (31.7f * 2f), position.y, position.z)).getData())));
			}
		}
		else
		{
			getNode().setTransform(new de.bht.jvr.core.Transform(
					matrixFromServer));

			this.rigidBody.proceedToTransform(new Transform(new Matrix4f(
					matrixFromServer.getData())));
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

	/**
	 * @return the rigidBody
	 */
	public RigidBody getRigidBody()
	{
		return rigidBody;
	}

	/**
	 * @param matrixFromServer
	 *            the matrixFromServer to set
	 */
	public void setMatrixFromServer(Matrix4 matrixFromServer)
	{
		this.matrixFromServer = matrixFromServer;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * @return the matrixFromServer
	 */
	public Matrix4 getMatrixFromServer()
	{
		return matrixFromServer;
	}

}
