package de.noobisoft.powerpong.network.messages;

/**
 * Class to create network messages from a given string.
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class MessageFactory
{
	public static long	MESSAGE_DEADLINE	= 500;

	/**
	 * Creates a INetworkMessage depending on the given data. It also calls
	 * their setData method, so they are properly set to use.
	 * 
	 * @param msgData
	 *            A String with the message representation given by the network
	 *            messages itself
	 * @return an INetworkMessage object filled with the data from the given
	 *         string
	 */
	public INetworkMessage createNetworkMessage(String msgData)
	{
		INetworkMessage ret = null;

		if (msgData.startsWith("u"))
		{
			ret = new UpdateWorld();
			ret.setData(msgData);
		}
		else if (msgData.startsWith("o"))
		{
			ret = new UpdateObjectGunObjects();
			ret.setData(msgData);
		}
		else if (msgData.startsWith("j"))
		{
			ret = new PlayerJoined();
			ret.setData(msgData);
		}
		else if (msgData.startsWith("m"))
		{
			ret = new PlayerMoved();
			ret.setData(msgData);
		}
		else if (msgData.startsWith("q"))
		{
			ret = new PlayerQuit();
			ret.setData(msgData);
		}
		else if (msgData.startsWith("s"))
		{
			ret = new ScoreBoard();
			ret.setData(msgData);
		}
		else if (msgData.startsWith("w"))
		{
			ret = new PlayerShot();
			ret.setData(msgData);
		}
		else if (msgData.startsWith("e"))
		{
			ret = new StartEffect();
			ret.setData(msgData);
		}
		else
		{
			ret = new Unknown();
			ret.setData(msgData);
		}

		// set the deadline for the message, until it should be executed
		ret.setDeadline(System.currentTimeMillis()
				+ MessageFactory.MESSAGE_DEADLINE);

		return ret;
	}
}
