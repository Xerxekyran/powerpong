package de.noobisoft.powerpong.effects;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.domain.player.ETeam;
import de.noobisoft.powerpong.domain.player.weapon.EWeapons;
import de.noobisoft.powerpong.domain.player.weapon.GlueBall;
import de.noobisoft.powerpong.domain.player.weapon.GluePaste;
import de.noobisoft.powerpong.domain.player.weapon.ObjectGunObject;
import de.noobisoft.powerpong.domain.player.weapon.Peaball;
import de.noobisoft.powerpong.physics.LifetimeObject;
import de.noobisoft.powerpong.physics.PhysicManager;

/**
 * This class handles all effects on the clients. It creates and updates things
 * like particles and weapon shots
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 */
public class ClientEffectManager
{
	private static ClientEffectManager	instance		= null;
	private Game						game			= null;

	private List<ParticleSystem>		particleSystems	= new LinkedList<ParticleSystem>();

	private List<ParticleSystem>		deleteList		= new LinkedList<ParticleSystem>();

	/**
	 * private ctor to fit the singleton pattern
	 */
	private ClientEffectManager()
	{
	}

	/**
	 * Updates the created effects, like moving the particles
	 * 
	 * @param elapsedTime
	 *            time passed sinsce the last update cycle
	 * @param game
	 *            the game the effects should be part of
	 */
	public void update(double elapsedTime, Game game)
	{
		// update the particle systems
		for (ParticleSystem p : this.particleSystems)
		{
			p.update(elapsedTime, game, false);
			if (p.isParticleSystemTooOld())
			{
				deleteList.add(p);
				((GroupNode) this.game.getNode()).removeChildNode(p.getNode());
			}
		}

		// do we have to delete a particle system? do so
		if (deleteList.size() > 0)
		{
			this.particleSystems.removeAll(deleteList);
			deleteList.clear();
		}

	}

	/**
	 * creates a new particle system at the given position for a weapon
	 * 
	 * @param position
	 *            the position of the new particle system
	 * @param direction
	 *            the main direction for the particles to fly
	 * @param weaponType
	 *            the type of weapon the particle system should be created for
	 */
	public void createWeaponParticleEffect(	Vector3 position,
											Vector3 direction,
											EWeapons weaponType)
	{
		ParticleSystem p = null;

		switch (weaponType)
		{
		case PeaGun:
			p = new ParticleSystem(position.add(direction.div(3)), // position
					direction, // the main direction
					new Vector3(0.02f, 0.02f, 0.02f), // directionDistortion
					4f, // minSpeed
					5f, // maxSpeed
					10, // number of particles
					10,// emit rate
					0.03f, // size of particles
					0.5, // duration time of the particle system in seconds
					false, // continously emitting (reuse particles that do not
					// have enough energy any more?)
					0.1, // the energy damping
					0.4f, // the start energy of each particle
					"textures/smoke.bmp", // the sprite texture file
					new Vector3(0f, -1.1f, 0f) // the gravity vector
			);
			break;
		case GluePaste:
			p = new ParticleSystem(position, // position
					direction, // the main direction
					new Vector3(1.0f, 0.2f, 1.0f), // directionDistortion
					2f, // minSpeed
					3f, // maxSpeed
					20, // number of particles
					10,// emit rate
					0.2f, // size of particles
					4, // duration time of the particle system in seconds
					true, // continously emitting (reuse particles that do not
					// have enough energy any more?)
					0.15, // the energy damping
					1f, // the start energy of each particle
					"textures/slime.bmp", // the sprite texture file
					new Vector3(0f, -3.1f, 0f) // the gravity vector
			);
			break;
		case ObjectActivator:
			p = new ParticleSystem(position, // position
					direction, // the main direction
					new Vector3(2.0f, 0.2f, 2.0f), // directionDistortion
					0.05f, // minSpeed
					1f, // maxSpeed
					20, // number of particles
					10,// emit rate
					0.3f, // size of particles
					2, // duration time of the particle system in seconds
					false, // continously emitting (reuse particles that do not
					// have enough energy any more?)
					0.1, // the energy damping
					0.8f, // the start energy of each particle
					"textures/smoke.bmp", // the sprite texture file
					new Vector3(0f, 1.0f, 0f) // the gravity vector
			);
			break;
		}

		if (p != null)
		{
			particleSystems.add(p);
			((GroupNode) this.game.getNode()).addChildNode(p.getNode());
		}
	}

	/**
	 * creates a fire pole at the given position
	 * 
	 * @param position
	 *            the position for the firepole
	 */
	public void createFirePole(Vector3 position)
	{
		ParticleSystem p = new ParticleSystem(position, // position
				new Vector3(0, 1, 0), // the main direction
				new Vector3(0.3f, 0.1f, 0.3f), // directionDistortion
				0.002f, // minSpeed
				0.3f, // maxSpeed
				100, // number of particles
				50,// emit rate
				0.4f, // size of particles
				0, // duration time of the particle system in seconds
				true, // continously emitting (reuse particles that do not
				// have enough energy any more?)
				0.1, // the energy damping
				1.9f, // the start energy of each particle
				"textures/fire.bmp", // the sprite texture file
				new Vector3(0.0f, 0.3f, -0.3f) // the gravity vector
		);

		particleSystems.add(p);
		((GroupNode) this.game.getNode()).addChildNode(p.getNode());
	}

	/**
	 * creates the visual and physical components of a weapon shot at a given
	 * position in the game world
	 * 
	 * @param weaponType
	 *            the weapon that should be fired
	 * @param position
	 *            the starting position of the shot
	 * @param direction
	 *            the direction of the shot
	 * @param team
	 *            for which team should the effect be created?
	 * @param id
	 *            if possible an id for the shot
	 */
	public Object createWeaponShot(	EWeapons weaponType,
									Vector3 position,
									Vector3 direction,
									ETeam team,
									int id)
	{
		Object ret = null;

		switch (weaponType)
		{
		case PeaGun:
			// create a new body for the shot
			Peaball newPeaGunShot = new Peaball(position, direction, team, id);

			// add it to the game
			((GroupNode) game.getNode()).addChildNode(newPeaGunShot.getNode());

			ret = newPeaGunShot;
			break;

		case GlueGun:
			// create a new body for the shot
			GlueBall newGlueGunShot = new GlueBall(position, direction, id);

			// add it to the game
			((GroupNode) game.getNode()).addChildNode(newGlueGunShot.getNode());

			ret = newGlueGunShot;
			break;

		case GluePaste:
			GluePaste paste = new GluePaste(position);

			// add it to the game
			((GroupNode) game.getNode()).addChildNode(paste.getNode());
			game.addGluePaste(paste);

			long timeToDel = (new Date()).getTime() + 4000;

			// add it to the list of objects to be deleted after a while
			PhysicManager.getInstance().getLifeTimeObjects().add(new LifetimeObject(
					new Date(timeToDel), paste.getRigidBody()));

			ret = paste;
			break;

		case ObjectActivator:
			// create a new body for the shot
			position = position.add(direction.mul(3));
			ObjectGunObject newObjectGunShot = new ObjectGunObject(position,
					direction, id, true);

			// add it to the game
			((GroupNode) game.getNode()).addChildNode(newObjectGunShot.getNode());

			ret = newObjectGunShot;
			break;
		}

		return ret;
	}

	/**
	 * this effect looks like little fire sparkles. its used to show the
	 * collision of the ball with the wall or paddle
	 * 
	 * @param position
	 *            the collision position
	 */
	public void createBallCollisionSparkle(Vector3 position)
	{
		Vector3 direction = null;

		// wall collision?
		if (position.x() < -28)
		{
			position = new Vector3(-32, position.y(), position.z());
			direction = new Vector3(1, 1, 0);
		}
		else if (position.x() > 28)
		{
			position = new Vector3(32, position.y(), position.z());
			direction = new Vector3(-1, 1, 0);
		}
		else
			direction = new Vector3(0, 1, 0);

		// paddle collision?
		if (position.z() > 44)
		{
			position = new Vector3(position.x(), position.y(), 47);
			direction = direction.add(new Vector3(0, 0, -1));
		}
		else if (position.z() < -44)
		{
			position = new Vector3(position.x(), position.y(), -47);
			direction = direction.add(new Vector3(0, 0, 1));
		}

		ParticleSystem p = new ParticleSystem(position, // position
				direction, // the main direction
				new Vector3(0.1f, 0.4f, 0.5f), // directionDistortion
				5f, // minSpeed
				10f, // maxSpeed
				10, // number of particles
				10,// emit rate
				0.2f, // size of particles
				1, // duration time of the particle system in seconds
				false, // continously emitting (reuse particles that do not
				// have enough energy any more?)
				0.99, // the energy damping
				1f, // the start energy of each particle
				"textures/fire.bmp", // the sprite texture file
				new Vector3(0f, -10.1f, 0f) // the gravity vector
		);

		particleSystems.add(p);
		((GroupNode) this.game.getNode()).addChildNode(p.getNode());
	}

	/**
	 * singleton getter
	 * 
	 * @return the singleton instance
	 */
	public static ClientEffectManager getInstance()
	{
		if (instance == null)
		{
			instance = new ClientEffectManager();
		}

		return instance;
	}

	/**
	 * @param game
	 *            the game to set
	 */
	public void setGame(Game game)
	{
		this.game = game;
	}

}
