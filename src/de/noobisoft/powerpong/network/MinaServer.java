package de.noobisoft.powerpong.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import de.noobisoft.powerpong.core.PongServer;

/**
 * This class represents the network layer using the Mina API and therefore java
 * nio. It opens a serversocket on the given port and listens for clients to
 * connect to it.
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class MinaServer
{
	static Logger			logger	= Logger.getLogger(MinaServer.class);

	private ServerHandler	handler	= null;

	/**
	 * 
	 * @param port
	 * @param pongServer
	 * @throws IOException
	 */
	public MinaServer(int port, PongServer pongServer) throws IOException
	{
		logger.debug("starting network server on port [" + port + "] ...");

		NioSocketAcceptor acceptor = new NioSocketAcceptor();

		// set the logger
		LoggingFilter logFilter = new LoggingFilter();
		logFilter.setMessageReceivedLogLevel(LogLevel.NONE);
		logFilter.setMessageSentLogLevel(LogLevel.NONE);
		logFilter.setExceptionCaughtLogLevel(LogLevel.NONE);
		logFilter.setSessionClosedLogLevel(LogLevel.NONE);
		logFilter.setSessionOpenedLogLevel(LogLevel.NONE);
		logFilter.setSessionCreatedLogLevel(LogLevel.NONE);

		acceptor.getFilterChain().addLast("logger", logFilter);
		acceptor.getFilterChain().addLast("codec",
				new PongProtocolCodecFilter(new TextLineCodecFactory(
						Charset.forName("UTF-8"))));

		// set the handler for the network events
		this.handler = new ServerHandler(pongServer);

		acceptor.setHandler(this.handler);
		acceptor.getSessionConfig().setReadBufferSize(2048);
		// acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(port));
	}

	/**
	 * sends the given obect to all connected clients
	 * 
	 * @param msg
	 *            the message to be send
	 */
	public void broadcast(Object msg)
	{
		this.handler.broadCast(msg);
	}

	/**
	 * 
	 * @param session
	 * @param player
	 */
	public void addConnectedPlayer(IoSession session, String playerID)
	{
		this.handler.addConnectedPlayer(session, playerID);
	}
}
