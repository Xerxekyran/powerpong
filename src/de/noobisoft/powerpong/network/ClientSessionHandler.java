package de.noobisoft.powerpong.network;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import de.noobisoft.powerpong.network.messages.INetworkMessage;
import de.noobisoft.powerpong.network.messages.MessageFactory;

/**
 * class that handles the network events for the client
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class ClientSessionHandler extends IoHandlerAdapter
{
	private boolean					finished		= false;				;
	private INetworkMessageReceiver	messageReceiver	= null;
	private MessageFactory			messageFac		= new MessageFactory();

	public ClientSessionHandler(INetworkMessageReceiver messageReceiver)
	{
		this.messageReceiver = messageReceiver;
	}

	public boolean isFinished()
	{
		return finished;
	}

	@Override
	public void messageReceived(IoSession session, Object message)
	{
		INetworkMessage msg = messageFac.createNetworkMessage((String) message);
		messageReceiver.addIncomingMessage(msg);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
	{
		session.close(true);
	}
}
