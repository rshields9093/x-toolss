/*
 * Copyright 2005 Mike Tinker, Gerry Dozier, Aaron Gerrett, Lauren Goff, 
 * Mike SanSoucie, and Patrick Hull
 * Copyright 2011 Joshua Adams
 * 
 * This file is part of X-TOOLSS.
 *
 * X-TOOLSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * X-TOOLSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with X-TOOLSS.  If not, see <http://www.gnu.org/licenses/>.
 */

import lib.genevot.Interval;
import lib.genevot.Individual;
//import lib.memespace.MemespaceProtocol;
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
