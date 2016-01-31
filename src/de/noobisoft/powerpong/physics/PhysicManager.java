package de.noobisoft.powerpong.physics;

import java.util.Date;
import java.util.LinkedList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.log4j.Logger;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.domain.player.AIPaddle;
import de.noobisoft.powerpong.domain.player.weapon.EWeapons;
import de.noobisoft.powerpong.domain.player.weapon.GlueBall;
import de.noobisoft.powerpong.domain.player.weapon.GlueGun;
import de.noobisoft.powerpong.domain.player.weapon.GluePaste;
import de.noobisoft.powerpong.domain.player.weapon.ObjectGun;
import de.noobisoft.powerpong.domain.player.weapon.ObjectGunObject;
import de.noobisoft.powerpong.domain.player.weapon.PeaGun;
import de.noobisoft.powerpong.domain.pong.Ball;
import de.noobisoft.powerpong.network.OutgoingMessageQueue;
import de.noobisoft.powerpong.network.messages.PlayerShot;
import de.noobisoft.powerpong.network.messages.StartEffect;

/**
 * a manager class handling the physical calculations
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class PhysicManager
{
	private static Logger				logger			= Logger.getLogger(PhysicManager.class);

	private static PhysicManager		instance		= null;
	private LinkedList<LifetimeObject>	lifeTimeObjects	= new LinkedList<LifetimeObject>();
	private DiscreteDynamicsWorld		dynWorld;

	/**
	 * singleton getter
	 * 
	 * @return
	 */
	public static PhysicManager getInstance()
	{
		if (instance == null)
		{
			instance = new PhysicManager();
			logger.info("PhysicManager up and running");
		}

		return instance;
	}

	/**
	 * private ctor for singleton pattern
	 */
	private PhysicManager()
	{
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();

		// Set up the collision configuration and dispatcher
		CollisionDispatcher dispatcher = new CollisionDispatcher(
				collisionConfiguration);

		// The actual physics solver
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		// the maximum size of the collision world.
		Vector3f worldAabbMin = new Vector3f(-110, -110, -110);
		Vector3f worldAabbMax = new Vector3f(110, 110, 110);
		int maxProxies = 4096;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin,
				worldAabbMax, maxProxies);

		dynWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache,
				solver, collisionConfiguration);

		dynWorld.setGravity(new Vector3f(0, -10f, 0));

		// create the groundplane
		createStaticPlanes();
	}

	/**
	 * 
	 */
	public void createStaticPlanes()
	{
		float mass = 0f;
		Vector3f localInertia = new Vector3f(0, 0, 0);

		// **********
		// Groundplane
		CollisionShape collShape = new BoxShape(new Vector3f(32.5f, 0.5f, 50f));

		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(0f, 0.5f, 0f));

		DefaultMotionState myMotionState = new DefaultMotionState(
				groundTransform);

		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass,
				myMotionState, collShape, localInertia);

		RigidBody body = new RigidBody(rbInfo);
		body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		body.setDamping(0f, 0f);
		dynWorld.addRigidBody(body);
		body.setUserPointer("Boden");

		// **********
		// Left wall A
		collShape = new BoxShape(new Vector3f(0.5f, 12.5f, 21.2f));
		groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(-32.5f, 12.5f, 28f));
		myMotionState = new DefaultMotionState(groundTransform);

		rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, collShape,
				localInertia);
		body = new RigidBody(rbInfo);
		body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		body.setRestitution(1.0f);
		body.setDamping(0f, 0f);
		body.setFriction(0.9f);
		dynWorld.addRigidBody(body);
		body.setUserPointer("Wall");

		// Left wall B
		collShape = new BoxShape(new Vector3f(0.5f, 12.5f, 21.2f));
		groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(-32.5f, 12.5f, -28f));
		myMotionState = new DefaultMotionState(groundTransform);

		rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, collShape,
				localInertia);
		body = new RigidBody(rbInfo);
		body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		body.setRestitution(1.0f);
		body.setDamping(0f, 0f);
		body.setFriction(0.9f);
		dynWorld.addRigidBody(body);
		body.setUserPointer("Wall");

		// Left wall TOP
		collShape = new BoxShape(new Vector3f(0.5f, 4f, 6.8f));
		groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(-32.5f, 12.5f, 0f));
		myMotionState = new DefaultMotionState(groundTransform);

		rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, collShape,
				localInertia);
		body = new RigidBody(rbInfo);
		body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		body.setRestitution(1.0f);
		body.setDamping(0f, 0f);
		body.setFriction(0.9f);
		dynWorld.addRigidBody(body);
		body.setUserPointer("Wall");

		// **********
		// Right wall A
		collShape = new BoxShape(new Vector3f(0.5f, 12.5f, 21.2f));
		groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(32.5f, 12.5f, 28f));
		myMotionState = new DefaultMotionState(groundTransform);

		rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, collShape,
				localInertia);
		body = new RigidBody(rbInfo);
		body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		body.setRestitution(1.0f);
		body.setDamping(0f, 0f);
		body.setFriction(0.9f);
		dynWorld.addRigidBody(body);
		body.setUserPointer("Wall");

		// Right wall B
		collShape = new BoxShape(new Vector3f(0.5f, 12.5f, 21.2f));
		groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(32.5f, 12.5f, -28f));
		myMotionState = new DefaultMotionState(groundTransform);

		rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, collShape,
				localInertia);
		body = new RigidBody(rbInfo);
		body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		body.setRestitution(1.0f);
		body.setDamping(0f, 0f);
		body.setFriction(0.9f);
		dynWorld.addRigidBody(body);
		body.setUserPointer("Wall");

		// Right wall TOP
		collShape = new BoxShape(new Vector3f(0.5f, 4f, 6.8f));
		groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(32.5f, 12.5f, 0f));
		myMotionState = new DefaultMotionState(groundTransform);

		rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, collShape,
				localInertia);
		body = new RigidBody(rbInfo);
		body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		body.setRestitution(1.0f);
		body.setDamping(0f, 0f);
		body.setFriction(0.9f);
		dynWorld.addRigidBody(body);
		body.setUserPointer("Wall");
	}

	/**
	 * creates a physical object, adds it to the world and returns its reference
	 * 
	 * @param mState
	 *            an object that receives the physical calculations
	 * @param colShape
	 *            the shape describing the kollision borders
	 * @param mass
	 *            the mass of the body
	 * @return a reference to the physical object
	 */
	public RigidBody createRigidBody(	MotionState mState,
										CollisionShape colShape,
										float mass)
	{
		// if the motionstate is null, set a default one
		if (mState == null)
			mState = new DefaultMotionState();

		// if mass is zero, it is a static body
		boolean isDynamic = (mass != 0f);

		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (isDynamic)
		{
			colShape.calculateLocalInertia(mass, localInertia);
		}

		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass,
				mState, colShape, localInertia);
		RigidBody body = new RigidBody(rbInfo);

		dynWorld.addRigidBody(body);

		return body;
	}

	/**
	 * creates a physical object for the given weapon type
	 * 
	 * @param weaponType
	 *            the type of weapon that should be fired
	 * @param position
	 *            the start position
	 * @param direction
	 *            the direction of the shot
	 * @param mState
	 *            the motion state object for the new physics object. if null is
	 *            given, a default object will be created
	 * @return a reference to the newly created physically object
	 */
	public RigidBody createShot(EWeapons weaponType,
								Vector3 position,
								Vector3 direction,
								MotionState mState,
								int id)
	{
		RigidBody newBody = null;

		switch (weaponType)
		{
		case PeaGun:
			SphereShape shape = new SphereShape(0.5f);
			newBody = createRigidBody(mState, shape, 0.5f);

			newBody.proceedToTransform(new Transform(new Matrix4f(
					Matrix4.translate(position).getData())));
			direction = direction.mul(PeaGun.POWER);
			newBody.setLinearVelocity(new Vector3f(direction.x(),
					direction.y(), direction.z()));

			long timeToDel = (new Date()).getTime() + PeaGun.SHOT_TTL;

			// add it to the list of objects to be deleted after a while
			this.lifeTimeObjects.add(new LifetimeObject(new Date(timeToDel),
					newBody));
			break;
		case GlueGun:
			if (mState == null)
			{
				mState = new GlueBall(position, direction, id);
				return ((GlueBall) mState).getRigidBody();
			}

			shape = new SphereShape(0.7f);

			newBody = createRigidBody(mState, shape, 0.1f);

			newBody.proceedToTransform(new Transform(new Matrix4f(
					Matrix4.translate(position).getData())));
			direction = direction.mul(GlueGun.POWER);
			newBody.setLinearVelocity(new Vector3f(direction.x(),
					direction.y(), direction.z()));

			timeToDel = (new Date()).getTime() + GlueGun.SHOT_TTL;

			// add it to the list of objects to be deleted after a while
			LifetimeObject timeOject = new LifetimeObject(new Date(timeToDel),
					newBody);
			newBody.setUserPointer(timeOject);
			this.lifeTimeObjects.add(timeOject);
			break;
		case ObjectActivator:
			if (mState == null)
			{
				mState = new ObjectGunObject(position, direction, id, true);
				return ((ObjectGunObject) mState).getRigidBody();
			}

			BoxShape bShape = new BoxShape(new Vector3f(1.0f, 1.0f, 1.0f));

			newBody = createRigidBody(mState, bShape, 0.4f);
			newBody.setDamping(0.9f, 0.9f);
			newBody.setFriction(0.5f);
			newBody.setHitFraction(0.5f);

			newBody.translate(new Vector3f(position.x(), position.y(),
					position.z()));
			// newBody.proceedToTransform(new Transform(
			// new Matrix4f(
			// Matrix4.scale(2f, 2f,
			// 2f).add(Matrix4.translate(position)).getData())));

			direction = direction.mul(ObjectGun.POWER);
			newBody.setLinearVelocity(new Vector3f(direction.x(),
					direction.y(), direction.z()));
			break;
		}

		return newBody;
	}

	/**
	 * 
	 * @param renderDuration
	 */
	public void update(double renderDuration, Game game)
	{
		dynWorld.stepSimulation((float) renderDuration, 10);

		int numManifolds = dynWorld.getDispatcher().getNumManifolds();

		for (int i = 0; i < numManifolds; i++)
		{
			PersistentManifold contactManifold = dynWorld.getDispatcher().getManifoldByIndexInternal(i);

			CollisionObject obA = (CollisionObject) contactManifold.getBody0();
			CollisionObject obB = (CollisionObject) contactManifold.getBody1();

			for (int j = 0; j < contactManifold.getNumContacts(); j++)
			{
				ManifoldPoint pt = contactManifold.getContactPoint(j);
				if (pt.getDistance() < 0.f)
				{
					Vector3f ptA = new Vector3f();
					Vector3f ptB = new Vector3f();
					ptA = pt.getPositionWorldOnA(ptA);
					ptB = pt.getPositionWorldOnB(ptB);
					// Vector3f normalOnB = pt.normalWorldOnB;

					RigidBody bodyA = (RigidBody) obA;
					RigidBody bodyB = (RigidBody) obB;

					checkGluePaste(bodyA, bodyB, game);

					checkBallWallCollide(bodyA, bodyB, game);
				}
			}
		}

		LinkedList<LifetimeObject> lifeTimeObjectsToDel = new LinkedList<LifetimeObject>();

		// check the lifetime objects for their deadline
		for (LifetimeObject o : this.lifeTimeObjects)
		{
			if (o.isTimeToDelete())
			{
				this.dynWorld.removeRigidBody(o.getRigidBody());
				MotionState ms = o.getRigidBody().getMotionState();

				// if there is a visual component, delete this one too
				if (AbstractSceneObject.class.isInstance(ms))
				{
					// remove it from the rootSceneNode of the game
					((GroupNode) game.getNode()).removeChildNode(((AbstractSceneObject) ms).getNode());

					if (ms instanceof GluePaste)
					{
						game.removeGluePaste((GluePaste) ms);
					}
				}

				lifeTimeObjectsToDel.add(o);
			}
		}

		// delete those, who are too old
		this.lifeTimeObjects.removeAll(lifeTimeObjectsToDel);
	}

	/**
	 * removes a rigid body
	 * 
	 * @param body
	 *            the body to delete
	 */
	public void removeRigidBody(RigidBody body)
	{
		this.dynWorld.removeRigidBody(body);
	}

	/**
	 * 
	 * @param bodyA
	 * @param bodyB
	 * @param game
	 */
	private void checkBallWallCollide(	RigidBody bodyA,
										RigidBody bodyB,
										Game game)
	{
		// the server checks for collisions and sends a message if needed
		if (game.isServermode())
		{
			Ball b = null;

			if ((bodyA.getMotionState().getClass().equals(AIPaddle.class))
					|| (bodyB.getMotionState().getClass().equals(AIPaddle.class))
					|| (bodyA.getUserPointer() != null
							&& bodyA.getUserPointer().getClass().equals(String.class) && ((String) bodyA.getUserPointer()).equals("Wall"))
					|| (bodyB.getUserPointer() != null
							&& bodyB.getUserPointer().getClass().equals(String.class) && ((String) bodyB.getUserPointer()).equals("Wall")))
			{

				if (bodyB.getMotionState().getClass().equals(Ball.class))
				{
					b = (Ball) bodyB.getMotionState();
				}
				else if (bodyA.getMotionState().getClass().equals(Ball.class))
				{
					b = (Ball) bodyA.getMotionState();
				}
			}

			// if we do have a ball object here, that means ball and wall had a
			// meeting
			if (b != null)
			{
				Vector3 ballpos = b.getNode().getTransform().getMatrix().translation();

				StartEffect effectMSG = new StartEffect();
				effectMSG.setEffectID(StartEffect.EFFECT_SPARK);
				effectMSG.setPosition(ballpos);

				OutgoingMessageQueue.getInstance().addMessage(effectMSG);
			}

		}
	}

	/**
	 * 
	 * @param bodyA
	 * @param bodyB
	 * @param game
	 */
	private void checkGluePaste(RigidBody bodyA, RigidBody bodyB, Game game)
	{
		if (ballHitsObject(bodyB, bodyA, GluePaste.class)
				| ballHitsObject(bodyA, bodyB, GluePaste.class))
		{
			Ball b = null;
			if (bodyA.getMotionState().getClass().equals(Ball.class))
			{
				b = (Ball) bodyA.getMotionState();
			}
			else
			{
				b = (Ball) bodyB.getMotionState();
			}

			Vector3f force = new Vector3f();
			force = b.getRigidBody().getLinearVelocity(force);
			force.scale(GlueGun.movementStopper);

			b.getRigidBody().setLinearVelocity(force);
		}

		// if the glueball hits the plane and therefore explodes
		if (objectHitsPlane(bodyA, bodyB, GlueBall.class)
				|| objectHitsPlane(bodyB, bodyA, GlueBall.class))
		{
			// set the lifetime of the object to 0 (glueball
			// explodes)
			((LifetimeObject) bodyB.getUserPointer()).setTimeToDelete(new Date(
					0));

			// the client visualisation is made when the network message is
			// received
			if (game.isServermode())
			{
				Vector3 position = ((GlueBall) bodyB.getMotionState()).getNode().getTransform().getMatrix().translation();
				GluePaste paste = createGlueAtPosition(position);
				((GroupNode) game.getNode()).addChildNode(paste.getNode());

				PlayerShot pShot = new PlayerShot();
				pShot.setWeaponType(EWeapons.GluePaste);
				pShot.setPosition(position);
				OutgoingMessageQueue.getInstance().addMessage(pShot);
			}
		}
	}

	/**
	 * checks the collision of two bodys for the purpose of glueBall and floor
	 * 
	 * @param bodyA
	 *            object a to test
	 * @param bodyB
	 *            object b to test
	 * @return true if its a blueBall / floor collision
	 */
	private boolean objectHitsPlane(RigidBody bodyA,
									RigidBody bodyB,
									Class<?> classToCheck)
	{
		return (bodyA.getUserPointer() != null
				&& bodyA.getUserPointer().getClass().equals(String.class)
				&& ((String) bodyA.getUserPointer()).equals("Boden") && bodyB.getMotionState().getClass().equals(classToCheck));
	}

	/**
	 * checks the collision of two bodys for the purpose of GluePaste and Ball
	 * 
	 * @param bodyA
	 *            object a to test
	 * @param bodyB
	 *            object b to test
	 * @return true if its a GluePaste / Ball collision
	 */
	private boolean ballHitsObject(	RigidBody bodyA,
									RigidBody bodyB,
									Class<?> objectToHit)
	{
		return (bodyA.getMotionState().getClass().equals(objectToHit) && bodyB.getMotionState().getClass().equals(Ball.class));
	}

	/**
	 * 
	 * @param position
	 */
	public GluePaste createGlueAtPosition(Vector3 position)
	{
		GluePaste paste = new GluePaste(position);

		long timeToDel = (new Date()).getTime() + 4000;

		// add it to the list of objects to be deleted after a while
		this.lifeTimeObjects.add(new LifetimeObject(new Date(timeToDel),
				paste.getRigidBody()));

		return paste;
	}

	/**
	 * @return the lifeTimeObjects
	 */
	public LinkedList<LifetimeObject> getLifeTimeObjects()
	{
		return lifeTimeObjects;
	}
}
