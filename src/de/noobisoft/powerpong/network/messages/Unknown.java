package de.noobisoft.powerpong.network.messages;

public class Unknown extends AbstractNetworkMessage
{
	String data = "";

	@Override
	public ENetworkMessages getType()
	{
		return ENetworkMessages.Unknown;
	}
	
	public String getUnknownData()
	{
		return this.data;
	}

	@Override
	public void setData(String msgData)
	{
		this.data = msgData;
	}

	@Override
	public String getNetworkData()
	{
		return "";
	}

}
