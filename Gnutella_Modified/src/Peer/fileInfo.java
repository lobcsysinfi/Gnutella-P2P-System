package Peer;

import java.io.Serializable;
import java.util.Date;

public class fileInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6313113217812040646L;
	private String fileName;
	private int versionNumber;
	private Node originServer;
	private String state; // valid|invalid|TTR expired
	
	private int TTR;
	private Date last_modified_Time;
	private String filePath;
	
//	public fileInfo(){
//		
//	}
	
	public fileInfo(String fileName){
		this.fileName = fileName;
		this.originServer = peerInfo.local.nick;
		this.versionNumber = 1;
		this.state = "valid";
		this.TTR = peerInfo.local.TTR;
		this.last_modified_Time = new Date();
		this.filePath = peerInfo.local.path;
	}
	
	public fileInfo(String fileName, Date date, String filePath){
		this.fileName = fileName;
		this.originServer = peerInfo.local.nick;
		this.versionNumber = 1;
		this.state = "valid";
		this.TTR = peerInfo.local.TTR;
		this.last_modified_Time = date;
		this.filePath = filePath;
	}
	
	public fileInfo(String fileName, Node originServer){
		this.fileName = fileName;
		this.originServer = originServer;
		this.versionNumber = 1;
		this.state = "valid";
		this.TTR = peerInfo.local.TTR;
		this.last_modified_Time = new Date();
		this.filePath = peerInfo.local.path;
	}

	public fileInfo(String fileName, int versionNumber, Node originServer, String state, int TTR, Date date, String filePath){
		this.fileName = fileName;
		this.originServer = originServer;
		this.versionNumber = versionNumber;
		this.state = state;
		this.TTR = TTR;
		this.last_modified_Time = date;
		this.filePath = filePath;
	}
	
	public void incVersionNum(){
		this.versionNumber++;
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public int getVersionNumber(){
		return versionNumber;
	}
	
	public Node getOriginServer(){
		return originServer;
	}
	
	public String getState(){
		return state;
	}
	
	public int getTTR(){
		return TTR;
	}
	
	public Date getModifyDate(){
		return last_modified_Time;
	}
	
	public String getFilePath(){
		return filePath;
	}

	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	public void setVersionNumber(int versionNumber){
		this.versionNumber = versionNumber;
	}
	
	public void setOriginServer(Node originServer){
		this.originServer = originServer;
	}
	
	public void setState(String state){
		this.state = state;
	}
	
	public void setTTR(int TTR){
		this.TTR = TTR;
	}
	
	public void setModifyDate(Date last_modified_Time){
		this.last_modified_Time = last_modified_Time;
	}
	
	public void setFilePath(String path){
		this.filePath = path;
	}
}
