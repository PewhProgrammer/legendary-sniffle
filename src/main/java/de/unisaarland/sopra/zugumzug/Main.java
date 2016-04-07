package de.unisaarland.sopra.zugumzug;

import java.io.IOException;

import de.unisaarland.sopra.zugumzug.client.ClientGameController;
import de.unisaarland.sopra.zugumzug.server.ServerGameController;

public class Main {
	
    public static void main(String[] args) {
    
    	for (String s : args) {
    		System.out.println(s);
    	}
    	
    	try {
    		if (args[0].equals("--server")) {
    			processServer(args);
    		} else if (args[0].equals("--gui")) {
    			processGUI(args);
    		} else if (args[0].equals("--ki")) {
    			processKI(args);	// KI = AI
    		} else if (args[0].equals("--help")) {
    			processHelp(args);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.exit(1);
    	}
    	
    	System.exit(0);
    }
    
    private static boolean isStrMatchingIPAndPort(String str) {
    	return str.matches(".*?:.*");
    }
    
    private static boolean isGUIInputValid(String[] args) {
    	
    	try {
    		
    		if (isStrMatchingIPAndPort(args[1])) {
    			
    			Integer.parseInt(args[1].split(":")[1]);
    			
    			return args.length == 4
            			&& args[2].equals("-n");
    		} else {
    			return false;
    		}
    	} catch (NumberFormatException e) {
    		return false;
    	}
    	
    }
    
    private static boolean isKIInputValid(String[] args) {

    	try {
    		
    		if (isStrMatchingIPAndPort(args[1])) {
    			
    			Integer.parseInt(args[1].split(":")[1]);
    			
    			if (args.length == 4) {
    				return args[2].equals("-n")
                			&& args[2].equals("-n");
    			} else {
    				return args.length == 2;
    			}
    			
    		} else {
    			return false;
    		}
    	} catch (NumberFormatException e) {
    		return false;
    	}
    	
	}
    
    /**
     * Expected input:
     *	--server <port> -map <mapfile> [-s <seed>] -p <num players>
     * 
     * @param args
     * @throws IOException 
     */
    private static void processServer(String[] args) throws IOException {
    	
    	String fileName;
    	int playerNum;
    	int port;
    	int seed = 0;	// TODO default value?
    	
    	if (isServerInputValid(args)) {
    		
    		port = Integer.parseInt(args[1]);
    		fileName = args[3];
    		
    		if (args.length == 8) {
        		seed = Integer.parseInt(args[5]);
    			playerNum = Integer.parseInt(args[7]);
        	} else {
    			playerNum = Integer.parseInt(args[5]);
        	}
    		
    	} else {
    		throw new IllegalArgumentException("Main processServer");
    	}
    	
    	ServerGameController server = new ServerGameController(fileName, playerNum, port, seed);
    	server.setup();
    	server.runGame();
    }
    
    private static boolean isServerInputValid(String[] args) {
    	
    	try {
    		if (args.length == 6) {
        		Integer.parseInt(args[1]);	// <port>
        		Integer.parseInt(args[5]);	// <num players>
        		
        		return args[2].equals("-map")
        				&& args[4].equals("-p");
        				
        	} else if (args.length == 8) {
        		Integer.parseInt(args[1]);	// <port>
        		Integer.parseInt(args[5]);	// <seed>
        		Integer.parseInt(args[7]);	// <num players>
        		
        		return args[2].equals("-map")
        				&& args[4].equals("-s")
        				&& args[6].equals("-p");
        	} else {
        		return false;
        	}
    	} catch (NumberFormatException e) {
    		return false;
    	}
    	
    }
    
    /**
     * Expected input:
     * 	--gui <ip>:<port> -n name
     * 
     * @param args
     * @throws IOException 
     */
    private static void processGUI(String[] args) throws IOException {
    	
    	String ip;
    	int port;
    	String name;
    	
    	if (isGUIInputValid(args)) {
    		String[] data = args[1].split(":");
    		ip = data[0];
    		port = Integer.parseInt(data[1]);
    		name = args[3];
    	} else {
    		throw new IllegalArgumentException("Main GUI");
    	}
    	
    	ClientGameController client = new ClientGameController(ip, port, true, false);
    	client.runGame();
    }
    
    /**
     * Expected input:
     * 	--ki <ip>:<port> [-n name]
     * 
     * @param args
     * @throws IOException 
     */
    private static void processKI(String[] args) throws IOException {
    	
    	String ip;
    	int port;
    	String name = "Aiden";
    	
    	if (isKIInputValid(args)) {
    		
    		String[] data = args[1].split(":");
    		ip = data[0];
    		port = Integer.parseInt(data[1]);
    		
    		if (args.length == 4)
    			name = args[3];
    		
    	} else {
    		throw new IllegalArgumentException("Main KI");
    	}
 
    	ClientGameController client = new ClientGameController(ip, port, false, true);
    	client.runGame();
    }
    
	/**
     * Expected input:
     * 	--help
     * 
     * @param args
     */
    private static void processHelp(String[] args) {
    	
    	if (args.length == 1) {
    		System.out.println("Start server:\n"
    							+ "--server <port> -map <mapfile> [-s <seed>] -p <num players>\n\n"
    				
    							+ "Start the game with GUI:\n"
    							+ "--gui <ip>:<port> -n name\n\n"
    							
    							+ "Start the game with AI:\n"
    							+ "--ki <ip>:<port> [-n name]\n\n");
    	}
    	
    }
    
}
