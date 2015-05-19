package Peer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class peerInfo {
	public static class local{
		public static int messageNum;
		public static int peerNum;
		public static int hitQueryRequest;
		public static int TTL = 3;	
		public static int TTR = 5; // 5 minutes
		public static int cutoffTime = 3000;  // 3 seconds
		public static boolean pull;
		public static boolean push;
		public static Node nick = new Node(); // local peer
		public static String path = "./share"; // master copy path
		public static String cachePath = "./cache"; // cached copy path
		public static String config = "./config.txt";
		public static String consistent = "./consistent.txt";
		public static ArrayList<Node> neighbor = new ArrayList<Node>();
//		public static ArrayList<fileInfo> fileList = new ArrayList<fileInfo>();
		public static Map<String,fileInfo> fileList = new ConcurrentHashMap<String,fileInfo>();
		public static ConcurrentHashMap<Integer,MessageID> messageTable = new ConcurrentHashMap<Integer,MessageID>();
	}
	
	public static class dest{
		// Store destination node
		public static ArrayList<Node> destPeer = new ArrayList<Node>();
		// Store file information
		public static Map<String,fileInfo> cacheFileList = new ConcurrentHashMap<String,fileInfo>();
	}
	
	public static void initial(){
		peerInfo.local.messageNum = 0;
		peerInfo.local.peerNum = 0;
		peerInfo.local.hitQueryRequest = 0;
		peerInfo.local.neighbor = new ArrayList<Node>();
		peerInfo.local.messageTable = new ConcurrentHashMap<Integer,MessageID>();
		peerInfo.dest.destPeer = new ArrayList<Node>();
		peerInfo.dest.cacheFileList = new ConcurrentHashMap<String,fileInfo>();
	}
	
}
