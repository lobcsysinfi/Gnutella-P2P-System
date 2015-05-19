package Peer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import Peer.peerFunction.downloadThread;

public class Peer {
	
	/*
	 *  Read configure file: config.txt
	 *  Set up IP address and port of local peer
	 */
	public static void readConfig(String config){
		FileWriter writer = null;
		String s = new String();
		try{
			writer = new FileWriter("./peerLog.txt",true);
			
			File file = new File(config);
			if(file.isFile() && file.exists()){
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferReader = new BufferedReader(read);
				while((s = bufferReader.readLine()) != null){
					peerInfo.local.peerNum++;
					String info[] = s.split(" ");
					if(peerInfo.local.nick.peerName.equals(info[0])){
						peerInfo.local.nick.IP = info[1];
						peerInfo.local.nick.port = Integer.parseInt(info[2]);
						if(info.length>2) 
						for(int i = 3; i < info.length; i++){
							BufferedReader reader = new BufferedReader(new FileReader(file));
							while((s = reader.readLine()) != null){
								String temp[] = s.split(" ");
								if(info[i].equals(temp[0])){
									Node node = new Node(temp[0], temp[1], Integer.parseInt(temp[2]));
									peerInfo.local.neighbor.add(node);
									writer.write(peerInfo.local.nick.peerName + " neighor peer information:");
									writer.write(node.peerName + " ");
								}
							}
						}
						writer.write("\t\n");
						System.out.println("Local peer information:");
						peerInfo.local.nick.NodeInfo();
						System.out.println("Neighbor peers information:");
						
						for(int i = 0; i<peerInfo.local.neighbor.size(); i++){
							peerInfo.local.neighbor.get(i).NodeInfo();
						}
					}
				}
			}else{
				System.out.println("Configure file is not exist!");
				writer.write("Configure file is not exist!\r\n");
			}		
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void readConsistent(String consistent){
		FileWriter writer = null;
		String s = new String();
		try{
			writer = new FileWriter("./peerLog.txt",true);
			
			File file = new File(consistent);
			if(file.isFile() && file.exists()){
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferReader = new BufferedReader(read);
				while((s = bufferReader.readLine()) != null){
					String info[] = s.split(" ");
					if("pull".equals(info[0])){
						if("on".equals(info[1])){
							peerInfo.local.pull = true;
						}else if("off".equals(info[1])){
							peerInfo.local.pull = false;
						}
					}else if("push".equals(info[0])){
						if("on".equals(info[1])){
							peerInfo.local.push = true;
						}else if("off".equals(info[1])){
							peerInfo.local.push = false;
						}
					}else if("TTR".equals(info[0]))
						peerInfo.local.TTR = Integer.parseInt(info[1]);
				}
				writer.write("pull/push:" + peerInfo.local.pull + "/" +peerInfo.local.push + "\t\n");
				System.out.println(peerInfo.local.pull);
				System.out.println(peerInfo.local.push);
			}else{
				System.out.println("Consistent file is not exist!");
				writer.write("Consistent file is not exist!\r\n");
			}		
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/*
	 *  File read function
	 *  Scan the file folder 
	 *  and register all files in the local file list.
	 *  
	 *  Monitor "share" folder 
	 *  Automatic register files in share folder.
	 *  Share folder stores master copy.
	 */
	public void fileMonitor(String path){
		File file = new File(path);
		if(!file.exists() && !file.isDirectory()){        
		    file.mkdir();    
		} 
		String test[];
		test = file.list();
		if(test.length!=0){
			for(int i = 0; i<test.length; i++){
				register(test[i], path);
			}
		}
	}
	
	/*  Register function
	 *  Set up a socket connection to the index server
	 *  Register the file to the index server
	 */ 
	public static void register(String fileName, String filePath){
		FileWriter writer = null;
		fileInfo fileinfo = null;

		try{		
			File file = new File(filePath + File.separator +
					fileName);
			if(!file.exists()){
				System.out.println(fileName+" is not exist!");
				
			}else{
				writer = new FileWriter("./peerLog.txt",true);	
				Date modifyDate = new Date();
				/*
				 *  register file from "share" folder or "cache" folder
				 */
				if(filePath.equals(peerInfo.local.path)){
					fileinfo = new fileInfo(fileName, modifyDate, filePath);
					peerInfo.local.fileList.put(fileName,fileinfo);
				}else if(filePath.equals(peerInfo.local.cachePath)){
					if(peerInfo.dest.cacheFileList.containsKey(fileName)){
						fileInfo cacheFile = peerInfo.dest.cacheFileList.get(fileName);
						cacheFile.setFilePath(filePath);
						peerInfo.local.fileList.put(fileName, cacheFile);
					}else{
						System.out.println("wrong");
					}				
				}		
				System.out.println("File "+fileName + " is registered!");
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = df.format(modifyDate);
//				String time = df.format(new Date());
				writer.write(time + "\t\tFile "+fileName + " is registered on the local peer!\r\n");
				writer.close();	
			}	

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*  Unregister function
	 *  Set up a socket connection to the index server
	 *  Unregister the file to the index server
	 */ 
	public static void unregister(String fileName){
		FileWriter writer = null;
		try{	
			if(peerInfo.local.fileList.containsKey(fileName)){
				writer = new FileWriter("./peerLog.txt",true);
				peerInfo.local.fileList.remove(fileName);		
				System.out.println("File "+fileName + " is unregistered!");
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = df.format(new Date());
				writer.write(time + "\t\tFile "+fileName + " is unregistered on the index server!\r\n");
				writer.close();
			}	

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	 *  Modify the version number of the file
	 */
	public void modify(String fileName, peerFunction peerfunc){
		FileWriter writer = null;
		try{	
			Date date = new Date();
			writer = new FileWriter("./peerLog.txt",true);
			if(peerInfo.local.fileList.containsKey(fileName)){
				// Judge if this file is in the master copy.
				if(peerInfo.local.fileList.get(fileName).getFilePath().equals(peerInfo.local.path)){
					peerInfo.local.fileList.get(fileName).incVersionNum();
					peerInfo.local.fileList.get(fileName).setModifyDate(date);
					if(peerInfo.local.push){
						MessageID messageID = new MessageID(0, peerInfo.local.nick);
						peerfunc.push(messageID, peerInfo.local.nick.peerName, fileName, 
								peerInfo.local.fileList.get(fileName).getVersionNumber(),
								peerInfo.local.TTL);
					}
				}else{
					System.out.println("You do not have the permission to modify this file!");
				}
				
			}else{
				System.out.println(fileName + "is not exsits!");
			}
					
			System.out.println("File "+fileName + " is modified!");
			System.out.println("New version number is "+peerInfo.local.fileList.get(fileName).getVersionNumber());
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(date);
			writer.write(time + "\t\tFile "+fileName + " is modified! Version number is " 
					+ peerInfo.local.fileList.get(fileName).getVersionNumber() + "\r\n");
			writer.close();	

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> Refresh(){
		ArrayList<String> result = new ArrayList<String>();
		Iterator it = peerInfo.local.fileList.entrySet().iterator();
		String fileName;
		fileInfo inf;
    	while(it.hasNext()){
    		Entry entry = (Entry) it.next();
    		fileName = (String) entry.getKey();
    		inf = (fileInfo) entry.getValue();
    		if("invalid".equals(inf.getState())){
    			result.add(fileName);
    		}
    	}
		return result;
		
	}
	
	public void talk(peerFunction peerfunc)throws IOException{
		
		boolean exit = false;
		// Store file name
		String fileName = null;
		
		BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
		
		// Usage Interface
		while(!exit){
			System.out.println("\n1 Set up peer name\n2 Search a file\n3 Modify\n4 Refresh\n5 Exit");
			switch(Integer.parseInt(localReader.readLine())){
			case 1:{		
				/*
				 *  1. Set up peer name.
				 *  2. Read configure file.
				 *  3. Automatic register files in share folder.
				 */
				System.out.println("Enter the peer name:");
				peerInfo.local.nick.peerName = localReader.readLine();
				peerInfo.initial();
				readConfig(peerInfo.local.config);
				readConsistent(peerInfo.local.consistent);
				fileMonitor(peerInfo.local.path);
				ServerSocket server = null;
			    try{
			    	server = new ServerSocket(peerInfo.local.nick.port);
			    	System.out.println("\nServer started!");
			    	new PeerThread(server, peerfunc);
			    }catch(IOException e){
			    	e.printStackTrace();
			    }
				break;
			}
			
			case 2:{			
//				peerInfo.local.messageTable = new HashMap<Integer,MessageID>();
				peerInfo.dest.destPeer = new ArrayList<Node>();
				peerInfo.dest.cacheFileList = new ConcurrentHashMap<String,fileInfo>();
				peerInfo.local.hitQueryRequest = 0;
				System.out.println("Enter the file name:");
				fileName = localReader.readLine();
				System.out.println("\nStart processing...\n");
				// Assemble messageID
				int num = peerInfo.local.messageNum + 1;
				MessageID messageID = new MessageID(num, peerInfo.local.nick);            	
            	// Send query message to the neighbors
				peerfunc.query(messageID, peerInfo.local.TTL-1, fileName);
				
				long runtime = 0;
				long start = System.currentTimeMillis();
				// Set cutoff time = 3s
				while(runtime<peerInfo.local.cutoffTime){
					long end = System.currentTimeMillis();
					runtime = end - start;
				}

				
				if(peerInfo.dest.destPeer.size()!=0){
					int index = 0;
					int indexNum = 0;
					System.out.println(fileName + " was found on peers!");
					System.out.println("\n1 Download the file\n2 Cancel and back");
					switch(Integer.parseInt(localReader.readLine())){
					case 1:	
						System.out.println("The destination peer list is:");
						for(int i=0; i<peerInfo.dest.destPeer.size(); i++){
							index = i + 1;
							System.out.println(index + ":" + peerInfo.dest.destPeer.get(i).IP 
									+ " " + peerInfo.dest.destPeer.get(i).port);
						}
						System.out.println("Chose which peer to download the file:");
						indexNum = Integer.parseInt(localReader.readLine());

						new DThread(peerInfo.local.nick.port+1,fileName,peerfunc);
						// Send down load message to destination peer.
						peerfunc.downLoad(fileName, indexNum, peerInfo.local.nick.IP, peerInfo.local.nick.port+1);					
						break;
					case 2:
						break;
					default:
						break;			
					}
				}else{
					System.out.println(fileName + " was not found on peers!");
				}
				break;
			}
			
			case 3:{
				System.out.println("Enter the name of the file need to be modified:");
				String name = localReader.readLine();
				modify(name, peerfunc);
				break;
			}
			
			case 4:{
				ArrayList<String> result = new ArrayList<String>();
				result = Refresh();
				if(result.size()!=0){
					int base = 2000;
					peerInfo.dest.destPeer = new ArrayList<Node>();
					peerInfo.dest.cacheFileList = new ConcurrentHashMap<String,fileInfo>();
					peerInfo.local.messageTable = new ConcurrentHashMap<Integer,MessageID>();
					peerInfo.local.messageNum = 0;
					for(int i = 0; i<result.size(); i++){					
						new DThread(peerInfo.local.nick.port+base+i,fileName,peerfunc);
						// Send request message to get file information
						fileInfo ver = peerInfo.local.fileList.get(result.get(i));
//						peerInfo.dest.destPeer.add(ver.getOriginServer());
						peerInfo.local.messageNum++;
						MessageID messageID = new MessageID(peerInfo.local.messageNum, peerInfo.local.nick);
						peerfunc.getFileInfo(messageID, fileName, ver.getOriginServer().IP, ver.getOriginServer().port);
						
						peerfunc.updateFile(result.get(i),ver.getOriginServer(),
								peerInfo.local.nick.IP, peerInfo.local.nick.port+base+i);
					}
				}else{
					System.out.println("All files are valid, refresh is not needed!");
				}
				
				break;	
			}
			
			case 5:{
				exit = true;
				System.exit(0);
				break;
			}
			default:
				break;
			}
			
		}	
	}
	
	public static void main(String args[]){
		Peer peer = new Peer();
		peerFunction peerfunc = new peerFunction();
		new WThread();
		try {
			peer.talk(peerfunc);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

/*
 *  Watch file
 *  Listening to the local file folder
 *  When there is a change, register or 
 *  unregister file in the local list.
 */
class WThread extends Thread {
	String sharePath = null;
	String cachePath = null;
//	peerInfo.local.fileList
	public WThread(){
		this.sharePath = peerInfo.local.path;
		this.cachePath = peerInfo.local.cachePath;
		// Record the original file list in the folder
		start();
	}
	
	public void run(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(peerInfo.local.fileList.size()!=0){
					
					 Iterator it = peerInfo.local.fileList.entrySet().iterator();
					 while(it.hasNext()){	 
						 Map.Entry entry = (Map.Entry) it.next();
						 String fileName = (String) entry.getKey();
						 File shareFile = new File(sharePath + File.separator +
									fileName);
						 File cacheFile = new File(cachePath + File.separator +
									fileName);
						 if((!shareFile.exists())&&(!cacheFile.exists())){
								System.out.println(entry.getKey()+" was removed!");
								Peer.unregister(fileName);								
							}
					 }
					
				}
			}
			
		}, 1000, 100);
		     
	}
}


/*
 *   Used to receive file from file client
 *   Step 1. Set up a server socket
 *   Step 2. Waiting for input data 
 */
class DThread extends Thread{
	private int port;
	private String fileName;
	private peerFunction peerfunc;
	public DThread(int port,String fileName,peerFunction peerfunc){
		this.port = port;
		this.fileName = fileName;
		this.peerfunc = peerfunc;
		start();
	}
	
	public void run(){
		try {
			ServerSocket server = new ServerSocket(port);
			//while(true){
				Socket socket = server.accept();  
                receiveFile(socket,fileName,peerfunc);  
                socket.close();
                server.close();
			//}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	// Receive function used to receive file and save the file in the local machine
	public static void receiveFile(Socket socket, String fileName, peerFunction peerfunc) throws IOException{
		byte[] inputByte = null;  
        int length = 0;  
        DataInputStream dis = null;  
        FileOutputStream fos = null;  
        String filePath = peerInfo.local.cachePath + File.separator + fileName;  
        try {  
            try {  
                dis = new DataInputStream(socket.getInputStream());  
                File f = new File(peerInfo.local.cachePath);  
                if(!f.exists()){  
                    f.mkdir();    
                }  

                fos = new FileOutputStream(new File(filePath));      
                inputByte = new byte[1024];     
                System.out.println("\nStart receiving..."); 
                System.out.println("display file " + fileName);
                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {  
                    fos.write(inputByte, 0, length);  
                    fos.flush();      
                }  
                System.out.println("Finish receive:"+fileName); 
                // Avoid duplicate register
                // Peer.unregister(fileName);
                // register received file.
                Peer.register(fileName, peerInfo.local.cachePath);
                // Set timer class
                // Monitor down load file
                if(peerInfo.local.pull){
                	pullTimer pullThread = new pullTimer(fileName, peerfunc);
            		Thread thread = new Thread(pullThread);
            		thread.start();
            		thread = null;
                }
                
            } finally {  
                if (fos != null)  
                    fos.close();  
                if (dis != null)  
                    dis.close();  
                if (socket != null)  
                    socket.close();   
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
}




