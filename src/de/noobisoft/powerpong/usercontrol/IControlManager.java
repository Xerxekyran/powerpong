package de.noobisoft.powerpong.usercontrol;

import de.noobisoft.powerpong.usercontrol.action.IUserAction;

public interface IControlManager
{
	public IUserAction activateUserAction(EUserInput inputType);
	
	public void deactivateUserAction(EUserInput input);

	public void perfomActiveUserActions(double timeElapsed);
}
