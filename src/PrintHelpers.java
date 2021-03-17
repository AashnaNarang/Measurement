import java.net.DatagramPacket;

/**
 * Class to reuse print methods for when you receive or send a packet
 * 
 * @author Aashna Narang
 *
 */
public class PrintHelpers {
	/**
	 * Get a string of all of the values inside a byte array
	 * 
	 * @param data
	 * @return
	 */
	public static String bytesToString(byte[] data) {
		String s = "";
		for (byte b : data) {
			s += b + ", ";
		}
		return s.substring(0, s.length() - 2);
	}

	/**
	 * Print out appropriate messages when sending a packet
	 * 
	 * @param sendPacket The packet that will be sent
	 */
	public static void printSendPacketInfo(DatagramPacket sendPacket) {
		System.out.println(Thread.currentThread().getName() + " sending this packet:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		int len = sendPacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(sendPacket.getData(), 0, len));
		System.out.println("As bytes: " + PrintHelpers.bytesToString(sendPacket.getData()) + "\n");
	}

	/**
	 * Print out appropriate messages when a packed has been received
	 * 
	 * @param receivePacket The packed that was received
	 * @param data          Data received in the packet
	 */
	public static void printReceivePacketInfo(DatagramPacket receivePacket, byte[] data) {
		System.out.println(Thread.currentThread().getName() + " received this packet:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " + new String(data, 0, len));
		System.out.println("As bytes: " + PrintHelpers.bytesToString(data) + "\n");
	}
}
