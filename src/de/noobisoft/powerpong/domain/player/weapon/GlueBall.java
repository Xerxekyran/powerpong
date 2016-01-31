package de.noobisoft.powerpong.domain.player.weapon;

import java.awt.Color;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.log4j.Logger;

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

/**
 * 
 * @author Lars George
 * 
 */
public class GlueBall extends AbstractSceneObject implements MotionState
{
	static Logger		logger		= Logger.getLogger(GlueBall.class);

	private SceneNode	node;
	private RigidBody	rigidBody	= null;
	private int			id;

	/**
	 * 
	 * @param position
	 * @param direction
	 */
	public GlueBall(Vector3 position, Vector3 direction, int id)
	{
		try
		{
			this.id = id;
			GroupNode ball = (GroupNode) ResourceLoader.getCollada("meshes/sphere.dae");
			// ball.setTransform(de.bht.jvr.core.Transform.scale(2.0f));

			ShapeNode ballShape = new ShapeNode("GlueBallShape",
					Finder.findGeometry(ball, null),
					MaterialGenerator.makeColloredPhongMaterial(new Color(10,
							25, 10, 255)),
					de.bht.jvr.core.Transform.scale(1.4f));

			node = new GroupNode();
			((GroupNode) node).addChildNode(ballShape);

			this.rigidBody = PhysicManager.getInstance().createShot(EWeapons.GlueGun,
					position,
					direction,
					this,
					this.id);
		}
		catch (Exception e)
		{

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

	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		// teleport
		if (getPosition().z() < 6.8f && getPosition().z() > -6.8f
				&& getPosition().x() < -31.7f)
		{
			this.rigidBody.proceedToTransform(new com.bulletphysics.linearmath.Transform(
					new Matrix4f(Matrix4.translate(new Vector3(
							getPosition().x() + (31.7f * 2f),
							getPosition().y(), getPosition().z())).getData())));
		}
		if (getPosition().z() < 6.8f && getPosition().z() > -6.8f
				&& getPosition().x() > 31.7f)
		{
			this.rigidBody.proceedToTransform(new com.bulletphysics.linearmath.Transform(
					new Matrix4f(Matrix4.translate(new Vector3(
							getPosition().x() - (31.7f * 2f),
							getPosition().y(), getPosition().z())).getData())));
		}
		Vector3f vec = new Vector3f();
		vec = rigidBody.getCenterOfMassPosition(vec);
		setPosition(new Vector3(vec.x, vec.y, vec.z));
		super.update(elapsedTime, game, inServerMode);

	}
}
