package de.noobisoft.powerpong.usercontrol;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import de.noobisoft.powerpong.usercontrol.action.IUserAction;
import de.noobisoft.powerpong.usercontrol.action.UAChangeCam;
import de.noobisoft.powerpong.usercontrol.action.UAChangeViewDirection;
import de.noobisoft.powerpong.usercontrol.action.UAChangeWeapon;
import de.noobisoft.powerpong.usercontrol.action.UAJump;
import de.noobisoft.powerpong.usercontrol.action.UAMoveBackward;
import de.noobisoft.powerpong.usercontrol.action.UAMoveForward;
import de.noobisoft.powerpong.usercontrol.action.UAMoveLeft;
import de.noobisoft.powerpong.usercontrol.action.UAMoveRight;
import de.noobisoft.powerpong.usercontrol.action.UAShoot;

/**
 * class that controls the user inputs
 * 
 * @author Lars George
 * @author Chris Krauss
 * 
 */
public class ControlManager implements IControlManager
{
	private ConcurrentHashMap<EUserInput, IUserAction>	activeActions	= new ConcurrentHashMap<EUserInput, IUserAction>();

	private HashMap<EUserInput, IUserAction>			userActions		= new HashMap<EUserInput, IUserAction>();

	/**
	 * default ctor
	 */
	public ControlManager()
	{
		initActionHashMap();
	}

	@Override
	public IUserAction activateUserAction(EUserInput inputType)
	{
		IUserAction ret = userActions.get(inputType);

		synchronized (activeActions)
		{
			activeActions.put(inputType, ret);
		}

		return ret;
	}

	@Override
	public void deactivateUserAction(EUserInput input)
	{
		synchronized (activeActions)
		{
			activeActions.remove(input);
		}
	}

	@Override
	public synchronized void perfomActiveUserActions(double timeElapsed)
	{
		synchronized (activeActions)
		{
			Vector<EUserInput> deleteActions = new Vector<EUserInput>();

			for (IUserAction action : activeActions.values())
			{
				// if the execution returns true, that means the action is not
				// repatable
				if (action.execute(timeElapsed))
				{
					// remember this type of action for deletion
					deleteActions.add(action.getType());
				}
			}

			// delete all marked actions
			for (EUserInput delInput : deleteActions)
				activeActions.remove(delInput);

		}
	}

	/**
	 * creates the user action objects (so they can be reutilized later)
	 */
	private void initActionHashMap()
	{
		userActions.put(EUserInput.MoveBackward, new UAMoveBackward(null));
		userActions.put(EUserInput.MoveForward, new UAMoveForward(null));
		userActions.put(EUserInput.MoveStrafeLeft, new UAMoveLeft(null));
		userActions.put(EUserInput.MoveStrafeRight, new UAMoveRight(null));
		userActions.put(EUserInput.Shoot, new UAShoot(null));
		userActions.put(EUserInput.Jump, new UAJump(null));

		userActions.put(EUserInput.ChangeWeapon, new UAChangeWeapon(null));

		userActions.put(EUserInput.ChangeViewDirection,
				new UAChangeViewDirection(null, 0.0f, 0.0f));

		userActions.put(EUserInput.ChangeCam, new UAChangeCam(null));
	}

}
