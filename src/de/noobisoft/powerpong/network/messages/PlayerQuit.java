package de.noobisoft.powerpong.network.messages;


public class PlayerQuit extends AbstractNetworkMessage
{

	String playerID;

	/**
	 * @return the playerID
	 */
	public String getPlayerID()
	{
		return playerID;
	}

	/**
	 * @param playerID the playerID to set
	 */
	public void setPlayerID(String playerID)
	{
		this.playerID = playerID;
	}

	@Override
	public ENetworkMessages getType()
	{
		return ENetworkMessages.PlayerQuit;
	}

	@Override
	public void setData(String msgStr)
	{
		msgStr = msgStr.substring(1);
		this.playerID = msgStr;
	}

	@Override
	public String getNetworkData()
	{
		return ("q" + this.playerID);
	}
}
