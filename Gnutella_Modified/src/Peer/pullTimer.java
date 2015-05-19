package Peer;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class pullTimer implements Runnable{
	private String fileName;
	private peerFunction peerfunc;
	
	public pullTimer(String fileName, peerFunction peerfunc){
		this.fileName = fileName;
		this.peerfunc = peerfunc;
	}

	@Override
	public void run() {
		
		int TTR = peerInfo.local.fileList.get(fileName).getTTR()*60000;
		Timer timer = new Timer();		
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				
				DateFormat df;
                String time;
				FileWriter writer = null;
                try {  
                	writer = new FileWriter("./peerLog.txt",true);
                	// Detect the state of this file
                	if(peerInfo.local.fileList.containsKey(fileName)){
    					if("valid".equals(peerInfo.local.fileList.get(fileName).getState())){
    						peerInfo.local.fileList.get(fileName).setState("TTR expired");
    						System.out.println(fileName + " is TTR expired!");
    						peerfunc.pull(fileName, peerInfo.local.nick.IP, peerInfo.local.nick.port, 
    								peerInfo.local.fileList.get(fileName).getVersionNumber());
    						
    						df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                			time = df.format(new Date());
                			writer.write(time + "\t\t"+fileName + " is TTR expired!\t\n");
    						
    					}else{
//    						System.out.println(fileName + "is invalid!");
    					}
    				}else{
//    					System.out.println(fileName + "is removed!");
    				}
                	writer.close();
                }catch (IOException e) {  
                    e.printStackTrace();
                } 
							
			}
			
		}, TTR);
		
		
	}
	
	

}
