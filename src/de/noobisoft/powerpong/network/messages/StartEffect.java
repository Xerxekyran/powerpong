package de.noobisoft.powerpong.network.messages;

import de.bht.jvr.math.Vector3;

/**
 * Message that informs about a effect that should be created at a certain
 * position
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class StartEffect extends AbstractNetworkMessage
{
	public static int	EFFECT_SPARK	= 0;

	private Vector3		position		= new Vector3();
	private int			effectID		= -1;

	@Override
	public ENetworkMessages getType()
	{
		return ENetworkMessages.StartEffect;
	}

	@Override
	public String getNetworkData()
	{
		return "e" + effectID + ";" + position.x() + "," + position.y() + ","
				+ position.z();
	}

	@Override
	public void setData(String msgStr)
	{
		msgStr = msgStr.substring(1);

		String[] chunks = msgStr.split(";");
		effectID = Integer.parseInt(chunks[0]);

		String[] values = chunks[1].split(",");
		position = new Vector3(Float.parseFloat(values[0]),
				Float.parseFloat(values[1]), Float.parseFloat(values[2]));
	}

	/**
	 * @return the position
	 */
	public Vector3 getPosition()
	{
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Vector3 position)
	{
		this.position = position;
	}

	/**
	 * @return the effectID
	 */
	public int getEffectID()
	{
		return effectID;
	}

	/**
	 * @param effectID
	 *            the effectID to set
	 */
	public void setEffectID(int effectID)
	{
		this.effectID = effectID;
	}

}
