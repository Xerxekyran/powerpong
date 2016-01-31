package de.noobisoft.powerpong.domain.player.weapon;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.MaterialGenerator;
import de.noobisoft.powerpong.util.ResourceLoader;

/**
 * The object that is created by the gluegun (formally known as bloooob)
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class GluePaste extends AbstractSceneObject implements MotionState
{

	private SceneNode	node;
	private RigidBody	rigidBody		= null;
	ShaderMaterial		shaderMaterial	= null;
	float				time			= 0;

	/**
	 * 
	 * @param position
	 * @param direction
	 */
	public GluePaste(Vector3 position)
	{
		// create the visual part
		GroupNode paste = (GroupNode) ResourceLoader.getCollada("meshes/blob/blob.DAE");
		paste.setTransform(de.bht.jvr.core.Transform.translate(new Vector3(
				position.x(), 0f, position.z())).mul(de.bht.jvr.core.Transform.rotateX(-(float) (Math.PI / 2)).mul(de.bht.jvr.core.Transform.scale(3,
				3,
				1))));

		shaderMaterial = MaterialGenerator.makeGluePasteMaterial();
		ShapeNode pasteShape = new ShapeNode("GluePasteShape",
				Finder.findGeometry(paste, null), shaderMaterial);

		pasteShape.setTransform(de.bht.jvr.core.Transform.translate(new Vector3(
				position.x(), 0.5f, position.z())).mul(de.bht.jvr.core.Transform.scale(3,
				1,
				3)));

		setPosition(new Vector3(position.x(), 1f, position.z()));

		// create the physical representation
		BoxShape shape = new BoxShape(new Vector3f(3f, 0.1f, 3f));

		this.rigidBody = PhysicManager.getInstance().createRigidBody(this,
				shape,
				0f);

		rigidBody.proceedToTransform(new Transform(new Matrix4f(
				Matrix4.translate(getPosition()).getData())));
		rigidBody.setDamping(0.9f, 0.9f);
		rigidBody.setFriction(0.9f);
		rigidBody.setHitFraction(0.9f);

		node = new GroupNode();
		((GroupNode) node).addChildNode(pasteShape);
	}

	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		time += elapsedTime;
		shaderMaterial.setUniform("AMBIENT", "waveTime", new UniformFloat(time));
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

}
