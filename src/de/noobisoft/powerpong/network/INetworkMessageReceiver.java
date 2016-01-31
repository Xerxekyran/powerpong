package de.noobisoft.powerpong.network;

import de.noobisoft.powerpong.network.messages.INetworkMessage;

public interface INetworkMessageReceiver
{
	public void addIncomingMessage(INetworkMessage msg);
}
