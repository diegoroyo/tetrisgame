package tetrisgame;

import java.applet.Applet;
import java.util.Date;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import tetrisgame.TetrisGrid.*;

@SuppressWarnings("serial")
public class MainGame extends Applet implements Runnable, KeyListener {

	TetrisGrid tetrisGrid;
	
	private Image[] tileImages;
	private Image image;
	private Graphics graphics, bufferG;
	private URL base;
	
	private long time;
	private final int LOOP_TIME = 500;
	
	@Override
	public void init() {
		addKeyListener(this);
        setSize(500, 600);
        setBackground(Color.LIGHT_GRAY);
        setFocusable(true);
        Frame frame = (Frame) this.getParent().getParent();
        frame.setTitle("Castañón, ponme el tetris");
        
        try {
			base = getDocumentBase();
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        tileImages = new Image[8];
        tileImages[0] = getImage(base, "data/tetrimino_blank.png"); // vacio
        tileImages[1] = getImage(base, "data/tetrimino_lightblue.png"); // I
        tileImages[2] = getImage(base, "data/tetrimino_purple.png"); // T
        tileImages[3] = getImage(base, "data/tetrimino_red.png"); // Z
        tileImages[4] = getImage(base, "data/tetrimino_green.png"); // S
        tileImages[5] = getImage(base, "data/tetrimino_yellow.png"); // O
        tileImages[6] = getImage(base, "data/tetrimino_orange.png"); // L
        tileImages[7] = getImage(base, "data/tetrimino_blue.png"); // J
        
        image = createImage(this.getWidth(), this.getHeight());
        bufferG = image.getGraphics();
        
        graphics = getGraphics();
        
	}
	
	@Override
	public void start() {
		tetrisGrid = new TetrisGrid(10, 10, 250, 500, 10, 20);
		tetrisGrid.addTetrimino();
		
		Thread thread = new Thread(this);
        thread.start();
	}
	
	@Override
	public void update(Graphics g) {
		
		//printElapsedTime();
		
		if (tetrisGrid.checkForLostGame()) {
			// TODO
		}
		else
		{
			tetrisGrid.applyForegroundGravity();
			tetrisGrid.applyBackgroundGravity();
			tetrisGrid.checkForFullRows();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		
		bufferG.drawImage(image, 0, 0, this);
		for (int i = 2; i < tetrisGrid.getGridHeight(); i++) {
			for (int j = 0; j < tetrisGrid.getGridWidth(); j++) {
				graphics.drawImage(getTileImage(tetrisGrid.getGrid()[i][j]),
						tetrisGrid.getStartX() + tetrisGrid.getTileWidth() * j,
						tetrisGrid.getStartY() + tetrisGrid.getTileHeight() * (i - 2),
						tetrisGrid.getTileWidth(),
						tetrisGrid.getTileHeight(),
						this);
			}
		}
		
	}
	
	// TODO Test
	private void printElapsedTime() {
		System.out.println(new Date().getTime()-time + " ms: " + ((new Date().getTime()-time) - LOOP_TIME));
		time = new Date().getTime();
	}
	
	private Image getTileImage(int id) {
		if (id < 0) {
			id = -id;
		}
		return tileImages[id];
	}
	
	@Override
	public void run() {
		
		while (true) {
			
            try {
                Thread.sleep(LOOP_TIME);
    			
    			update(graphics);
    			paint(graphics);
    			
            } catch (InterruptedException e) {
                System.out.println("Interrumpido");
            	e.printStackTrace();
            }
        }
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
