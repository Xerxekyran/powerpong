package de.noobisoft.powerpong.usercontrol;

import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.KeyListener;
import de.noobisoft.powerpong.audio.AudioManager;
import de.noobisoft.powerpong.core.Renderer;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.network.MinaClient;
import de.noobisoft.powerpong.usercontrol.action.UAChangeCam;
import de.noobisoft.powerpong.usercontrol.action.UAChangeWeapon;
import de.noobisoft.powerpong.usercontrol.action.UAJump;
import de.noobisoft.powerpong.usercontrol.action.UAMoveBackward;
import de.noobisoft.powerpong.usercontrol.action.UAMoveForward;
import de.noobisoft.powerpong.usercontrol.action.UAMoveLeft;
import de.noobisoft.powerpong.usercontrol.action.UAMoveRight;

/**
 * The KeyListener sets the corresponding actions active if the configured keys
 * are pressed
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class SimpleKeyListener implements KeyListener
{
	private IControlManager	controlManager	= null;
	private Game			game			= null;
	private MinaClient		networkClient	= null;

	/**
	 * 
	 * @param controlManager
	 *            the controlmanager to set the actions
	 * @param game
	 *            the game this listener should react to
	 * @param networkClient
	 *            the network layer object. needed to close the connection on
	 *            application shutdown
	 */
	public SimpleKeyListener(IControlManager controlManager,
			Game game,
			MinaClient networkClient)
	{
		this.game = game;
		this.controlManager = controlManager;
		this.networkClient = networkClient;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		// 10 = Enter
		// 32 = Space
		// 27 = Esc
		// 44 = ,
		// 46 = .
		// 49 = 1
		// 50 = 2
		// 51 = 3
		// 87 = w
		// 65 = a
		// 83 = s
		// 68 = d

		UAChangeWeapon changeWeaponAction = null;
		// System.out.println(e.getKeyChar() + " = " + e.getKeyCode());
		switch (e.getKeyCode())
		{
		case 73:
			// I: inversion toggle
			Renderer.swapInversionRendering();

			break;
		case 78:
			// N: nightvision toggle
			this.game.setObjectGunObjectsVisible(Renderer.swapNightVisionRendering());
			break;
		case 79:
			// O: bloom toggle
			Renderer.swapBloomRendering();
			break;
		case 32:
			// jump
			UAJump actionJump = (UAJump) controlManager.activateUserAction(EUserInput.Jump);
			actionJump.setPlayer(game.getCurrentPlayer());
			break;
		case 27:
			// Esc = kill the application
			networkClient.shutdown();
			System.exit(0);
			break;
		case 87:
		case 38:
			// up
			UAMoveForward actionForw = (UAMoveForward) controlManager.activateUserAction(EUserInput.MoveForward);
			actionForw.setPlayer(game.getCurrentPlayer());
			break;
		case 68:
		case 39:
			// right
			UAMoveRight actionRight = (UAMoveRight) controlManager.activateUserAction(EUserInput.MoveStrafeRight);
			actionRight.setPlayer(game.getCurrentPlayer());
			break;
		case 83:
		case 40:
			// down
			UAMoveBackward actionBack = (UAMoveBackward) controlManager.activateUserAction(EUserInput.MoveBackward);
			actionBack.setPlayer(game.getCurrentPlayer());
			break;
		case 65:
		case 37:
			// left
			UAMoveLeft actionLeft = (UAMoveLeft) controlManager.activateUserAction(EUserInput.MoveStrafeLeft);
			actionLeft.setPlayer(game.getCurrentPlayer());
			break;
		case 49:
			// 1: first weapon
			changeWeaponAction = (UAChangeWeapon) controlManager.activateUserAction(EUserInput.ChangeWeapon);
			changeWeaponAction.setPlayer(game.getCurrentPlayer());
			changeWeaponAction.setNewWeaponIndex(0);
			break;
		case 50:
			// 2: second weapon
			changeWeaponAction = (UAChangeWeapon) controlManager.activateUserAction(EUserInput.ChangeWeapon);
			changeWeaponAction.setPlayer(game.getCurrentPlayer());
			changeWeaponAction.setNewWeaponIndex(1);
			break;
		case 51:
			// 3: second weapon
			changeWeaponAction = (UAChangeWeapon) controlManager.activateUserAction(EUserInput.ChangeWeapon);
			changeWeaponAction.setPlayer(game.getCurrentPlayer());
			changeWeaponAction.setNewWeaponIndex(2);
			break;
		case 44:
			// ,: stop song
			AudioManager.getInstance().stopMusic();
			break;
		case 46:
			// .: next song
			AudioManager.getInstance().playNextMusicFile();
			break;

		/*
		 * // DEBUG KEYS case 52: // 4 this.game.getCurrentPlayer().debugPosX -=
		 * 0.01; this.game.getCurrentPlayer().debugEnergyBar(); break; case 53:
		 * this.game.getCurrentPlayer().debugPosX += 0.01;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; case 54:
		 * this.game.getCurrentPlayer().debugPosY -= 0.01;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; case 55:
		 * this.game.getCurrentPlayer().debugPosY += 0.01;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; case 56: // 8
		 * this.game.getCurrentPlayer().debugPosZ -= 0.01;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; case 57: // 9
		 * this.game.getCurrentPlayer().debugPosZ += 0.01;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; case 82: // r
		 * this.game.getCurrentPlayer().debugRotX -= 1;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; case 84: // t
		 * this.game.getCurrentPlayer().debugRotX += 1;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; case 90: // z
		 * this.game.getCurrentPlayer().debugRotY -= 1;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; case 85: // u
		 * this.game.getCurrentPlayer().debugRotY += 1;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; case 73: // i
		 * this.game.getCurrentPlayer().debugRotZ -= 1;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; case 79: // o
		 * this.game.getCurrentPlayer().debugRotZ += 1;
		 * this.game.getCurrentPlayer().debugEnergyBar(); break; // DEBUG KEYS
		 * END
		 */
		}

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case 87:
		case 38:
			// up
			controlManager.deactivateUserAction(EUserInput.MoveForward);
			break;
		case 68:
		case 39:
			// right
			controlManager.deactivateUserAction(EUserInput.MoveStrafeRight);
			break;
		case 83:
		case 40:
			// down
			controlManager.deactivateUserAction(EUserInput.MoveBackward);
			break;
		case 65:
		case 37:
			// left
			controlManager.deactivateUserAction(EUserInput.MoveStrafeLeft);
			break;
		case 112:
			// f1
			UAChangeCam actionChangeCam = (UAChangeCam) controlManager.activateUserAction(EUserInput.ChangeCam);
			actionChangeCam.setPlayer(game.getCurrentPlayer());
			break;
		}

	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

}
