package src;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {
	
	
	private transient boolean[] keys = new boolean[65536];
	public transient boolean up, down,left, right, enter, escape, p, m;
	
	public void update() {
		up   = keys[KeyEvent.VK_UP];
		down = keys[KeyEvent.VK_DOWN];
		left = keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_RIGHT];
		enter = keys[KeyEvent.VK_ENTER];
		escape= keys[KeyEvent.VK_ESCAPE];
		p = keys[KeyEvent.VK_P];
		m = keys[KeyEvent.VK_M];
	}
	
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	public void keyTyped(KeyEvent arg0) {
		
	}

}
