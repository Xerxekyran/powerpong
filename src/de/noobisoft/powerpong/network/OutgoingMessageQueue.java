package de.noobisoft.powerpong.network;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.noobisoft.powerpong.network.messages.INetworkMessage;

/**
 * 
 * 
 * @author Lars George
 * 
 */
public class OutgoingMessageQueue
{
	private static OutgoingMessageQueue				instance		= null;
	private ConcurrentLinkedQueue<INetworkMessage>	messageQueue	= new ConcurrentLinkedQueue<INetworkMessage>();

	/**
	 * private ctor for singleton pattern
	 */
	private OutgoingMessageQueue()
	{

	}

	/**
	 * adds a new message to the outgoing queu
	 * 
	 * @param msg
	 */
	public void addMessage(INetworkMessage msg)
	{
		synchronized (messageQueue)
		{
			this.messageQueue.add(msg);
		}

	}

	/**
	 * 
	 * @return the head of the message queue. null if the queue is empty
	 */
	public INetworkMessage getNextMessage()
	{
		INetworkMessage ret = null;

		synchronized (messageQueue)
		{
			ret = this.messageQueue.poll();
		}
		return ret;
	}

	/**
	 * singleton getter
	 * 
	 * @return the singleton instance
	 */
	public static OutgoingMessageQueue getInstance()
	{
		if (instance == null)
		{
			instance = new OutgoingMessageQueue();
		}

		return instance;
	}
}
