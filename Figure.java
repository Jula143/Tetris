package tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

class Figure{
	private int x, y, WIDTH, HEIGHT; //width height of one square block
	private int[][] figure;
	private int[][][] rotations;
	private int currentRotation;
	private int FIGURE_WIDTH, FIGURE_HEIGHT;
	private int dx = 0, dy = 0;
	private Shape shape;
	private boolean fallingFinished;
	
	public static enum Shape{
		I, O, T, S, Z, J, L
	}
	
	public Figure(int winWidth, int width, int height, Shape fShape) {
		
		WIDTH = width;
		HEIGHT = height;
		shape = fShape;
		x = winWidth/2 - width;
		y = 0;
		
		fallingFinished = false;
		fillFigureArray(shape);
		currentRotation=0;
		fillRotations();
		
		FIGURE_WIDTH = figure[0].length*width;
		FIGURE_HEIGHT = figure.length*width;
		
	}
	
	private void fillRotations() {
		rotations = new int[4][][];
		rotations[0]=figure;
		for(int i = 1; i<4; i++) {
			int rows = rotations[i-1][0].length;
			int columns = rotations[i-1].length;
			
			rotations[i] = new int[rows][columns];
			for(int j=0; j<rows; j++) {
				for(int k=0; k<columns; k++) {
					rotations[i][j][k]=rotations[i-1][columns-k-1][j];
				}
			}
		}
	}
	
	
	public void fillFigureArray(Shape shape) {
		if(shape.equals(Shape.O)) {
			figure = new int[][]{{1,1},{1,1}};
		}
		else if(shape.equals(Shape.I)) {
			figure = new int[][]{{2,2,2,2}};
		}
		else if(shape.equals(Shape.J)) {
			figure = new int[][]{{3,3,3},{0,0,3}};
		}
		else if(shape.equals(Shape.L)) {
			figure = new int[][]{{4,4,4},{4,0,0}};
		}
		else if(shape.equals(Shape.Z)) {
			figure = new int[][]{{5,5,0},{0,5,5}};
		}
		else if(shape.equals(Shape.S)) {
			figure = new int[][]{{0,6,6},{6,6,0}};
		}
		else if(shape.equals(Shape.T)) {
			figure = new int[][]{{7,7,7},{0,7,0}};
		}
	}
	
	public void drawShape(Graphics g) {
		
		int cx = x;
		int cy = y;
		
		for(int i=0; i<figure.length; i++) {
			for (int j=0; j<figure[0].length; j++){
				if(figure[i][j]!=0) {
					drawRect(g, cx, cy, figure[i][j], WIDTH, HEIGHT);
				}
				cx+=WIDTH;
			}
			cx = x;
			cy += HEIGHT;
			
		}
			
	}
	
	public static void drawRect(Graphics g, int x, int y, int color, int w, int h) {
		if(color==1)
			g.setColor(Color.YELLOW);
		else if(color==2)
			g.setColor(Color.CYAN);
		else if(color==3)
			g.setColor(Color.BLUE);
		else if(color==4)
			g.setColor(Color.ORANGE);
		else if(color==5)
			g.setColor(Color.RED);
		else if(color==6)
			g.setColor(Color.GREEN);
		else if(color==7)
			g.setColor(Color.PINK);
		g.fillRect(x, y, w, h);
	}
	
	
	public static Shape getRandomShape() {
		Shape[] shapes = Shape.values();
		Random rand = new Random();
		return shapes[rand.nextInt(shapes.length)];	
	}
	
	public void rotateFigure() {
		currentRotation++;
		
		if(currentRotation>=4) {
			currentRotation=0;
		}
		
		figure = rotations[currentRotation];
		FIGURE_WIDTH = figure[0].length*WIDTH;
		FIGURE_HEIGHT = figure.length*HEIGHT;
	}
	
	public void reverseRotation() {
		currentRotation--;
		
		if(currentRotation<=0) {
			currentRotation=0;
		}
		
		figure = rotations[currentRotation];
		FIGURE_WIDTH = figure[0].length*WIDTH;
		FIGURE_HEIGHT = figure.length*HEIGHT;
	}
	
	public int[][] getNextRotation() {
		int nextRotation = currentRotation+1;
		if(nextRotation>=4) {
			nextRotation=0;
		}
		
		return rotations[nextRotation];
	}
	
	public int getValueFromArray(int i, int j) {
		return figure[i][j];
	}
	
	public void moveLeft() {
		dx = -25;
		dy = 0;
	}
	
	public void moveRight() {
		dx = 25;
		dy = 0;
	}
	
	public void moveDown() {
		dx = 0;
		dy = 25;
	}
	
	
	public void update(int winHeight, int winWidth) {
		if(!fallingFinished) {
			if (x + dx >= 0 && x + dx <= winWidth - FIGURE_WIDTH) {
				x += dx;
				dx = 0;
			}
		
			if (y + dy >= 0 && y + dy <= winHeight - FIGURE_HEIGHT) {
				y += dy;
				dy = 0;
			}
			else if (y + dy > winHeight - FIGURE_HEIGHT){
				fallingFinished = true;
			}
		}
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public boolean isFallingFinished() {
		return fallingFinished;
	}

	public void setFallingFinished(boolean fallingFinished) {
		this.fallingFinished = fallingFinished;
	}

	public int getFIGURE_WIDTH() {
		return FIGURE_WIDTH;
	}

	public int getFIGURE_HEIGHT() {
		return FIGURE_HEIGHT;
	}

	public int[][] getFigure() {
		return figure;
	}
	
}



