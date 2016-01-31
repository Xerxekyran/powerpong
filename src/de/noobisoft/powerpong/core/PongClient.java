package de.noobisoft.powerpong.core;

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.audio.AudioManager;
import de.noobisoft.powerpong.audio.ESoundEffects;
import de.noobisoft.powerpong.config.ClientConfiguration;
import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.domain.player.weapon.EWeapons;
import de.noobisoft.powerpong.domain.player.weapon.ObjectGunObject;
import de.noobisoft.powerpong.effects.ClientEffectManager;
import de.noobisoft.powerpong.network.INetworkMessageReceiver;
import de.noobisoft.powerpong.network.MinaClient;
import de.noobisoft.powerpong.network.OutgoingMessageQueue;
import de.noobisoft.powerpong.network.messages.INetworkMessage;
import de.noobisoft.powerpong.network.messages.PlayerJoined;
import de.noobisoft.powerpong.network.messages.PlayerMoved;
import de.noobisoft.powerpong.network.messages.PlayerQuit;
import de.noobisoft.powerpong.network.messages.PlayerShot;
import de.noobisoft.powerpong.network.messages.ScoreBoard;
import de.noobisoft.powerpong.network.messages.StartEffect;
import de.noobisoft.powerpong.network.messages.Unknown;
import de.noobisoft.powerpong.network.messages.UpdateObjectGunObjects;
import de.noobisoft.powerpong.network.messages.UpdateWorld;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.ResourceLoader;
import de.noobisoft.powerpong.util.SplashScreen;

/**
 * The client main class
 * 
 * @author Lars George
 * 
 */
public class PongClient extends Thread implements INetworkMessageReceiver
{
	static Logger									logger					= Logger.getLogger(PongClient.class);

	private ConcurrentLinkedQueue<INetworkMessage>	incomingMessageQueue	= new ConcurrentLinkedQueue<INetworkMessage>();
	private Renderer								renderer				= null;
	private MinaClient								networkClient			= null;

	/* The time interval for the client to send updates to the server */
	private final double							serverUpdateInterval	= 1.0 / 20.0;

	PhysicManager									physics					= null;

	/**
	 * @throws Throwable
	 * 
	 */
	public PongClient() throws Throwable
	{
		try
		{
			SplashScreen startScreen = new SplashScreen();
			startScreen.setVisible(true);

			logger.info("Starting client.");
			double startTime = System.nanoTime();

			// init physics
			physics = PhysicManager.getInstance();

			// init audio
			AudioManager.getInstance();

			// init mesh loading
			ResourceLoader.preLoadModels();

			// create the network layer
			networkClient = new MinaClient(ClientConfiguration.serverIP,
					Integer.parseInt(ClientConfiguration.serverPort), this);

			// create a renderer (this one holds the game as well)
			renderer = new Renderer(networkClient);

			// this call is needed, to init all objects, that needed a working
			// game object
			renderer.getGame().initAfterCreation();

			// time measurement of the starting process
			DecimalFormat f = new DecimalFormat("#0.00");
			startTime = ((System.nanoTime() - startTime) * 1e-9);
			logger.info("Client up and running [" + f.format(startTime) + "s]");

			// cYa start splash screen
			startScreen.setVisible(false);
			startScreen.dispose();

			Thread t = new Thread(networkClient);
			t.setDaemon(true);
			t.start();
		}
		catch (Exception e)
		{
			logger.error(e.toString());
		}
	}

	@Override
	public void run()
	{
		try
		{
			// send the join message
			PlayerJoined helloMSG = new PlayerJoined();
			helloMSG.setPlayer(this.renderer.getGame().getCurrentPlayer());

			while (!networkClient.send(helloMSG.getNetworkData()))
			{
				Thread.sleep(100);
			}

			// Save system time before entering the loop.
			long start = System.nanoTime();

			double timeTillLastUpdate = 0;

			while (renderer.getViewer().isRunning())
			{
				// Calculate frame duration in seconds.
				long now = System.nanoTime();
				double delta = (now - start) * 1e-9;
				start = now;

				timeTillLastUpdate += delta;

				// execute all incoming network events
				executeNetworkEvents();

				renderer.update(delta);

				// is it time to send an update to the server?
				if (timeTillLastUpdate >= this.serverUpdateInterval)
				{
					timeTillLastUpdate = 0;
					sendUpdateToServer();
				}

				// send the outgoing network messages
				executeOutgoingNetworkEvents();
			}
		}
		catch (Exception e)
		{
			logger.error(e.toString());
		}
	}

	/**
	 * sends messages in the outgoing Queue to the server
	 */
	private void executeOutgoingNetworkEvents()
	{
		INetworkMessage msg;
		for (int i = 0; i < 10; i++)
		{
			msg = OutgoingMessageQueue.getInstance().getNextMessage();
			if (msg == null)
				return;

			this.networkClient.send(msg.getNetworkData());
		}
	}

	/**
	 * adds a message with the current player status information to the outgoing
	 * queue
	 */
	private void sendUpdateToServer()
	{
		PlayerMoved playerMSG = new PlayerMoved();
		Player p = this.renderer.getGame().getCurrentPlayer();
		playerMSG.setPlayerID(p.getId());
		playerMSG.setNewPosition(p.getPosition());
		playerMSG.setNewRotationY(p.getRotationY());

		OutgoingMessageQueue.getInstance().addMessage(playerMSG);
	}

	/**
	 * 
	 */
	private void executeNetworkEvents()
	{
		INetworkMessage msg;
		for (int i = 0; i < 10; i++)
		{

			// Wait for data to become available
			synchronized (incomingMessageQueue)
			{
				if (incomingMessageQueue.isEmpty())
					return;

				msg = incomingMessageQueue.remove();
			}

			switch (msg.getType())
			{
			// -----------------------------------------------------------------------------
			// PlayerJoined
			// -----------------------------------------------------------------------------
			case PlayerJoined:

				PlayerJoined pjMsg = (PlayerJoined) msg;

				// if the message is not about our player add a new one
				if (!this.renderer.getGame().getCurrentPlayer().getId().equals(pjMsg.getPlayer().getId()))
				{
					this.renderer.getGame().addPlayer(pjMsg.getPlayer().getId(),
							pjMsg.getPlayer().getTeam());
				}

				break;
			// -----------------------------------------------------------------------------
			// PlayerShot
			// -----------------------------------------------------------------------------
			case PlayerShot:

				PlayerShot pShot = (PlayerShot) msg;

				// for the shooting ball use the exact position of the message,
				// so the calculations are more like the one on the server
				Object shot = ClientEffectManager.getInstance().createWeaponShot(pShot.getWeaponType(),
						pShot.getPosition().add(pShot.getDirection()),
						pShot.getDirection(),
						this.renderer.getGame().getTeamForName(pShot.getPlayerID()),
						pShot.getShotID());

				// if we have a new object activator object
				if (pShot.getWeaponType().equals(EWeapons.ObjectActivator)
						&& shot != null)
				{
					this.renderer.addObjectGunObject((ObjectGunObject) shot);
				}

				boolean iAmTheShooter = pShot.getPlayerID().equals(this.renderer.getGame().getCurrentPlayer().getId());
				Vector3 spawnPosition = pShot.getPosition();
				Vector3 shotDirection = pShot.getDirection();

				// if i am the shooter, than use the more precise position on
				// the client to create the visual effect
				if (iAmTheShooter)
				{
					spawnPosition = this.renderer.getGame().getCurrentPlayer().getPosition();
					shotDirection = this.renderer.getGame().getCurrentPlayer().getShootDirection();
				}

				switch (pShot.getWeaponType())
				{
				case GlueGun:
					AudioManager.getInstance().playSoundEffect(ESoundEffects.Blob);

					// create a particle effect for the shot
					ClientEffectManager.getInstance().createWeaponParticleEffect(spawnPosition,
							shotDirection,
							pShot.getWeaponType());
					break;
				case PeaGun:
					AudioManager.getInstance().playSoundEffect(ESoundEffects.Frrrr);
					// create a particle effect for the shot
					ClientEffectManager.getInstance().createWeaponParticleEffect(spawnPosition,
							shotDirection,
							pShot.getWeaponType());
					break;
				case ObjectActivator:
					AudioManager.getInstance().playSoundEffect(ESoundEffects.Schwup);
					// create a particle effect for the shot
					ClientEffectManager.getInstance().createWeaponParticleEffect(spawnPosition.add(shotDirection.mul(0.5f)),
							shotDirection,
							pShot.getWeaponType());
					break;
				case GluePaste:
					AudioManager.getInstance().playSoundEffect(ESoundEffects.Blobftsch);

					// create a particle effect for the shot
					ClientEffectManager.getInstance().createWeaponParticleEffect(pShot.getPosition(),
							new Vector3(0, 1, 0),
							pShot.getWeaponType());
					break;
				}

				break;
			// -----------------------------------------------------------------------------
			// PlayerQuit
			// -----------------------------------------------------------------------------
			case PlayerQuit:

				PlayerQuit pQuit = (PlayerQuit) msg;
				boolean removed = this.renderer.getGame().removePlayer(pQuit.getPlayerID());

				logger.debug("Removing player [" + pQuit.getPlayerID() + "]: "
						+ removed);

				break;
			// -----------------------------------------------------------------------------
			// StartEffect
			// -----------------------------------------------------------------------------
			case StartEffect:

				StartEffect sEffect = (StartEffect) msg;

				if (sEffect.getEffectID() == StartEffect.EFFECT_SPARK)
					ClientEffectManager.getInstance().createBallCollisionSparkle(sEffect.getPosition());

				break;
			// -----------------------------------------------------------------------------
			// ScoreBoard
			// -----------------------------------------------------------------------------
			case ScoreBoard:

				ScoreBoard sb = (ScoreBoard) msg;

				// playing sound for new scoreboard
				if (sb.getScoreTeam1() == 25)
				{
					AudioManager.getInstance().playSoundEffect(ESoundEffects.BlueWin);
					this.renderer.setTextOverlay("Blue wins!", 10);
					logger.debug("Playing sound 1");
				}
				else if (sb.getScoreTeam2() == 25)
				{
					AudioManager.getInstance().playSoundEffect(ESoundEffects.RedWin);
					this.renderer.setTextOverlay("Red wins!", 10);
					logger.debug("Playing sound 2");
				}
				else if (this.renderer.getGame().getTeam1().getScore() != sb.getScoreTeam1())
				{
					AudioManager.getInstance().playSoundEffect(ESoundEffects.BluePoint);
					this.renderer.setTextOverlay("Blue point", 3);
					logger.debug("Playing sound 3");
				}
				else if (this.renderer.getGame().getTeam2().getScore() != sb.getScoreTeam2())
				{
					AudioManager.getInstance().playSoundEffect(ESoundEffects.RedPoint);
					this.renderer.setTextOverlay("Red point", 3);
					logger.debug("Playing sound 4");
				}
				else
				{
					AudioManager.getInstance().playSoundEffect(ESoundEffects.RefereesBall);
					logger.debug("Playing sound 5");
				}

				this.renderer.getGame().getTeam1().setScore(sb.getScoreTeam1());
				this.renderer.getGame().getTeam2().setScore(sb.getScoreTeam2());
				logger.debug("New Score [" + sb.getScoreTeam1() + ":"
						+ sb.getScoreTeam2() + "]");

				break;
			// -----------------------------------------------------------------------------
			// UpdateWorld
			// -----------------------------------------------------------------------------
			case UpdateWorld:
				UpdateWorld upMsg = (UpdateWorld) msg;

				this.renderer.getGame().getPaddle1().setPosition(upMsg.getPaddle1Position());
				this.renderer.getGame().getPaddle2().setPosition(upMsg.getPaddle2Position());
				this.renderer.getGame().getGhostTrain().setPosition(upMsg.getTrainPosition());
				this.renderer.getGame().getBall().setMatrixFromServer(upMsg.getBallTransform());

				for (Player p : upMsg.getPlayers())
				{
					// ignore position of yourself
					if (p.getId().equals(this.renderer.getGame().getCurrentPlayer().getId()))
					{
						this.renderer.getGame().changeWeaponEnergyOfPlayer(p.getId(),
								p.getWeaponEnergy());
						continue;
					}

					// set the players to their new position
					this.renderer.getGame().changePositionOfPlayer(p.getId(),
							p.getPosition(),
							p.getRotationY());
				}

				break;
			// -----------------------------------------------------------------------------
			// UpdateObjectGunObjects
			// -----------------------------------------------------------------------------
			case UpdateObjectGunObjects:
				UpdateObjectGunObjects upGunMsg = (UpdateObjectGunObjects) msg;
				this.renderer.getGame().updateObjectGunObjects(upGunMsg.getObjects());
				break;
			// -----------------------------------------------------------------------------
			// Unknown
			// -----------------------------------------------------------------------------
			case Unknown:
				logger.warn("Got an unknown network message... something is wrong ["
						+ ((Unknown) msg).getUnknownData() + "]");
				break;
			}
		}
	}

	@Override
	public void addIncomingMessage(INetworkMessage msg)
	{
		synchronized (incomingMessageQueue)
		{
			this.incomingMessageQueue.add(msg);
		}
	}
}
