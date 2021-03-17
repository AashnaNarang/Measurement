import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class serves as the server. It sends UDP packets with requests for data
 * and receives responses. Also send a response based on the response of the
 * first.
 * 
 * @author Aashna Narang
 *
 */
public class Server extends SenderReceiver implements Runnable {
	private DatagramPacket receivePacket;
	private static final byte[] WRITE = new byte[] { 0, 4, 0, 0 };
	private static final byte[] READ = new byte[] { 0, 3, 0, 1 };

	/**
	 * Public constructor to initialize instance variables
	 */
	public Server(int intHostPort) {
		super(intHostPort);
	}

	/**
	 * Send a request for data to intermediate host and wait to receive data NOTE:
	 * Kept method with duplicate code for readability
	 */
	public void sendRequestAndReceiveData() {
		byte[] msg = "Please send me data thx".getBytes();
		try {
			sendPacket(msg, InetAddress.getLocalHost());
			receivePacket = receivePacket();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			closeSocket();
			System.exit(1);
		}

	}

	/**
	 * Send a packet with data to intermediate host and wait to receive an
	 * acknowledgement
	 * 
	 * @throws IOException if received invalid data. 
	 */
	public void sendDataAndReceiveAck() throws IOException {
		byte[] response = validateAndCreateResponse(receivePacket.getData());
		if (response != null) {
			System.out.println("Received valid data\n");
		} else {
			System.out.println("Received invalid data\n");
			throw new IOException();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			closeSocket();
			System.exit(1);
		}
		sendPacket(response, receivePacket.getAddress());
		receivePacket();
	}

	/**
	 * Validate the bytes/format of the incoming message. If valid, send the
	 * appropriate response otherwise return null
	 * 
	 * @param data Incoming data from the packet
	 * @return The message to send back or null if incoming message was invalid
	 */
	private byte[] validateAndCreateResponse(byte data[]) {
		if (data[0] != (byte) 0) {
			return null;
		}
		if ((data[1] != (byte) 1) && (data[1] != (byte) 2)) {
			return null;
		}
		if (data[2] < (byte) 32) {
			return null;
		}
		int i;
		for (i = 2; i < data.length; i++) {
			if (data[i] < 32) {
				break;
			}
		}
		if ((data[i] != (byte) 0) || (data[i + 1] < (byte) 32)) {
			return null;
		}
		for (i = i + 1; i < data.length; i++) {
			if (data[i] < 32) {
				break;
			}
		}
		for (; i < data.length; i++) {
			if (data[i] != (byte) 0) {
				return null;
			}
		}
		return data[1] == 1 ? READ : WRITE;
	}

	@Override
	public void run() {
		while (true) {
			try {
				sendRequestAndReceiveData();
				sendDataAndReceiveAck();
			} catch (IOException e) {
				System.out.println("Server closed.");
				closeSocket();
				System.exit(1);
			}
		}

	}

}
