import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Class used by the client and server to reduce duplicate code for sending/receiving packets.
 * @author Aashna Narang
 *
 */
public class SenderReceiver {
	private int intHostPort;
	private DatagramSocket sendReceiveSocket;
	
	/**
	 * Public constructor to initalize instance variables
	 * @param intHostPort
	 */
	public SenderReceiver(int intHostPort) {
		try {
			this.intHostPort = intHostPort; //should be 23
			this.sendReceiveSocket = new DatagramSocket();
			this.sendReceiveSocket.setSoTimeout(10000);
		} catch (SocketException se) {
			closeSocket();
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Send a datagram packet 
	 * @param msg Message to include in the packet
	 * @param addy Address to send the packet to
	 */
	protected void sendPacket(byte[] msg, InetAddress addy) {
		try {
			DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, addy, intHostPort);
			sendReceiveSocket.send(sendPacket);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			closeSocket();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			closeSocket();
			System.exit(1);
		} 
//		System.out.println(Thread.currentThread().getName() + ": Packet sent.\n");
	}
	
	/**
	 * Receive a datagram packet 
	 * @return the packet that was received
	 */
	protected DatagramPacket receivePacket() {
		// Construct a DatagramPacket for receiving packets up
		// to 100 bytes long (the length of the byte array).

		byte data[] = new byte[1100];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		try {
//			System.out.println(Thread.currentThread().getName() + " is waiting...");
			sendReceiveSocket.receive(receivePacket);
		} catch (SocketTimeoutException e1) {
			closeSocket();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			closeSocket();
			System.exit(1);
		}

//		System.out.println(Thread.currentThread().getName() + ": Packet received:");
		return receivePacket;
	}
	
	/**
	 * Close the sendReceive socket
	 */
	protected void closeSocket() {
		sendReceiveSocket.close();
	}

}
