package tetrisgame;

import java.applet.Applet;
import java.util.ArrayList;
import java.util.Arrays;
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
	StatsMenu statsMenu;
	
	private Image[] tileImages;
	private Image image;
	private Graphics graphics, bufferG;
	private URL base;
	
	private long time;
	private final int LOOP_TIME = 500;
	private final boolean PRINT_ELAPSED_TIME = false;
	
	private ArrayList<Integer> VALID_KEYS = new ArrayList<Integer>(
		Arrays.asList(
				KeyEvent.VK_RIGHT, // Mover derecha
				KeyEvent.VK_LEFT, // Mover izquierda
				KeyEvent.VK_DOWN, // Bajar 1 vez
				KeyEvent.VK_SPACE, // Bajar abajo del todo
				KeyEvent.VK_UP, // Rotar (sentido agujas)
				KeyEvent.VK_SHIFT // Rotar (sentido contrario)
		));
	
	@Override
	public void init() {
		addKeyListener(this);
        setSize(400, 520);
        setBackground(Color.LIGHT_GRAY);
        setFocusable(true);
        Frame frame = (Frame) this.getParent().getParent();
        frame.setTitle("Castañón, ponme el tetris");
        
        try {
			base = getDocumentBase();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Hay que llamar a Castañón porque no se encuentra la base");
		}
        
        tileImages = new Image[9];
        tileImages[0] = getImage(base, "data/tetrimino_blank.png"); // vacio
        tileImages[1] = getImage(base, "data/tetrimino_lightblue.png"); // I
        tileImages[2] = getImage(base, "data/tetrimino_purple.png"); // T
        tileImages[3] = getImage(base, "data/tetrimino_red.png"); // Z
        tileImages[4] = getImage(base, "data/tetrimino_green.png"); // S
        tileImages[5] = getImage(base, "data/tetrimino_yellow.png"); // O
        tileImages[6] = getImage(base, "data/tetrimino_orange.png"); // L
        tileImages[7] = getImage(base, "data/tetrimino_blue.png"); // J
        tileImages[8] = getImage(base, "data/tetrimino_shadow.png"); // sombra
        
        image = createImage(this.getWidth(), this.getHeight());
        bufferG = image.getGraphics();
        
        graphics = getGraphics();
        
	}
	
	@Override
	public void start() {
		
		statsMenu = new StatsMenu(
				270, 10, // startX, startY
				120, 500); // width, height
		
		tetrisGrid = new TetrisGrid(
				10, 10, // startX, startY
				250, 500, // width, height
				10, 20, // gridWidth, gridHeight
				tileImages, // tiles usados
				statsMenu); // menu el cual controla
		
		tetrisGrid.start();
		
		Thread thread = new Thread(this);
        thread.start();
	}
	
	@Override
	public void update(Graphics g) {
		
		if (PRINT_ELAPSED_TIME) {
			printElapsedTime();
		}
		
		if (tetrisGrid.checkForLostGame()) {
			// TODO
		}
		else
		{
			tetrisGrid.applyForegroundGravity();
			tetrisGrid.checkForFullRows();
			tetrisGrid.applyBackgroundGravity();
			tetrisGrid.updateShadowPosition();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		
		bufferG.drawImage(image, 0, 0, this);
		
		tetrisGrid.paint(graphics);
		
	}
	
	private void printElapsedTime() {
		System.out.println(new Date().getTime()-time + " ms: " + ((new Date().getTime()-time) - LOOP_TIME));
		time = new Date().getTime();
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

		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			tetrisGrid.moveTetrimino(Direction.LEFT);
			break;
		case KeyEvent.VK_RIGHT:
			tetrisGrid.moveTetrimino(Direction.RIGHT);
			break;
		case KeyEvent.VK_DOWN:
			tetrisGrid.moveTetrimino(Direction.DOWN);
			break;
		case KeyEvent.VK_SPACE:
			tetrisGrid.moveTetrimino(Direction.BOTTOM);
			break;
		case KeyEvent.VK_UP:
			tetrisGrid.rotateTetrimino(true);
			break;
		case KeyEvent.VK_SHIFT:
			tetrisGrid.rotateTetrimino(false);
			break;
		}
		
		if (isValidKey(e.getKeyCode())) {
			tetrisGrid.updateShadowPosition();
			tetrisGrid.paint(graphics);
		}
		
	}
	
	private boolean isValidKey(Integer c) {
		return VALID_KEYS.contains(c);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Nada
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// Nada
	}

}
