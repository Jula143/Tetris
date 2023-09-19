package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JPanel implements KeyListener, Runnable{
	private static final long serialVersionUID = 1L;
	private final static int WIDTH = 400;
	private final static int HEIGHT = 500;
	private final static int CELL_SIZE = 25;
	private int[][] gameBoard;
	private Figure curFigure;
	private boolean running;
	private boolean gameEnded;
	private Thread threadPlayer;
	private Thread gameThread;
	private int playerScore = 1000;
	
	public Game() {
		setFocusable(true);
		addKeyListener(this);
		setPreferredSize(new Dimension(WIDTH + 200, HEIGHT));
		running = true;
		gameBoard = new int[HEIGHT/CELL_SIZE][WIDTH/CELL_SIZE];
		spawnNewFigure();
		
		threadPlayer = new Thread(this);
		threadPlayer.start();
		
		threadGame();
		
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Tetris");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(new Game());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		

	}
	
	private void threadGame() {
		gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
            	while (running) {
            		if(!gameEnded) {
            		
	    				if(curFigure.isFallingFinished()) {
	    					saveFigure();
	    					spawnNewFigure();
	    					searchFilledRows();
	    				}
	    				
	            		curFigure.moveDown();
	            		checkCollisions(1,0);
	            		
	            		checkGameEndCondition();
            		}
            		else {
            			curFigure.setFallingFinished(false);
            		}
    				
    				try {
    					Thread.sleep(700);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    			}
            }
            
        });

        gameThread.start();
	}
	
	private void spawnNewFigure() {
		curFigure = new Figure(WIDTH, CELL_SIZE, CELL_SIZE, Figure.getRandomShape());
	}
	
	private void saveFigure() {
		int x = curFigure.getX();
		int y = curFigure.getY();
		int column = x/CELL_SIZE;
		int row = y/CELL_SIZE;
		for(int i = 0; i < curFigure.getFigure().length; i++) {
			for(int j = 0; j<curFigure.getFigure()[0].length; j++) {
				if(curFigure.getValueFromArray(i, j)!=0) {
					gameBoard[row][column] = curFigure.getValueFromArray(i, j);
				}
				column++;
			}
			row++;
			column=x/CELL_SIZE;
		}
	}
	
	private void drawBoard(Graphics g) {
		for(int i = 0; i < gameBoard.length; i++) {
			for(int j = 0; j<gameBoard[0].length; j++) {
				if(gameBoard[i][j]!=0) {
					Figure.drawRect(g, j*CELL_SIZE, i*CELL_SIZE, gameBoard[i][j], CELL_SIZE, CELL_SIZE);
				}
			}
		}
	}
	
	private void clearBoard() {
		for(int i = 0; i < gameBoard.length; i++) {
			for(int j = 0; j<gameBoard[0].length; j++) {
				gameBoard[i][j]=0;
			}
		}
	}
	
	private void searchFilledRows() {
		for(int i = 0; i < gameBoard.length; i++) {
			int colorRect = 0;
			for(int j = 0; j<gameBoard[0].length; j++) {
				if(gameBoard[i][j]==0) {
					break;
				}
				else {
					colorRect++;
				}
				if(colorRect==gameBoard[0].length) {
					clearRow(i);
					playerScore+=100;
				}
			}
			colorRect=0;
		}
	}
	
	private void clearRow(int row) {
		for(int i = row; i>0; i--) {
			 System.arraycopy(gameBoard[i - 1], 0, gameBoard[i], 0, gameBoard[i].length);
		}
		Arrays.fill(gameBoard[0], 0);
	}
	
	
	private boolean checkCollisions(int verticalMove, int horizontalMove) {
		//vercical = 0 or 1
		//horizontal = -1 0 1
		int x = curFigure.getX();
		int y = curFigure.getY();
		int column = x/CELL_SIZE;
		int row = y/CELL_SIZE;
		for(int i = 0; i < curFigure.getFigure().length; i++) {
			for(int j = 0; j<curFigure.getFigure()[0].length; j++) {
				if(curFigure.getValueFromArray(i, j)!=0) {
					if(verticalMove!=0) {
						if(row+1>=HEIGHT/CELL_SIZE || gameBoard[row+1][column]!=0) {
							curFigure.setFallingFinished(true);
							return true;
						}
					}
					else {
						if(column+horizontalMove<0 ||
								column+horizontalMove>=WIDTH/CELL_SIZE || gameBoard[row][column+horizontalMove]!=0) {
							return true;
						}
					}	
				}
				column++;
			}
			row++;
			column=x/CELL_SIZE;
		}
		return false;
	}
		
	private boolean possibleRotation() {
		
		int x = curFigure.getX();
		int y = curFigure.getY();
		int column = x/CELL_SIZE;
		int row = y/CELL_SIZE;
		
		int newHeight = curFigure.getNextRotation().length;
		int newWidth = curFigure.getNextRotation()[0].length;
		
		if(column+newWidth>WIDTH/CELL_SIZE) {
			curFigure.setX(curFigure.getX()-CELL_SIZE*(column+newWidth-WIDTH/CELL_SIZE));
			if(possibleRotation())
				return true;
			else {
				curFigure.setX(x);
				return false;
			}
		}
		
		if(row+newHeight>HEIGHT/CELL_SIZE) {
			curFigure.setY(curFigure.getY()-CELL_SIZE*(row+newHeight-HEIGHT/CELL_SIZE));
			if(possibleRotation())
				return true;
			else {
				curFigure.setY(y);
				return false;
			}
		}	
		
		for(int i = 0; i < newHeight; i++) {
			for(int j = 0; j < newWidth; j++) {
				if(curFigure.getNextRotation()[i][j]!=0) {
					if(gameBoard[row][column]!=0) {
						return false;
						}	
					}
				column++;
			}
			row++;
			column=x/CELL_SIZE;
		}
		
		return true;
	}
	
	public void paint(Graphics g) {
		super.paint(g);

		// draw background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		if(!gameEnded) {
			curFigure.drawShape(g);
			drawBoard(g);
			
			Font font = new Font("Times New Roman", Font.PLAIN, 24); 
	        g.setFont(font);
			
			g.setColor(Color.BLACK);
			g.drawString("Score: ", WIDTH + 35, 50);
			g.drawString(Integer.toString(playerScore), WIDTH + 110, 50);
			
			Font fontUI = new Font("Times New Roman", Font.PLAIN, 17); 
	        g.setFont(fontUI);
	        
			g.drawString("Rotate: W", WIDTH + 35, HEIGHT / 2 - 20);
			g.drawString("Move right: D ", WIDTH + 35, HEIGHT / 2);
			g.drawString("Move left: A", WIDTH + 35, HEIGHT / 2 + 20);
			g.drawString("Soft drop: S", WIDTH + 35, HEIGHT / 2 + 40);
			g.drawString("Hard drop: Q", WIDTH + 35, HEIGHT / 2 + 60);
		}
		else {
			g.setColor(Color.WHITE);
			g.drawString("Game Over", WIDTH / 2 - 40, HEIGHT / 2);
			g.drawString("Your final score: " + Integer.toString(playerScore), WIDTH / 2 - 55, HEIGHT / 2 + 20);
			g.drawString("Press 'R' to play again", WIDTH / 2 - 65, HEIGHT / 2 + 40);
		}
	}

	@Override
	public void run() {
		while (running) {
			update();
			repaint();
				
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void update() {
		curFigure.update(HEIGHT,WIDTH);
	}
	
	private void checkGameEndCondition() {
		if(gameBoard[0][(WIDTH/2)/CELL_SIZE]!=0)
			gameEnded=true;
	}
	

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			if(possibleRotation() && !curFigure.isFallingFinished()) {
				curFigure.rotateFigure();
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_S) {
			checkCollisions(1,0);
			curFigure.moveDown();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_A) {
			if(!checkCollisions(0,-1))
				curFigure.moveLeft();
		}

		if (e.getKeyCode() == KeyEvent.VK_D) {
			if(!checkCollisions(0,1)) 
				curFigure.moveRight();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_Q) {
			while(!checkCollisions(1,0)) { 
				curFigure.moveDown();
				update();
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_R && gameEnded) {
			clearBoard();
			gameEnded=false;
			playerScore = 0;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

}


	
	