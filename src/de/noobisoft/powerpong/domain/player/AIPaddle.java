package de.noobisoft.powerpong.domain.player;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.log4j.Logger;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.ResourceLoader;

/**
 * The AIPaddle class
 * 
 * @author Lars George
 * 
 */
public class AIPaddle extends AbstractSceneObject implements MotionState
{

	static Logger		logger			= Logger.getLogger(AIPaddle.class);

	private double		velocity		= 3;								// laengeneinheiten/ms

	private RigidBody	rigidBody		= null;
	private GroupNode	node			= null;
	private ETeam		team;

	/**
	 * 
	 * @param isTeamOne
	 *            is this paddle for team one?
	 */
	public AIPaddle(ETeam team)
	{
		try
		{
			float mass = 0f;

			BoxShape shape = new BoxShape(new Vector3f(5f, 4f, 0.5f));

			this.rigidBody = PhysicManager.getInstance().createRigidBody(this,
					shape,
					mass);

			this.rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
			this.rigidBody.setRestitution(1.5f);
			this.rigidBody.setDamping(0f, 0f);
			this.rigidBody.setFriction(1f);

			SceneNode paddle;
			node = new GroupNode();

			this.team = team;

			setRotationX(-(float) Math.PI / 2);

			if (team == ETeam.RED)
			{
				// loading Paddle mesh
				paddle = ResourceLoader.loadCollada("meshes/paddle_red.DAE");

				// paddleNode.setTransform(Transform.translate(position).mul(Transform.rotateX(-(float)
				// (Math.PI / 2))));

				this.rigidBody.proceedToTransform(new Transform(
						new Matrix4f(Matrix4.translate(new Vector3(0f, 0f,
								-48.0f)).getData())));
			}
			else
			{
				// loading Paddle mesh
				paddle = ResourceLoader.loadCollada("meshes/paddle_blue.DAE");
				// paddleNode.setTransform(Transform.translate(position).mul(Transform.rotateX(-(float)
				// (Math.PI / 2))));

				this.rigidBody.proceedToTransform(new Transform(
						new Matrix4f(Matrix4.translate(new Vector3(0.0f, 0f,
								46.5f)).getData())));
			}
			node.addChildNode(paddle);
		}
		catch (Exception e)
		{
			logger.error(e);
		}
	}

	@Override
	public SceneNode getNode()
	{
		return this.node;
	} 

	/**
	 * @return the velocity
	 */
	public double getVelocity()
	{
		return velocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(double velocity)
	{
		this.velocity = velocity;
	}

	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		// only calc AI in server mode
		if (inServerMode)
		{
			// paddle tries to get to the ball position as fast as possible
			Vector3f ballPosition = new Vector3f();
			game.getBall().getRigidBody().getCenterOfMassPosition(ballPosition);

			Vector3 moveDir = (new Vector3(ballPosition.x, getPosition().y(),
					getPosition().z())).sub(getPosition());

			if (moveDir.length() > (float) (velocity * elapsedTime))
			{				
				moveDir = moveDir.normalize().mul((float) (velocity * elapsedTime));
			}

			Vector3 newPos = getPosition().add(moveDir);
			if (newPos.x() < 27f && newPos.x() > -27f)
				this.setPosition(newPos);
		}
//		else
//		{
//			// client mode interpolates its position
//			Vector3 movementDirection = positionToReach.sub(getPosition());
//			if (movementDirection.length() > (float) (velocity * elapsedTime))
//			{
//				movementDirection = movementDirection.normalize().mul((float) (velocity * elapsedTime));
//			}
//
//			Vector3 newPos = getPosition().add(movementDirection);
//			setPosition(newPos);
//		}

		// transform the rigid body
		if (team.equals(ETeam.RED))
		{
			this.rigidBody.proceedToTransform(new Transform(
					new Matrix4f(Matrix4.translate(new Vector3(
							getPosition().x(), 4f, -48.0f)).getData())));
		}
		else
		{
			this.rigidBody.proceedToTransform(new Transform(
					new Matrix4f(Matrix4.translate(new Vector3(
							getPosition().x(), 4f, 48.0f)).getData())));
		}

		super.update(elapsedTime, game, inServerMode);
	}

	public ETeam getTeam()
	{
		return team;
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
	}

}
