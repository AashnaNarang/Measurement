import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class serves as the client. It sends UDP packets with different requests
 * and receives responses.
 * 
 * @author Aashna Narang
 *
 */
public class Client extends SenderReceiver implements Runnable {
	private int maxRequests;
	private int requestNumber;

	/**
	 * Public constructor to initialize the instance variables
	 * 
	 * @param maxRequests The maximum number of requests the client can make before
	 *                    closing its socket
	 * @param intHostPort The port to use to communicate to the IntermediateHost
	 */
	public Client(int maxRequests, int intHostPort) {
		super(intHostPort);
		this.maxRequests = maxRequests;
		this.requestNumber = 0;
	}

	/**
	 * Send a packet with data to intermediate host and wait to receive an
	 * acknowledgement
	 */
	public void sendDataAndReceiveAck() {
		requestNumber++;
//		System.out.println("Client request number: " + requestNumber);
		byte[] msg = createMsg();
		try {
			sendPacket(msg, InetAddress.getLocalHost());
			receivePacket();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			closeSocket();
			System.exit(1);
		}
	}

	/**
	 * Send a request for data to intermediate host and wait to receive data NOTE:
	 * Kept method with duplicate code for readability
	 */
	public void sendRequestAndReceiveData() {
		byte[] msg = "Please send me data thx".getBytes();
		try {
			sendPacket(msg, InetAddress.getLocalHost());
			receivePacket();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			closeSocket();
			System.exit(1);
		}
	}

	/**
	 * Create the data message for a packet depending on the current request number
	 * 
	 * @return The byte array to be set as the data of the packet
	 */
	private byte[] createMsg() {
		if (requestNumber == maxRequests) {
			byte msg[] = new byte[20];
			for (int i = 0; i < msg.length; i++) {
				msg[i] = (byte) 0;
			}
			return msg;
		}
//		String s = "test.txt";
		String s = generateRandomTextFilename();
		String mode = "ocTEt";
		ArrayList<Byte> bytes = new ArrayList<>();
		bytes.add((byte) 0);
		if (requestNumber % 2 == 0) {
			bytes.add((byte) 1);
		} else {
			bytes.add((byte) 2);
		}
		for (byte b : s.getBytes()) {
			bytes.add(b);
		}
		bytes.add((byte) 0);
		for (byte b : mode.getBytes()) {
			bytes.add(b);
		}
		bytes.add((byte) 0);

		byte msg[] = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			msg[i] = bytes.get(i);
		}
		return msg;
	}
	
	private String generateRandomTextFilename() {
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 996;
	    Random random = new Random();

	    return random.ints(leftLimit, rightLimit + 1)
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString() + ".txt";
	}

	@Override
	/**
	 * Repeatedly send data and send requests for data. Repeat as many times as specified
	 */
	public void run() {
		for (int i = 0; i < maxRequests; i++) {
			sendDataAndReceiveAck();
			sendRequestAndReceiveData();
		}
		closeSocket();
	}

}
