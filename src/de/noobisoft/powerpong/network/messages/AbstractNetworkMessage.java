package de.noobisoft.powerpong.network.messages;

import org.apache.mina.core.session.IoSession;

import de.noobisoft.powerpong.network.MinaServer;

/**
 * Abstract network class to support low level functionality for every message
 * 
 * @author Lars George
 * 
 */
public abstract class AbstractNetworkMessage implements INetworkMessage
{
	protected MinaServer	networkServer	= null;
	protected IoSession		session			= null;
	protected long			deadline		= 0;
	protected int			priority		= 5;

	@Override
	public void setNetworkServer(MinaServer networkServer)
	{
		this.networkServer = networkServer;
	}

	@Override
	public MinaServer getNetworkServer()
	{
		return this.networkServer;
	}

	@Override
	public IoSession getSession()
	{
		return this.session;
	}

	@Override
	public void setClientSession(IoSession session)
	{
		this.session = session;
	}

	@Override
	public long getDeadline()
	{
		return deadline;
	}

	@Override
	public void setDeadline(long deadline)
	{
		this.deadline = deadline;
	}

	@Override
	public int getPriority()
	{
		return priority;
	}

	@Override
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
}
