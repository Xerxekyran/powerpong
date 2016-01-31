package de.noobisoft.powerpong.usercontrol;

import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.WindowListener;

public class SimpleWindowListener implements WindowListener
{

	@Override
	public void windowClose(RenderWindow win)
	{
		System.exit(0);		
	}

	@Override
	public void windowReshape(RenderWindow win, int width, int height)
	{
		// TODO Auto-generated method stub
		
	}

}
