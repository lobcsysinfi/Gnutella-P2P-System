package Peer;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PeerThread extends Thread{
	private ServerSocket serversocket;
	private Socket socket;
	private BufferedReader br;
	public peerFunction peerfunc;
	
	public PeerThread(ServerSocket serversocket, peerFunction peerfunc)throws IOException{
		super();
		this.serversocket = serversocket;	
		this.peerfunc = peerfunc;
		start();
	}
	
	public void run(){  
	    Socket socket = null;         
		try{
			while(true){	
				socket = serversocket.accept();	
				invoke(socket, peerfunc);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(socket!=null){
					socket.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	private static void invoke(final Socket socket, final peerFunction peerfunc) throws IOException {
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ObjectInputStream is = null;  
                ObjectOutputStream os = null; 
                String command;
                MessageID messageID;
                int TTL;
                String fileName;
                String IP = null;
                int port = -1;
//                fileInfo flag = null;
                // Record the number of the hitQuery message
                
                DateFormat df;
                String time;
                FileWriter writer = null;
                try {  
                	writer = new FileWriter("./peerLog.txt",true);
                	
                    is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));  
                    os = new ObjectOutputStream(socket.getOutputStream());  
  
                    Object obj = is.readObject();  
                    Message message = (Message)obj;  
                    
                    command = message.getCommand();
  
                    if("query".equals(command)){
                    	
                    	df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            			time = df.format(new Date());
            			writer.write(time + "\t\tReceive: "+command + " " + message.getMessageID().getSequenceNumber() + " " +message.getfileName()+" "
            					+ message.getMessageID().getPeerID().peerName + "\t\n");
                    	
                    	messageID = message.getMessageID();
                    	TTL = message.getTTL();
                    	fileName = message.getfileName();
                    	fileInfo flag = null;
                    	
                    	if(TTL>0){
                    		TTL = TTL - 1;                   		
                    		
                    		peerfunc.query(messageID, TTL, fileName);
                    		flag = peerfunc.search(fileName);
                    		if((flag != null)&&("valid".equals(flag.getState()))){
                    			System.out.println(fileName+" is on "+peerInfo.local.nick.peerName);
                    			peerfunc.hitQuery(messageID, TTL, fileName, peerInfo.local.nick.IP, peerInfo.local.nick.port, flag);
                    			
                    			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    			time = df.format(new Date());
                    			writer.write(time + "\t\tFile "+fileName + " is found on " + peerInfo.local.nick.peerName+"\r\n");
                    				
                    			
                    		}else{
                    			peerfunc.hitQuery(messageID, TTL, fileName, IP, port, flag);
                    		}
                    	}else{
                    		flag = peerfunc.search(fileName);
                    		if((flag != null)&&("valid".equals(flag.getState()))){
                    			// hitQuery messageID = messageID
                    			// hitQuery keep messageID num unchanged
                    			
                    			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    			time = df.format(new Date());
                    			writer.write(time + "\t\tFile "+fileName + " is found on " + peerInfo.local.nick.peerName+"\r\n");
                    			
                    			System.out.println(fileName+" is on "+peerInfo.local.nick.peerName);
                    			peerfunc.hitQuery(messageID, TTL, fileName, peerInfo.local.nick.IP, peerInfo.local.nick.port, flag);            			
                    		}else{
                    			peerfunc.hitQuery(messageID, TTL, fileName, IP, port, flag);
                    		}
                    	}
                    	
                    }else if("hitQuery".equals(command)){
                    	
                    	df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            			time = df.format(new Date());
            			writer.write(time + "\t\tReceive: "+command + " " + message.getMessageID().getSequenceNumber() + " " 
            					+message.getfileName()+" " + message.getMessageID().getPeerID().peerName + "\t\n");                 	
                    	TTL = message.getTTL();                 	
                    	if(TTL>=0){
                    		TTL = TTL - 1;
                    		peerfunc.searchMessage(message, TTL);
                        		
                        }
                    	
                    }else if("download".equals(command)){
                    	// The peer who needs to down load the file
                    	// fileName IP and Port 
                    	fileName = message.getfileName();
                    	String peerip = message.getPeerIP();
                    	int peerport = message.getPort();
                    	peerfunc.sendFile(fileName, peerip, peerport);
                    }else if("push".equals(command)){
                    	
                    	df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            			time = df.format(new Date());
            			writer.write(time + "\t\tReceive: "+command + " " + message.getfileName()+" version "
            					+ message.versionNumber + "\t\n");
                    	
                    	messageID = message.getMessageID();
                    	TTL = message.getTTL();
                    	fileName = message.getfileName();
                    	String serverID = message.serverID;
                    	int versionNum = message.versionNumber;
                    	fileInfo info = null;
                    	
                    	if(TTL>0){
                    		TTL = TTL - 1;                   		                 		
                    		peerfunc.push(messageID, serverID, fileName, versionNum, TTL);           		
                    		info = peerfunc.search(fileName);
                    		if((info != null) && peerInfo.local.cachePath.equals(info.getFilePath())){  
                    			if((versionNum != info.getVersionNumber())){
                    				peerInfo.local.fileList.get(fileName).setState("invalid");
                    			}
                    			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    			time = df.format(new Date());
                    			writer.write(time + "\t\tFile "+fileName + " is invalid!\r\n");			
                    		}
                    	}else{
                    		info = peerfunc.search(fileName);
                    		if(info != null && peerInfo.local.cachePath.equals(info.getFilePath())){
                    			if(!(versionNum == info.getVersionNumber())){
                    				peerInfo.local.fileList.get(fileName).setState("invalid");
                    			}
                    			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    			time = df.format(new Date());
                    			writer.write(time + "\t\tFile "+fileName + " is invalid!\r\n");           			
                    			           			
                    		}
                    	}
                    }else if("pull".equals(command)){
                    	
                    	df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            			time = df.format(new Date());
            			writer.write(time + "\t\tReceive: "+command + " " + message.getfileName()+" version "
            					+ message.versionNumber + "\t\n");
                    	
                    	fileName = message.getfileName();
                    	peerfunc.checkValid(fileName, message.getPeerIP(), message.getPort(), message.versionNumber);
                    }else if("update".equals(command)){
                    	
                    	df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            			time = df.format(new Date());
            			writer.write(time + "\t\tReceive: "+command + " " + message.getfileName()+" state "
            					+ message.state + "\t\n");
                    	
                    	fileName = message.getfileName();
                    	String state = message.state;
                    	if("valid".equals(state)){
                    		peerInfo.local.fileList.get(fileName).setState(state);
                    		time = df.format(new Date());
                			writer.write(time + "\t\t" + message.getfileName()+" state "
                					+ message.state + "\t\n");
                    		pullTimer pullThread = new pullTimer(fileName, peerfunc);
                    		Thread thread = new Thread(pullThread);
                    		thread.start();
                    		thread = null;
                    		
                    	}else{
                    		peerInfo.local.fileList.get(fileName).setState(state);
                    	}
                    }else if("getFileInfo".equals(command)){
                    	messageID = message.getMessageID();
                    	TTL = message.getTTL();
                    	fileName = message.getfileName();
                    	fileInfo flag = peerfunc.search(message.getfileName());
                		if((flag != null)&&peerInfo.local.path.equals(flag.getFilePath())){
                			peerfunc.hitQuery(messageID, TTL, fileName, peerInfo.local.nick.IP, peerInfo.local.nick.port, flag);          			
                		}else{
                			peerfunc.hitQuery(messageID, TTL, fileName, IP, port, flag);
                		}
                    }
 
                    writer.close();
                } catch (IOException e) {  
                    e.printStackTrace();
                } catch(ClassNotFoundException e) {  
                    e.printStackTrace(); 
                } finally {  
                    try {  
                        is.close();  
                    } catch(Exception ex) {}  
                    try {  
                        os.close();  
                    } catch(Exception ex) {}  
                    try {  
                        socket.close();  
                    } catch(Exception ex) {}  
                }  
			}
			
		}).start();
	}
}
