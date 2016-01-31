package de.noobisoft.powerpong.usercontrol;

import org.apache.log4j.Logger;

import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.JoystickEvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.NunchukButtonsEvent;
import wiiusej.wiiusejevents.physicalevents.NunchukEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.utils.WiimoteListener;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.DisconnectionEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.StatusEvent;
import de.noobisoft.powerpong.core.PongClient;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.usercontrol.action.UAChangeViewDirection;
import de.noobisoft.powerpong.usercontrol.action.UAChangeWeapon;
import de.noobisoft.powerpong.usercontrol.action.UAMoveBackward;
import de.noobisoft.powerpong.usercontrol.action.UAMoveForward;
import de.noobisoft.powerpong.usercontrol.action.UAMoveLeft;
import de.noobisoft.powerpong.usercontrol.action.UAMoveRight;
import de.noobisoft.powerpong.usercontrol.action.UAShoot;

/**
 * A handler class for wiimote controls
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class SimpleWiimoteListener implements WiimoteListener
{
	private IControlManager	controlManager		= null;
	private Game			game				= null;

	private boolean			isShotLocked		= false;
	private boolean			isStrafingLeft		= false;
	private boolean			isStrafingRight		= false;
	private boolean			isMovingForward		= false;
	private boolean			isMovingBackward	= false;
	static Logger			logger				= Logger.getLogger(PongClient.class);
	private Wiimote[]		wiimotes;

	public SimpleWiimoteListener(IControlManager controlManager, Game game)
	{
		this.game = game;
		this.controlManager = controlManager;

		// init logging system
		// Log.addLogListener(new LogPrinter(-1, 0, -1)); // ignore warnings

		// connect the wiimote
		this.connectWiimote();

	}

	public void connectWiimote()
	{
		// connect wiimote
		logger.info("Put Wiimote in discoverable mode (press 1+2)");
		if (WiiUseApiManager.getNbConnectedWiimotes() < 1)
		{
			logger.error("Timeout - No Wiimotes found.");
		}
		else
		{
			wiimotes = WiiUseApiManager.getWiimotes(1, false);
			logger.info("Wiimote connected.");
			wiimotes[0].addWiiMoteEventListeners(this);
		}
	}

	@Override
	public void onButtonsEvent(WiimoteButtonsEvent e)
	{
		// weapon changing
		if (e.isButtonAPressed())
		{
			UAChangeWeapon changeWeaponAction = (UAChangeWeapon) controlManager.activateUserAction(EUserInput.ChangeWeapon);
			changeWeaponAction.setPlayer(game.getCurrentPlayer());
			changeWeaponAction.setNewWeaponIndex(game.getCurrentPlayer().getNextWeaponIndex());
		}

		// toggle motionsensor
		if (e.isButtonOneJustReleased())
		{
			wiimotes[0].deactivateMotionSensing();
		}
		if (e.isButtonTwoJustReleased())
		{
			wiimotes[0].activateMotionSensing();
		}

		// shot
		if (e.isButtonBJustReleased() && !isShotLocked)
		{
			UAShoot actionShoot = (UAShoot) controlManager.activateUserAction(EUserInput.Shoot);
			actionShoot.setPlayer(game.getCurrentPlayer());
			isShotLocked = true;
		}
		if (e.isButtonBJustReleased())
		{
			isShotLocked = false;
		}

		// pressed
		if (e.isButtonUpHeld() || e.isButtonUpJustPressed()
				|| e.isButtonUpPressed())
		{
			UAChangeViewDirection changeVD = (UAChangeViewDirection) controlManager.activateUserAction(EUserInput.ChangeViewDirection);
			changeVD.setPlayer(game.getCurrentPlayer());
			changeVD.setDeltaY(0.02f);
		}

		if (e.isButtonDownHeld() || e.isButtonDownJustPressed()
				|| e.isButtonDownPressed())
		{
			UAChangeViewDirection changeVD = (UAChangeViewDirection) controlManager.activateUserAction(EUserInput.ChangeViewDirection);
			changeVD.setPlayer(game.getCurrentPlayer());
			changeVD.setDeltaY(-0.02f);
		}

		if (e.isButtonRightHeld() || e.isButtonRightJustPressed()
				|| e.isButtonRightPressed())
		{
			UAChangeViewDirection changeVD = (UAChangeViewDirection) controlManager.activateUserAction(EUserInput.ChangeViewDirection);
			changeVD.setPlayer(game.getCurrentPlayer());
			changeVD.setDeltaX(-0.03f);
		}

		if (e.isButtonLeftHeld() || e.isButtonLeftJustPressed()
				|| e.isButtonLeftPressed())
		{
			UAChangeViewDirection changeVD = (UAChangeViewDirection) controlManager.activateUserAction(EUserInput.ChangeViewDirection);
			changeVD.setPlayer(game.getCurrentPlayer());
			changeVD.setDeltaX(0.03f);
		}
	}

	@Override
	public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnectionEvent(DisconnectionEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onExpansionEvent(ExpansionEvent e)
	{
		// Handle Nunchuk-Events
		if (e instanceof NunchukEvent)
		{
			NunchukEvent nunchukEvent = (NunchukEvent) e;

			// Handle buttons
			NunchukButtonsEvent buttonsEvent = nunchukEvent.getButtonsEvent();
			if (buttonsEvent.isButtonZPressed())
			{
				UAShoot actionShoot = (UAShoot) controlManager.activateUserAction(EUserInput.Shoot);
				actionShoot.setPlayer(game.getCurrentPlayer());
			}

			// //Handle motions
			// MotionSensingEvent motionEvent =
			// nunchukEvent.getNunchukMotionSensingEvent();
			// nunchukPitch = motionEvent.getGforce().getY();
			// nunchukRoll = motionEvent.getGforce().getX();
			//
			// Handle joystick
			JoystickEvent joystickEvent = nunchukEvent.getNunchukJoystickEvent();
			double angle = joystickEvent.getAngle() * Math.PI / 180;
			double x = Math.sin(angle);
			double y = Math.cos(angle);
			float nunchukJoyX = (float) (x * joystickEvent.getMagnitude());
			float nunchukJoyY = (float) (-y * joystickEvent.getMagnitude());

			// front
			if (nunchukJoyY < -0.3 && !isMovingForward)
			{
				UAMoveForward actionFor = (UAMoveForward) controlManager.activateUserAction(EUserInput.MoveForward);
				actionFor.setPlayer(game.getCurrentPlayer());
				isMovingForward = true;
			}
			else
			{
				if (isMovingForward)
					controlManager.deactivateUserAction(EUserInput.MoveForward);
				isMovingForward = false;
			}
			// back
			if (nunchukJoyY > 0.3 && !isMovingBackward)
			{
				UAMoveBackward actionBack = (UAMoveBackward) controlManager.activateUserAction(EUserInput.MoveBackward);
				actionBack.setPlayer(game.getCurrentPlayer());
				isMovingBackward = true;
			}
			else
			{
				if (isMovingBackward)
					controlManager.deactivateUserAction(EUserInput.MoveBackward);
				isMovingBackward = false;
			}
			// left
			if (nunchukJoyX < -0.3 && !isStrafingLeft)
			{
				UAMoveLeft actionLeft = (UAMoveLeft) controlManager.activateUserAction(EUserInput.MoveStrafeLeft);
				actionLeft.setPlayer(game.getCurrentPlayer());
				isStrafingLeft = true;
			}
			else
			{
				if (isStrafingLeft)
					controlManager.deactivateUserAction(EUserInput.MoveStrafeLeft);
				isStrafingLeft = false;
			}
			// right
			if (nunchukJoyX > 0.3 && !isStrafingRight)
			{
				UAMoveRight actionRight = (UAMoveRight) controlManager.activateUserAction(EUserInput.MoveStrafeRight);
				actionRight.setPlayer(game.getCurrentPlayer());
				isStrafingRight = true;
			}
			else
			{
				if (isStrafingRight)
					controlManager.deactivateUserAction(EUserInput.MoveStrafeRight);
				isStrafingRight = false;
			}
		}
	}

	@Override
	public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onIrEvent(IREvent e)
	{
	}

	@Override
	public void onMotionSensingEvent(MotionSensingEvent e)
	{
		float aRoll = e.getRawAcceleration().getZ();
		float x = -e.getGforce().getX();
		float y = -e.getGforce().getY();
		//float z = e.getGforce().getZ();

		// if(x>0.1 && x<-0.1)
		{
			UAChangeViewDirection changeVD = (UAChangeViewDirection) controlManager.activateUserAction(EUserInput.ChangeViewDirection);
			changeVD.setPlayer(game.getCurrentPlayer());
			changeVD.setDeltaX(aRoll * x * 0.0003f);
		}
		// if(y>0.1 && y<-0.1)
		{
			UAChangeViewDirection changeVD = (UAChangeViewDirection) controlManager.activateUserAction(EUserInput.ChangeViewDirection);
			changeVD.setPlayer(game.getCurrentPlayer());
			changeVD.setDeltaY(y * 0.01f);
		}
	}

	@Override
	public void onNunchukInsertedEvent(NunchukInsertedEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onNunchukRemovedEvent(NunchukRemovedEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusEvent(StatusEvent arg0)
	{
		// TODO Auto-generated method stub

	}

}
