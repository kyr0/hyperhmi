package org.hyperhmi.interfacer;

import java.util.Date;

import javax.swing.JProgressBar;

import wiiremotej.AnalogStickData;
import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRNunchukExtensionEvent;
import wiiremotej.event.WRStatusEvent;

public class ThereminMotionHandler extends MotionHandler {

	protected double pitch = 60;
	protected double volume = 0;
	protected int velocity = 60;
	protected int sampleTime = 1;
	protected int irSignalCount = 0;     
	public int cutSignalCount = 1;
	protected boolean isToneIsolation = true;
	protected int curTone = -1;
	protected int curPitch = -1;
	protected boolean hasPitchLock = false;
	
	
	/**
	 * Button input Wii Remote (WiiMote)
	 * 
	 * @param evt
	 */
	public void buttonInput(WiiRemote remote, WRButtonEvent evt) {
		
        // Velocity
        int hibernateVelocity = velocity;
		if (evt.wasPressed(WRButtonEvent.DOWN))  {
			hibernateVelocity = hibernateVelocity - 5;
        }
        if (evt.wasPressed(WRButtonEvent.UP)) {
			hibernateVelocity = hibernateVelocity + 5;
        } 
        
        if (hibernateVelocity < 0) hibernateVelocity = 0;
        if (hibernateVelocity > 127) hibernateVelocity = 127;
        
        // Change UI ProgressBar
    	if (HyperHMI.cproxy.uiHandler instanceof ThereminUIHandler) {
    		
    		ThereminUIHandler uih = (ThereminUIHandler) HyperHMI.cproxy.uiHandler;
    		uih.setVelocity((int) hibernateVelocity);
    	}
        
        velocity = hibernateVelocity;
        
        // Sample time
        int hibernateSampleTime = sampleTime;
        if (evt.wasPressed(WRButtonEvent.PLUS))  {
        	hibernateSampleTime = hibernateSampleTime + 1;
        }
        if (evt.wasPressed(WRButtonEvent.MINUS)) {
        	hibernateSampleTime = hibernateSampleTime - 1;
        } 
        
        if (hibernateSampleTime < 1) hibernateSampleTime = 1;
        
        // Change UI JSlider
    	if (HyperHMI.cproxy.uiHandler instanceof ThereminUIHandler) {
    		
    		ThereminUIHandler uih = (ThereminUIHandler) HyperHMI.cproxy.uiHandler;
    		uih.setSampleTime((int) hibernateSampleTime);
    	}
    	sampleTime = hibernateSampleTime;
    	
    	
        // Sample rate
        int sampleRate = cutSignalCount;
		if (evt.wasPressed(WRButtonEvent.LEFT))  {
			sampleRate = sampleRate - 1;
			if (sampleRate < 0) sampleRate = 0;
        }
        if (evt.wasPressed(WRButtonEvent.RIGHT)) {
        	sampleRate = sampleRate + 1;
        } 
        
        // Change UI JSlider
    	if (HyperHMI.cproxy.uiHandler instanceof ThereminUIHandler) {
    		
    		ThereminUIHandler uih = (ThereminUIHandler) HyperHMI.cproxy.uiHandler;
    		uih.setSampleRate((int) sampleRate);
    	}
    	
        cutSignalCount = sampleRate;
        
        // Tone isolation switch
        if (evt.wasPressed(WRButtonEvent.A))  {
            isToneIsolation = !isToneIsolation;
            
            ThereminUIHandler uih = (ThereminUIHandler) HyperHMI.cproxy.uiHandler;
    		uih.setToneIsolation(isToneIsolation);
        }
        
        // Pitch lock switch
        if (evt.wasPressed(WRButtonEvent.B))  {
            hasPitchLock = !hasPitchLock;
            
            ThereminUIHandler uih = (ThereminUIHandler) HyperHMI.cproxy.uiHandler;
    		uih.setPitchLock(hasPitchLock);
        }
	}
	
	
	/**
	 * Combined input from Wii Remote
	 */
	public void combinedInput(WiiRemote remote, WRCombinedEvent evt) {
	}
	
	
	/**
	 * Acceleration input
	 */
	public void accelerationInput(WiiRemote remote, WRAccelerationEvent evt) {
	}
	
	
	/**
	 * Status report input
	 */
	public void statusInput(WiiRemote remote, WRStatusEvent evt) {
	}
	
	
	/**
	 * IR input
	 */
	protected int lastSec = 0;
	public void irInput(WiiRemote remote, WRIREvent evt) {
		
		// Type of tone
		int i = 0;
		int tone = 0;
		int pitch = 0;
		
		for (IRLight light : evt.getIRLights()) {
			
            if (light != null) {
            	
        		irSignalCount++;
            	
            	System.out.println("IR pos (" + i + "): ");
            	System.out.println("Tone: "+ (128 - this.normalize(light.getX())));
            	System.out.println("Pitch: "+ this.normalize(light.getY()));
            	
            	tone = (128 - (int) this.normalize(light.getX()));
            	pitch =  (int) this.normalize(light.getY());
            	
            	if (isToneIsolation) {
            		
            		if (tone-3 < curTone || tone+3 > curTone) {
            			return;
            		}
            	}

            	if (this.hasPitchLock && curPitch != -1) {
            		pitch = curPitch;
            	}
            	
            	curTone = tone;
            	curPitch = pitch;
            	
            	HyperHMI.cproxy.getUIHandler().repack(tone, pitch);

        		if (irSignalCount < cutSignalCount) {
        			return;
        		}
        		irSignalCount = 0;
        		
            	HyperHMI.mproxy.getOutputDevice().sendMidi(new byte[]{(byte)144, (byte) tone, (byte) velocity}, de.humatic.mmj.MidiSystem.getHostTime());
            	
            	HyperHMI.mproxy.getOutputDevice().sendMidi(new byte[]{(byte)224, (byte) pitch, (byte) pitch}, de.humatic.mmj.MidiSystem.getHostTime());
            	
            	try {
            		System.out.println("ST " + sampleTime);
            		Thread.sleep(sampleTime);
            	} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	HyperHMI.mproxy.getOutputDevice().sendMidi(new byte[]{(byte)128, (byte) tone, (byte) 0}, de.humatic.mmj.MidiSystem.getHostTime());

                i++;
            }
        }
		i = 0;
	}
	
	
	/**
	 * Extension input
	 */
	public void extInput(WiiRemote remote, WRExtensionEvent evt) {
		
		if (evt instanceof WRNunchukExtensionEvent)
        {	
			// Stick data
            WRNunchukExtensionEvent NEvt = (WRNunchukExtensionEvent)evt;
            AnalogStickData AS = NEvt.getAnalogStickData();
            double yPos = this.normalize(AS.getY());
        	
        	// Change UI ProgressBar
        	if (HyperHMI.cproxy.uiHandler instanceof ThereminUIHandler) {
        		
        		ThereminUIHandler uih = (ThereminUIHandler) HyperHMI.cproxy.uiHandler;
        		uih.setVolume((int) yPos);
        	}

        	HyperHMI.mproxy.getOutputDevice().sendMidi(new byte[]{(byte)176, (byte)7, (byte) (int) yPos}, de.humatic.mmj.MidiSystem.getHostTime());
        }
	}
	
	
	/**
	 * Normalize
	 * 
	 * @param toneValue Tone value
	 * @return
	 */
	protected double normalize(double toneValue) {
		
		double val = toneValue * 130;
		if (val > 127) val = 127;
		if (val < 1) val = 1;
		return val;
	}
}
