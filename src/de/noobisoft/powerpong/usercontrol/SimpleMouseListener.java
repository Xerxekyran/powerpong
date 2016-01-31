package de.noobisoft.powerpong.usercontrol;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;

import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.input.MouseListener;
import de.bht.jvr.renderer.RenderWindow;
import de.noobisoft.powerpong.config.ClientConfiguration;
import de.noobisoft.powerpong.domain.Game;
import de.noobisoft.powerpong.usercontrol.action.UAChangeViewDirection;
import de.noobisoft.powerpong.usercontrol.action.UAShoot;

/**
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class SimpleMouseListener implements MouseListener
{
	private IControlManager	controlManager	= null;
	private Game			game			= null;
	private boolean			inLookMode		= false;

	// private Cursor invisibleCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	private Robot			robot;

	protected Point			mousePos		= new Point();	// cursor

	// position

	/**
	 * 
	 */
	public SimpleMouseListener(IControlManager controlManager,
			Game game,
			RenderWindow renderwindow)
	{
		this.controlManager = controlManager;
		this.game = game;
		try
		{
			robot = new Robot();
			robot.setAutoDelay(0);
		}
		catch (AWTException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		/*
		 * 
		 * rotY += (mousePos.getX() - e.getX()) / 200; // rotate camera (y-axis)
		 * rotX += (mousePos.getY() - e.getY()) / 200; // rotate camera (x-axis)
		 * mousePos.setLocation(e.getX(), e.getY()); // save mouse position
		 * mouseDragged = true; // activate dragging mode
		 */

		// float rotX = (float) (mousePos.getX() - e.getX()) / 200.0f;
		// float rotY = (float) (mousePos.getY() - e.getY()) / 200.0f;
		//
		// UAChangeViewDirection actionChangeView = (UAChangeViewDirection)
		// this.controlManager.activateUserAction(EUserInput.ChangeViewDirection);
		// actionChangeView.setDeltaX(rotX);
		// actionChangeView.setDeltaY(rotY);
		// actionChangeView.setPlayer(game.getPlayer());
		//
		// // this.controlManager.addUserAction(new
		// // UAChangeViewDirection(game.getPlayer(), rotX, rotY));
		//
		// // game.getPlayer().changeViewDirection(rotX, rotY);
		// mousePos.setLocation(e.getX(), e.getY());

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		if (inLookMode)
		{
			float rotX = (float) (mousePos.getX() - e.getX()) / 200.0f;
			float rotY = (float) (mousePos.getY() - e.getY()) / 200.0f;

			UAChangeViewDirection actionChangeView = (UAChangeViewDirection) this.controlManager.activateUserAction(EUserInput.ChangeViewDirection);
			actionChangeView.setDeltaX(rotX);
			actionChangeView.setDeltaY(rotY);
			actionChangeView.setPlayer(game.getCurrentPlayer());

			// mousePos.setLocation(e.getX(), e.getY());
			Integer borderX = 0;
			Integer borderY = 0;
			if (!ClientConfiguration.isFullscreen)
			{
				borderX = 8;
				borderY = 30;
			}

			Integer windowWidth = ClientConfiguration.screenWidth;
			Integer windowHeight = ClientConfiguration.screenHeight;
			Integer windowPosX = 0;
			Integer windowPosY = 0;

			Integer x = windowWidth / 2;// + renderwindow.getFrame().getX();
			Integer y = windowHeight / 2;// + renderwindow.getFrame().getY();
			robot.mouseMove(x + windowPosX + borderX, y + windowPosY + borderY);
			mousePos.setLocation(x, y);
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		mousePos.setLocation(e.getX(), e.getY());
		switch (e.getButton())
		{
		case 1:
			UAShoot actionShoot = (UAShoot) controlManager.activateUserAction(EUserInput.Shoot);
			actionShoot.setPlayer(game.getCurrentPlayer());
			break;

		default:
			inLookMode = !inLookMode;
			// if (inLookMode)
			// {
			// renderwindow.getFrame().setCursor(getCursor());
			// }
			// else
			// {
			// renderwindow.getFrame().setCursor(Cursor.getDefaultCursor());
			// }
			break;
		}

	}

	// private Cursor getCursor()
	// {
	// return invisibleCursor;
	// }

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseWheelMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

}
