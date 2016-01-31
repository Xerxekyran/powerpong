package de.noobisoft.powerpong.network;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import de.noobisoft.powerpong.core.PongServer;
import de.noobisoft.powerpong.network.messages.INetworkMessage;
import de.noobisoft.powerpong.network.messages.MessageFactory;

/**
 * this class handles all network events, like incoming messages and new
 * connected clients.
 * 
 * @author Lars George
 * 
 */
public class ServerHandler extends IoHandlerAdapter
{
	static Logger					logger				= Logger.getLogger(ServerHandler.class);
	private Map<IoSession, String>	connectedPlayers	= Collections.synchronizedMap(new HashMap<IoSession, String>());
	private PongServer				server				= null;
	private MessageFactory			messageFac			= new MessageFactory();

	/**
	 * default ctor
	 * 
	 * @param server
	 *            a reference to the server object is needed, so the incoming
	 *            messages can be passed to it (as INetworkMessage objects)
	 */
	public ServerHandler(PongServer server)
	{
		this.server = server;
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception
	{
		String disconnectedPlayerID = connectedPlayers.remove(session);
		if (disconnectedPlayerID == null)
		{
			logger.warn("Could not remove the client from the list of connected clients, cause it was not found there.");
		}
		else
		{
			this.server.removePlayer(disconnectedPlayerID);
			logger.info("Client successfully removed from the list of active clients.");
		}
		session.close(true);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception
	{
		logger.error(cause.getMessage());
		String disconnectedPlayerID = connectedPlayers.remove(session);
		if (disconnectedPlayerID == null)
		{
			logger.warn("Could not remove the client from the list of connected clients, cause it was not found there.");
		}
		else
		{
			this.server.removePlayer(disconnectedPlayerID);
			logger.info("Client successfully removed from the list of active clients.");
		}
		session.close(true);
	}

	/**
	 * sends the given message to all connected clients
	 * 
	 * @param msg
	 *            the message to be send
	 */
	public void broadCast(Object msg)
	{
		synchronized (connectedPlayers)
		{
			for (IoSession s : connectedPlayers.keySet())
			{
				if (s.isConnected())
					s.write((String) msg);
			}
		}
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception
	{
		INetworkMessage msg = messageFac.createNetworkMessage((String) message);

		msg.setClientSession(session);
		server.addMessage(msg);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception
	{
		logger.debug("IDLE " + session.getIdleCount(status));
	}

	/**
	 * 
	 * @param session
	 * @param player
	 */
	public void addConnectedPlayer(IoSession session, String playerID)
	{
		this.connectedPlayers.put(session, playerID);
	}
}
