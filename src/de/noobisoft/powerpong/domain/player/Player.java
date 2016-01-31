package de.noobisoft.powerpong.domain.player;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.log4j.Logger;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.audio.AudioManager;
import de.noobisoft.powerpong.audio.ESoundEffects;
import de.noobisoft.powerpong.domain.AbstractSceneObject;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.domain.player.weapon.AbstractWeapon;
import de.noobisoft.powerpong.domain.player.weapon.GlueGun;
import de.noobisoft.powerpong.domain.player.weapon.ObjectGun;
import de.noobisoft.powerpong.domain.player.weapon.PeaGun;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.MaterialGenerator;
import de.noobisoft.powerpong.util.ResourceLoader;

/**
 * A Player. This can be the active controlled player or the others
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class Player extends AbstractSceneObject implements MotionState
{
	static Logger					logger						= Logger.getLogger(Player.class);

	private final double			ENERGY_RECOVER_PER_SECOND	= 3;
	private String					id;

	private CameraNode				activeCamera;
	private CameraNode				thirdPCamera;
	private CameraNode				egoCamera;
	private CameraNode				satelliteCamera;

	// 0:ego,
	// 1:thirdP,
	// 2:satellite
	private int						camNr						= 0;

	private GroupNode				playerAndCam;
	private SceneNode				playerNode;

	private RigidBody				rigidBody					= null;

	private ETeam					team;

	// private boolean isInvertedMovement = false;
	private boolean					isLockedMovement			= false;

	private Vector3					viewDirection				= new Vector3(
																		0.0f,
																		0.0f,
																		-1.0f);
	private Vector3					shootDirection				= new Vector3(
																		0.0f,
																		0.0f,
																		-1.0f);

	// private Vector3 moveDirection = new Vector3();
	private Vector3					startPosition				= new Vector3(
																		0, 2, 0);
	private final Vector3			upVector					= new Vector3(
																		0.0f,
																		1.0f,
																		0.0f);

	// koordinateneinheit pro ms
	// private float velocity = 10.0f;

	private boolean					firstSpawn					= true;
	// a list of weapons the player can use
	private List<AbstractWeapon>	weaponArsenal				= new LinkedList<AbstractWeapon>();
	private int						activeWeaponIndex			= 0;

	// the energy that is used for firing weapons
	private double					weaponEnergy				= 100;

	private SceneNode				currentWeaponNode			= new GroupNode();

	private SceneNode				energyBar					= null;
	private ShaderMaterial			energyBarShaderMaterial		= null;

	private boolean					wantToRespawn				= false;

	/**
	 * 
	 * @param id
	 *            the identifier for the player
	 * @param team
	 *            on which team is this player
	 * @param withWholeInit
	 *            should everything beeing initalized? false if the created
	 *            player object is only for data storage
	 */
	public Player(String id, ETeam team, boolean withWholeInit)
	{
		try
		{
			this.id = id;
			this.team = team;

			if (withWholeInit)
			{
				init();
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Initializes all parts of the player
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception
	{
		weaponArsenal.add(new PeaGun(team));
		weaponArsenal.add(new GlueGun(team));
		weaponArsenal.add(new ObjectGun(team));

		// physic stuff start *****
		float mass = 0.02f;

		BoxShape shape = new BoxShape(new Vector3f(1.0f, 1.0f, 1.0f));

		this.rigidBody = PhysicManager.getInstance().createRigidBody(this,
				shape,
				mass);

		this.rigidBody.proceedToTransform(new com.bulletphysics.linearmath.Transform(
				new Matrix4f(Matrix4.translate(startPosition).getData())));

		this.rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		this.rigidBody.setRestitution(1.0f);
		this.rigidBody.setDamping(1.5f, 1.5f);
		this.rigidBody.setFriction(0.1f);
		// physic stuff end *****

		playerAndCam = new GroupNode();

		// load the model
		if (team == ETeam.BLUE)
		{
			playerNode = ResourceLoader.getCollada("meshes/playerBlueRabbid.DAE");
		}
		else
		{
			playerNode = ResourceLoader.getCollada("meshes/playerRedMonkey.DAE");
		}

		// And a camera (aspect ration: 16:9 and field of view 60 degrees).
		thirdPCamera = new CameraNode("PlayerCameraThirdPerson", 1, 60);
		thirdPCamera.setTransform(Transform.translate(0, 0, 3));

		egoCamera = new CameraNode("PlayerCameraEgo", 1, 60);
		egoCamera.setTransform(Transform.translate(0, 0, 0));

		satelliteCamera = new CameraNode("PlayerSateliteEgo", 1, 60);
		satelliteCamera.setTransform(Transform.translate(0, 0, 100).mul(Transform.rotateXDeg(-90)));

		activeCamera = new CameraNode("Active Playercam", 1, 60);
		activeCamera.setTransform(egoCamera.getTransform());

		playerAndCam.addChildNode(playerNode);
		playerAndCam.addChildNode(activeCamera);
		playerAndCam.addChildNode(thirdPCamera);
		playerAndCam.addChildNode(egoCamera);

		// add the node for the weapon
		playerAndCam.addChildNode(currentWeaponNode);

		// setNextCam();

		// create the energy bar
		SceneNode box = ResourceLoader.getCollada("meshes/plane.dae");
		energyBarShaderMaterial = MaterialGenerator.makeEnergyBarMaterial();
		energyBarShaderMaterial.setMaterialClass("Weapon");
		energyBar = new ShapeNode("pdaShape", Finder.findGeometry(box, null),
				energyBarShaderMaterial);
		setEnergyBarTransformForActiveWeapon();
		
		respawn();
	}

	/**
	 * updates the uniform variables for the energy bar, so the shader shows the
	 * correct values
	 */
	private void updateEnergyBar()
	{
		// the shader needs a normalized value (between 0 and 1)
		float energyLevel = (float) getWeaponEnergy() / 100f;
		energyBarShaderMaterial.setUniform("AMBIENT",
				"energyLevel",
				new UniformFloat(energyLevel));
	}

	/**
	 * makes sure the active weapon is added to the scene node. removes the old
	 * weapon before that
	 */
	private void showCurrentWeapon()
	{
		playerAndCam.removeChildNode(currentWeaponNode);
		currentWeaponNode = this.weaponArsenal.get(this.activeWeaponIndex).getNode();
		playerAndCam.addChildNode(currentWeaponNode);

		playerAndCam.removeChildNode(energyBar);
		setEnergyBarTransformForActiveWeapon();
		playerAndCam.addChildNode(energyBar);
	}

	/**
	 * removes the scenenode of the weapon from the player groupnode
	 */
	private void hideCurrentWeapon()
	{
		playerAndCam.removeChildNode(currentWeaponNode);
		playerAndCam.removeChildNode(energyBar);
	}

	/**
	 * switches the weapon
	 */
	public void switchToNextWeapon()
	{
		activeWeaponIndex++;
		if (activeWeaponIndex > this.weaponArsenal.size())
			activeWeaponIndex = 0;

		// only show the weapon in first person view
		if (camNr == 0)
			showCurrentWeapon();
	}

	/**
	 * 
	 * @param weaponIndex
	 *            the index of the weapon that should be used
	 */
	public void changeWeapon(int weaponIndex)
	{
		if (weaponIndex > this.weaponArsenal.size() || weaponIndex < 0)
		{
			logger.warn("Tried to access a weapon index that is not possible ["
					+ weaponIndex + "]");
			return;
		}

		activeWeaponIndex = weaponIndex;

		// only show the weapon in first person view
		if (camNr == 0)
			showCurrentWeapon();
	}

	// for debugging purpose !!
	// public float debugPosX = 0.47f;
	// public float debugPosY = -0.45f;
	// public float debugPosZ = -1.22f;
	// public float debugRotZ = -2f;
	// public float debugRotY = 4f;
	// public float debugRotX = -87f;
	//
	// public void debugEnergyBar()
	// {
	// energyBar.setTransform(Transform.translate(debugPosX,
	// debugPosY,
	// debugPosZ).mul(Transform.rotateZDeg(debugRotZ)).mul(Transform.rotateYDeg(debugRotY)).mul(Transform.rotateXDeg(debugRotX).mul(Transform.scale(0.07f,
	// 0.07f, 0.07f))));
	// System.out.println(debugPosX + " || " + debugPosY + " || " + debugPosZ
	// + " || " + debugRotZ + " || " + debugRotY + " || " + debugRotX);
	// }

	/**
	 * sets the energy bar transformation corresponding for the currently active
	 * weapon
	 */
	private void setEnergyBarTransformForActiveWeapon()
	{
		switch (activeWeaponIndex)
		{
		case 0:
			// peagun
			energyBar.setTransform(Transform.translate(0.43f, -0.29f, -1.04f).mul(Transform.rotateZDeg(21f)).mul(Transform.rotateYDeg(-41f)).mul(Transform.rotateXDeg(51f)).mul(Transform.scale(0.07f,
					0.1f,
					0.07f)));
			break;
		case 1:
			// gluegun
			energyBar.setTransform(Transform.translate(0.48f, -0.44f, -1.28f).mul(Transform.rotateZDeg(-2f)).mul(Transform.rotateYDeg(2f)).mul(Transform.rotateXDeg(-87f)).mul(Transform.scale(0.07f,
					0.35f,
					0.07f)));
			break;
		case 2:
			// object activator
			energyBar.setTransform(Transform.translate(0.5f, -0.46f, -1.13f).mul(Transform.rotateZDeg(-3)).mul(Transform.rotateYDeg(5f)).mul(Transform.rotateXDeg(-10f)).mul(Transform.scale(0.07f,
					0.07f,
					0.07f)));
			break;
		}

	}

	/**
	 * 
	 * @return the current active weapon index
	 */
	public int getNextWeaponIndex()
	{
		activeWeaponIndex++;
		if (activeWeaponIndex >= this.weaponArsenal.size()
				|| activeWeaponIndex < 0)
			activeWeaponIndex = 0;
		return activeWeaponIndex;
	}

	/**
	 * 
	 * @return the weapon that is cvurrently equipped by the player
	 */
	public AbstractWeapon getActiveWeapon()
	{
		return weaponArsenal.get(activeWeaponIndex);
	}

	/**
	 * @return the isThirdPCam
	 */
	public int getCamNr()
	{
		return this.camNr;
	}

	/**
	 * @param isThirdPCam
	 *            the isThirdPCam to set
	 */
	public void setCam(int camNr)
	{
		// if we change view
		if (this.camNr != camNr)
		{
			switch (camNr)
			{
			case 0:
				this.activeCamera.setTransform(this.egoCamera.getTransform());
				showCurrentWeapon();
				isLockedMovement = false;
				break;
			case 1:
				this.activeCamera.setTransform(this.thirdPCamera.getTransform());
				hideCurrentWeapon();
				isLockedMovement = false;
				break;
			case 2:
				this.activeCamera.setTransform(this.satelliteCamera.getTransform());
				hideCurrentWeapon();
				isLockedMovement = true;
				// moveDirection = new Vector3();
				break;
			default:
				this.activeCamera.setTransform(this.egoCamera.getTransform());
				isLockedMovement = false;
				break;
			}
		}

		this.camNr = camNr;
	}

	/**
	 * @param isThirdPCam
	 *            the isThirdPCam to set
	 */
	public void setNextCam()
	{
		if (this.camNr < 1)
			setCam(camNr + 1);
		else
			setCam(0);
	}

	/**
	 * adds a vector up to simulate a jump move
	 */
	public void jump()
	{
		if (!isLockedMovement)
		{
			// only jump if not already in the air
			if (getPosition().y() <= 2)
			{
				this.rigidBody.applyCentralForce(new Vector3f(0, 7, 0));
			}
		}
	}

	/**
	 * Adds a vector to move the player in viewing direction
	 */
	public void moveForward()
	{
		if (!isLockedMovement)
		{
			Vector3 force = viewDirection.mul(0.2f);
			Vector3f newForce = new Vector3f(force.x(), force.y(), force.z());
			Vector3f currentForce = new Vector3f();
			this.rigidBody.getLinearVelocity(currentForce);
			currentForce = new Vector3f(newForce.x + currentForce.x, newForce.y
					+ currentForce.y, newForce.z + currentForce.z);

			if (currentForce.length() < 10)
			{
				this.rigidBody.applyCentralForce(new Vector3f(force.x(),
						force.y(), force.z()));
			}
		}
	}

	/**
	 * Adds a vector to move the player in the opposite of the viewing direction
	 */
	public void moveBackward()
	{
		if (!isLockedMovement)
		{
			Vector3 force = viewDirection.mul(-0.2f);
			Vector3f newForce = new Vector3f(force.x(), force.y(), force.z());
			Vector3f currentForce = new Vector3f();
			this.rigidBody.getLinearVelocity(currentForce);
			currentForce = new Vector3f(newForce.x + currentForce.x, newForce.y
					+ currentForce.y, newForce.z + currentForce.z);

			if (currentForce.length() < 10)
			{
				this.rigidBody.applyCentralForce(new Vector3f(force.x(),
						force.y(), force.z()));
			}
		}
	}

	/**
	 * Adds a vector to move the player to the left
	 */
	public void strafeLeft()
	{
		if (!isLockedMovement)
		{
			Vector3 force = viewDirection.cross(upVector).mul(-0.2f);
			Vector3f newForce = new Vector3f(force.x(), force.y(), force.z());
			Vector3f currentForce = new Vector3f();
			this.rigidBody.getLinearVelocity(currentForce);
			currentForce = new Vector3f(newForce.x + currentForce.x, newForce.y
					+ currentForce.y, newForce.z + currentForce.z);

			if (currentForce.length() < 10)
			{
				this.rigidBody.applyCentralForce(new Vector3f(force.x(),
						force.y(), force.z()));
			}
		}
	}

	/**
	 * Adds a vector to move the player to the right
	 */
	public void strafeRight()
	{
		if (!isLockedMovement)
		{
			Vector3 force = viewDirection.cross(upVector).mul(0.2f);
			Vector3f newForce = new Vector3f(force.x(), force.y(), force.z());
			Vector3f currentForce = new Vector3f();
			this.rigidBody.getLinearVelocity(currentForce);
			currentForce = new Vector3f(newForce.x + currentForce.x, newForce.y
					+ currentForce.y, newForce.z + currentForce.z);

			if (currentForce.length() < 10)
			{
				this.rigidBody.applyCentralForce(new Vector3f(force.x(),
						force.y(), force.z()));
			}
		}
	}

	/**
	 * fires the quipped weapon if possible
	 */
	public boolean shoot()
	{
		boolean weaponFired = false;

		// check the energy
		if (this.weaponEnergy >= getActiveWeapon().getNeededEnegery())
		{
			// the energy of the weapon is now used to shoot
			this.weaponEnergy -= getActiveWeapon().getNeededEnegery();

			weaponFired = true;
		}

		return weaponFired;
	}

	/**
	 * 
	 * @return
	 */
	private void checkPlayerPosition()
	{

		// teleport
		if (getPosition().z() < 6.8f && getPosition().z() > -6.8f
				&& getPosition().x() < -31.7f)
		{
			this.rigidBody.proceedToTransform(new com.bulletphysics.linearmath.Transform(
					new Matrix4f(Matrix4.translate(new Vector3(
							getPosition().x() + (31.7f * 2f),
							getPosition().y(), getPosition().z())).getData())));

			// setPosition(new
			// Vector3(getPosition().x()+(31.7f*2f),getPosition().y(),getPosition().z()));
		}
		else if (getPosition().z() < 6.8f && getPosition().z() > -6.8f
				&& getPosition().x() > 31.7f)
		{
			this.rigidBody.proceedToTransform(new com.bulletphysics.linearmath.Transform(
					new Matrix4f(Matrix4.translate(new Vector3(
							getPosition().x() - (31.7f * 2f),
							getPosition().y(), getPosition().z())).getData())));

			// setPosition(new
			// Vector3(getPosition().x()-(31.7f*2f),getPosition().y(),getPosition().z()));
		}

		if (getPosition().y() < -0.1 || getPosition().z() > 51
				|| getPosition().z() < -51)
		{
			respawn();
		}
	}

	/**
	 * called by the renderer to be able to add some effect flavour to this
	 * event
	 */
	public void doRespawn()
	{
		Vector3 spawnPosition = null;

		if (getTeam().equals(ETeam.RED))
		{
			spawnPosition = startPosition.sub(new Vector3(
					(new Random()).nextInt(50) - 25, 0, 20));
		}
		else
		{
			spawnPosition = startPosition.add(new Vector3(
					(new Random()).nextInt(50) - 25, 0, 20));
		}

		this.rigidBody.setLinearVelocity(new Vector3f());
		this.rigidBody.proceedToTransform(new com.bulletphysics.linearmath.Transform(
				new Matrix4f(Matrix4.translate(spawnPosition).getData())));

		// is this the initial spawn?
		if (firstSpawn)
		{
			firstSpawn = false;
		}
		else
		{
			// play sound
			AudioManager.getInstance().playSoundEffect(ESoundEffects.Aaaah);
		}

		wantToRespawn = false;
	}

	/**
	 * sets the player rabdomly positioned in his field (depending on the team)
	 */
	public void respawn()
	{
		// mark this player to be respawned (the action itself must be called by
		// the renderer)
		wantToRespawn = true;
	}

	/**
	 * 
	 * @return true if the player wants to respwan
	 */
	public boolean wantsToRespawn()
	{
		return this.wantToRespawn;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void changeViewDirection(float x, float y)
	{
		float xRot = getRotationX() + y;
		if (xRot >= -1.5f && xRot <= 1.5f)
			setRotationX(xRot);
		setRotationY(getRotationY() + x);
	}

	@Override
	public void update(double elapsedTime, Game game, boolean inServerMode)
	{
		if (inServerMode && weaponEnergy < 100)
		{
			// slowly regain energy
			this.weaponEnergy = this.weaponEnergy
					+ (elapsedTime * ENERGY_RECOVER_PER_SECOND);
			if (weaponEnergy > 100)
				weaponEnergy = 100;
		}
		else
		// only client updates
		{
			// update the energy bar
			updateEnergyBar();

			// maybe the player teleports or is off the ground
			checkPlayerPosition();
		}

		// set new position / view
		if (!isLockedMovement)
		{
			playerNode.setTransform(Transform.translate(new Vector3(0, 0, 0)).mul(Transform.rotateX(-getRotationX()
					- (float) (Math.PI / 2))));

			// change view direction
			super.update(elapsedTime, game, inServerMode);

			// this.playerAndCam.setTransform(Transform.translate(position).mul(Transform.rotateY(deltaXView)).mul(Transform.rotateX(deltaYView)));
			viewDirection = playerAndCam.getTransform().getMatrix().rotationMatrix().mul(new Vector3(
					0, 0, -1));
			viewDirection = viewDirection.sub(new Vector3(0, viewDirection.y(),
					0));
			viewDirection.normalize();

			shootDirection = playerAndCam.getTransform().getMatrix().rotationMatrix().mul(new Vector3(
					0, 0, -1));
		}

		// only use bullet coordinates if its you
		if (game.getCurrentPlayer() != null
				&& game.getCurrentPlayer().getId().equals(getId()))
		{
			Vector3f v = new Vector3f();
			v = this.rigidBody.getCenterOfMassPosition(v);
			setPosition(new Vector3(v.x, v.y, v.z));
		}
	}

	/**
	 * 
	 * @return
	 */
	public CameraNode getCamera()
	{
		// if (isThirdPCam)
		return this.activeCamera;
		// else
		// return this.egoCamera;
	}

	@Override
	public SceneNode getNode()
	{
		return this.playerAndCam;
	}

	public ETeam getTeam()
	{
		return team;
	}

	public String getId()
	{
		return this.id;
	}

	@Override
	public com.bulletphysics.linearmath.Transform getWorldTransform(com.bulletphysics.linearmath.Transform out)
	{
		if (getNode() != null)
		{
			float[] t = getNode().getTransform().getMatrix().getData();
			out.set(new Matrix4f(t));
		}
		return out;
	}

	@Override
	public void setWorldTransform(com.bulletphysics.linearmath.Transform worldTrans)
	{
		if (getNode() != null)
		{
			Matrix4f m = new Matrix4f();
			worldTrans.getMatrix(m);
			float[] newTrans = new float[] { m.m00, m.m01, m.m02, m.m03, m.m10,
					m.m11, m.m12, m.m13, m.m20, m.m21, m.m22, m.m23, m.m30,
					m.m31, m.m32, m.m33 };

			getNode().setTransform(new de.bht.jvr.core.Transform(new Matrix4(
					newTrans)));
		}
	}

	/**
	 * @return the viewDirection
	 */
	public Vector3 getShootDirection()
	{
		return shootDirection;
	}

	public void setCameraRatio(float ratio)
	{
		this.activeCamera.setAspectRatio(ratio);
		this.egoCamera.setAspectRatio(ratio);
		this.satelliteCamera.setAspectRatio(ratio);
		this.thirdPCamera.setAspectRatio(ratio);
	}

	/**
	 * @return the weaponEnergy
	 */
	public double getWeaponEnergy()
	{
		return weaponEnergy;
	}

	/**
	 * @param weaponEnergy
	 *            the weaponEnergy to set
	 */
	public void setWeaponEnergy(double weaponEnergy)
	{
		this.weaponEnergy = weaponEnergy;
	}
}
