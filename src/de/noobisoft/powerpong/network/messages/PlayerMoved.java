package de.noobisoft.powerpong.network.messages;

import de.bht.jvr.math.Vector3;

/**
 * A Message that tells the server about the new player position
 * 
 * @author Lars George
 * 
 */
public class PlayerMoved extends AbstractNetworkMessage
{
	public static double	CALCULATION_TIME_MS	= 0.06;

	private String			playerID			= "";
	private Vector3			newPosition			= new Vector3();
	private float			newRotationY		= 0.0f;
	
	/**
	 * default ctor
	 */
	public PlayerMoved()
	{
		setPriority(6);
	}

	@Override
	public ENetworkMessages getType()
	{
		return ENetworkMessages.PlayerMoved;
	}

	@Override
	public String getNetworkData()
	{
		return "m" + playerID + ";" + newPosition.x() + "," + newPosition.y()
				+ "," + newPosition.z() + ";" + newRotationY;
	}

	@Override
	public void setData(String msgStr)
	{
		msgStr = msgStr.substring(1);

		String[] chunks = msgStr.split(";");

		// playerID
		this.playerID = chunks[0];

		// playerPosition
		String[] values = chunks[1].split(",");
		newPosition = new Vector3(Float.parseFloat(values[0]),
				Float.parseFloat(values[1]), Float.parseFloat(values[2]));

		// player viewing direction
		newRotationY = Float.parseFloat(chunks[2]);
	}

	/**
	 * @param newPosition
	 *            the newPosition to set
	 */
	public void setNewPosition(Vector3 newPosition)
	{
		this.newPosition = newPosition;
	}

	/**
	 * @return the newPosition
	 */
	public Vector3 getNewPosition()
	{
		return newPosition;
	}

	/**
	 * @param playerID
	 *            the playerID to set
	 */
	public void setPlayerID(String playerID)
	{
		this.playerID = playerID;
	}

	/**
	 * @return the playerID
	 */
	public String getPlayerID()
	{
		return playerID;
	}

	/**
	 * @return the newRotationY
	 */
	public float getNewRotationY()
	{
		return newRotationY;
	}

	/**
	 * @param newRotationY
	 *            the newRotationY to set
	 */
	public void setNewRotationY(float newRotationY)
	{
		this.newRotationY = newRotationY;
	}

}
