package org.hyperhmi.interfacer;

import javax.swing.JProgressBar;
import javax.swing.JWindow;

public class UIHandler extends JWindow implements Runnable {

	private static final long serialVersionUID = 1L;
	protected UpdaterThread updater = null;
	protected JProgressBar volume = null;
	
	/**
	 * Repaint method
	 * 
	 * @return void
	 */
	public void repack(int x, int y) {
		updater.x = x;
		updater.y = y;
	}
	
	
	/**
     * A thread for updating the dataset.
     */
    private class UpdaterThread extends Thread {
    	
    	public int y = 1;
    	public int x = 1;
    	
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            setPriority(this.NORM_PRIORITY); // be nice

            while (true) {

        		System.out.println("RENDER");
        		System.out.println("X: " + x);
        		System.out.println("Y: " + y);

                try {
                    sleep(1);
                }
                catch (InterruptedException e) {
                    // suppress
                }
            }
        }
    }


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
