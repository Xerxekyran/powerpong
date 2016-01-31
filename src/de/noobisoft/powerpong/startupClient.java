package de.noobisoft.powerpong;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import de.noobisoft.powerpong.config.ClientConfiguration;
import de.noobisoft.powerpong.config.ClientConfigurationScreen;
import de.noobisoft.powerpong.core.PongClient;
import de.noobisoft.powerpong.domain.player.ETeam;

public class startupClient
{

	/**
	 * The main entry point.
	 * 
	 * @param args
	 *            ignored
	 */
	public static void main(String[] args)
	{
		try
		{
			BasicConfigurator.configure();
			PropertyConfigurator.configure("cfg/client.properties");

			registerSystemTrayIcon();

			configScreen();

			(new PongClient()).start();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * displays a configuration dialog and fills the values of the settings
	 * object
	 * 
	 * @param settings
	 *            the configuration settings object that should be filled
	 * @throws InterruptedException
	 *             if the dialog had a malfunction
	 */
	private static void configScreen() throws InterruptedException
	{

		// show the dialog
		ClientConfigurationScreen configScreen = new ClientConfigurationScreen();
		configScreen.setVisible(true);
		configScreen.setLocationRelativeTo(configScreen.getParent());
		// whait until the player finished the dialog
		while (configScreen.isVisible())
		{
			Thread.sleep(1000);
		}

		// read the values the player choosed
		ClientConfiguration.playerName = configScreen.getTxt_name().getText();

		if (configScreen.getRb_red().isSelected())
			ClientConfiguration.playerTeam = ETeam.RED;
		if (configScreen.getRb_blue().isSelected())
			ClientConfiguration.playerTeam = ETeam.BLUE;

		ClientConfiguration.isFullscreen = configScreen.getCb_vollbild().isSelected();

		ClientConfiguration.world = (String) configScreen.getCb_world().getSelectedItem();

		ClientConfiguration.screenWidth = Integer.parseInt(configScreen.getTxt_width().getText());
		ClientConfiguration.screenHeight = Integer.parseInt(configScreen.getTxt_height().getText());

		ClientConfiguration.serverIP = configScreen.getTxt_ip().getText();
		ClientConfiguration.serverPort = configScreen.getTxt_port().getText();

		ClientConfiguration.soundOn = configScreen.getCb_soundAn().isSelected();
		ClientConfiguration.musikOn = configScreen.getCb_musikAn().isSelected();

		ClientConfiguration.isHolodeck = configScreen.getCb_isHolodeck().isSelected();

		// does he want to start his own server?
		if (configScreen.getCb_startOwnServer().isSelected())
		{
			try
			{
				ClientConfiguration.serverPort = "9090";
				ClientConfiguration.serverIP = "127.0.0.1";

				new ProcessBuilder("Powerpong SERVER.bat", "").start();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		configScreen.dispose();
	}

	/**
	 * initializes the tray icon
	 */
	private static void registerSystemTrayIcon()
	{
		final TrayIcon trayIcon;

		if (SystemTray.isSupported())
		{
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("textures/client_trayicon.png");

			MouseListener mouseListener = new MouseListener()
			{

				public void mouseClicked(MouseEvent e)
				{
					// System.out.println("Tray Icon - Mouse clicked!");
				}

				public void mouseEntered(MouseEvent e)
				{
					// System.out.println("Tray Icon - Mouse entered!");
				}

				public void mouseExited(MouseEvent e)
				{
					// System.out.println("Tray Icon - Mouse exited!");
				}

				public void mousePressed(MouseEvent e)
				{
					// System.out.println("Tray Icon - Mouse pressed!");
				}

				public void mouseReleased(MouseEvent e)
				{
					// System.out.println("Tray Icon - Mouse released!");
				}
			};

			ActionListener exitListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}
			};

			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem("Exit");
			defaultItem.addActionListener(exitListener);
			popup.add(defaultItem);

			trayIcon = new TrayIcon(image, "Powerpong - Client", popup);

			ActionListener actionListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					trayIcon.displayMessage("Action Event",
							"An Action Event Has Been Performed!",
							TrayIcon.MessageType.INFO);
				}
			};

			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(actionListener);
			trayIcon.addMouseListener(mouseListener);

			try
			{
				tray.add(trayIcon);
			}
			catch (AWTException e)
			{
				System.err.println("TrayIcon could not be added.");
			}
		}
		else
		{
			// System Tray is not supported
		}
	}

}
