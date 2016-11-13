package org.hyperhmi.interfacer;

import java.io.IOException;

import wiiremotej.AnalogStickData;
import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteExtension;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRClassicControllerExtensionEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRGuitarExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRNunchukExtensionEvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteAdapter;
import wiiremotej.event.WiiRemoteListener;

public class ControllerProxy implements Runnable {
	
	
	protected WiiRemote primaryController = null;
	protected HyperHMI main = null;
	protected MotionHandler motionHandler = null;
	protected UIHandler uiHandler = null;
	
	/**
	 * Constructor for initialization and management
	 * of remote controls.
	 */
	public ControllerProxy(HyperHMI main) {
		
		this.main = main;
	}
	
	
	/**
	 * Initialize
	 * 
	 * @return void
	 */
	protected void init() {
		
		// Init bluetooth stuff
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");  
		WiiRemoteJ.setConsoleLoggingAll(); 
	}
	
	
	/**
	 * Searches for controllers and registeres them
	 * 
	 * @return void
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	public void searchPrimaryController() throws IllegalStateException, IOException {
		
		//Find and connect to a Wii Remote
		primaryController = null;
        
        while (primaryController == null) {
            try {
            	primaryController = WiiRemoteJ.findRemote();
            }
            catch(Exception e) {
            	primaryController = null;
                e.printStackTrace();
                System.out.println("Searching for remote... not found.");
                
                try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
            }
        }

        // Call handler
    	main.handleControllerFound();
	}
	
	
	/**
	 * Registering controller listener
	 * 
	 * @return void
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	public void registerController() throws IllegalStateException, IOException {
		
		// Register controller
    	primaryController.addWiiRemoteListener(simpleEventLisntener);  
    	primaryController.setAccelerometerEnabled(true);  
    	primaryController.setSpeakerEnabled(true);  
    	primaryController.setIRSensorEnabled(true, WRIREvent.BASIC);  
    	primaryController.setUseMouse(true);
    	primaryController.setLEDIlluminated(0, true); 
    	
    	// Nice disconnect on thread shutdown
    	final WiiRemote remoteF = primaryController;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){public void run(){remoteF.disconnect();}}));
	}
	
	
	/**
	 * Returns the primary controller instance
	 * 
	 * @return Primary controller
	 */
	public WiiRemote getPrimaryController() {
		return primaryController;
	}
	
	
	/**
	 * Sets the motion handler by instrument
	 * 
	 * @return void
	 */
	public void handleMotionsByInstrument(int instrumentIndex) {
		
		System.out.println("Instrument: "+instrumentIndex);
		
		// Switch instruments
		switch (instrumentIndex) {
		
			case HyperHMI.INSTRUMENT_GUITAR:
				
				System.out.println("Guitar choosen. Good. Rock on, guitar hero!!");
				registerMotionHandler(new GuitarMotionHandler());
				
				try {
					HyperHMI.cproxy.getPrimaryController().setExtensionEnabled(true);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
				
			case HyperHMI.INSTRUMENT_THEREMIN:
				
				System.out.println("Okay luke. Walk the stars!!");

				UIHandler uih = new ThereminUIHandler();
				new Thread(uih).start();
				registerUIHandler(uih);
				
				ThereminMotionHandler tmh = new ThereminMotionHandler();
				Thread theT = new Thread(tmh);
				theT.start();
				tmh.setCurThread(theT);
				registerMotionHandler(tmh);
				
				
				try {
					HyperHMI.cproxy.getPrimaryController().setExtensionEnabled(true);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
		}
	}
	
	
	/**
	 * Registeres a Motion Handler
	 * 
	 * @return void
	 */
	protected void registerMotionHandler(MotionHandler mh) {
		motionHandler = mh;
	}
	
	
	/**
	 * Registers a UI handler
	 * 
	 * @return void
	 */
	protected void registerUIHandler(UIHandler uh) {
		uiHandler = uh;
	}
	
	
	/**
	 * Get ui handler instance
	 * 
	 * @return void
	 */
	public UIHandler getUIHandler() {
		return uiHandler;
	}
	
	
	/**
	 * Event listener-class for handling
	 */
	private static WiiRemoteListener simpleEventLisntener = new WiiRemoteAdapter() {          
		
		@Override
		public void disconnected() {  
            System.out.println("Remote disconnected... Please Wii again.");  
            System.exit(0);  
        }  
  
        @Override
        public void buttonInputReceived(WRButtonEvent evt) {  

        	// Propagate event
        	HyperHMI.cproxy.motionHandler.buttonInput(HyperHMI.cproxy.getPrimaryController(), evt);
        }
        
        @Override
        public void combinedInputReceived(WRCombinedEvent evt) {

        	// Propagate event
        	HyperHMI.cproxy.motionHandler.combinedInput(HyperHMI.cproxy.getPrimaryController(), evt);
        }
        
        
        /**
         * Receive accelleration input
         */
        @Override
        public void accelerationInputReceived(WRAccelerationEvent evt) {

        	// Propagate event
        	HyperHMI.cproxy.motionHandler.accelerationInput(HyperHMI.cproxy.getPrimaryController(), evt);
        }
        
        /**
         * Status report
         */
        public void statusReported(WRStatusEvent evt)
        {
        	// Propagate event
        	HyperHMI.cproxy.motionHandler.statusInput(HyperHMI.cproxy.getPrimaryController(), evt);
        }
        
        /**
         * IR input
         */
        public void IRInputReceived(WRIREvent evt)
        {
        	HyperHMI.cproxy.motionHandler.irInput(HyperHMI.cproxy.getPrimaryController(), evt);
        }
        
        
        /**
         * Extension input
         */
        public void extensionInputReceived(WRExtensionEvent evt)
        {
        	HyperHMI.cproxy.motionHandler.extInput(HyperHMI.cproxy.getPrimaryController(), evt);
        }
        
        
        /**
         * Extension connect handler
         */
        public void extensionConnected(WiiRemoteExtension extension)
        {
            System.out.println("Extension connected!");
            try
            {
            	HyperHMI.cproxy.getPrimaryController().setExtensionEnabled(true);
            } catch(Exception e) {e.printStackTrace();}
        }
        
        /**
         * Handler on partially inserted extension
         */
        public void extensionPartiallyInserted()
        {
            System.out.println("Extension partially inserted. Push it in more next time!");
        }
        
        /**
         * Handler on unknown extension
         */
        public void extensionUnknown()
        {
            System.out.println("Extension unknown. Did you try to plug in a toaster or something?");
        }
        
        /**
         * Extension disconnect
         */
        public void extensionDisconnected(WiiRemoteExtension extension)
        {
            System.out.println("Extension disconnected. Why'd you unplug it, eh?");
        }
    };

	@Override
	public void run() {

		init();
	}  
}
