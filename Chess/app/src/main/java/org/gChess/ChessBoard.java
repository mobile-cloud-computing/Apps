package org.gChess;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.location.Address;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import hugo.weaving.DebugLog;

/** 
 * This class is effectively an array of ChessPiece objects.
 * Also included are methods for finding legal moves
 * and moving pieces.
 */
public class ChessBoard {
	
	public static final int ROWS = 8;
	public static final int COLS = 8;
	
	private ChessSquare[][] grid;
	private ArrayList<ChessPiece> whitePieces;
	private ArrayList<ChessPiece> blackPieces;
	private ChessView view;
	
	LinearLayout.LayoutParams params;
	/**
	 * Creates a new chess board with width and height in pixels.
	 * (so we can fit the screen size)
	 * @param width
	 * @param height
	 */
	@DebugLog
	public ChessBoard(ChessView view, LinearLayout.LayoutParams params) {
		this.view = view;
		
		grid = new ChessSquare[ROWS][COLS];
		initGrid();
		initPieces();
		
		this.params = params;

		
	}

	@DebugLog
	public ChessView getView() {
		return view;
	}
	
	/**
	 * wraps a call to the move method of the chess piece to be moved.
	 * @param cp
	 */
	@DebugLog
	public void movePiece(ChessPiece cp, Location loc) {
		remove(cp.getLoc());
		putPiece(cp, loc);
	}

	@DebugLog
	public void remove(Location loc) {
		grid[loc.getRow()][loc.getCol()].setOccupant(null);
	}
	
	/**
	 * This method puts all our squares in the grid, ready for
	 * hard-core chess-piece placing action.
	 */
	@DebugLog
	private void initGrid() {
		for (int x=0; x < grid.length; x++) {
			for (int y=0; y < grid[x].length; y++) {
				grid[y][x] = new ChessSquare(new Location(y,x));
			}
		}
	}
	
	/** draws the chessboard onto the passed canvas.
	 * @param canvas
	 */
	@DebugLog
	protected void render(Canvas canvas) {
		//int squareLength = canvas.getWidth() / COLS;
		//int squareHeight = (canvas.getHeight() - 20) / ROWS;
		
		int squareLength = params.width / COLS;
		int squareHeight = (params.height - 20) / ROWS;
		
		for (int x=0; x< grid.length; x++) {
			for (int y=0; y < grid[x].length; y++) {
				grid[y][x].setSize(squareLength, squareHeight, new Location(y,x));
				grid[y][x].render(canvas);
			}
		}
	}

	/**
	 * @param loc
	 * @return the ChessPiece at the given Location.
	 * <code>null</code> if no ChessPiece at that location
	 */
	@DebugLog
	public ChessPiece getPieceAt(Location loc) {
		if (loc.isValid()) {
			ChessSquare cs = grid[loc.getRow()][loc.getCol()];
			return cs.getOccupant();
		}
		Log.e("ACCESS ERROR", "getPieceAt called on bad location " + loc);
		return null;
	}

	@DebugLog
	public void putPiece(ChessPiece cp, Location loc) {
		if (loc.isValid()) {
			grid[loc.getRow()][loc.getCol()].setOccupant(cp);
			cp.setLoc(loc);
		}
		else Log.e("ERROR", "Tried to place ChessPiece at invalid location:" + loc.getRow() + " " + loc.getCol());
	}
	
	/**
	 * @param loc
	 * @return true if the Location is occupied, false otherwise.
	 */
	@DebugLog
	public boolean isOccupied(Location loc) {
		return getPieceAt(loc) != null;
	}
	
	/**
	 * Grabs locations in a certain direction until it hits something:
	 * a wall, a ChessPiece, whatnot. If the ChessPiece is on the other
	 * "team" it is added to the ArrayList.
	 * @param loc - The starting Location
	 * @param direction - A member of the direction constants in Location
	 * @param playerColor - Either ChessPiece.BLACK or ChessPiece.WHITE
	 * @return ArrayList of Locations that are legal to move to in the given direction.
	 */
	@DebugLog
	public ArrayList<Location> locsInDirUntilBlocked(Location loc, int direction, int playerColor) {
		ArrayList<Location> locs = new ArrayList<Location>();
		Location nextLoc = loc.getLocationInDir(direction);
		while (nextLoc.isValid()) {
			if (getPieceAt(nextLoc) == null) {
				locs.add(nextLoc);
			}
			else if (getPieceAt(nextLoc).getColor() == playerColor) {
				return locs;
			}
			else {
				locs.add(nextLoc);
				return locs;
			}
			nextLoc = nextLoc.getLocationInDir(direction);
		}
		return locs;
	}
	
	
	/**
	 * This method WILL RETURN NULL if x,y are not on the board.
	 * MAKE SURE you check for null values when using this method.
	 * @param x
	 * @param y
	 * @return ChessSquare containing the coords x,y
	 */
	@DebugLog
	public ChessSquare squareAtCoords(int x, int y) {
		for (ChessSquare[] row : grid) {
			for (ChessSquare cs : row) {
				if (cs.contains(x, y)) {
					return cs;
				}
			}
		}
		return null;
	}

	@DebugLog
	private void initPieces() {
		putPiece(new Rook(ChessPiece.BLACK, new Location(0,0), this, "Rook"), new Location(0,0));
		putPiece(new Knight(ChessPiece.BLACK, new Location(0,1), this, "Knight"), new Location(0,1));
		putPiece(new Bishop(ChessPiece.BLACK, new Location(0,2), this, "Bishop"), new Location(0,2));
		putPiece(new Queen(ChessPiece.BLACK, new Location(0,3), this, "Queen"), new Location(0,3));
		putPiece(new King(ChessPiece.BLACK, new Location(0,4), this, "King"), new Location(0,4));
		putPiece(new Bishop(ChessPiece.BLACK, new Location(0,5), this, "Bishop"), new Location(0,5));
		putPiece(new Knight(ChessPiece.BLACK, new Location(0,6), this, "Knight"), new Location(0,6));
		putPiece(new Rook(ChessPiece.BLACK, new Location(0,7), this, "Rook"), new Location(0,7));
		
		for (int x = 0; x < 8; x++) {
			putPiece(new Pawn(ChessPiece.BLACK, new Location(1,x),this, "Pawn"), new Location(1,x));
		}

		putPiece(new Rook(ChessPiece.WHITE, new Location(7,0), this, "Rook"), new Location(7,0));
		putPiece(new Knight(ChessPiece.WHITE, new Location(7,1), this, "Knight"), new Location(7,1));
		putPiece(new Bishop(ChessPiece.WHITE, new Location(7,2), this, "Bishop"), new Location(7,2));
		putPiece(new Queen(ChessPiece.WHITE, new Location(7,3), this, "Queen"), new Location(7,3));
		putPiece(new King(ChessPiece.WHITE, new Location(7,4), this, "King"), new Location(7,4));
		putPiece(new Bishop(ChessPiece.WHITE, new Location(7,5), this, "Bishop"), new Location(7,5));
		putPiece(new Knight(ChessPiece.WHITE, new Location(7,6), this, "Knight"), new Location(7,6));
		putPiece(new Rook(ChessPiece.WHITE, new Location(7,7), this, "Rook"), new Location(7,7));

		for (int x = 0; x < 8; x++) {
			putPiece(new Pawn(ChessPiece.WHITE, new Location(6,x),this, "Pawn"), new Location(6,x));
		}


	}
	
	/**
	 * 
	 * @param color
	 * @return
	 */
	@DebugLog
	public ArrayList<ChessPiece> getPiecesByColor(int color){
		
		ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>();
		
		for (int i = 0; i<ROWS; i++){
			
			for (int j = 0; j< COLS ; j++){
				ChessSquare cs = (ChessSquare) grid[i][j];
				
				if (isOccupied(cs.getLocation())){
					if (cs.getOccupant().getColor() == color){
						pieces.add(cs.getOccupant());
					}
					
				}
				
				
				
			}
		}
		
	return pieces;
	} 
	
	/**
	 * 
	 * @return
	 * 1King
	 * 2Queen
	 * 3Rook
     * 4Bishop
     * 5Knight
     * 6Pawn
	 */
	
	private static int King = 1;
	private static int Queen = 2;
	private static int Rook = 3;
	private static int Bishop = 4;
	private static int Knight = 5;
	private static int Pawn = 6;

	@DebugLog
	public int [][] getChessBoard(){
		int [][] board = new int [ROWS] [COLS];
		
		for (int i = 0; i<ROWS; i++){
			
			for (int j = 0; j< COLS ; j++){
				ChessSquare cs = (ChessSquare) grid[i][j];
				
				if (isOccupied(cs.getLocation())){
					
					if ((cs.getOccupant().getName() =="King") && (cs.getOccupant().getColor() == ChessPiece.BLACK)){
						board [i][j] = (King)*-1;
					}else{
						if ((cs.getOccupant().getName() =="King") && (cs.getOccupant().getColor() == ChessPiece.WHITE)){
							board [i][j] = King;
						}
					}
					
					if ((cs.getOccupant().getName() =="Queen") && (cs.getOccupant().getColor() == ChessPiece.BLACK)){
						board [i][j] = (Queen)*-1;
					}else{
						if ((cs.getOccupant().getName() =="Queen") && (cs.getOccupant().getColor() == ChessPiece.WHITE)){
							board [i][j] = Queen;
						}
					}
					
					if ((cs.getOccupant().getName() =="Rook") && (cs.getOccupant().getColor() == ChessPiece.BLACK)){
						board [i][j] = (Rook)*-1;
					}else{
						if ((cs.getOccupant().getName() =="Rook") && (cs.getOccupant().getColor() == ChessPiece.WHITE)){
							board [i][j] = Rook;
						}
					}
					
					if ((cs.getOccupant().getName() =="Bishop") && (cs.getOccupant().getColor() == ChessPiece.BLACK)){
						board [i][j] = (Bishop)*-1;
					}else{
						if ((cs.getOccupant().getName() =="Bishop") && (cs.getOccupant().getColor() == ChessPiece.WHITE)){
							board [i][j] = Bishop;
						}
					}
					
					if ((cs.getOccupant().getName() =="Knight") && (cs.getOccupant().getColor() == ChessPiece.BLACK)){
						board [i][j] = (Knight)*-1;
					}else{
						if ((cs.getOccupant().getName() =="Knight") && (cs.getOccupant().getColor() == ChessPiece.WHITE)){
							board [i][j] = Knight;
						}
					}
					
					if ((cs.getOccupant().getName() =="Pawn") && (cs.getOccupant().getColor() == ChessPiece.BLACK)){
						board [i][j] = (Pawn)*-1;
					}else{
						if ((cs.getOccupant().getName() =="Pawn") && (cs.getOccupant().getColor() == ChessPiece.WHITE)){
							board [i][j] = Pawn;
						}
					}
					
					
				}else{
					board [i][j] = 0;
 				}
				
				
				
			}
		}
		
		
		
		return board;
		
	}
	
	
}
