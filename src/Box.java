/**
 * Box class used to store data coming from/to send to client/server
 * 
 * @author Aashna Narang
 *
 */
public class Box {
	private byte[] data;
	private boolean empty;

	/**
	 * Public constructor to initialize instance variables
	 */
	public Box() {
		data = null;
		empty = true;
	}

	/**
	 * Put data in the box is there isn't data already in there. Otherwise, wait.
	 * 
	 * @param data Data to put in the box
	 */
	public synchronized void put(byte[] data) {
		while (!empty) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		this.data = data;
		empty = false;
		System.out.println(Thread.currentThread().getName() + " put in box");
		notifyAll();
	}

	/**
	 * Get data from the box if data exists, otherwise wait.
	 * 
	 * @return Data that was in the box
	 */
	public synchronized byte[] get() {
		while (empty) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		byte[] d = data;
		data = null;
		empty = true;
		notifyAll();
		System.out.println(Thread.currentThread().getName() + " got from box");
		return d;
	}
}
