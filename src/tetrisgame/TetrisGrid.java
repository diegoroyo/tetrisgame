package tetrisgame;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TetrisGrid {
	
	// TODO: Hacer que cambie el nivel respecto a las lineas que lleva
	// TODO: Mejorar el moveTetrimino y rotateTetrimino para cuando la pieza este chocandose contra la pared/suelo, a veces no deja moverlo

	StatsMenu statsMenu;

	public enum Direction {
		LEFT, RIGHT, DOWN, BOTTOM;
	}

	private int startX, startY, totalWidth, totalHeight, tileWidth, tileHeight;
	private int gridWidth, gridHeight;

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
	private ArrayList<int[]> tilesChanged;
	private boolean firstPaint;

	private int[][] currentTetrimino;
	private int currentTetriminoId;
	private int stopCooldown;
	
	private int[][] shadowPos;
	
	private BufferedImage imageTetriminos;
	
	private boolean isBeingPainted;

	public TetrisGrid(int startX, int startY, int totalWidth, int totalHeight, int gridWidth, int gridHeight, BufferedImage imageTetriminos, StatsMenu statsMenu) {
		this.statsMenu = statsMenu;
		
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
		stopCooldown = 2;
		
		shadowPos = new int[4][2];
		
		this.imageTetriminos = imageTetriminos;
		
		tilesChanged = new ArrayList<int[]>();
		firstPaint = false;
		
		isBeingPainted = false;
		
	}
	
	public void start(Graphics g) {
		statsMenu.start(1);
		firstPaint = false;
		
		// Añadir un tetrimino y que comience
		// TODO: llamarlo aparte?
		addTetrimino();
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

		// Nueva posicion del tetrimino, un bloque mas abajo
		int[][] newPos = new int[currentTetrimino.length][2];
		for (int i = 0; i < currentTetrimino.length; i++) {
			newPos[i][0] = currentTetrimino[i][0] + 1;
			newPos[i][1] = currentTetrimino[i][1];
		}
		
		if (canFitInGrid(newPos)) {
			// Cambia el tetrimino para que baje un bloque
			for (int i = 0; i < currentTetrimino.length; i++) {
				grid[currentTetrimino[i][0]][currentTetrimino[i][1]] = 0;
				
				// Cambiar el tile
				addChangedTile(currentTetrimino[i][1], currentTetrimino[i][0]);
			}
			for (int i = 0; i < newPos.length; i++) {
				grid[newPos[i][0]][newPos[i][1]] = currentTetriminoId;
				
				// Cambiar el tile
				addChangedTile(newPos[i][1], newPos[i][0]);
			}
			currentTetrimino = newPos;
		} else {
			stopCooldown--;
			if (stopCooldown <= 0) {
				for (int i = 0; i < currentTetrimino.length; i++) {
					grid[currentTetrimino[i][0]][currentTetrimino[i][1]] = -currentTetriminoId;
					
					// Cambiar el tile
					addChangedTile(currentTetrimino[i][1], currentTetrimino[i][0]);
				}
				checkForFullRows();
				addTetrimino();
			}
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
					};
					
					// Cambia el tile
					addChangedTile(k, i + 1);
					addChangedTile(k, i);
				}
				
				// Volver a bajar las piezas
				applyBackgroundGravity();
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
				statsMenu.addLines(1);
				statsMenu.addPoints(10);
				for (int k = 0; k < gridWidth; k++) {
					grid[i][k] = 0;
					// TODO: puntos que cambien segun si haces 1-2-3-4 lineas + back to back tetris etc
				}
				
				applyBackgroundGravity();
			}
		}

	}

	// Comprueba que se ha perdido la partida
	public boolean checkForLostGame() {

		boolean hasLost = false;
		for (int i = 0; i < gridWidth; i++) {
			if (grid[0][i] > 0 || grid[1][i] > 0) {
				hasLost = true;
				// TODO quitar puntos
				break;
			}
		}

		return hasLost;

	}

	// Añade otro tetrimino al tablero
	public void addTetrimino() {

		// Crear el tetrimino
		int type = statsMenu.getNextTetrimino();
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
		
		stopCooldown = 2;

	}

	// Rota el tetrimino si se puede
	public void rotateTetrimino(boolean rotateClockwise) {
		
		// Si es un cuadrado apaga y vamonos
		// y si no se debe mover pues tambien
		if (currentTetriminoId == -5 || stopCooldown == 0) { return; }

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
			stopCooldown = 2;
			changePosition(currentTetrimino, newPos);
		}

	}

	// Mueve el tetrimino (izquierda, derecha, abajo, abajo del todo)
	public void moveTetrimino(Direction direction) {
		
		if (stopCooldown == 0) { return; } // para cuando no se deba mover
		
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
			
			int[][] shadowPos = getShadowPosition();
			
			for (int i = 0; i < 4; i++) {
				newPos[i][0] = shadowPos[i][0];
				newPos[i][1] = shadowPos[i][1];
			}
			
			break;
			
		}
		
		if (canFitInGrid(newPos)) {
			changePosition(currentTetrimino, newPos);
			if (direction == Direction.BOTTOM) {
				stopCooldown = 0; // si se baja del todo convertirlo a solido
				
				for (int i = 0; i < currentTetrimino.length; i++) {
					grid[currentTetrimino[i][0]][currentTetrimino[i][1]] = -currentTetriminoId;
					
					// Cambiar el tile
					addChangedTile(currentTetrimino[i][1], currentTetrimino[i][0]);
				}
				checkForFullRows();
				addTetrimino();
				
				// applyForegroundGravity();
			} else {
				stopCooldown = 2; // si no, se puede seguir moviendo
			}
		}
		
	}
	
	// Cambia la posicion de la sombra
	public void updateShadowPosition() {
		shadowPos = getShadowPosition();
	}
	
	// Consigue la posicion de la sombra
	private int[][] getShadowPosition() {
		int[][] shadowPos = new int[4][2];
		
		int[][] lastGoodPos = new int[4][2];
		for (int i = 0; i < 4; i++) {
			lastGoodPos[i][0] = currentTetrimino[i][0];
			lastGoodPos[i][1] = currentTetrimino[i][1];
			shadowPos[i][1] = currentTetrimino[i][1];
		}
		
		while (true) {
			
			// Moverlo hacia abajo
			for (int i = 0; i < 4; i++) {
				shadowPos[i][0] = lastGoodPos[i][0] + 1;
			}
			
			// Si se puede seguir, se sigue
			if (canFitInGrid(shadowPos)) {
				for (int i = 0; i < 4; i++) {
					lastGoodPos[i][0] = shadowPos[i][0];
				}
			} else {
				break;
			}
			
		}

		for (int i = 0; i < 4; i++) {
			shadowPos[i][0] = lastGoodPos[i][0];
		}
		
		return shadowPos;
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
		
		if (oldPos.length != newPos.length) { return; }
		
		int id = grid[oldPos[0][0]][oldPos[0][1]];
		
		// Quitar uno
		for (int i = 0; i < oldPos.length; i++) {
			grid[oldPos[i][0]][oldPos[i][1]] = 0;
			
			// Cambia el tile
			addChangedTile(oldPos[i][1], oldPos[i][0]);
		}

		// Poner otro
		for (int i = 0; i < oldPos.length; i++) {
			oldPos[i][0] = newPos[i][0];
			oldPos[i][1] = newPos[i][1];
			grid[newPos[i][0]][newPos[i][1]] = id;
			
			// Cambia el tile
			addChangedTile(newPos[i][1], newPos[i][0]);
		}
		
	}
	
	public void paint(Graphics g) {
		
		if (isBeingPainted) {
			firstPaint = false;
			System.out.println("Double paint");
		} else {
			isBeingPainted = true;
		}
		
		// Dibujar los tiles
		if (firstPaint) {
			
			// Dibujar la sombra debajo
			for (int i = 0; i < 4; i++) {
				if (shadowPos[i][0] >= 2 && shadowPos[i][0] < gridHeight &&
						shadowPos[i][1] >= 0 && shadowPos[i][1] < gridWidth) {
					if (grid[shadowPos[i][0]][shadowPos[i][1]] == 0) {
						
						// Quitarlo de la lista de cambiadas si existe
						checkChangedTile(shadowPos[i][1], shadowPos[i][0], true);
						
						// Dibujar la sombra
						g.drawImage(getTileImage(8),
								startX + tileWidth * shadowPos[i][1],
								startY + tileHeight * (shadowPos[i][0] - 2),
								tileWidth,
								tileHeight,
								null);
						
					}
				} else {
					System.out.println("Esto no deberia pasar (1)");
				}
			}
			
			// Dibujar SOLO los tiles que han cambiado
			for (int i = 0; i < tilesChanged.size(); i++) {
				int x = tilesChanged.get(i)[0];
				int y = tilesChanged.get(i)[1];
				g.drawImage(getTileImage(grid[y][x]),
						startX + tileWidth * x,
						startY + tileHeight * (y - 2),
						tileWidth,
						tileHeight,
						null);
			}

			if (!firstPaint) {
				tilesChanged.clear();
			}
			
		} else {

			// Dibujar todos los tiles
			for (int i = 2; i < gridHeight; i++) {
				for (int j = 0; j < gridWidth; j++) {
					g.drawImage(getTileImage(grid[i][j]),
								startX + tileWidth * j,
								startY + tileHeight * (i - 2),
								tileWidth,
								tileHeight,
								null);
				}
			}
			
			// Dibujar la sombra encima
			for (int i = 0; i < 4; i++) {
				g.drawImage(getTileImage(8),
						startX + tileWidth * shadowPos[i][1],
						startY + tileHeight * (shadowPos[i][0] - 2),
						tileWidth,
						tileHeight,
						null);
			}
			
			firstPaint = true;
			
		}
		
		// Añadir la sombra actual para la proxima vez que se llame esta funcion
		for (int i = 0; i < 4; i++) {
			// Cambiar el tile
			addChangedTile(shadowPos[i][1], shadowPos[i][0]);
		}
		
		if (isBeingPainted) {
			isBeingPainted = false;
		}
		
	}
	
	private BufferedImage getTileImage(int id) {
		if (id < 0) {
			id = -id;
		}
		return imageTetriminos.getSubimage(16 * id, 0, 16, 16); // x, y, width, height
	}
	
	// Para dibujar solo los tiles cambiados: en changePosition y applyForegroundGravity (todo lo movil) y applyBackgroundGravity (lo del fondo)
	private void addChangedTile(int x, int y) {
		if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
			if (y < 2) { return; } // No dibujar tiles en las dos primeras filas porque no se ven
			if (!checkChangedTile(x, y, false)) {
				tilesChanged.add(new int[]{x, y}); 
			}
		}
	}
	
	// Quita una tile de la lista de las que han cambiado si existe en la lista
	private boolean checkChangedTile(int x, int y, boolean remove) {
		for (int i = 0; i < tilesChanged.size(); i++) {
			if (tilesChanged.get(i)[0] == x && tilesChanged.get(i)[1] == y) {
				if (remove) {
					tilesChanged.remove(i);
				}
				return true;
			}
		}
		return false;
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
