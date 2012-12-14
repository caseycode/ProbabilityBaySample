package bnp;

import java.util.ArrayList;

/*
 * class BayesNetDFSGraph:
 * 
 * Using a Depth First Search (DFS) algorithm, 
 * this class finds an elimination sequence that
 * has a small total table size.
 * 
 * 
 * A BayesNetDFSGraph object is constructed with a BayesNetGraph object.
 * 
 * BayesNetDFSGraph makes its own hard copies of nodes and connections
 * so that BayesNetGraph won't be affected.
 * 
 */
import java.util.Random;
public class BayesNetDFSGraph {
	private ArrayList<BayesNetDFSNode> nodeList;
	private ArrayList<BayesNetDFSNode> remainingNodeList;
	private BayesNetDFSCliqueSet cliqueSet;
	private ArrayList<BayesNetDFSNode> eliminatedNodeSequence;
	private BayesNetDFSLineSet addedLineSetSinceLastCliqueUpdate;
	private boolean isInitialCliqueSetIdentified;
	
	// private constructor: used for initialization
	private BayesNetDFSGraph() {
		nodeList = new ArrayList<BayesNetDFSNode>();
		remainingNodeList = new ArrayList<BayesNetDFSNode>();
		isInitialCliqueSetIdentified = false;
		cliqueSet = new BayesNetDFSCliqueSet();
		
		addedLineSetSinceLastCliqueUpdate = new BayesNetDFSLineSet();
		eliminatedNodeSequence = new ArrayList<BayesNetDFSNode>();
	}
	
	// constructor
	// this constructor makes its own hard copies of the BayesNetGraph object. 
	BayesNetDFSGraph(BayesNetGraph graph) {
		this();
		// add nodes
		for(int i=0; i<graph.getNodeList().size(); i++) {
			BayesNetNode eachNode = graph.getNodeList().get(i);
			String name = eachNode.getName();
			int size = eachNode.getSize();
			nodeList.add(new BayesNetDFSNode(name, size));
		}
		
		// add connections
		for(int i=0; i<graph.getNodeList().size(); i++) {
			BayesNetNode eachNode = graph.getNodeList().get(i);
			for(BayesNetNode eachNeighbor : 
					eachNode.getNonDirectionalLineList()) 
			{
				// add connections to both 
				// allConnections and remainingConnections
				int index = graph.getNodeList().indexOf(eachNeighbor);
				BayesNetDFSNode dfsNodeToConnect = nodeList.get(index);
				nodeList.get(i).addNeighborToBothConnections(dfsNodeToConnect);
			}
		}

		remainingNodeList.addAll(nodeList);
	}
	
	// make a hard copy of this BayesNetDFSGraph
	BayesNetDFSGraph copy() {
		BayesNetDFSGraph copiedGraph = new BayesNetDFSGraph();
		// copy each node, connections of each node in nodeList,
		for(BayesNetDFSNode eachNode : nodeList) {
			copiedGraph.nodeList.add(eachNode.copyExcludingConnections());
		}
		// add connections
		for(int i=0; i<this.nodeList.size(); i++) {
			BayesNetDFSNode eachNode = this.nodeList.get(i);
			// copy allConnectedNodelist
			for(BayesNetDFSNode eachNeighbor : 
					eachNode.getAllNeighborNodeSet().getNodelist())
			{
				int index = this.nodeList.indexOf(eachNeighbor);
				BayesNetDFSNode dfsNodeToConnect = 
						copiedGraph.nodeList.get(index);
				copiedGraph.nodeList.get(i).addFillinNeighbor(dfsNodeToConnect);
			}
		}
		
		// copy remainingNodeList
		for(int i=0; i<this.remainingNodeList.size(); i++) {
			
			BayesNetDFSNode eachNode = this.remainingNodeList.get(i);
			int index = this.nodeList.indexOf(eachNode);
			copiedGraph.remainingNodeList.add(copiedGraph.nodeList.get(index));
			
			// copy remainingConnectedNodelist
			for(BayesNetDFSNode eachNeighbor : 
				eachNode.getRemainingNeighborNodeSet().getNodelist())
			{
				int index1 = this.nodeList.indexOf(eachNeighbor);
				BayesNetDFSNode dfsNodeToConnect = 
						copiedGraph.nodeList.get(index1);
				copiedGraph.remainingNodeList.get(i).
								addRemainingNeighbor(dfsNodeToConnect);
			}
		}
	
		// 	cliqueSet
		if(this.isInitialCliqueSetIdentified()) {
			copiedGraph.cliqueSet = new BayesNetDFSCliqueSet();
			for(int i=0; i<this.cliqueSet.getCliqueList().size(); i++) {
				BayesNetDFSClique eachOldClique = 
						this.cliqueSet.getCliqueList().get(i);
				ArrayList<BayesNetDFSNode> oldCliqueNodelist = 
						eachOldClique.getNodelist();
				ArrayList<BayesNetDFSNode> newCliqueNodelist = 
						new ArrayList<BayesNetDFSNode>();
				for(BayesNetDFSNode eachOldNode : oldCliqueNodelist) {
					int index = this.nodeList.indexOf(eachOldNode);
					BayesNetDFSNode eachNewNode = 
							copiedGraph.nodeList.get(index);
					newCliqueNodelist.add(eachNewNode);
				}
				
				BayesNetDFSNodeSet newNodeSet = 
						new BayesNetDFSNodeSet(newCliqueNodelist);
				copiedGraph.cliqueSet.add(new BayesNetDFSClique(newNodeSet));
			}
		}
		
		// eliminationNodeSequence
		for(BayesNetDFSNode eachOldNode : this.eliminatedNodeSequence) {
			int index = this.nodeList.indexOf(eachOldNode);
			if(index==-1) {
				System.out.println(eachOldNode.getName()+" "+eachOldNode);
				for(BayesNetDFSNode eachNode : this.nodeList) {
					System.out.print(eachNode.getName()+" "+eachNode+ "  ");
				}
			}
			copiedGraph.eliminatedNodeSequence.add(
					copiedGraph.nodeList.get(index));
		}
		// eliminatedLineSetSinceLastCliqueUpdate
		for(BayesNetDFSLine eachLine : 
						this.addedLineSetSinceLastCliqueUpdate.getLineList()) {
			BayesNetDFSNode oldNodeA = eachLine.nodeA;
			int indexA = this.nodeList.indexOf(oldNodeA);
			BayesNetDFSNode newNodeA = 
					copiedGraph.nodeList.get(indexA);
	
			BayesNetDFSNode oldNodeB = eachLine.nodeB;
			int indexB = this.nodeList.indexOf(oldNodeB);
			BayesNetDFSNode newNodeB = 
					copiedGraph.nodeList.get(indexB);
			
			copiedGraph.addedLineSetSinceLastCliqueUpdate.addLine(
					new BayesNetDFSLine(newNodeA, newNodeB));
		}
		// isInitialCliqueSetIdentified
		copiedGraph.isInitialCliqueSetIdentified = 
				this.isInitialCliqueSetIdentified;
		if(!checkCopy(copiedGraph)) throw new RuntimeException("--");
		return copiedGraph;
	}
	// assert whether properly copied or not
	private boolean checkCopy(BayesNetDFSGraph copiedGraph) {
		// copy each node, connections of each node in nodeList,
		for(int i=0; i<nodeList.size(); i++) {
			nodeList.get(i).hasSameNodeFxiedInfo(copiedGraph.nodeList.get(i));
			if(nodeList.get(i)==copiedGraph.nodeList.get(i)) {
				throw new RuntimeException();
			}
		}
		// 	cliqueSet,
		if(this.isInitialCliqueSetIdentified()) {
			if(this.cliqueSet.getCliqueList()==copiedGraph.cliqueSet.cliqueList) {
				throw new RuntimeException();
			}
			if(this.cliqueSet.getCliqueList().size()!=copiedGraph.cliqueSet.getCliqueList().size()) {
				throw new RuntimeException();
			}
			
			for(int i=0; i<this.cliqueSet.getCliqueList().size(); i++) {
				BayesNetDFSClique eachOldClique = this.cliqueSet.getCliqueList().get(i);
				BayesNetDFSClique eachNewClique = copiedGraph.cliqueSet.getCliqueList().get(i);
				ArrayList<BayesNetDFSNode> newCliqueNodelist = eachNewClique.getNodelist();
				for(int j=0; j<eachOldClique.getNodelist().size(); j++) {
						BayesNetDFSNode eachOldNode = eachOldClique.getNodelist().get(j);
						BayesNetDFSNode eachNewNode = eachNewClique.getNodelist().get(j);
					if(!eachOldNode.hasSameNodeFxiedInfo(eachNewNode)) return false;
					if(eachOldNode==eachNewNode) return false;
				}
				if(eachOldClique.getNodelist().size()!=newCliqueNodelist.size()) {
					throw new RuntimeException(eachOldClique.getNodelist().size()+" vs "+newCliqueNodelist.size());
				}
			}
		}
		if(this.eliminatedNodeSequence.size()!=copiedGraph.eliminatedNodeSequence.size())
			return false;
		for(int i=0; i<this.eliminatedNodeSequence.size(); i++) {
			if(this.eliminatedNodeSequence.get(i)==copiedGraph.eliminatedNodeSequence.get(i))
				return false;
			if(!this.eliminatedNodeSequence.get(i).hasSameNodeFxiedInfo(copiedGraph.eliminatedNodeSequence.get(i)))
				return false;
		}
		// isInitialCliqueSetIdentified
		if(copiedGraph.isInitialCliqueSetIdentified!= 
				this.isInitialCliqueSetIdentified) return false;
		return true;
	}
	private void setIsInitialCliqueSetIdentified(boolean value) {
		isInitialCliqueSetIdentified = value;
	}
	private boolean isInitialCliqueSetIdentified() {
		return isInitialCliqueSetIdentified;
	}
	
	void eliminateAllSimplicials() {
		boolean isSimplicialFound;
		do {
			isSimplicialFound = false;
			for(int i=remainingNodeList.size()-1; 0<=i; i--) {
				BayesNetDFSNode eachNode = remainingNodeList.get(i);
				if(eachNode.isRemainingSimplicial()) {
					eliminateNode(eachNode);
					isSimplicialFound = true;
				}
			}
		} while(isSimplicialFound && remainingNodeList.size()!=0);
	}
	// eliminate the node if it still exists
	void eliminateNode(BayesNetDFSNode node) {
		if(remainingNodeList.contains(node)) {
			BayesNetDFSLineSet fillinSet = node.addAllFillins();
			node.removeAllRemainingConnections();
			eliminatedNodeSequence.add(node);
			addedLineSetSinceLastCliqueUpdate.add(fillinSet);
			remainingNodeList.remove(node);
		}
	}
	int totalTableSize() {
		if(!isInitialCliqueSetIdentified()) {
			findAllCliques();
			setIsInitialCliqueSetIdentified(true);
		}
		if(0<=addedLineSetSinceLastCliqueUpdate.size()) {
			this.updateCliques(cliqueSet, addedLineSetSinceLastCliqueUpdate);
		}
		int totalTableSize = 1;
		for(BayesNetDFSClique eachClique : cliqueSet.getCliqueList()) {
			totalTableSize += eachClique.tableSize();
		}
		return totalTableSize;
	}
	private void findAllCliques() {
		BayesNetDFSNodeSet copiedDFSNodeSet = 
				new BayesNetDFSNodeSet(this.nodeList).copy();
		BayesNetDFSCliqueSet maximalCliqueSet = 
				BronKerbosch(new BayesNetDFSNodeSet(), copiedDFSNodeSet, new BayesNetDFSNodeSet());
		this.cliqueSet = maximalCliqueSet;
	}
	
	void updateCliques(BayesNetDFSCliqueSet C,
			BayesNetDFSLineSet F) {
		BayesNetDFSNodeSet U = F.nodeSet();

		//G' = (V, E U F)
		//addLines(F);
		BayesNetDFSCliqueSet Cnew = 
				BronKerbosch(new BayesNetDFSNodeSet(), U.allFamilyNodeSet(), new BayesNetDFSNodeSet());
		for(int i=C.getCliqueList().size()-1; 0<=i; i--) {
			BayesNetDFSClique c = C.getCliqueList().get(i);
			// if(c intersection U !=empty) C = C \ { c }			
			if(0 < c.sizeOfIntersectionWith(U)) C.remove(c);
		}
		/*
		for(BayesNetDFSClique c :C.getCliqueList()) {
			// if(c intersection U !=empty) C = C \ { c }
			if(c.sizeOfIntersectionWith(U) != 0) C.remove(c);
		}
		*/
		for(BayesNetDFSClique c : Cnew.getCliqueList()) {
			// if(c intersection U !=empty) C = C U { c }
			if(0 < c.sizeOfIntersectionWith(U)) {
				C.addIfDoesNotExist(c);
			}
		}
		this.cliqueSet = C;
	}

	
	// Original Bron-Kerbosch algorithm
	private ArrayList<BayesNetDFSClique>
		BronKerboschOriginal(BayesNetDFSNodeSet R, 
				BayesNetDFSNodeSet P, 
				BayesNetDFSNodeSet X) {
		System.out.print("KB: ");
		System.out.print("R ");
		R.print();
		System.out.print("P ");
		P.print();
		System.out.print("X ");
		X.print();
		System.out.println();
		// C = empty
		ArrayList<BayesNetDFSClique> C = new ArrayList<BayesNetDFSClique>();
		if(P.size()==0 && X.size()==0) {
			C.add(new BayesNetDFSClique(R));
			return C;
		} else {
			//C = empty, is used above instead;
			while(0<P.size()) {
				System.out.print("R ");
				R.print();
				System.out.print("P ");
				P.print();
				System.out.print("X ");
				X.print();
				System.out.println();
				
				BayesNetDFSNode v = P.get(0);
				System.out.println(v.getName()+" has been selected");
				//P = P\{v}
				P.subtract(v);		
				//Rnew = R U {v}
				BayesNetDFSNodeSet Rnew = R.copy();
				Rnew.add(v);
				//Pnew = P intersect nb(v)
				BayesNetDFSNodeSet Pnew = 
						P.intersection(v.getAllNeighborNodeSet());
				//Xnew = X intersect nb(v)
				BayesNetDFSNodeSet Xnew = 
						X.intersection(v.getAllNeighborNodeSet());
				//K = BronKerbosch(Rnew, Pnew, Xnew);				
				ArrayList<BayesNetDFSClique> K = 
						BronKerboschOriginal(Rnew, Pnew, Xnew);
				//X = X U {v}
				X.add(v);
				
				//C = C U K
				for(BayesNetDFSClique eachClique : K) {
					//if(!C.contains(eachClique)) 
					C.add(eachClique);
					 
				}
			}
			return C;
		}
	}

	//Bron-Kerbosch algorithm with pivot
	private BayesNetDFSCliqueSet
		BronKerbosch(BayesNetDFSNodeSet R, 
				BayesNetDFSNodeSet P, 
				BayesNetDFSNodeSet X) {
		// C = empty
		if(P.size()==0 && X.size()==0) {
			BayesNetDFSCliqueSet C = new BayesNetDFSCliqueSet(R);
			return C;
		} else {
			//C = empty;
			BayesNetDFSCliqueSet C = new BayesNetDFSCliqueSet();
			BayesNetDFSNode u = selectPivot(P, X);
			BayesNetDFSNodeSet PWithoutNeighborU =
					P.copy();
			PWithoutNeighborU.subtract(u.getAllNeighborNodeSet());
			while(0<PWithoutNeighborU.size()) {
				BayesNetDFSNode v = PWithoutNeighborU.get(0);
				
				PWithoutNeighborU.subtract(v);
				//P = P\{v}				
				P.subtract(v);
				//Rnew = R U {v}
				BayesNetDFSNodeSet Rnew = R.copy();
				Rnew.add(v);
				//Pnew = P intersect nb(v)
				BayesNetDFSNodeSet Pnew = 
						P.intersection(v.getAllNeighborNodeSet());
				//Xnew = X intersect nb(v)
				BayesNetDFSNodeSet Xnew = 
						X.intersection(v.getAllNeighborNodeSet());
				//K = BronKerbosch(Rnew, Pnew, Xnew);				
				BayesNetDFSCliqueSet K = BronKerbosch(Rnew, Pnew, Xnew);
				//X = X U {v}
				X.add(v);
				//C = C U K
				for(BayesNetDFSClique eachClique : K.getCliqueList()) {
					C.add(eachClique);
				}
			}
			return C;
		}
	}
	
	void println() {
		System.out.println("Graph:");
		System.out.println("nodeList");
		for(BayesNetDFSNode eachNode : nodeList) {
			eachNode.print();
			System.out.println();
		}
		System.out.println("remainingNodeList");
		for(BayesNetDFSNode eachNode : remainingNodeList) {
			eachNode.print();
			System.out.println();
		}
		if(isInitialCliqueSetIdentified()) {
			System.out.println("CliqueList");
			for(BayesNetDFSClique eachClique : cliqueSet.getCliqueList()) {
				eachClique.print();
				System.out.println();//(" ");
			}
		}
		System.out.print("Eliminated nodeList:");
		for(BayesNetDFSNode eachNode : eliminatedNodeSequence) {
			System.out.print(eachNode.getName()+ " ");
		}
		System.out.println();
	}
	
	// for test purpose
	void addLineAndUpdateCliques(String nodeNameA, String nodeNameB) {
		BayesNetDFSNode nodeA = null, nodeB = null;

		for(BayesNetDFSNode eachNode : remainingNodeList) {
			if(eachNode.getName().equals(nodeNameA)) {
				nodeA = eachNode;
				break;
			}
		}
		for(BayesNetDFSNode eachNode : remainingNodeList) {
			if(eachNode.getName().equals(nodeNameB)) {
				nodeB = eachNode;
				break;
			}
		}
		if(nodeA==null || nodeB==null)
			throw new RuntimeException("Node A or Node B not fonud");
		BayesNetDFSLine newLine = new BayesNetDFSLine(nodeA, nodeB);
		BayesNetDFSLineSet lineset = 
				new BayesNetDFSLineSet();
		lineset.addLine(newLine);
		updateCliques(cliqueSet, lineset);
	}
	
	int numOfRemainingNodes() {
		return remainingNodeList.size();
	}
	
	void minFill() {
		System.out.println("min fill");
		while(0<remainingNodeList.size()) {
			int minFillinSize = Integer.MAX_VALUE;
			BayesNetDFSNode minFillinNode = null;
			for(BayesNetDFSNode eachNode : remainingNodeList) {
				int fillinSize = eachNode.fillinSize();
				System.out.print(eachNode.getName()+" " + fillinSize+"   ");
				if(fillinSize < minFillinSize) {
					minFillinSize = fillinSize;
					minFillinNode = eachNode;
				}
			}
			System.out.println();
			// find each minFill
			System.out.println("min: "+minFillinNode.getName());
			eliminateNode(minFillinNode);
		}
		System.out.println("min fill finished");
	}
	int remainingNodeListSize() {
		return remainingNodeList.size();
	}
	ArrayList<BayesNetDFSNode> getRemainingNodeList() {
		return remainingNodeList;
	}
	boolean hasSameRemainingNodeList(BayesNetDFSGraph graph) {
		if(remainingNodeList.size()!=graph.remainingNodeList.size()) 
			return false;
		for(int i=0; i<remainingNodeList.size(); i++) {
			if(!remainingNodeList.get(i).hasSameNodeFxiedInfo(
					graph.remainingNodeList.get(i)) ) {
				return false;
			}
		}
		return true;
	}

	
	BayesNetDFSNode selectPivot(BayesNetDFSNodeSet P,
			BayesNetDFSNodeSet X) 
	{
		// do not consider P intersection X
		// to save computation time
		// thus, it does not pick totally random, but somewhat random.
		int size = P.size() + X.size();
		Random random = new Random();
		
		int randomNumber = random.nextInt(size);
		if(randomNumber<P.size()) return P.get(randomNumber);
		else return X.get(randomNumber-P.size());
		
	}
	
	
	BayesNetDFSNode equivalentNodeitselfExceptConnections(BayesNetDFSNode node) 
	{
		for(BayesNetDFSNode eachNode : nodeList) {
			if(eachNode.hasSameNodeFxiedInfo(node)) {
				return eachNode;
			}
		}
		throw new RuntimeException("No such node found in "
				+ "equivalentNodeitselfExceptConnections: "+node.getName());
	}
	
	ArrayList<String> eliminationOrder() {
		ArrayList<String> eliminationOrder = new ArrayList<String>();
		for(BayesNetDFSNode eachNode : eliminatedNodeSequence) {
			eliminationOrder.add(eachNode.getName());
		}
		return eliminationOrder;
	}
	ArrayList<BayesNetDFSNode> getEliminatedNodeSequence() {
		return eliminatedNodeSequence;
	}
	private class BayesNetDFSClique {
		private ArrayList<BayesNetDFSNode> nodelist;
		BayesNetDFSClique(BayesNetDFSNodeSet nodeSet) {
			this.nodelist = nodeSet.getNodelist();
		}
		int sizeOfIntersectionWith(BayesNetDFSNodeSet nodeSet) {
			int size = 0;
			for(BayesNetDFSNode eachNode : nodelist) {
				if(nodeSet.contains(eachNode)) size++;
			}
			return size;
		}
		boolean equals(BayesNetDFSClique clique) {
			if(this==clique) return true;
			else if(this.nodelist.size()==clique.nodelist.size()
					&& nodelist.containsAll(clique.nodelist)) return true;
			return false;
		}
		void print() {
			System.out.print("Clique(");
			System.out.print("["+tableSize()+"] ");
			for(BayesNetDFSNode eachNode : nodelist) {
				System.out.print(eachNode.getName()+ " ");
				//+ eachNode + " ");
			}
			System.out.print(")");
		}
		int tableSize() {
			int size = 1;
			for(BayesNetDFSNode eachNode : nodelist) {
				size *= eachNode.tableSize();
			}
			return size;
		}
		ArrayList<BayesNetDFSNode> getNodelist() {
			return nodelist;
		}
		
	}
	private class BayesNetDFSCliqueSet {
		private ArrayList<BayesNetDFSClique> cliqueList;
		BayesNetDFSCliqueSet() {
			cliqueList = new ArrayList<BayesNetDFSClique>();
		}
		BayesNetDFSCliqueSet(BayesNetDFSNodeSet nodeSet) {
			this();
			cliqueList.add(new BayesNetDFSClique(nodeSet));
		}
		ArrayList<BayesNetDFSClique> getCliqueList() {
			return cliqueList;
		}
		// check if the contents of clique exists in this cliqueList 
		void addIfDoesNotExist(BayesNetDFSClique clique) {
			for(BayesNetDFSClique eachClique : cliqueList) {
				if(eachClique.equals(clique)) {
					return;
				}
			}
			add(clique);
		}
		// do not check whether the clique exists or not
		void add(BayesNetDFSClique clique) {
			cliqueList.add(clique);
		}
		// remove the instance of clique from the list
		// does not remove, if they are different objects, but they have the
		// same contents
		void remove(BayesNetDFSClique clique) {
			cliqueList.remove(clique);
		}
	}
}
