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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.noobisoft.powerpong.core.PongServer;

public class startupServer
{
	private static int	PORT	= 9090;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			registerSystemTrayIcon();
			
			for (String arg : args)
			{
				if (arg.startsWith("-port"))
				{
					String p = arg.substring(6);
					PORT = Integer.parseInt(p);
				}
			}
			
			BasicConfigurator.configure();
			PropertyConfigurator.configure("cfg/server.properties");
			Logger.getRootLogger().setLevel(Level.DEBUG);

			(new PongServer(PORT)).start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

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
			Image image = Toolkit.getDefaultToolkit().getImage("textures/server_trayicon.png");

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

			trayIcon = new TrayIcon(image, "Powerpong - Server", popup);

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
