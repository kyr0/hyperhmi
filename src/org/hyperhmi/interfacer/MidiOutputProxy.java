package org.hyperhmi.interfacer;


import de.humatic.mmj.MidiListener;
import de.humatic.mmj.MidiOutput;
import de.humatic.mmj.MidiSystemListener;

public class MidiOutputProxy implements MidiListener, MidiSystemListener, Runnable {

	protected String[] hexChars = new String[] {"0","1", "2", "3", "4", "5","6","7","8","9","A", "B","C","D","E","F"};
	protected HyperHMI main = null;
	protected MidiOutput out = null;
	
	/**
	 * Constructor for playback of MIDI to internal virtual interface
	 */
	public MidiOutputProxy(HyperHMI main) {
		
		this.main = main;
	}
	
	
	/**
	 * Opens the output device
	 * 
	 * @return void
	 */
	public void openOutputDevice() {
		
		de.humatic.mmj.MidiSystem.addSystemListener(this);
		
		String[] outputs = de.humatic.mmj.MidiSystem.getOutputs();
		
		// Target MIDI device search
		int targetIndex = 0;
		boolean mmjDestFound = false;
		while (!mmjDestFound) {
			outputs = de.humatic.mmj.MidiSystem.getOutputs();
			for (int i=0; i<outputs.length; i++) {
				if (outputs[i].equals("mmj src")) {
					targetIndex = i;
					mmjDestFound = true;
				}
			}
		}
		out = de.humatic.mmj.MidiSystem.openMidiOutput(targetIndex);
		
		System.out.println("Using output device:");
		System.out.println(out+"\n"+out.getDeviceInfo());	
		
		// Test play tone
		testPlayTone();
	}
	
	
	/**
	 * Test playback tone
	 * 
	 * @return void
	 */
	public void testPlayTone() {
		
		System.out.println("Play");
		out.sendMidi(new byte[]{(byte)144, (byte)36, (byte)75}, de.humatic.mmj.MidiSystem.getHostTime());
	
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Stop");
		out.sendMidi(new byte[]{(byte)128, (byte)36, (byte)0}, de.humatic.mmj.MidiSystem.getHostTime());
	}
	
	
	/**
	 * Returns the output device
	 * 
	 * @return MidiOutput Output device instance
	 */
	public MidiOutput getOutputDevice() {
		return out;
	}

	
	/**
	 * MIDI input listener
	 */
	@Override
	public void midiInput(byte[] data) {
	
		for (int i = 0; i < data.length; i++) {
			//System.out.print((data[i] & 0xFF)+"  ");
			System.out.print(hexChars[(data[i] & 0xFF) / 16] );
			System.out.print(hexChars[(data[i] & 0xFF) % 16]+"  ");
			if (data.length > 5 && i % 15 == 0) System.out.println("");
		}
		System.out.println("");
	}

	
	/**
	 * Incoming MIDI system change event listener
	 */
	@Override
	public void systemChanged() {
		System.out.println("MIDI SYSTEM CHANGED");
	}


	@Override
	public void run() {

		boolean sendActiveSensing = false;
		try { 
			de.humatic.mmj.MidiSystem.enableActiveSensing(sendActiveSensing); 
		} catch (Exception e) {}
		
		de.humatic.mmj.MidiSystem.initMidiSystem("mmj src", "mmj dest");
	}
}