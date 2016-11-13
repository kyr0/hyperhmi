package org.hyperhmi.interfacer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

class PositionCanvas extends Canvas {

	private static final long serialVersionUID = 1L;
	private int x = -1;			
	private int y = -1;	

	private Color bgcolor = Color.BLACK;

	private int baseX = 127;
	private int baseY = 127;
	private int curX = 127;
	private int curY = 127;
  
	private boolean xGreater = false;
	private int greaterGap = 0;
	private double scale = 1;

  
	public PositionCanvas(int baseX, int baseY) {
		this.baseX = baseX;
		this.baseY = baseY;
    	setBackground(bgcolor);
    	setForeground(Color.RED);
	}

  
	public void setCurXY(int x, int y) {

		this.curX = x;
		this.curY = y;
		
		if (x < y) {
			xGreater = false;
			greaterGap = y - x;
		    scale = (x / baseX); 	
		} else {
			xGreater = true;
			greaterGap = x - y;
		    scale = (y / baseY);
		}		
	}
  
  
	public int scalePos(int pos, boolean isX) {
	  
		if (isX && xGreater) {
			return pos + (greaterGap / 2);
		}
		return pos;
	}
  
  
	public void paint(Graphics g) {
	  
		g.setXORMode(this.bgcolor);
		//g.clearRect(0, 0, curX, curY);
	  
		// Border
		g.drawLine(0, 0, getWidth(), scalePos(0, false));
		g.drawLine(scalePos(0, true), scalePos(0, false), scalePos(0, true), this.scale(127, scale));
		g.drawLine(scalePos(this.scale(127, scale), true), scalePos(0, false), scalePos(this.scale(127, scale), true), this.scale(127, scale));
		g.drawLine(0, this.scale(127, scale), getWidth(), this.scale(127, scale));
	 
		// Pointer
		g.fillRect(scalePos(x, true), scalePos(y, false), this.scale(5, scale/4), this.scale(5, scale/4));
	}

  
	public void mouseDragged(int x, int y) {

		if (x == 1) x = 0;
		if (y == 1) y = 0;
		if (y == 127) y = 126;
		if (x == 127) x = 126;
	
		this.x = this.scale(x, scale);
		this.y = this.scale(y, scale);
	 
		repaint();
    
		setCurXY(this.getWidth(), this.getHeight());
	} 
  

	protected int scale(int val, double factor) {
		return (int) (val * factor);
	}
}