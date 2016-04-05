package tetrisgame;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class StatsMenu {

	private int startX, startY, menuWidth, menuHeight;

	private Random random;
	private int[] nextTetriminos;

	private int savedTetrimino;

	private int points, level, lines, time;
	
	private ArrayList<BufferedImage> imageResources;
	public enum Images {
		MENU_BACKGROUND,
		NUMBERS,
		POINTS_BACKGROUND;
	}
	
	private Graphics graphics; // TODO hacerlo de otra manera que no tenga que guardar los graphics aqui
	private boolean firstPaint;

	public StatsMenu(int startX, int startY, int menuWidth, int menuHeight, ArrayList<BufferedImage> imageResources, Graphics graphics) {
		this.startX = startX;
		this.startY = startY;
		this.menuWidth = menuWidth;
		this.menuHeight = menuHeight;
	
		nextTetriminos = new int[3];
		random = new Random();
		
		this.imageResources = imageResources;
		
		this.graphics = graphics;
		
		firstPaint = false;
	}

	public void start(int level) {
		this.level = level;
		points = 0;
		
		firstPaint = true;

		for (int i = 0; i < 3; i++) {
			nextTetriminos[i] = random.nextInt(7) + 1;
		}
	}

	public void reset() {
		points = 0;
		level = 0;
		savedTetrimino = 0;
		nextTetriminos = new int[3];
	}

	public int getNextTetrimino() {
		int[] tempTetriminos = new int[3];
		for (int i = 0; i < 3; i++) {
			tempTetriminos[i] = nextTetriminos[i];
		}

		nextTetriminos[0] = tempTetriminos[1];
		nextTetriminos[1] = tempTetriminos[2];
		nextTetriminos[2] = random.nextInt(7) + 1;

		return tempTetriminos[0];
	}
	
	public void addPoints(int pointsToAdd) {
		points += pointsToAdd;
		
		paint(graphics);
	}
	
	public void paint(Graphics g) {
		
		if (firstPaint) {
			g.drawImage(imageResources.get(Images.MENU_BACKGROUND.ordinal()),
					startX, startY,
					menuWidth, menuHeight,
					null);
		}
		
		// dibujar el fondo del score para que no 
		g.drawImage(imageResources.get(Images.POINTS_BACKGROUND.ordinal()),
				startX + 2, startY + 203, // x, y
				96, 16, // width, height
				null);
		
		// dibujar el score
		String pointsString = getNumberAsString(points, 6);
		for (int i = 0; i < 6; i++) {
			g.drawImage(getNumber(Character.getNumericValue(pointsString.charAt(i))), // el numero
					startX + 2 + 16 * i, startY + 203, // x, y
					16, 16, // width, height
					null);
		}
		
		String levelString = getNumberAsString(level, 2);
		for (int i = 0; i < 2; i++) {
			g.drawImage(getNumber(Character.getNumericValue(levelString.charAt(i))), // el numero
					startX + 2 + 16 * i, startY + 258, // x, y
					16, 16, // width, height
					null);
		}
		
		String linesString = getNumberAsString(lines, 3);
		for (int i = 0; i < 3; i++) {
			g.drawImage(getNumber(Character.getNumericValue(linesString.charAt(i))), // el numero
					startX + 50 + 16 * i, startY + 258, // x, y
					16, 16, // width, height
					null);
		}
		
		String timeString = getNumberAsString((int) Math.floor(time / 60), 2) + ":" + getNumberAsString(time % 60, 2);
		for (int i = 0; i < 5; i++) {
			g.drawImage(getNumber(Character.getNumericValue(timeString.charAt(i))), // el numero
					startX + 10 + 16 * i, startY + 432, // x, y
					16, 16, // width, height
					null);
		}
		
	}
	
	private String getNumberAsString(int points, int zeroes) {
		String s = Integer.toString(points);
		return new String(new char[zeroes - s.length()]).replace('\0', '0') + s;
	}
	
	private BufferedImage getNumber(int number) {
		if (number == -1) { return imageResources.get(Images.NUMBERS.ordinal()).getSubimage(160, 0, 16, 16); }
		return imageResources.get(Images.NUMBERS.ordinal()).getSubimage(16 * number, 0, 16, 16);
	}
	
}
