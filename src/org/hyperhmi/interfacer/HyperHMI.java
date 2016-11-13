package org.hyperhmi.interfacer;
import java.io.IOException;
import javax.swing.JOptionPane;

public class HyperHMI {
	
	protected static MidiOutputProxy mproxy = null;
	protected static ControllerProxy cproxy = null;
	
	public static final int INSTRUMENT_GUITAR = 0;
	public static final int INSTRUMENT_THEREMIN = 1;
	
	/**
	 * Constructor for Hyper-HMI class
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	public HyperHMI() throws IllegalStateException, IOException {
		
		// Initialize the UI and Toolkits
		init();
	}
	
	
	/**
	 * Initializes and shows up the GUI to interface with.
	 * 
	 * @return void
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	protected void init() throws IllegalStateException, IOException {
		
		StringBuffer sb = new StringBuffer("HyperHMI - Wii Controller MIDI transmitter by Aron Homberg, 2010.\n");
		sb.append("jar: "+de.humatic.mmj.MidiSystem.getJarVersion()+", dll: "+de.humatic.mmj.MidiSystem.getLibraryVersion()+"\n");
		sb.append("JDK: "+System.getProperty("java.version")+"\n");
		sb.append("OS: "+System.getProperty("os.name")+" "+System.getProperty("os.version")+" "+System.getProperty("os.arch"));
		System.out.println(sb.toString());

		// UI waiting interaction
		Object[] options = { "OK" };
		
		// Create midi proxy
		mproxy = new MidiOutputProxy(this);
		new Thread(mproxy).start();
		
		// Holding-Dialog 0 f?r Init
		JOptionPane.showOptionDialog(null, "Das MIDI-System wird initialisiert.\n"
									+"Klicken Sie auf OK. Klingt ein Ton, funktioniert es.", 
									 "Schritt 0: MIDI-System initialisieren",
		   						 	 JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
		   						 	 null, options, options[0]);

		mproxy.openOutputDevice();

		// Holding-Dialog 1 f?r Subsysteme
		JOptionPane.showOptionDialog(null, "Soundsystem und Bluetooth-Subsysteme wurde initialisiert.\n"
									+"Klicken Sie auf OK, um den WiiMote-Controller zu registrieren.", 
									 "Schritt 1: Ger?teinitialisierung",
		   						 	 JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
		   						 	 null, options, options[0]);
		
		// Create controller proxy
		cproxy = new ControllerProxy(this);
		new Thread(cproxy).start();

		// Holding-Dialog 2 f?r Subsysteme
		JOptionPane.showOptionDialog(null, "Schlie?en Sie diesen Dialog mit dem OK-Button. DANN: Dr?cken Sie die Tasten 1 und 2 gleichzeitig auf der WiiMote\n"
									+"wenn die WiiMote noch aus ist. Ist die WiiMote an, stellen Sie sicher, das Ihre Wii-Konsole aus ist.\n"
									+"?ffnen Sie dann das Batteriefach auf der R?ckseite der WiiMote und dr?cken Sie den roten Knopf!", 
									 "Schritt 2: Controller registrieren",
									 JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
									 null, options, options[0]);
		
		cproxy.searchPrimaryController();
	}
	
	
	/**
	 * Handles the event as controller was found
	 * 
	 * @retrun void
	 */
	public void handleControllerFound() {
		
		// UI waiting interaction
		//Object[] options = { "Gitarre", "Schlagzeug", "Theremin" };
		Object[] options = { "Gitarre", "Theremin" };
		
		// Holding-Dialog 2 f?r Subsysteme
		int instrumentIndex = JOptionPane.showOptionDialog(null, "Das Programm wird nun den Controller registrieren. \n"
								    +"Bitte w?hlen Sie dazu einen Eingabemodus aus! \n"
								    +"Die LED leuchtet, sobald der Controller registriert wurde.", 
								     "Schritt 3: Eingabemodus w?hlen",
								     JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
								     null, options, options[0]);
		
		
		
		try {
			cproxy.handleMotionsByInstrument(instrumentIndex);
			cproxy.registerController();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Constructor for initialization
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	public static void main(String[] args) throws IllegalStateException, IOException {
		
		new HyperHMI();
	}
}
