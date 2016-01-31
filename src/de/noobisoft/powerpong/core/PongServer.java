package de.noobisoft.powerpong.core;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import com.bulletphysics.dynamics.RigidBody;

import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.domain.player.ETeam;
import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.domain.player.weapon.EWeapons;
import de.noobisoft.powerpong.domain.player.weapon.ObjectGunObject;
import de.noobisoft.powerpong.domain.pong.GameRules;
import de.noobisoft.powerpong.network.MinaServer;
import de.noobisoft.powerpong.network.OutgoingMessageQueue;
import de.noobisoft.powerpong.network.PriorityMessageQueue;
import de.noobisoft.powerpong.network.messages.INetworkMessage;
import de.noobisoft.powerpong.network.messages.PlayerJoined;
import de.noobisoft.powerpong.network.messages.PlayerMoved;
import de.noobisoft.powerpong.network.messages.PlayerQuit;
import de.noobisoft.powerpong.network.messages.PlayerShot;
import de.noobisoft.powerpong.network.messages.Unknown;
import de.noobisoft.powerpong.network.messages.UpdateObjectGunObjects;
import de.noobisoft.powerpong.network.messages.UpdateWorld;
import de.noobisoft.powerpong.physics.PhysicManager;
import de.noobisoft.powerpong.util.ResourceLoader;

/**
 * The server main class.
 * 
 * @author Lars George
 * 
 */
public class PongServer extends Thread
{
	static Logger					logger					= Logger.getLogger(PongServer.class);

	// private ConcurrentLinkedQueue<INetworkMessage> messageQueue = new
	// ConcurrentLinkedQueue<INetworkMessage>();
	private PriorityMessageQueue	priorMessageQueue		= new PriorityMessageQueue();
	private boolean					abort					= false;
	private Game					game					= null;
	private MinaServer				networkServer			= null;
	private int						missedMessages			= 0;

	private static int				idCounter				= 0;

	// how often should the server send messages to client with the new data?
	private double					updateInterval			= 1.0 / 35.0;
	private final double			MIN_FOR_ONE_UPDATECYCLE	= 1.0 / 500.0;

	/**
	 * 
	 */
	public PongServer(int port)
	{
		try
		{
			logger.info("Starting server...");
			double startTime = System.nanoTime();

			// start the network layer
			networkServer = new MinaServer(port, this);

			// tell the resource loader, that we are in server mode (no models
			// pls)
			ResourceLoader.setServer(true);

			// create a game (the virtual world)
			game = new Game(true, null);

			// create the object that checks the rules
			game.setGameRules(new GameRules(game, networkServer));

			// time measurement of the starting process
			DecimalFormat f = new DecimalFormat("#0.00");
			startTime = ((System.nanoTime() - startTime) * 1e-9);
			logger.info("Server is up and running [" + f.format(startTime)
					+ "s]");
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

			this.networkServer.broadcast(msg.getNetworkData());
		}
	}

	@Override
	public void run()
	{
		// Save system time before entering the loop.
		long start = System.nanoTime();
		double timeTillLastUpdate = 0;

		while (!abort)
		{
			// Calculate frame duration in seconds.
			long now = System.nanoTime();
			double delta = (now - start) * 1e-9;

			// Die Ausführung zeitlich begrenzen, damit die CPU auch noch mal
			// atmen kann
			try
			{
				if (delta < MIN_FOR_ONE_UPDATECYCLE)
				{
					// Kurz warten
					long sleepTimeMS = (long) ((MIN_FOR_ONE_UPDATECYCLE - delta) * 10000.0);
					Thread.sleep(sleepTimeMS);

					// Die benötigte Zeit neu berechnen
					delta = (System.nanoTime() - start) * 1e-9;
				}
			}
			catch (InterruptedException e)
			{
				logger.error("Error while setting the Simulation to sleep for a short while..."
						+ e.toString());
			}

			start = now;
			timeTillLastUpdate += delta;

			// execute all incoming network events
			executeNextNetworkEvent();

			// perform simulation
			this.simulate(delta);

			if (timeTillLastUpdate >= this.updateInterval)
			{
				timeTillLastUpdate = 0;
				updateWorldToClients();
			}

			executeOutgoingNetworkEvents();
		}
	}

	/**
	 * Simulate the world for one frame.
	 * 
	 * @param elapsed
	 *            Duration of time to simulate.
	 */
	private void simulate(double elapsed)
	{
		PhysicManager.getInstance().update(elapsed, this.game);

		game.update(elapsed, this.game);
	}

	/**
	 * setter for the abort attribute to shut down the running method
	 * 
	 * @param abort
	 *            the new value
	 */
	public void setAbort(boolean abort)
	{
		this.abort = abort;
	}

	/**
	 * 
	 */
	public void executeNextNetworkEvent()
	{
		try
		{
			INetworkMessage msg;

			// Wait for data to become available
			synchronized (priorMessageQueue)
			{
				msg = priorMessageQueue.getNextMessage();

				if (msg == null)
					return;

				// check for the deadline -> should we execute it, or is it too
				// late?
				if (msg.getDeadline() < System.currentTimeMillis())
				{
					logger.error("Message Missed its Deadline [by "
							+ (msg.getDeadline() - System.currentTimeMillis())
							+ "ms] [total nr: " + missedMessages++ + "]");
					executeNextNetworkEvent();
					return;
				}
			}

			switch (msg.getType())
			{
			// -----------------------------------------------------------------------------
			// PlayerJoined
			// -----------------------------------------------------------------------------
			case PlayerJoined:
				PlayerJoined pjMsg = (PlayerJoined) msg;

				this.networkServer.addConnectedPlayer(pjMsg.getSession(),
						pjMsg.getPlayer().getId());

				this.game.addPlayer(pjMsg.getPlayer().getId(),
						pjMsg.getPlayer().getTeam());

				this.networkServer.broadcast(pjMsg.getNetworkData());

				// send the new client all players, who are already on the
				// server
				for (Player p : this.game.getAllPlayers().values())
				{
					PlayerJoined playerMessage = new PlayerJoined();
					playerMessage.setPlayer(p);
					pjMsg.getSession().write(playerMessage.getNetworkData());
				}

				// update the paddle movement
				if (pjMsg.getPlayer().getTeam().equals(ETeam.RED))
				{
					this.game.getPaddle1().setVelocity(this.game.getPaddle1().getVelocity() + 1);
				}
				else
				{
					this.game.getPaddle2().setVelocity(this.game.getPaddle2().getVelocity() + 1);
				}

				break;
			// -----------------------------------------------------------------------------
			// PlayerMoved
			// -----------------------------------------------------------------------------
			case PlayerMoved:
				PlayerMoved pMvd = (PlayerMoved) msg;
				this.game.changePositionOfPlayer(pMvd.getPlayerID(),
						pMvd.getNewPosition(),
						pMvd.getNewRotationY());
				break;

			// -----------------------------------------------------------------------------
			// PlayerShot
			// -----------------------------------------------------------------------------
			case PlayerShot:
				PlayerShot pShot = (PlayerShot) msg;
				Player shootingPlayer = this.game.getAllPlayers().get(pShot.getPlayerID());

				// does the player have enough energy?
				if (shootingPlayer.shoot())
				{
					// send the message to all clients, so they can create the
					// visual component for the shot
					pShot.setShotID(PongServer.idCounter++);
					OutgoingMessageQueue.getInstance().addMessage(pShot);

					// create the physical part, so it interacts with the world
					RigidBody body = PhysicManager.getInstance().createShot(pShot.getWeaponType(),
							pShot.getPosition(),
							pShot.getDirection(),
							null,
							pShot.getShotID());

					if (pShot.getWeaponType().equals(EWeapons.ObjectActivator))
						this.game.addObjectGunObject((ObjectGunObject) body.getMotionState());
				}

				break;

			// -----------------------------------------------------------------------------
			// Unknown
			// -----------------------------------------------------------------------------
			case Unknown:
				logger.warn("Got an unknown network message... something is wrong ["
						+ ((Unknown) msg).getUnknownData() + "]");
				break;
			}

			// Return to sender
			// msg.getNetworkServer().send(msg.getSocket(),
			// msg.getType().toString().getBytes());
		}
		catch (Exception e)
		{
			logger.error(e.toString());
		}
	}

	/**
	 * adds a new network message to be proceeded
	 * 
	 * @param msg
	 *            the new message that should be handled
	 */
	public void addMessage(INetworkMessage msg)
	{
		synchronized (priorMessageQueue)
		{
			priorMessageQueue.addMessage(msg);
		}
	}

	/**
	 * Removes if possible the player with the given id from the game
	 * 
	 * @param id
	 */
	public void removePlayer(String id)
	{
		// the methods returns true, if the player was successfully removed
		// update the paddle movement
		if (game.getAllPlayers().get(id).getTeam().equals(ETeam.RED))
		{
			this.game.getPaddle1().setVelocity(this.game.getPaddle1().getVelocity() - 1);
		}
		else
		{
			this.game.getPaddle2().setVelocity(this.game.getPaddle2().getVelocity() - 1);
		}

		if (this.game.removePlayer(id))
		{
			// send a message to all connected clients, that this player left
			// the game
			PlayerQuit pQuit = new PlayerQuit();
			pQuit.setPlayerID(id);
			OutgoingMessageQueue.getInstance().addMessage(pQuit);
			// this.networkServer.broadcast(pQuit.getNetworkData());
		}
	}

	/**
	 * 
	 */
	private void updateWorldToClients()
	{
		UpdateWorld msg = new UpdateWorld();
		msg.setPaddle1Position(this.game.getPaddle1().getPosition());
		msg.setPaddle2Position(this.game.getPaddle2().getPosition());
		msg.setBallTransform(this.game.getBall().getNode().getTransform().getMatrix());
		msg.setTrainPosition(this.game.getGhostTrain().getPosition());
		msg.setPlayers(this.game.getAllPlayers().values());

		OutgoingMessageQueue.getInstance().addMessage(msg);

		// now the object gun objects
		if (game.getObjectGunObjects().size() > 0)
		{
			UpdateObjectGunObjects gunMsg = new UpdateObjectGunObjects();
			gunMsg.setObjects(this.game.getObjectGunObjects());
			OutgoingMessageQueue.getInstance().addMessage(gunMsg);
		}
	}
}
