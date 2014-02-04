package src;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.lang.reflect.Field;

import javax.swing.JFrame;


/**
 * 	the main game engine that will hande drawing and loading of sounds and images, handles keyboard input.
 * 	in order to use this class put "import src.GameClass;" without the quotes at the top of every class file
 * 
 */
public class GameClass extends Canvas implements Runnable {
	
	private static transient  final Logger LOG = Logger.getLogger(GameClass.class.getName());
	private static final long serialVersionUID = 1L;
	
	private static transient int SCREEN_WIDTH;
	private static transient int SCREEN_HEIGHT;
	private static transient String SCREEN_TITLE;
	private static transient double FRAMERATE = 60.0;
	
	private transient Thread thread;
	
	/**
	 *  keyboard input handler
	 */
	public final transient Keyboard keyboard;
	private final transient Screen screen;
	private final transient JFrame frame;
    
	private transient boolean running;
	
	private transient double screenShakeTicks;
	
	private transient boolean muted;
	
	/**
	 *  should we upodate the logic
	 */
	public transient boolean tickUpdate;
	
	/**
	 *  how long the session has been running (use full for timing)
	 */
	public transient int upTime;
	
	/**
	 *  current frames per second
	 */
	public transient double frames;
	
	/**
	 *  current ticks per second
	 */
	public transient double ticks;
	
	ArrayList<Sound> sounds = new ArrayList<Sound>(); 
	ArrayList<Images> textures = new ArrayList<Images>(); 
	
	ArrayList<Images> offset = new ArrayList<Images>(); 
	ArrayList<Integer> x = new ArrayList<>(); 
	ArrayList<Integer> y = new ArrayList<>(); 

	/**
	 *  Creates a window to the specified height and width, and set the titles
	 *  also loads in the icon from res/icon.png
	 *  
	 *  @param ScreenWidth width of the screen
	 *  @param ScreenHeight height of the screen
	 *  @param title title of the screen
	 */
	public GameClass (final int ScreenWidth, final int ScreenHeight, final String title) {
		setPreferredSize(new Dimension(ScreenWidth,ScreenHeight));
		
		frame  = new JFrame(title);
		screen = new Screen(ScreenWidth, ScreenHeight);
		
		keyboard = new Keyboard();
		addKeyListener(keyboard);
		
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		
		final Image icon = Toolkit.getDefaultToolkit().getImage("res/icon.png");
		frame.setIconImage(icon);
		
		frame.setIgnoreRepaint( true );
		frame.setResizable(false);
	    frame.setVisible(true);
	    frame.setLocationRelativeTo(null);
	    frame.toFront();
	    
	    
	    running = false;
	    muted = false;
	    screenShakeTicks = 0;
	    SCREEN_WIDTH = ScreenWidth;
	    SCREEN_HEIGHT = ScreenHeight;
	    SCREEN_TITLE = title;
	    
	    this.start();
	}
	
	/**
	 *  IGNORE DO NOT TOUCH!
	 */
	public void run() {
		long lastTime = System.nanoTime();
		final double ns = 1000000000.0D / FRAMERATE;
		long timer = System.currentTimeMillis();
		
		double delta = 0;
		double fps = 0;
		double tick = 0;
		upTime = 0;
		while(running) {
			final long now  = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
				while (delta >= 1) {
					ticks();
					tickUpdate = true;
					if (screenShakeTicks > 0) {
						screenShakeTicks -= delta;
					}
					tick++;
					delta--;
				}
			 render();
			 fps++;
			 tickUpdate = false;
			 
		    if (System.currentTimeMillis() - timer > 1000) {
		    	frames = fps;
				ticks = tick;	
		    	
			  frame.setTitle(SCREEN_TITLE +" | Frames: " + fps + " | ticks: " + tick);
			  timer += 1000;
			  fps = 0;
			  tick = 0;
			  
			  upTime += 1;
			}
			
		}
	}
	
	private void ticks() {
		keyboard.update();
		
			this.offset.clear();
			this.x.clear();
			this.y.clear();
			
			try {
				TimeUnit.MILLISECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (keyboard.m) {
				if (muted) {
					muted = false;
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				} else {
					muted = true;
					for (int i = 0; i < sounds.size(); i++) {
						sounds.get(i).stopClip();
					}
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
	}
	
	public void render() {
		 final BufferStrategy buffer = getBufferStrategy();
         if (buffer == null) {
             createBufferStrategy(3);
             return;
         }
         
         final Graphics g = buffer.getDrawGraphics();
       
         screen.clear();
         
         for (int i = 0; i < this.offset.size()  - 1; i++) {
 			screen.blit(this.offset.get(i), this.x.get(i), this.y.get(i));
         }
         
         if(screenShakeTicks > 0){
        	Random rnd = new Random();
        	int x = rnd.nextInt(10);
        	int y = rnd.nextInt(10);
        	g.translate(x, y);
        }
         
         g.drawImage(screen.image, 0, 0, getWidth(), getHeight() , null);
         
         g.dispose();
         buffer.show();
    }
	
	/**
	 *  Loads a sound to the current session, ONLY SUPPORTS .WAV
	 *  
	 *  @param path the path to the sound file .. idealy in "res/sounds"
	 *  
	 *  @return returns the int index to sound
	 */
	public int addSound(final String path) {
		sounds.add( new Sound(path));
		return sounds.size() - 1;
	}
	
	/**
	 *  Plays a loaded sound with the given index
	 *  
	 *  @param offset offset for the sound
	 *  @param decibleOffset for Ajusting the volume of the sound
	 *  @param loop should it loop forever
	 */
	public void playSound(final int offset, final int decibleOffset, final boolean loop) {
		if (!muted) {
			sounds.get(offset).playClip(decibleOffset, loop);
		}
	}
	
	
	/**
	 *  stops playing the sound
	 * 
	 * @param offset the offset for the sound
	 */
	public void stopSound(final int offset) {
		sounds.get(offset).stopClip();
	}
	
	/**
	 * returns wether the current session is muted
	 * @return true is muted else false
	 */
	public boolean getMuted() {
		return this.muted;
	}
	
	/**
	 *  Loads a image to the current session
	 *  
	 *  @param path the path to the image file .. idealy in "res/textues"
	 *  
	 *  @return returns the int index to image
	 */
	public int addImage(final String path) {
		Images image = Art.Load(path); 
		textures.add(image);
		return textures.size() - 1;
	}
	
	/**
	 * Draws a loaded image to the screen
	 * 
	 * @param Offset offset for the image
	 * @param x x position in the screen
	 * @param y y position in the screen 
	 */
	public void drawImage(final int Offset, final int x, final int y) {
		this.offset.add(this.textures.get(Offset));
		this.x.add(x);
		this.y.add(y);
	}
	
	/**
	 * pauses the application for a few seconds
	 */
	public void pause() {
		try {
			TimeUnit.MILLISECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 *  Shakes the screen, just have to see how long it lasts i'am not sure exactly :D
	 *  
	 *  @param milliseconds duration of the shake
	 */
	public void screenShake(double milliseconds) {
		screenShakeTicks = milliseconds;
	}
	
	/**
	 *  Draws any string to the screen
	 *  
	 *  @param string the string to draw
	 *  @param colour the colour of the string e.g "white"
	 *  @param x x postion to draw
	 *  @param y y postion to draw
	 *  @param size size of the text
	 */
	public void drawText(final String string, final String colour, final int x, final int y, final int size) {
		final BufferStrategy buffer = getBufferStrategy();
        if (buffer == null) {
            createBufferStrategy(3);
            return;
        }
        final Graphics g = buffer.getDrawGraphics();

         Color color = getColour(colour);
         g.setColor(color);
		 g.setFont(new Font("consolas", Font.PLAIN, size));
		 g.drawString(string, x, y);
	}
	
	
	/**
	 *  returns the screen width
	 *  
	 *  @return screen width
	 */
	public int getScreenWidth() {
		return this.SCREEN_WIDTH;
	}
	
	
	/**
	 *  returns the screen height
	 *  
	 *  @return screen height
	 */
	public int getScreenHeight() {
		return this.SCREEN_HEIGHT;
	}
	
	private Color getColour(final String string) {
		try {
		final Field f = Color.class.getField(string);
		return (Color) f.get(null);
		 } catch (Exception ce) {
			 return Color.white;
		 }
	}
	
	private void start() {
		  thread = new Thread(this, SCREEN_TITLE);
		  thread.start();
		  running = true;
		  tickUpdate = false;
	}
	
	/**
	 * stops the current session
	 */
    public void stop() {
    	System.exit(0);
      try {
		thread.join();
	  } catch (InterruptedException e) {
		  LOG.log(null, "Could Not Stop Thread!", e);
	  }
	}

}
