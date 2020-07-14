package org.gChess;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;

import hugo.weaving.DebugLog;

public class ChessSquare {

	private Rect rect;
	private ChessPiece occupant;
	private Paint paint;
	private Location loc;
	
	public ChessSquare(Location loc) {
		occupant = null;
		
		//even row odd column, odd row even column
		if ((loc.getRow() + loc.getCol()) % 2 != 0) {
			paint = ChessView.GREEN;
		}
		//even even and odd odd.
		else paint = ChessView.CYAN;
		
		this.loc = loc;
	}

	@DebugLog
	public void setOccupant(ChessPiece cp) {
		occupant = cp;
	}

	@DebugLog
	public ChessPiece getOccupant() {
		return occupant;
	}

	@DebugLog
	public void render(Canvas canvas) {
		canvas.drawRect(rect, paint);
		if (occupant != null) {
			Drawable d = occupant.getImage();
			d.setBounds(rect);
			d.draw(canvas);
		}
	}

	@DebugLog
	public void setSize(int squareLength, int squareHeight, Location loc) {
		int left   = loc.getCol() * squareLength;
		int top    = loc.getRow() * squareHeight;
		int right  = left + squareLength;
		int bottom = top + squareHeight;
		rect = new Rect(left, top, right, bottom);
	}

	@DebugLog
	public boolean contains(int x, int y) {
		return rect.contains(x, y);
	}

	@DebugLog
	public Location getLocation() {
		return loc;
	}

	@DebugLog
	public String toString() {
		return "ChessSquare@(" + loc.getCol() + " " + loc.getRow()+ ")";
	}
}
