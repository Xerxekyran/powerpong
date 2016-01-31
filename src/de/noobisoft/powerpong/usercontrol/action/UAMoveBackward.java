package de.noobisoft.powerpong.usercontrol.action;

import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.usercontrol.EUserInput;

/**
 * a user action for a movement backwards
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class UAMoveBackward implements IUserAction
{
	private Player	player;

	/**
	 * 
	 * @param moverNode
	 *            the scene node that should be moved
	 */
	public UAMoveBackward(Player player)
	{
		this.player = player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}

	@Override
	public boolean execute(double timeElapsed)
	{
		this.player.moveBackward();

		return false;
	}

	@Override
	public EUserInput getType()
	{
		return EUserInput.MoveBackward;
	}
}
