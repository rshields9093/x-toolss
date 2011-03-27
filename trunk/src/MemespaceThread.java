


import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class MemespaceThread implements Runnable {
    private Socket socket = null;
	private MemespaceServer memespaceServer;

    public MemespaceThread(Socket socket, MemespaceServer ms) {
		this.socket = socket;
		memespaceServer = ms;
    }

    public void run() {
		try {
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String inputLine, outputLine;
			MemespaceProtocol protocol = new MemespaceProtocol(memespaceServer);

			while ((inputLine = in.readLine()) != null) {
				System.out.println("Receiving: " + inputLine);
				outputLine = protocol.processInput(inputLine);
				if(outputLine != null) {
					out.println(outputLine);
				}
				System.out.println("Sending: " + outputLine);
			}
			out.close();
			in.close();
			socket.close();
		} 
		catch(SocketException e) {
			System.out.println("Connection lost.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}	
}

