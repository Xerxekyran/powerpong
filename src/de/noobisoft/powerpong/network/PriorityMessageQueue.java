package de.noobisoft.powerpong.network;

import java.util.Iterator;
import java.util.LinkedList;

import de.noobisoft.powerpong.network.messages.INetworkMessage;

/**
 * This queue sorts its elements by their priority
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class PriorityMessageQueue
{
	private LinkedList<INetworkMessage>	messages	= new LinkedList<INetworkMessage>();

	/**
	 * adds a new message to the queue
	 * 
	 * @param newMessage
	 */
	public void addMessage(INetworkMessage newMessage)
	{
		if (messages.size() > 5)
		{
			int index = messages.size();

			Iterator<INetworkMessage> it = this.messages.descendingIterator();
			while (it.hasNext())
			{
				INetworkMessage msg = it.next();

				// use deadline and priority to calculate a proper priority
				if ((msg.getPriority() / msg.getDeadline()) > (newMessage.getPriority() / newMessage.getDeadline()))
				{
					break;
				}

				index--;
			}

			this.messages.add(index, newMessage);
		}
		else
		{

			this.messages.add(newMessage);
		}

	}

	/**
	 * 
	 * @return the next network message to execute. it is mainly choosen by its
	 *         priority
	 */
	public INetworkMessage getNextMessage()
	{
		return this.messages.poll();
	}
}
