
//package edu.auburn.eng.aci.xtoolss;

import edu.auburn.eng.aci.genevot.Interval;
import edu.auburn.eng.aci.genevot.Individual;
import edu.auburn.eng.aci.memespace.MemespaceProtocol;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class XTOOLSMemespaceInterface {
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private String hostName;
	private int portNumber;
	

	public XTOOLSMemespaceInterface(String host, int port) {
		hostName = host;
		portNumber = port;
	}
	
	private boolean openConnection() {
		try {
			socket = new Socket(hostName, portNumber);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return true;
		} 
		catch (UnknownHostException e) { return false; }
		catch (IOException e) { return false; }			
	}
	
	private void closeConnection() {
		try {
			out.close();
			in.close();
			socket.close();	
		}
		catch(IOException e) {}
	}
	
	public Individual migrateToMemespace(Individual incoming) {
		if(openConnection()) {
			out.println(MemespaceProtocol.convertIndividual(incoming));
			String response = null;
			try {
				response = in.readLine().trim();
				closeConnection();
			}
			catch(IOException e) {
				closeConnection();
				return null;
			}
			if(response.equals("ERROR")) {
				return null;
			}
			else {
				Individual outgoing = MemespaceProtocol.createIndividual(response);
				return outgoing;
			}
		}
		else {
			return null;
		}
	}
}
