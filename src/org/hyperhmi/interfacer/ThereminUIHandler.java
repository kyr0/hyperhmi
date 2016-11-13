package org.hyperhmi.interfacer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;


public class ThereminUIHandler extends UIHandler {
	
	protected static final int SIZE  = 128;
	protected Thread updater = null;
	protected PositionCanvas pc = null;
	GraphicsDevice device = null;
	boolean inFullscreen = false;
	protected JLabel volume = null;
	protected JLabel velocity = null;
	ThereminUIHandler win = null;
	public JLabel st = null;
	public JLabel sr = null;
	public JLabel ti = null;
	public JLabel pl = null;
	
	/**
	 * Constructor creating JFrame with positioning marker
	 * 
	 * @return void
	 */
	public ThereminUIHandler() {
		
		win = this;
		System.out.println("Created JFRame");

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = env.getScreenDevices();
		device = devices[0];
		
		setBackground(Color.black);
		
		// Layout
		renderLayout();
		setWindowSize();
		pc.setCurXY(getBounds().width, getBounds().height);

		pack();
		setVisible(true);
		
		// Goto fullscreen for maximum usabillity
		try {
			device.setFullScreenWindow(this);
			inFullscreen = true;
		} catch (Exception e) {
			device.setFullScreenWindow(null);	
			setWindowSize();
			inFullscreen = false;
		}
	}
	
	
	public void setWindowSize() {

		Toolkit tk = Toolkit.getDefaultToolkit();  
		int xSize = ((int) tk.getScreenSize().getWidth());  
		int ySize = ((int) tk.getScreenSize().getHeight());  
		setSize(xSize, ySize);
	}
	
	
	public void addVolumeter() {
		
		
	}


	/**
	 * Repaint method
	 * 
	 * @return void
	 */
	public void repack(int x, int y) {
		pc.mouseDragged(x, y);
	}
	
	
	protected void renderLayout() {
		
		// Set BorderLayout
		this.setLayout(new BorderLayout());
		
		// Create toolbar
		JPanel tb = new JPanel();
		tb.setLayout(new FlowLayout());
		add(tb, BorderLayout.PAGE_START);
		
		
		// Fullscreen toggle
		final JButton exitFullscreen = new JButton("Exit Fullscreen");
		exitFullscreen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (inFullscreen) {
					device.setFullScreenWindow(null);	
					setWindowSize();
					inFullscreen = false;
					exitFullscreen.setText("Enter Fullscreen");
				} else {
					device.setFullScreenWindow(win);
					inFullscreen = true;
					exitFullscreen.setText("Exit Fullscreen");
				}
			}
		});
		tb.add(exitFullscreen);
		
		// Exit 
		final JButton exit = new JButton("Quit");
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		tb.add(exit);
		
		// --- Left volumeter
		JPanel leftMeter = new JPanel();
		leftMeter.setLayout(new BorderLayout());
		add(leftMeter, BorderLayout.WEST);
		
		ti = new JLabel("");
		setToneIsolation(true);
		leftMeter.add(ti, BorderLayout.NORTH);

		volume = new JLabel("");
		setVolume(0);
		leftMeter.add(volume, BorderLayout.CENTER);
		
		velocity = new JLabel("");
		setVelocity(0);
		leftMeter.add(velocity, BorderLayout.SOUTH);
		
		// --- Center canvas
		pc = new PositionCanvas(127, 127);
		add(pc, BorderLayout.CENTER);

		// --- Right volumeter
		
		JPanel rightMeter = new JPanel();
		rightMeter.setLayout(new BorderLayout());
		add(rightMeter, BorderLayout.EAST);

		st = new JLabel("");
		setSampleTime(1);
		rightMeter.add(st, BorderLayout.NORTH);
		
		sr = new JLabel("");
		setSampleRate(1);
		rightMeter.add(sr, BorderLayout.CENTER);
		
		pl = new JLabel("");
		setPitchLock(false);
		rightMeter.add(pl, BorderLayout.SOUTH);
		
	}
	
	
	public void setVolume(int vol) {
		volume.setText("<html><font color='#ff0000' size='16'><b>Volume: <br>"+ vol +"</b></font></html>");
	}
	
	
	public void setVelocity(int vel) {
		velocity.setText("<html><font color='#ff0000' size='16'><b>Velocity: <br>"+ vel +"</b></font></html>");
	}
	
	
	public void setSampleTime(int stval) {
		st.setText("<html><font color='#ff0000' size='16'><b>Sample<br>Time: <br>"+ stval +"</b></font></html>");
	}
	
	public void setSampleRate(int rateVal) {
		sr.setText("<html><font color='#ff0000' size='16'><b>Sample<br>Stepping: <br>"+ rateVal +"</b></font></html>");
	}
	
	public void setToneIsolation(boolean on) {
		if (!on) {
			ti.setText("<html><font color='#ff0000' size='16'><b>Tone<br>Sampling: <br>ON</b></font></html>");
		} else {
			ti.setText("<html><font color='#ff0000' size='16'><b>Tone<br>Sampling: <br>OFF</b></font></html>");
		}
	}
	
	public void setPitchLock(boolean locked) {
		if (!locked) {
			pl.setText("<html><font color='#ff0000' size='16'><b>Pitch<br>Lock: <br>OFF</b></font></html>");
		} else {
			pl.setText("<html><font color='#ff0000' size='16'><b>Pitch<br>Lock: <br>ON</b></font></html>");
		}
	}
}
