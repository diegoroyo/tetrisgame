package tetrisgame;

import java.util.Random;

public class TetrisGrid {

	private int startX, startY, totalWidth, totalHeight, tileWidth, tileHeight;
	private int gridWidth, gridHeight;
	
	Random random;
	
	/* 
	 * 0 -> aire
	 * 1~7 -> piezas fijas
	 * -1~-7 -> piezas moviles 
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
		for (int i = 0; i < gridWidth; i++)
		{
			for (int j = 0; j < gridHeight; j++) {
				grid[j][i] = 0;
			}
		}
	}
	
	// Bajar las piezas de una columna al suelo
	public void applyGravity() {
		
		// Gravedad de tiles no posicionados
		boolean canFall = true;
		// nueva posicion del tetrimino, un bloque mas abajo
		int[][] newPos = new int[currentTetrimino.length][2];
		for (int i = 0; i < currentTetrimino.length; i++) {
			newPos[i][0] = currentTetrimino[i][0] + 1;
			newPos[i][1] = currentTetrimino[i][1];
			if (newPos[i][0] == gridHeight) {
				canFall = false;
				break;
			} else if (grid[newPos[i][0]][newPos[i][1]] > 0) {
				canFall = false;
				break;
			}
		}
		if (canFall) {
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
		
		// Gravedad de tiles ya posicionados
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
	
	public int[][] getGrid() {
		return grid;
	}
	
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
			currentTetrimino[1][1] = currentPosX + 1;
			currentTetrimino[2][0] = 1;
			currentTetrimino[2][1] = currentPosX - 1;
			currentTetrimino[3][0] = 0;
			currentTetrimino[3][1] = currentPosX + 1;
			break;
		case 7: // J
			currentTetrimino[1][0] = 1;
			currentTetrimino[1][1] = currentPosX + 1;
			currentTetrimino[2][0] = 1;
			currentTetrimino[2][1] = currentPosX - 1;
			currentTetrimino[3][0] = 0;
			currentTetrimino[3][1] = currentPosX - 1;
			break;
		}
		
	}

	// TODO Rotar, mover, etc
	
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
