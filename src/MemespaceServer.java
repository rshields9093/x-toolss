import java.util.HashMap;
import java.util.Set;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.text.DateFormat;


public class MemespaceServer {
	private HashMap memespaceHashTable;
	private int currentID;
	private boolean available;
	private String rootUsername;
	private String rootPassword;
	
	
	public MemespaceServer() {
		memespaceHashTable = new HashMap();
		currentID = 1;
		available = true;
		try {
			BufferedReader in = new BufferedReader(new FileReader("memespace.ini"));
			rootUsername = in.readLine().trim();
			rootPassword = in.readLine().trim();
		}
		catch(IOException e) {
			System.out.println("Could not find memespace initialization file. Root access is disabled.");
			rootUsername = null;
			rootPassword = null;
		}
	}
	
	public synchronized int addMemespace(int size, MemespaceInformation info) {
		while(!available) {
			try {
				wait();
			}
			catch(InterruptedException e) {}
		}
		available = false;
		notifyAll();
		int id = currentID;
		Memespace previous = (Memespace)memespaceHashTable.put(new Integer(id), new Memespace(size, info));
		currentID++;
		available = true;
		notifyAll();
		return id;
	}
	
	public synchronized Memespace getMemespace(int id) {
		while(!available) {
			try {
				wait();
			}
			catch(InterruptedException e) {}
		}
		available = false;
		notifyAll();
		Memespace m = (Memespace)memespaceHashTable.get(new Integer(id));
		available = true;
		notifyAll();
		return m;
	}
	
	public synchronized boolean removeMemespace(int id) {
		while(!available) {
			try {
				wait();
			}
			catch(InterruptedException e) {}
		}
		available = false;
		notifyAll();
		Memespace m = (Memespace)memespaceHashTable.remove(new Integer(id));
		available = true;
		notifyAll();
		return m != null;
	}
	
	public synchronized MemespaceInformation[] getAllMemespaces() {
		while(!available) {
			try {
				wait();
			}
			catch(InterruptedException e) {}
		}
		available = false;
		notifyAll();
		MemespaceInformation[] mi = null;
		if(memespaceHashTable.size() > 0) {
			Set keys = memespaceHashTable.keySet();
			Object[] id = keys.toArray();
			mi = new MemespaceInformation[id.length];
			for(int i = 0; i < mi.length; i++) {
				Memespace m = (Memespace)memespaceHashTable.get(id[i]);
				mi[i] = new MemespaceInformation(m.getInfo().getUsername(), m.getInfo().getPassword(), m.getInfo().getMigrationPassword(), m.getInfo().getCreator(), m.getInfo().getDateCreated(), m.getInfo().getProblemDescription(), m.getInfo().getBounds());
				mi[i].setID(((Integer)id[i]).intValue());
			}
		}
		available = true;
		notifyAll();
		return mi;
	}
	
	public String getRootUsername() {
		return rootUsername;
	}
	
	public String getRootPassword() {
		return rootPassword;
	}
	
    public static void main(String[] args) throws IOException {
		MemespaceServer memespaceServer = new MemespaceServer();
        ServerSocket serverSocket = null;
        boolean listening = true;
		int port = 13100;
		
		if(args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException nfe) {
				System.err.println("You have specified an invalid port number: " + args[0]);
				System.err.println("Server will listen on port " + port + ".");
			}
		}
		
        try {
            serverSocket = new ServerSocket(port);
        } 
		catch (IOException e) {
            System.err.println("Could not listen on port " + port + ".");
            System.exit(-1);
        }
		
        while(listening) {
			System.out.println("Listening for connection...");
			Socket socket = serverSocket.accept();
			Date now = new Date();
			DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG); 
			String nowStr = formatter.format(now);
			System.out.println("Connection accepted from " + socket.getInetAddress().getHostName() + " on " + nowStr + ".");
			(new Thread(new MemespaceThread(socket, memespaceServer))).start();
		}
        serverSocket.close();
    }
}

