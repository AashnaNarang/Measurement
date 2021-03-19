/**
 * Main class to test and invoke the Remote Procedure Calls
 * @author Aashna Narang
 *
 */
public class Main {

	public static void main(String[] args) {
		// Create all of the objects and threads
		Box b = new Box();
		IntermediateHost ih1 = new IntermediateHost(23, b, true);
		IntermediateHost ih2 = new IntermediateHost(69, b, false);
		Thread ihClient = new Thread(ih1, "Intermediate Host-Client");
		Thread ihServer = new Thread(ih2, "Intermediate Host-Server");
		Client c = new Client(1000, 23);
		Thread client = new Thread(c, "Client");
		Server s = new Server(69);
		Thread server = new Thread(s, "Server");
		
		// Start all the threads, threads must be started in this order to avoid client/servers sending packets
		// and not having someone to receive them
		ihClient.start();
		ihServer.start();
		server.start();
		client.start();
	}

}
