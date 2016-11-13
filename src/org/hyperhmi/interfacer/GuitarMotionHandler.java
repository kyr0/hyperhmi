package org.hyperhmi.interfacer;

import java.util.ArrayList;
import java.util.ListIterator;

import wiiremotej.AnalogStickData;
import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRClassicControllerExtensionEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRGuitarExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRNunchukExtensionEvent;
import wiiremotej.event.WRStatusEvent;

/**
 * Special guitar controller motion handler
 */
public class GuitarMotionHandler extends MotionHandler {
	

    protected int toneNumber = 60; // C1
	protected int curPitch = 60;
	protected double curVelocity = 60;
	protected double lastToneValue = 0;
	protected ArrayList toneList = null;
	protected boolean tonePlayed = false;
	protected boolean according = false;
	protected ArrayList overallToneList = null;
	
	
	protected boolean greenPressed = false;
	protected boolean bluePressed = false;
	protected boolean orangePressed = false;
	protected boolean redPressed = false;
	protected boolean yellowPressed = false;
    
	/**
	 * Button input Wii Remote (WiiMote)
	 * 
	 * @param evt
	 */
	public void buttonInput(WiiRemote remote, WRButtonEvent evt) {
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
	public void irInput(WiiRemote remote, WRIREvent evt) {
	}
	
	
	/**
	 * Extension input
	 */
	public void extInput(WiiRemote remote, WRExtensionEvent evt) {
		
		if (!(toneList instanceof ArrayList)) {
			toneList = new ArrayList();
		}
		
		if (!(overallToneList instanceof ArrayList)) {
			overallToneList = new ArrayList();
		}
		
		if (evt instanceof WRGuitarExtensionEvent)
        {
            WRGuitarExtensionEvent GEvt = (WRGuitarExtensionEvent)evt;
            
            // Evaluate velocity
            AnalogStickData AS = GEvt.getAnalogStickData();
            curVelocity = AS.getY();
            curVelocity = 60 + ((curVelocity * 100) * -1) - 3.44;
            if (curVelocity > 127) curVelocity = 127;
            if (curVelocity < 0) curVelocity = 0;

            // Pitch
            if (GEvt.wasPressed(WRGuitarExtensionEvent.MINUS)) curPitch = curPitch - 10;
            if (GEvt.wasPressed(WRGuitarExtensionEvent.PLUS)) curPitch = curPitch + 10;
            if (GEvt.wasPressed(WRGuitarExtensionEvent.STRUM_UP))System.out.println("Strum up!");
            
            toneNumber = -1;
            
            if (GEvt.wasPressed(WRGuitarExtensionEvent.GREEN)) {
            	
            	mayClear();
            	
            	greenPressed = true;
            	toneNumber = 74; 
            	toneList.add(new Integer(toneNumber));
            }
            if (GEvt.wasPressed(WRGuitarExtensionEvent.RED)) {

            	mayClear();
            	
            	redPressed = true;
            	toneNumber = 67;
            	toneList.add(new Integer(toneNumber));
            }
            if (GEvt.wasPressed(WRGuitarExtensionEvent.YELLOW)) {

            	mayClear();
            	
            	yellowPressed = true;
            	toneNumber = 60;
            	toneList.add(new Integer(toneNumber));
            }
            if (GEvt.wasPressed(WRGuitarExtensionEvent.BLUE)) {

            	mayClear();
            	
            	bluePressed = true;
            	toneNumber = 53; 
            	toneList.add(new Integer(toneNumber));
            }
            if (GEvt.wasPressed(WRGuitarExtensionEvent.ORANGE)) {

            	mayClear();
            	
            	orangePressed = true;
            	toneNumber = 45;
            	toneList.add(new Integer(toneNumber));
            }
            
            if (GEvt.wasPressed(WRGuitarExtensionEvent.STRUM_DOWN))System.out.println("Strum down!");
            
            // Evaluate tone-fader
            double toneValue = GEvt.getWhammyBar() * 140;
            if (toneValue > 85) toneValue = 85;
            if (toneValue < 30) toneValue = 30;
            toneValue = 120 - toneValue - 60;
            
            // Play tones
            if (GEvt.wasPressed(WRGuitarExtensionEvent.STRUM_DOWN)) {
            	
            	int size = toneList.size();
                Integer curTone = null;
            	for (int i=0; i<size; i++) {
                	curTone = (Integer) toneList.get(i);
                	playTone((int) curTone.intValue(), (int) toneValue, (int) curVelocity, (int) curPitch);
                }
            	orangePressed = false;
            	redPressed = false;
            	yellowPressed = false;
            	greenPressed = false;
            	bluePressed = false;
            }
            
            // Stop tone
            if (GEvt.wasPressed(WRGuitarExtensionEvent.STRUM_UP)) {
            	
            	// Stop every played tone immediately
            	int size = overallToneList.size();
                Integer curTone = null;
            	for (int i=0; i<size; i++) {
                	curTone = (Integer) overallToneList.get(i);
                	System.out.println("TONE OFF: " + curTone);
                	HyperHMI.mproxy.getOutputDevice().sendMidi(new byte[]{(byte)128, (byte) (int) curTone, (byte) 0}, de.humatic.mmj.MidiSystem.getHostTime());
                }
            	overallToneList.clear();
            }
        }
	}
	
	public void mayClear() {

		if (!yellowPressed && !orangePressed && !redPressed &&
			!bluePressed && !greenPressed) {
			toneList.clear();
		}
	}
	
	
	public int playTone(int toneNumber, int toneValue, int curVelocity, int curPitch) {
		
		// Calculate multiplied tone
        toneValue = toneNumber + toneValue;

        System.out.println("Playing tone: " + toneValue);
        System.out.println("Playing pitch: " + curPitch);
        System.out.println("Playing velocity: " + curVelocity);
        
        // Tone
    	HyperHMI.mproxy.getOutputDevice().sendMidi(new byte[]{(byte)144, (byte)toneValue, (byte) curVelocity}, de.humatic.mmj.MidiSystem.getHostTime());
    	
    	// Pitch value
    	HyperHMI.mproxy.getOutputDevice().sendMidi(new byte[]{(byte)224, (byte)curPitch, (byte) curPitch}, de.humatic.mmj.MidiSystem.getHostTime());
    	
    	overallToneList.add(new Integer(toneValue));
    	overallToneList.add(new Integer(curPitch));
        
    	lastToneValue = toneValue;
        
        return (int) toneValue;
	}

}
