package udpgroupchat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

	// constants
	public static final int DEFAULT_PORT = 30000;
	public static final int MAX_PACKET_SIZE = 512;
	
	//in seconds
	public static final int WAIT_TIME = 3;

	// port number to listen on
	protected int port;
	
	
	public static AtomicInteger nextId = new AtomicInteger();
	

	// map of clientEndPoints
	// note that this is synchronized, i.e. safe to be read/written from
	// concurrent threads without additional locking
	protected static final Map<Integer,ClientEndPoint> clientEndPoints = Collections
			.synchronizedMap(new HashMap<Integer,ClientEndPoint>());
	
	// map of group names to the clients in that group.
	protected static final Map<String,ArrayList<ClientEndPoint>> serverGroups = Collections
			.synchronizedMap(new HashMap<String,ArrayList<ClientEndPoint>>());
	
	//create a set of word as word bank
	protected static final ArrayList<String> wordBank = new ArrayList<String>();
	
	// map of leaderboard who finish the the 10 words ID with time they finish
	protected static final Map<Long,String> leaderboard = Collections
			.synchronizedMap(new HashMap<Long,String>());
	
	protected static  Map<Long,String> treeMap = new TreeMap<Long,String>(leaderboard);
	
	public static int currentIndex = 0;
	public static ArrayList<Integer> usedWord = new ArrayList<Integer>();
	
	
	
	// constructor
	Server(int port) {
		this.port = port;
		
		wordBank.add("Algeria");
		wordBank.add("Angola");
		wordBank.add("Bahrain");
		wordBank.add("China");
		wordBank.add("Canada");
		wordBank.add("Denmark");
		wordBank.add("Egypt");
		wordBank.add("Fiji");
		wordBank.add("Haiti");
		wordBank.add("Japan");
		wordBank.add("Korea");
		wordBank.add("Libya");
		wordBank.add("Mali");
		wordBank.add("Niue");
		wordBank.add("Oman");
		wordBank.add("Peru");
		wordBank.add("Qatar");
		wordBank.add("Russia");
		wordBank.add("Spain");
		wordBank.add("Togo");
		wordBank.add("Ukraine");
		wordBank.add("Vietnam");
		wordBank.add("Yemen");
		wordBank.add("Zambia");
		wordBank.add("Turkey");
		wordBank.add("Norway");
		wordBank.add("Malta");
		wordBank.add("Iran");
		wordBank.add("Chile");
		wordBank.add("Cuba");
	}

	// start up the server
	public void start() {
		DatagramSocket socket = null;
		try {
			// create a datagram socket, bind to port port. See
			// http://docs.oracle.com/javase/tutorial/networking/datagrams/ for
			// details.

			socket = new DatagramSocket(port);

			// receive packets in an infinite loop
			while (true) {
				// create an empty UDP packet
				byte[] buf = new byte[Server.MAX_PACKET_SIZE];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				// call receive (this will poulate the packet with the received
				// data, and the other endpoint's info)
				socket.receive(packet);
				// start up a worker thread to process the packet (and pass it
				// the socket, too, in case the
				// worker thread wants to respond)
				WorkerThread t = new WorkerThread(packet, socket);
				t.start();
			}
		} catch (IOException e) {
			// we jump out here if there's an error, or if the worker thread (or
			// someone else) closed the socket
			e.printStackTrace();
		} finally {
			if (socket != null && !socket.isClosed())
				socket.close();
			System.out.println("Server terminated!");
		}
	}

	// main method
	public static void main(String[] args) {
		int port = Server.DEFAULT_PORT;

		// check if port was given as a command line argument
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("Invalid port specified: " + args[0]);
				System.out.println("Using default port " + port);
			}
		}

		// instantiate the server
		Server server = new Server(port);

		System.out
				.println("Starting server. Connect with netcat (nc -u localhost "
						+ port
						+ ") or start multiple instances of the client app to test the server's functionality.");

		// start it
		server.start();

	}

}
