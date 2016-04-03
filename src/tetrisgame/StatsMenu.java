package tetrisgame;

import java.util.Random;

public class StatsMenu {

	int startX, startY, menuWidth, menuHeight;

	Random random;
	int[] nextTetriminos;

	int savedTetrimino;

	int points, level;

	public StatsMenu(int startX, int startY, int menuWidth, int menuHeight) {
		this.startX = startX;
		this.startY = startY;
		this.menuWidth = menuWidth;
		this.menuHeight = menuHeight;
	
		nextTetriminos = new int[3];
		random = new Random();
	}

	public void start(int level) {
		this.level = level;

		points = 0;

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
	
}
