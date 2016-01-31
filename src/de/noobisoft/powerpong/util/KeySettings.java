package de.noobisoft.powerpong.util;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * This screen shows the key configuration of the game
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 * 
 */
public class KeySettings extends JFrame
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 790862006276801840L;

	/**
	 * default ctor
	 * 
	 * @throws IOException
	 */
	public KeySettings() throws IOException
	{
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);

		setBounds(new java.awt.Rectangle(100, 250, 401, 250));

		setLocationRelativeTo(getParent());
		setLocation(getLocation().x, getLocation().y + 150);

		setUndecorated(true);
		setIconImage(Toolkit.getDefaultToolkit().createImage("textures/client_trayicon.png"));

		JLabel splashImage = new JLabel();
		BufferedImage image = ImageIO.read(new File("textures/keysettings.jpg"));
		splashImage.setIcon(new ImageIcon(image));
		splashImage.setOpaque(false);
		splashImage.setBounds(0, 0, 401, 250);

		add(splashImage);
	}
}
