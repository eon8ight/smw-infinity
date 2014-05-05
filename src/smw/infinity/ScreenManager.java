package smw.infinity;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class ScreenManager
{
	public static final int DEFAULT_WINDOW_WIDTH = 768, DEFAULT_WINDOW_HEIGHT = 576;
	public static final Dimension DEFAULT_DIMENSION = new Dimension(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	
	private static GraphicsDevice graphicsCard = null;
	private static GraphicsConfiguration graphicsConfig = null;
	
	private static Frame frame = null;
	private static Canvas canvas = null;
	private static BufferStrategy buffer = null;
	
	private static Scene currentScene = null;		//there has got to be a better way to do this
	
	private ScreenManager() throws SMWException
	{
		throw new SMWException("Can't instantiate ScreenManager!");
	}
	
	public static void init()
	{
		graphicsCard = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		graphicsConfig = graphicsCard.getDefaultConfiguration();
		
		frame = new Frame();
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setIgnoreRepaint(true);
		
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent e)
			{
				return;
			}

			@Override
			public void windowClosed(WindowEvent e)
			{
				return;
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				if(currentScene != null)
					currentScene.stop();
				
				while(currentScene.isRunning());
				
				dispose();
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
				return;
			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
				return;
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
				return;
			}

			@Override
			public void windowOpened(WindowEvent e)
			{
				return;
			}
		});
		
		canvas = new Canvas();
		canvas.setSize(DEFAULT_DIMENSION);
		frame.add(canvas);
		frame.pack();
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((d.width - frame.getWidth()) / 2, ((d.height - frame.getHeight()) / 2) - 16);
		
		canvas.createBufferStrategy(3);
		buffer = canvas.getBufferStrategy();
	}
	
	public static void setVisible(boolean b)
	{
		frame.setVisible(b);
	}
	
	public static Graphics getGraphics()
	{
		return buffer.getDrawGraphics();
	}
	
	public static void show()
	{
		if(buffer == null || buffer.contentsLost())
			return;
		
		buffer.show();
	}
	
	public static BufferedImage createCompatibleImage(String url) throws IOException
	{
		BufferedImage orig = ImageIO.read(new File(url));
		BufferedImage toReturn = graphicsConfig.createCompatibleImage(orig.getWidth(), orig.getHeight(), orig.getTransparency());
		Graphics2D g2D = toReturn.createGraphics();
		g2D.drawImage(orig, 0, 0, orig.getWidth(), orig.getHeight(), null);
		g2D.dispose();
		return toReturn;
	}
	
	public static VolatileImage createCompatibleVolatileImage(String url) throws IOException
	{
		BufferedImage orig = ImageIO.read(new File(url));
		VolatileImage toReturn = graphicsConfig.createCompatibleVolatileImage(orig.getWidth(), orig.getHeight(), orig.getTransparency());
		Graphics2D g2D = toReturn.createGraphics();
		g2D.drawImage(orig, 0, 0, orig.getWidth(), orig.getHeight(), null);
		g2D.dispose();
		return toReturn;
	}
	
	public static void setCurrentScene(Scene s)
	{
		currentScene = s;
	}
	
	public static void dispose()
	{
		frame.setVisible(false);
		
		if(buffer != null)
			buffer.dispose();
		
		frame.dispose();
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		dispose();
		super.finalize();
	}
}