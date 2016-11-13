package org.hyperhmi.interfacer;

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

public class MotionHandler implements Runnable {
	
	protected Thread thread = null;
	
	/**
	 * Button input Wii Remote (WiiMote)
	 * 
	 * @param evt
	 */
	public void buttonInput(WiiRemote remote, WRButtonEvent evt) {
		
		System.out.println("Button EVENT:");
		
		String message = "";  
        if (evt.wasPressed(WRButtonEvent.TWO))   message="2";  
        if (evt.wasPressed(WRButtonEvent.ONE))   message="1";  
        if (evt.wasPressed(WRButtonEvent.B))     message="B";  
        if (evt.wasPressed(WRButtonEvent.A))     message="A";  
        if (evt.wasPressed(WRButtonEvent.MINUS)) message="Minus";  
        if (evt.wasPressed(WRButtonEvent.HOME))  message="Home";  
        if (evt.wasPressed(WRButtonEvent.LEFT))  message="Left";  
        if (evt.wasPressed(WRButtonEvent.RIGHT)) message="Right";  
        if (evt.wasPressed(WRButtonEvent.DOWN))  message="Down";  
        if (evt.wasPressed(WRButtonEvent.UP))    message="Up";  
        if (evt.wasPressed(WRButtonEvent.PLUS))  message="Plus";  
        
        System.out.println(message);
	}
	
	
	public void setCurThread(Thread t) {
		thread = t;
	} 
	
	
	/**
	 * Combined input from Wii Remote
	 */
	public void combinedInput(WiiRemote remote, WRCombinedEvent evt) {
		
		System.out.println("Combined event found:");
		System.out.println(evt.getButtonEvent());
		System.out.println(evt.getAccelerationEvent());
		System.out.println(evt.getExtensionEvent());
		System.out.println(evt.getIREvent());
		System.out.println("END Combined event found:");
	}
	
	
	/**
	 * Acceleration input
	 */
	public void accelerationInput(WiiRemote remote, WRAccelerationEvent evt) {

    	System.out.println("Acceleration:");
    	System.out.println("X = " + evt.getXAcceleration());
    	System.out.println("Y = " + evt.getYAcceleration());
    	System.out.println("Z = " + evt.getZAcceleration());
	}
	
	
	/**
	 * Status report input
	 */
	public void statusInput(WiiRemote remote, WRStatusEvent evt) {
		
		System.out.println("Battery level: " + (double)evt.getBatteryLevel()/2+ "%");
        System.out.println("Continuous: " + evt.isContinuousEnabled());
        System.out.println("Remote continuous: " + remote.isContinuousEnabled());
	}
	
	
	/**
	 * IR input
	 */
	public void irInput(WiiRemote remote, WRIREvent evt) {
		
		for (IRLight light : evt.getIRLights())
        {
            if (light != null)
            {
            	System.out.println("IR pos:");
                System.out.println("X: "+light.getX());
                System.out.println("Y: "+light.getY());
            }
        }
	}
	
	
	/**
	 * Extension input
	 */
	public void extInput(WiiRemote remote, WRExtensionEvent evt) {
		
		if (evt instanceof WRNunchukExtensionEvent)
        {
        	System.out.println("Nunchuck controller input: ");
        	
            WRNunchukExtensionEvent NEvt = (WRNunchukExtensionEvent)evt;
            
            if (NEvt.wasReleased(WRNunchukExtensionEvent.C))System.out.println("Jump...");
            if (NEvt.wasPressed(WRNunchukExtensionEvent.Z))System.out.println("And crouch.");
            
            System.out.println("Nunchunk stick:");
            AnalogStickData AS = NEvt.getAnalogStickData();
            System.out.println("Analog- X: " + AS.getX() + " Y: " + AS.getY());
        }
        else if (evt instanceof WRClassicControllerExtensionEvent)
        {
        	System.out.println("Classic controller input: ");
        	
            WRClassicControllerExtensionEvent CCEvt = (WRClassicControllerExtensionEvent)evt;
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.A))System.out.println("A!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.B))System.out.println("B!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.Y))System.out.println("Y!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.X))System.out.println("X!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.LEFT_Z))System.out.println("ZL!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.RIGHT_Z))System.out.println("ZR!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.LEFT_TRIGGER))System.out.println("TL!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.RIGHT_TRIGGER))System.out.println("TR!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.DPAD_LEFT))System.out.println("DL!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.DPAD_RIGHT))System.out.println("DR!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.DPAD_UP))System.out.println("DU!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.DPAD_DOWN))System.out.println("DD!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.PLUS))System.out.println("Plus!");
            if (CCEvt.wasPressed(WRClassicControllerExtensionEvent.MINUS))System.out.println("Minus!");
            if (CCEvt.isPressed(WRClassicControllerExtensionEvent.HOME))
            {
                System.out.println("L shoulder: " + CCEvt.getLeftTrigger());
                System.out.println("R shoulder: " + CCEvt.getRightTrigger());
            }
        }
        else if (evt instanceof WRGuitarExtensionEvent)
        {
        	System.out.println("Guitar Hero controller input: ");
        	
            WRGuitarExtensionEvent GEvt = (WRGuitarExtensionEvent)evt;
            if (GEvt.wasPressed(WRGuitarExtensionEvent.MINUS))System.out.println("Minus!");
            if (GEvt.wasPressed(WRGuitarExtensionEvent.PLUS))System.out.println("Plus!");
            if (GEvt.wasPressed(WRGuitarExtensionEvent.STRUM_UP))System.out.println("Strum up!");
            if (GEvt.wasPressed(WRGuitarExtensionEvent.YELLOW))System.out.println("Yellow!");
            if (GEvt.wasPressed(WRGuitarExtensionEvent.GREEN))System.out.println("Green!");
            if (GEvt.wasPressed(WRGuitarExtensionEvent.BLUE))System.out.println("Blue!");
            if (GEvt.wasPressed(WRGuitarExtensionEvent.RED))System.out.println("Red!");
            if (GEvt.wasPressed(WRGuitarExtensionEvent.ORANGE))System.out.println("Orange!");
            if (GEvt.wasPressed(WRGuitarExtensionEvent.STRUM_DOWN))System.out.println("Strum down!");
            if (GEvt.wasPressed(WRGuitarExtensionEvent.GREEN+WRGuitarExtensionEvent.RED))
            {
                System.out.println("Thats an accord!!");
            }
            
            System.out.println("Whammy bar: " + GEvt.getWhammyBar());
            AnalogStickData AS = GEvt.getAnalogStickData();
            System.out.println("Analog- X: " + AS.getX() + " Y: " + AS.getY());
            
            System.out.println(GEvt.getTouchBar());
        }
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
