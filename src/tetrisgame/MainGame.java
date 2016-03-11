package tetrisgame;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

@SuppressWarnings("serial")
public class MainGame extends Applet implements Runnable, KeyListener {

	private Graphics graphics;
	private URL base;
	
	@Override
	public void init() {
		addKeyListener(this);
        setSize(500, 600);
        setBackground(Color.LIGHT_GRAY);
        setFocusable(true);
        Frame frame = (Frame) this.getParent().getParent();
        frame.setTitle("Castañón, ponme el tetris");
        
        System.out.println("Applet iniciado");
        
        try {
			base = getDocumentBase();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public void start() {
		Thread thread = new Thread(this);
        thread.start();
	}
	
	public void update(Graphics g) {
		// TODO
	}
	
	public void paint(Graphics g) {
		// TODO
	}
	
	@Override
	public void run() {
		while (true) {
            try {
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
