package de.noobisoft.powerpong.network;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * The Network layer based on the MINA Framework. It uses NIO
 * 
 * @author Lars George
 * 
 */
public class MinaClient extends Thread
{
	static Logger					logger			= Logger.getLogger(MinaClient.class);
	private static final long		CONNECT_TIMEOUT	= 30 * 1000L;							// 30
	// seconds

	private String					hostName		= "";
	private int						port			= 0;
	private NioSocketConnector		connector		= null;
	private ClientSessionHandler	sessionHandler	= null;
	private IoSession				session			= null;
	private boolean					abort			= false;

	/**
	 * 
	 * @param hostName
	 * @param port
	 * @param messageReceiver
	 * @throws Throwable
	 */
	public MinaClient(String hostName,
			int port,
			INetworkMessageReceiver messageReceiver) throws Throwable
	{
		this.hostName = hostName;
		this.port = port;

		connector = new NioSocketConnector();

		// Configure the service.
		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);

		connector.getFilterChain().addLast("codec",
				new PongProtocolCodecFilter(new TextLineCodecFactory(
						Charset.forName("UTF-8"))));

		LoggingFilter logger = new LoggingFilter();
		logger.setMessageReceivedLogLevel(LogLevel.NONE);
		logger.setMessageSentLogLevel(LogLevel.NONE);
		logger.setSessionClosedLogLevel(LogLevel.NONE);
		logger.setSessionOpenedLogLevel(LogLevel.NONE);
		logger.setSessionCreatedLogLevel(LogLevel.NONE);

		connector.getFilterChain().addLast("logger", logger);

		sessionHandler = new ClientSessionHandler(messageReceiver);

		connector.setHandler(sessionHandler);
	}
	
	public void shutdown()
	{
		logger.info("Closing network connection to server");
		connector.dispose();
	}


	public boolean send(Object msg)
	{
		if (session == null)
			return false;

		session.write(msg);
		return true;
	}

	@Override
	public void run()
	{
		while (!abort)
		{
			try
			{
				ConnectFuture future = connector.connect(new InetSocketAddress(
						hostName, port));
				future.awaitUninterruptibly();
				this.session = future.getSession();
				break;
			}
			catch (Exception e)
			{
				logger.error("Failed to connect: " + e.toString());
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					logger.error(e1.toString());
				}
			}
		}
		// wait until the summation is done
		// session.getCloseFuture().awaitUninterruptibly();

		// connector.dispose();
	}

	/**
	 * @return the abort
	 */
	public boolean isAbort()
	{
		return abort;
	}

	/**
	 * @param abort
	 *            the abort to set
	 */
	public void setAbort(boolean abort)
	{
		this.abort = abort;
		connector.dispose();
	}
}
