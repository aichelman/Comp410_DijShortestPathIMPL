import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		ArrayList<String> listOfStrings = new ArrayList<String>();
		String fileName = "p2graphData.txt";
		String line = null;
		Map m = new Map();
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(fileName);

			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				listOfStrings.add(line);
			}		
			for(int i =0;i<listOfStrings.size();i++){
				if(listOfStrings.get(i).length()<=0){
					continue;
				}
				if(listOfStrings.get(i).equals("-1")){
					
					i++;//Single source index
					m.identifySource(listOfStrings.get(i));
					i++;// //======== index]
					System.out.println("########################################");
					m.print();
					System.out.println();
					//m.topSort();
					m.shortestPathDijkstra();
					System.out.println();
					m = new Map();
				}else{
			 		String[] s = listOfStrings.get(i).split("\\s+");
					m.addNode(s[0]);
					m.addNode(s[1]);
					m.addEdge(s[1], s[0], Integer.parseInt(s[2]));
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
		}
	}
}

/*
  Java program that implements Dijkstra's shortest algorithm. The algorithm will compute on a connected directed graph with weights on the edges. 
 It will find the shortest path from a single source node to each other node in the graph. 
 
 Implemented using a priority queue (binary min heap)
 
 Input:
  There is one edge per line. Each edge (line) has a source node name (si), a destination node name (di),
   and an edge weight (wi, which will be a positive integer). The line that is "-1" signals the end of the graph structure input. 
  The line after that will contain one of the node names, which will be used as the single source node for Dijkstra's algorithm. 
  Example of input can be found in 'p2graphData.txt'
 */

class Map {
	ArrayList<Node> nodesArrayList = new ArrayList<Node>();
	ArrayList<String> vertexNames = new ArrayList<String>();
	 
	
	private int nodeSize;
	private int edgeSize;
	private int serialNumNode;
	private int serialNumEdge;
	private String sourceName;
	
	public Map(){
		serialNumNode=0;
		serialNumEdge = 0;
		nodeSize =0;
		edgeSize=0;
	}
	
	public boolean addNode(String name) {
		if (!vertexNames.contains(name) && name != null) {
			// The vertex name is original
			vertexNames.add(name);
			Node addNode = new Node(name,serialNumNode);
			serialNumNode++;
			nodesArrayList.add(addNode);
			nodeSize++;
			
			return true;
		}
		return false;
	}
	public boolean addEdge(String toN, String fromN, int weight){
		
		int w = weight;
		
		if(!vertexNames.contains(fromN)||!vertexNames.contains(toN)){return false;} //Check if both nodes exists
		
		
		Node hold_f= nodesArrayList.get(vertexNames.indexOf(fromN)); //Vertex that the edge is from
		Node hold_t = nodesArrayList.get(vertexNames.indexOf(toN)); //Vertex that the edge is going to
		
		
		if(!hold_f.checkDestExistAlready(hold_t)){
			//Add in the edge
			Edges newEdge = new Edges(hold_t, hold_t, w, serialNumEdge);
			serialNumEdge++;
			hold_f.addEdge(newEdge);
			edgeSize++;
			hold_t.incInDeg();
			return true;
		}else{
			return false;
		}
	}
	public void print(){
		for(int i = 0;i<nodesArrayList.size();i++){
			System.out.println("("+nodesArrayList.get(i).getSerialNum()+") "+nodesArrayList.get(i).getName());
			if(nodesArrayList.get(i).hasEdge()){
				//Has Edges, so print out destination
				for(int k = 0; k<nodesArrayList.get(i).numEdgesOutOf();k++){
					System.out.print("   (");
					System.out.print(nodesArrayList.get(i).returnEdge(k).getSN()+")"); //print serial number of edge
					if(nodesArrayList.get(i).returnEdge(k).hasLabel()){
						//If the label exists for the edge
						
						System.out.print("("+nodesArrayList.get(i).returnEdge(k).getLabel()+")");
					}
					System.out.print("--->");
					System.out.println(nodesArrayList.get(i).returnEdge(k).getTo().getName());
				}
			}
		}
	}
	public void printSize(){
		System.out.println("Node Size "+ nodeSize);
		System.out.println("Edge Size "+edgeSize);
	}
	
	public void printNode(String nameTP){
		if(vertexNames.contains(nameTP)){
			Node temp = nodesArrayList.get(vertexNames.indexOf(nameTP));
			//print for temp.
			System.out.println("("+temp.getSerialNum()+")"+temp.getName());
			
			if(temp.hasEdge()){
				//Has Edges, so print out destination
				for(int k = 0; k<temp.numEdgesOutOf();k++){
					System.out.print("   (");
					System.out.print(temp.returnEdge(k).getSN()+")"); //print serial number of edge
					if(temp.returnEdge(k).hasLabel()){
						//If the label exists for the edge
						
						System.out.print("("+temp.returnEdge(k).getLabel()+")");
					}
					System.out.print("--->");
					System.out.println(temp.returnEdge(k).getTo().getName());
				}
			}
		}else{System.out.println("Does not have node");}
	}
	public boolean deleteEdge(){
		Scanner s = new Scanner(System.in);
		System.out.println("From Node");
		String FN = s.nextLine();
		System.out.println("To Node");
		String TN = s.nextLine();
		if(!vertexNames.contains(FN)||!vertexNames.contains(TN)){return false;} //Check if both nodes exists
		else{
			Node holder = nodesArrayList.get(vertexNames.indexOf(FN));
			Node tooNode = nodesArrayList.get(vertexNames.indexOf(TN));
			edgeSize--;
			return holder.removeEdge(tooNode);
		}
		
		
	}
	
	public boolean deleteNode(String nameToDel){
		if(!vertexNames.contains(nameToDel)){return false;} //Check if the Node Exists
		
		//First remove every single edge going into the Node
		Node nodeToDelete = nodesArrayList.get(vertexNames.indexOf(nameToDel));
		for(int i = 0;i<nodesArrayList.size();i++){
			if(nodesArrayList.get(i).hasEdge()){
				nodesArrayList.get(i).removeEdge(nodeToDelete);
			}
		}
		//Need to decrease inDegree of all nodes that this node has an edge that points to
		nodeToDelete.decInDegEdgesOut();
		
		//Then delete the actual node
		nodesArrayList.remove(vertexNames.indexOf(nameToDel));
		vertexNames.remove(nameToDel);
		return true;
	}
	
	public void topSort(){
		Queue holder = new Queue();
		int ha = nodesArrayList.size();
		int iter =0;
		
		while(iter<ha){
			for(int i=0;i<nodesArrayList.size();i++){
				if(nodesArrayList.get(i).getInDeg()==0){
					//add it to the Queue;
					//System.out.println(nodesArrayList.get(i));
					holder.enque(nodesArrayList.get(i));
					//Delete the element
					this.deleteNode(nodesArrayList.get(i).getName());
				}
			}
			iter++;
		}
		//holder.printQueue();
		if(holder.size==ha){
			//Cycle does not exist
			int counter = 0;
			while (!holder.empty()){
				System.out.print(holder.deque());
				counter++;
				if(holder.size>0){
					System.out.print(", ");
				}
				if(counter==6){
					System.out.println();
					counter=0;
				}
			}
		}else{
			System.out.println("Topological Sort");
			System.out.println("Cycle found... no sort possible");
		}
		
	}
	public void identifySource(String s){
		sourceName = s;
	}

	public void printWithInDeg(){
		for(int i =0;i<nodesArrayList.size();i++){
			//nodesArrayList.get(i).decInDeg();
			System.out.println(nodesArrayList.get(i).getName()+" "+nodesArrayList.get(i).getInDeg());
			
		}
	}
	public void shortestPathDijkstra(){
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(nodesArrayList.size());
		ArrayList<Integer>key = new ArrayList<Integer>();
		ArrayList<Node>val = new ArrayList<Node>();
		nodesArrayList.get(vertexNames.indexOf(sourceName)).setShortestPathFromSource(0); //From Source to Source length is 0
		pq.add(nodesArrayList.get(vertexNames.indexOf(sourceName)).getShortestPathFromSource()); //Source node put in the queue
		key.add(0);
		val.add(nodesArrayList.get(vertexNames.indexOf(sourceName)));
		while(!pq.isEmpty()){
			int s = pq.peek();
			pq.remove();
			int inde = key.indexOf(s);
			key.remove(inde);
			Node n = val.get(inde);
			val.remove(inde);
			
			if(n.handled()){continue;}
			n.handle();
			
			for(int i=0;i<n.numEdgesOutOf();i++){
				//Each edge coming out of it 
				if(!n.returnEdge(i).getTo().handled()){//If the node the edge is going into is handled
					int pathLength = n.getShortestPathFromSource()+n.returnEdge(i).getWeight();
					if(pathLength<n.returnEdge(i).getTo().getShortestPathFromSource()){
						n.returnEdge(i).getTo().setShortestPathFromSource(pathLength);
						n.returnEdge(i).getTo().setPred(n);
						pq.add(pathLength);
						key.add(pathLength);
						val.add(n.returnEdge(i).getTo());
					}
				}
			}
		}
		System.out.println("Shortest Paths from source node ("
				+nodesArrayList.get(vertexNames.indexOf(sourceName)).getSerialNum() +") "
				+ nodesArrayList.get(vertexNames.indexOf(sourceName)).getName() +" to :");
		for(int k = 0;k<nodesArrayList.size();k++){
			
			System.out.print("("+nodesArrayList.get(k).getSerialNum()+") ");
			System.out.print(nodesArrayList.get(k).getName()+": ");
			if(nodesArrayList.get(k).getShortestPathFromSource()==Integer.MAX_VALUE){
				System.out.print("no path");
				}else{
				System.out.print(nodesArrayList.get(k).getShortestPathFromSource());
				Node n = nodesArrayList.get(k);
				System.out.print(" (");
				while(true){
					System.out.print(n.getName());
					n = n.getPred();
					if(n==null){
						break;
					}
					System.out.print(", ");
				}
				System.out.print(")");
			}
			System.out.println();
		}
	}
}
class Queue {
	Node[] vertixNames;
	int front;
	int back;
	int size;

	public Queue() {
		vertixNames = new Node[10];
		front = 0;
		back = 0;
	}

	public void enque(Node n) {
		if (size == 0) {
			vertixNames[front] = n;
			
		} else {
			if (size == vertixNames.length) {
				Node[] array2 = new Node[size * 2];
				for (int i = 0; i < size; i++) {
					array2[i] = vertixNames[i];
					vertixNames = array2;
				}
			}
			vertixNames[back+1]=n;
			back++;
		}
		size++;
	}

	public String deque() {
		Node n;
		if(size==0){
			n = null;
		}
		else if(front==back){
			n = vertixNames[front];
			size--;
		}else{
			n = vertixNames[front];
			front++;
			size--;
		}
		
		return "("+n.getSerialNum()+")"+n.getName();
		
	}
	public boolean empty(){
		return (size<=0);
	}
	public void printQueue(){
		for(int i =0;i<vertixNames.length;i++){
			System.out.println(vertixNames[i].getName());
		}
	}
}
class Node {
	
	private String name;
	private Node next;
	private int serialNumber;
	private Edges adj;
	private String label;
	private int inDeg;
	private int shortestPathFromSource;
	private Node pred;
	private boolean handled;
	private ArrayList<Edges> edgeHolder = new ArrayList<Edges>();
	
	public Node(String name, int sn){
		this.name=name;
		this.serialNumber = sn;
		this.shortestPathFromSource= Integer.MAX_VALUE;
		pred = null;
		handled = false;
		
	}
	public boolean handled(){
		return handled;
	}
	public void handle(){
		handled = true;
	}
	public int getShortestPathFromSource(){
		return shortestPathFromSource;
	}
	public void setShortestPathFromSource(int s){
		shortestPathFromSource = s;
	}
	public void setPred(Node p){
		pred=p;
	}
	public Node getPred(){
		return pred;
	}
	public int getSerialNum(){
		return serialNumber;
	}
	public void setNext(Node next){
		this.next=next;
	}
	public String getName(){
		return name;
	}
	public String getLabel(){
		if(label=="null");{
			label = "No Label was entered";
		}
		return label;
	}
	public void addEdge(Edges e){
		edgeHolder.add(e);
	}
	public int numEdgesOutOf(){
		return edgeHolder.size();
	}
	public boolean checkDestExistAlready(Node too){
		//Check if the edge already exists
		boolean ret = false;
		for(int i =0;i<edgeHolder.size();i++){
			if(edgeHolder.get(i).getTo().getName().equals(too.getName())){
				ret = true;
			}
		}
		return ret;
	}
	public boolean hasEdge(){
		if(edgeHolder.size()>0){
			return true;
		}
		return false;
	}
	public Edges returnEdge(int i ){
		return edgeHolder.get(i);
	}
	public boolean removeEdge(Node deleteNode){
		for(int i = 0;i<edgeHolder.size();i++){
			if(deleteNode.name.equals(edgeHolder.get(i).getTo().name)){
				//Edge exists to be deleted
				deleteNode.decInDeg();
				//Delete Edge
				edgeHolder.remove(edgeHolder.get(i));
				//Return True
				return true;
			}
		}
		return false;
	}
	public void incInDeg(){
		inDeg++;
	}
	public void decInDeg(){
		if(inDeg>0){
			inDeg--;
		}
	}
	public int getInDeg(){
		return inDeg;
	}
	public void decInDegEdgesOut(){
		for(int i =0;i<edgeHolder.size();i++){
			edgeHolder.get(i).getTo().decInDeg();
		}
	}
}
class Edges {
	private Node from;
	private Node to;
	private int weight;
	private int serialNum;
	private String label;
	
	public Edges(Node f, Node t, int w, int sn){
		from = f;
		to = t;
		weight = w;
		serialNum = sn;
		//this.label=label;
	}
	public Node getfrom(){
		return from;
	}
	public Node getTo(){
		return to;
	}
	public int getWeight(){
		return weight;
	}
	public int getSN(){
		return serialNum;
	}
	public String getLabel(){
		return label;
	}
	public boolean hasLabel(){
		if(label==null){
			return false;
		}
		return true;
	}
}

