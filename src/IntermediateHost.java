import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LongSummaryStatistics;

/**
 * Intermediate Host to bridge communication between client and server
 * @author Aashna Narang
 *
 */
public class IntermediateHost implements Runnable {
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket, receiveSocket;
	private Box box;
	private ArrayList<Long> singlePacketExecTimes;
	private boolean measureTime;
	private long startTime;
	private long endTime;

	/**
	 * Public constructor to initialize instance variables and set up a socket for a specific port
	 * @param port Port to communicate with
	 * @param box Box object to store data that is being communicated between IntermediateHost threads
	 */
	public IntermediateHost(int port, Box box, boolean measureTime) {
		try {
			receiveSocket = new DatagramSocket(port);
			sendSocket = new DatagramSocket();
			receiveSocket.setSoTimeout(5000);
			this.box = box;
			this.singlePacketExecTimes = new ArrayList<Long>();
			this.measureTime = measureTime;
			this.startTime = 0;
			this.endTime = 0;
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Send a datagram packet
	 */
	private void sendPacket() {
		System.out.println(Thread.currentThread().getName() + ": Sending packet:");
		
		try {
			sendSocket.send(sendPacket);
			if(measureTime) {
				String msg = new String(sendPacket.getData(), 0, sendPacket.getLength());
				if(!msg.equals("Request acknowledged")) {
					endTime = System.nanoTime();
					long timeElapsed = endTime - startTime;
					singlePacketExecTimes.add(timeElapsed);
			        System.out.println(Thread.currentThread().getName() + " RPC Execution time in nanoseconds  : " + timeElapsed);
				}
			}
		} catch (SocketException se) {
			sendSocket.close();
			receiveSocket.close();
			se.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			sendSocket.close();
			receiveSocket.close();
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(Thread.currentThread().getName() + ": packet sent\n");
	}

	
	/**
	 * Wait to receive a packet and either send data or send request acknowledgement
	 */
	private void receivePacket() {
		byte data[] = new byte[1010];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println(Thread.currentThread().getName() + ": Waiting for Packet.\n");

		try {
			System.out.println(Thread.currentThread().getName() + " is waiting...");
			receiveSocket.receive(receivePacket);
		} catch (SocketTimeoutException e1) {
			if(measureTime) {
				System.out.println(Thread.currentThread().getName() + " received wait timeout");
				System.out.println("Number of times collected: " + singlePacketExecTimes.size());
				LongSummaryStatistics stats = singlePacketExecTimes.stream().mapToLong(Long::longValue).summaryStatistics();
				double average = stats.getAverage();
				System.out.println("average: " + average);
				double sumDiffs = 0;
				for(long time: singlePacketExecTimes) {
					double temp = time - average;
					sumDiffs += Math.pow(temp, 2);
				}
				
				System.out.println("variance: " + sumDiffs/(singlePacketExecTimes.size() - 1));
				sendSocket.close();
				receiveSocket.close();
				System.exit(1);
			} else {
				System.out.println(Thread.currentThread().getName() + " received wait timeout");
				sendSocket.close();
				receiveSocket.close();
			}
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			sendSocket.close();
			receiveSocket.close();
			System.exit(1);
		}

		System.out.println(Thread.currentThread().getName() + ": Packet received:");
		
		String msg = new String(data, 0, receivePacket.getLength());
		byte[] resp;
		if (msg.equals("Please send me data thx")) {
			resp = box.get();
		} else {
			// Now we know this is the remote procedure call starting point bc we got data
			if (measureTime) {
				startTime = System.nanoTime();
			}
			box.put(data);
			resp = "Request acknowledged".getBytes();
		}
		
		sendPacket = new DatagramPacket(resp, resp.length, receivePacket.getAddress(), receivePacket.getPort());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


	@Override
	/**
	 * Continuously receive packets and send responses
	 */
	public void run() {
		while (true) {
//			long startTime = System.nanoTime();
			receivePacket();
			sendPacket();
//			long endTime = System.nanoTime();
//			long timeElapsed = endTime - startTime;
//			singlePacketExecTimes.add(timeElapsed);
//	        System.out.println(Thread.currentThread().getName() + " Execution time in nanoseconds  : " + timeElapsed);
		}
	}
	
	

}
