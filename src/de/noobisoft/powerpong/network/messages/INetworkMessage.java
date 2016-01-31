package de.noobisoft.powerpong.network.messages;

import org.apache.mina.core.session.IoSession;

import de.noobisoft.powerpong.network.MinaServer;

/**
 * the interface for all network messages
 * 
 * @author Lars George
 * 
 */
public interface INetworkMessage
{
	public void setNetworkServer(MinaServer networkServer);

	public MinaServer getNetworkServer();

	public void setClientSession(IoSession session);

	public IoSession getSession();

	public void setData(String data);

	public ENetworkMessages getType();

	public String getNetworkData();

	public long getDeadline();

	public void setDeadline(long deadline);

	public int getPriority();

	public void setPriority(int priority);
}
