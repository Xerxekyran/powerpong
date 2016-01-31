package de.noobisoft.powerpong.network.messages;

import de.bht.jvr.math.Vector3;
import de.noobisoft.powerpong.domain.player.weapon.EWeapons;

/**
 * The message that a player fired a gun
 * 
 * @author Lars George
 * 
 */
public class PlayerShot extends AbstractNetworkMessage
{
	public static double	CALCULATION_TIME_MS	= 0.7;

	private String			playerID			= "";
	private Vector3			direction			= new Vector3();
	private EWeapons		weaponType			= EWeapons.PeaGun;
	private Vector3			position			= new Vector3();
	private int				shotID				= 0;

	/**
	 * default ctor
	 */
	public PlayerShot()
	{
		setPriority(3);
	}

	@Override
	public String getNetworkData()
	{
		return "w" + playerID + ";" + direction.x() + "," + direction.y() + ","
				+ direction.z() + ";" + weaponType.toString() + ";"
				+ position.x() + "," + position.y() + "," + position.z() + ";"
				+ shotID;
	}

	@Override
	public void setData(String msgStr)
	{
		msgStr = msgStr.substring(1);

		String[] chunks = msgStr.split(";");

		// player id
		this.playerID = chunks[0];

		// direction
		String[] values = chunks[1].split(",");
		direction = new Vector3(Float.parseFloat(values[0]),
				Float.parseFloat(values[1]), Float.parseFloat(values[2]));

		// weapon
		this.weaponType = EWeapons.valueOf(chunks[2]);

		// position
		values = chunks[3].split(",");
		this.position = new Vector3(Float.parseFloat(values[0]),
				Float.parseFloat(values[1]), Float.parseFloat(values[2]));

		// id
		this.shotID = Integer.parseInt(chunks[4]);
	}

	@Override
	public ENetworkMessages getType()
	{
		return ENetworkMessages.PlayerShot;
	}

	/**
	 * @return the playerID
	 */
	public String getPlayerID()
	{
		return playerID;
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
	 * @return the direction
	 */
	public Vector3 getDirection()
	{
		return direction;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(Vector3 direction)
	{
		this.direction = direction;
	}

	/**
	 * @return the weaponType
	 */
	public EWeapons getWeaponType()
	{
		return weaponType;
	}

	/**
	 * @param weaponType
	 *            the weaponType to set
	 */
	public void setWeaponType(EWeapons weaponType)
	{
		this.weaponType = weaponType;
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
	 * @return the shotID
	 */
	public int getShotID()
	{
		return shotID;
	}

	/**
	 * @param shotID
	 *            the shotID to set
	 */
	public void setShotID(int shotID)
	{
		this.shotID = shotID;
	}

}
