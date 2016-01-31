package de.noobisoft.powerpong.domain.player.weapon;

import java.awt.Color;

import javax.vecmath.Matrix4f;

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
import de.noobisoft.powerpong.domain.player.ETeam;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.MaterialGenerator;
import de.noobisoft.powerpong.util.ResourceLoader;

/**
 * the amunition of the peagun.
 * 
 * @author Lars George
 * @autho Chris Krauﬂ
 * 
 */
public class Peaball extends AbstractSceneObject implements MotionState
{

	private SceneNode	node;

	private RigidBody	rigidBody	= null;
	private int			id;

	/**
	 * 
	 * @param position
	 * @param direction
	 */
	public Peaball(Vector3 position, Vector3 direction, ETeam team, int id)
	{
		this.id = id;

		GroupNode ball = (GroupNode) ResourceLoader.getCollada("meshes/sphere.dae");

		Color ballColor = null;
		if (team == ETeam.RED)
			ballColor = new Color(80, 10, 10, 255);
		else
			ballColor = new Color(10, 10, 80, 255);

		ShapeNode ballShape = new ShapeNode("PeaBallShape",
				Finder.findGeometry(ball, null),
				MaterialGenerator.makeColloredPhongMaterial(ballColor));

		node = new GroupNode();
		((GroupNode) node).addChildNode(ballShape);

		this.rigidBody = PhysicManager.getInstance().createShot(EWeapons.PeaGun,
				position,
				direction,
				this,
				this.id);
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

		super.update(elapsedTime, game, inServerMode);
	}

}
