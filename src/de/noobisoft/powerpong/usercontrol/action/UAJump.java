package de.noobisoft.powerpong.usercontrol.action;

import de.noobisoft.powerpong.domain.player.Player;
import de.noobisoft.powerpong.usercontrol.EUserInput;

/**
 * a user action for a jump
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class UAJump implements IUserAction
{
	private Player	player;

	/**
	 * 
	 * @param player the player that should jump
	 */
	public UAJump(Player player)
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
		this.player.jump();

		return true;
	}

	@Override
	public EUserInput getType()
	{
		return EUserInput.Jump;
	}
}
