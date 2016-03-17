package tetrisgame;

import java.util.Random;

public class TetrisGrid {

	public enum Direction {
		LEFT, RIGHT, DOWN, BOTTOM;
	}

	private int startX, startY, totalWidth, totalHeight, tileWidth, tileHeight;
	private int gridWidth, gridHeight;

	Random random;

	/*
	 * 0 -> aire
	 * 1~7 -> piezas fijas
	 * -1~-7 -> piezas moviles
	 * 
	 * 1: I, azul claro
	 * 2: T, morado
	 * 3: Z, rojo
	 * 4: S, verde
	 * 5: O, amarillo
	 * 6: L, naranja
	 * 7: J, azul oscuro
	 */
	private int[][] grid;

	private int[][] currentTetrimino;
	private int currentTetriminoId;

	public TetrisGrid(int startX, int startY, int totalWidth, int totalHeight, int gridWidth, int gridHeight) {
		this.startX = startX;
		this.startY = startY;
		this.totalWidth = totalWidth;
		this.totalHeight = totalHeight;

		this.tileWidth = totalWidth / gridWidth;
		this.tileHeight = totalHeight / gridHeight;

		// +2 porque arriba del todo hay 2 filas que no se ven
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight + 2;

		grid = new int[this.gridHeight][gridWidth];
		currentTetrimino = new int[4][2];

		random = new Random();
	}

	// Vaciar todo
	public void clear() {
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				grid[j][i] = 0;
			}
		}
	}

	// Gravedad de tiles no posicionados
	public void applyForegroundGravity() {

		//boolean canFall = true;

		// nueva posicion del tetrimino, un bloque mas abajo
		int[][] newPos = new int[currentTetrimino.length][2];
		for (int i = 0; i < currentTetrimino.length; i++) {
			newPos[i][0] = currentTetrimino[i][0] + 1;
			newPos[i][1] = currentTetrimino[i][1];
			/*if (newPos[i][0] == gridHeight) {
				canFall = false;
				break;
			} else if (grid[newPos[i][0]][newPos[i][1]] > 0) {
				canFall = false;
				break;
			}*/
		}
		//if (canFall) {
		if (canFitInGrid(newPos)) {
			// Cambia el tetrimino para que baje un bloque
			for (int i = 0; i < currentTetrimino.length; i++) {
				grid[currentTetrimino[i][0]][currentTetrimino[i][1]] = 0;
			}
			for (int i = 0; i < newPos.length; i++) {
				grid[newPos[i][0]][newPos[i][1]] = currentTetriminoId;
			}
			currentTetrimino = newPos;
		} else {
			for (int i = 0; i < currentTetrimino.length; i++) {
				grid[currentTetrimino[i][0]][currentTetrimino[i][1]] = -currentTetriminoId;
			}
			addTetrimino();
		}

	}

	// Gravedad de tiles ya posicionados
	public void applyBackgroundGravity() {

		for (int i = gridHeight - 2; i >= 0; i--) {

			// Comprobar si necesita mover las piezas hacia abajo
			boolean gravity = false;
			for (int j = 0; j < gridWidth; j++) {
				if (grid[i + 1][j] == 0 && grid[i][j] > 0) {
					gravity = true;
				} else if (grid[i + 1][j] > 0 && grid[i][j] > 0) {
					gravity = false;
					break;
				}
			}

			// Mover las piezas hacia abajo en ese caso
			if (gravity) {
				for (int k = 0; k < gridWidth; k++) {
					if (grid[i + 1][k] == 0 && grid[i][k] > 0) {
						grid[i + 1][k] = grid[i][k];
						grid[i][k] = 0;
					}
				}
			}
		}
	}

	// Mira si hay filas llenas y las quita, y luego las baja
	public void checkForFullRows() {

		for (int i = 2; i < gridHeight; i++) {

			// Comprobar si esta lleno
			boolean isFull = true;
			for (int j = 0; j < gridWidth; j++) {
				if (grid[i][j] <= 0) {
					isFull = false;
					break;
				}
			}

			// Quitar la fila
			if (isFull) {
				System.out.println("Fila llena");
				for (int k = 0; k < gridWidth; k++) {
					grid[i][k] = 0;
					// TODO Dar puntos
				}
			}
		}

	}

	// Comprueba que se ha perdido la partida
	public boolean checkForLostGame() {

		boolean hasLost = false;
		for (int i = 0; i < gridWidth; i++) {
			if (grid[0][i] > 0 || grid[1][i] > 0) {
				hasLost = true;
				// TODO Puntos
				break;
			}
		}

		return hasLost;

	}

	// Añade otro tetrimino al tablero
	public void addTetrimino() {

		// Crear el tetrimino
		int type = random.nextInt(7) + 1;
		currentTetrimino = new int[4][2];
		currentTetriminoId = -type;

		// Posicion de la pieza "central"
		currentTetrimino[0][0] = 1;
		int currentPosX = (int) Math.floor(gridWidth / 2) - 1;
		currentTetrimino[0][1] = currentPosX;

		// Posicion de las demas piezas dependiendo del tipo
		switch (type) {
		case 1: // I
			currentTetrimino[1][0] = 1;
			currentTetrimino[1][1] = currentPosX + 1;
			currentTetrimino[2][0] = 1;
			currentTetrimino[2][1] = currentPosX + 2;
			currentTetrimino[3][0] = 1;
			currentTetrimino[3][1] = currentPosX - 1;
			break;
		case 2: // T
			currentTetrimino[1][0] = 1;
			currentTetrimino[1][1] = currentPosX + 1;
			currentTetrimino[2][0] = 0;
			currentTetrimino[2][1] = currentPosX;
			currentTetrimino[3][0] = 1;
			currentTetrimino[3][1] = currentPosX - 1;
			break;
		case 3: // Z
			currentTetrimino[1][0] = 0;
			currentTetrimino[1][1] = currentPosX;
			currentTetrimino[2][0] = 0;
			currentTetrimino[2][1] = currentPosX + 1;
			currentTetrimino[3][0] = 1;
			currentTetrimino[3][1] = currentPosX - 1;
			break;
		case 4: // S
			currentTetrimino[1][0] = 0;
			currentTetrimino[1][1] = currentPosX;
			currentTetrimino[2][0] = 1;
			currentTetrimino[2][1] = currentPosX + 1;
			currentTetrimino[3][0] = 0;
			currentTetrimino[3][1] = currentPosX - 1;
			break;
		case 5: // O
			currentTetrimino[1][0] = 0;
			currentTetrimino[1][1] = currentPosX;
			currentTetrimino[2][0] = 1;
			currentTetrimino[2][1] = currentPosX + 1;
			currentTetrimino[3][0] = 0;
			currentTetrimino[3][1] = currentPosX + 1;
			break;
		case 6: // L
			currentTetrimino[1][0] = 1;
			currentTetrimino[1][1] = currentPosX - 1;
			currentTetrimino[2][0] = 1;
			currentTetrimino[2][1] = currentPosX + 1;
			currentTetrimino[3][0] = 0;
			currentTetrimino[3][1] = currentPosX + 1;
			break;
		case 7: // J
			currentTetrimino[1][0] = 1;
			currentTetrimino[1][1] = currentPosX - 1;
			currentTetrimino[2][0] = 1;
			currentTetrimino[2][1] = currentPosX + 1;
			currentTetrimino[3][0] = 0;
			currentTetrimino[3][1] = currentPosX - 1;
			break;
		}

	}

	// Rota el tetrimino si se puede
	public void rotateTetrimino(boolean rotateClockwise) {

		if (currentTetriminoId == -5) { return; } // Si es un cuadrado apaga y vamonos

		int[][] newPos = new int[4][2];

		int pivotY = currentTetrimino[0][0];
		int pivotX = currentTetrimino[0][1];
		newPos[0][0] = pivotY;
		newPos[0][1] = pivotX;

		for (int i = 1; i < 4; i++) {

			// Obtener la nueva posicion
			if (rotateClockwise) {
				newPos[i][0] = pivotY + (currentTetrimino[i][1] - pivotX);
				newPos[i][1] = pivotX - (currentTetrimino[i][0] - pivotY);
			} else {
				newPos[i][0] = pivotY - (currentTetrimino[i][1] - pivotX);
				newPos[i][1] = pivotX + (currentTetrimino[i][0] - pivotY);
			}

		}

		// Cambiar las posiciones
		if (canFitInGrid(newPos)) {
			changePosition(currentTetrimino, newPos);
		}

	}

	// Mueve el tetrimino (izquierda, derecha, abajo, abajo del todo)
	public void moveTetrimino(Direction direction) {
		
		int[][] newPos = new int[4][2];
		
		switch (direction) {
		
		case LEFT: // Izquierda
			
			for (int i = 0; i < 4; i++) {
				newPos[i][0] = currentTetrimino[i][0];
				newPos[i][1] = currentTetrimino[i][1] - 1;
			}
			break;
			
		case RIGHT: // Derecha
			
			for (int i = 0; i < 4; i++) {
				newPos[i][0] = currentTetrimino[i][0];
				newPos[i][1] = currentTetrimino[i][1] + 1;
			}
			break;
			
		case DOWN: // Abajo (1 vez)
			
			for (int i = 0; i < 4; i++) {
				newPos[i][0] = currentTetrimino[i][0] + 1;
				newPos[i][1] = currentTetrimino[i][1];
			}
			break;
			
		case BOTTOM: // Abajo (del todo)
			
			int[][] lastGoodPos = new int[4][2];
			for (int i = 0; i < 4; i++) {
				lastGoodPos[i][0] = currentTetrimino[i][0];
				lastGoodPos[i][1] = currentTetrimino[i][1];
				newPos[i][1] = currentTetrimino[i][1];
			}
			
			while (true) {
				
				// Moverlo hacia abajo
				for (int i = 0; i < 4; i++) {
					newPos[i][0] = lastGoodPos[i][0] + 1;
				}
				
				// Si se puede seguir, se sigue
				if (canFitInGrid(newPos)) {
					for (int i = 0; i < 4; i++) {
						lastGoodPos[i][0] = newPos[i][0];
					}
				} else {
					break;
				}
				
			}

			for (int i = 0; i < 4; i++) {
				newPos[i][0] = lastGoodPos[i][0];
				newPos[i][1] = lastGoodPos[i][1];
			}
			
			break;
			
		}
		
		if (canFitInGrid(newPos)) {
			changePosition(currentTetrimino, newPos);
		}
		
	}
	
	// Comprueba si el tetrimino dado cabe en el grid sin salirse ni chocar
	private boolean canFitInGrid(int[][] tetrimino) {
		boolean canFit = true;
		
		for (int i = 0; i < tetrimino.length; i++) {
				if (tetrimino[i][0] >= 0 && tetrimino[i][0] < gridHeight
						&& tetrimino[i][1] >= 0 && tetrimino[i][1] < gridWidth) {
					if (grid[tetrimino[i][0]][tetrimino[i][1]] > 0) {
						canFit = false;
						break;
					}
				} else {
					canFit = false;
					break;
				}
		}
		
		return canFit;
	}
	
	// Pasa el tetrimino de oldPos a newPos
	private void changePosition(int[][] oldPos, int[][] newPos) {
		
		if (oldPos.length != newPos.length) { System.out.println("Esto no deberia pasar (1)"); return; }
		
		int id = grid[oldPos[0][0]][oldPos[0][1]];
		
		// Quitar uno
		for (int i = 0; i < oldPos.length; i++) {
			grid[oldPos[i][0]][oldPos[i][1]] = 0;
		}

		// Poner otro
		for (int i = 0; i < oldPos.length; i++) {
			oldPos[i][0] = newPos[i][0];
			oldPos[i][1] = newPos[i][1];
			grid[newPos[i][0]][newPos[i][1]] = id;
		}
		
	}

	public int[][] getGrid() {
		return grid;
	}

	public int getGridWidth() {
		return gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public int getTotalWidth() {
		return totalWidth;
	}

	public int getTotalHeight() {
		return totalHeight;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

}
