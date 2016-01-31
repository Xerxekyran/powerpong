package de.noobisoft.powerpong.util;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

/**
 * this is just the starting splash screen
 * 
 * @author Lars George
 * @author Chris Krauﬂ
 */
public class SplashScreen extends JFrame
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 9139589031962295274L;
	private JLabel				text				= null;
	private static SplashScreen	splashObj			= null;
	private KeySettings			keySettingScreen	= null;

	/**
	 * default ctor
	 * 
	 * @throws IOException
	 */
	public SplashScreen() throws IOException
	{
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);

		setTitle("Powerpong");
		setBounds(new java.awt.Rectangle(200, 200, 600, 250));

		setLocationRelativeTo(null);
		setLocation(getLocation().x, getLocation().y - 100);

		setUndecorated(true);
		setIconImage(Toolkit.getDefaultToolkit().createImage("textures/client_trayicon.png"));

		JLayeredPane jlp = new JLayeredPane();

		JLabel splashImage = new JLabel();
		BufferedImage image = ImageIO.read(new File("textures/splashscreen.jpg"));
		splashImage.setIcon(new ImageIcon(image));
		splashImage.setOpaque(false);
		splashImage.setBounds(0, 0, 600, 250);

		text = new JLabel("Loading");
		text.setForeground(Color.WHITE);
		text.setBounds(132, 173, 330, 50);
		text.setHorizontalAlignment(SwingConstants.CENTER);

		jlp.add(splashImage, 1);
		jlp.add(text, 0);

		add(jlp);

		splashObj = this;
		keySettingScreen = new KeySettings();
		keySettingScreen.setVisible(true);
	}

	@Override
	public void dispose()
	{
		keySettingScreen.dispose();
		super.dispose();
	}

	/**
	 * sets the new text in the loading label
	 * 
	 * @param txt
	 *            the string to show
	 */
	public void setText(String txt)
	{
		this.text.setText("Loading: " + txt);

	}

	/**
	 * sets the new text in the loading label, if the splashscreen is active
	 * 
	 * @param txt
	 *            the string to show
	 */
	public static void setLoadingText(String txt)
	{
		if (splashObj != null)
			splashObj.setText(txt);
	}

}
