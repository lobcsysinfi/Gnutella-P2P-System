package Peer;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private MessageID messageID;
	private int TTL;
	private String fileName;
	private String peerIP;
	private String command;
	private int port;
	private fileInfo fileinfo;
	
	public String serverID;
	public int versionNumber;
	public String state;
	
	// Query message 
	public Message(String command, MessageID messageID, int TTL, String fileName){
		this.command = command;
		this.messageID = messageID;
		this.TTL = TTL;
		this.fileName = fileName;
	}

	// HitQuery message
	public Message(String command, MessageID messageID, int TTL, String fileName, 
			String peerIP, int port, fileInfo fileinfo){
		this.command = command;
		this.messageID = messageID;
		this.TTL = TTL;
		this.fileName = fileName;
		this.peerIP = peerIP;
		this.port = port;
		this.fileinfo = fileinfo;
	}
	
	// Down load message
	public Message(String command, String fileName, String peerIP, int port){
		this.command = command;
		this.fileName = fileName;
		this.peerIP = peerIP;
		// Set down load port
		this.port = port;
	}
	
	// push message
	public Message(String command, MessageID messageID, String serverID, 
			String fileName, int versionNumber, int TTL){
		this.command = command;
		this.messageID = messageID;
		this.serverID = serverID;
		this.versionNumber = versionNumber;
		this.TTL = TTL;
		this.fileName = fileName;
	}
	
	// pull message
	public Message(String command, String fileName, String peerIP, int port, int versionNumber){
		this.command = command;
		this.versionNumber = versionNumber;
		this.fileName = fileName;
		this.peerIP = peerIP;
		this.port = port;
		
	}
	
	// Out of date message
	public Message(String command, String fileName, String state){
		this.command = command;
		this.fileName = fileName;
		this.state = state;
	}
		
	
	public void setCommand(String command){
		this.command = command;
	}
	
	public void setMessageID(MessageID messageID){
		this.messageID = messageID;
	}
	
	public void setTTL(int TTL){
		this.TTL = TTL;
	}
	
	public void setfileName(String fileName){
		this.fileName = fileName;
	}
	
	public void setPeerIP(String peerIP){
		this.peerIP = peerIP;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public void setFileInfo(fileInfo fileinfo){
		this.fileinfo = fileinfo;
	}
	
	public MessageID getMessageID(){
		return messageID;
	}
	
	public int getTTL(){
		return TTL;
	}
	
	public String getfileName(){
		return fileName;
	}
	
	public String getPeerIP(){
		return peerIP;
	}
	
	public int getPort(){
		return port;
	}
	
	public String getCommand(){
		return command;
	}
	
	public fileInfo getFileInfo(){
		return fileinfo;
	}
}