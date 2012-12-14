package bnp;

import java.util.ArrayList;

class BayesNetDFSNode {
	// adding fill ins are added to allConnectedNodelist
	// removing node is applied to remainingConnectedNodelist
	
	// Original connections + fill-ins
	private ArrayList<BayesNetDFSNode> allConnectedNodelist;
	// connections that are remaining after removing nodes
	private ArrayList<BayesNetDFSNode> remainingConnectedNodelist;
	// copied nodes share the same nodeFixedInfo to save memory
	private BayesNetDFSNodeFixedInfo nodeFixedInfo;
	private BayesNetDFSNode() {
		allConnectedNodelist = new ArrayList<BayesNetDFSNode>();
		remainingConnectedNodelist = new ArrayList<BayesNetDFSNode>();
	}
	BayesNetDFSNode(String name, int size) {
		this();
		nodeFixedInfo = new BayesNetDFSNodeFixedInfo(name, size);
	}
	private BayesNetDFSNode(BayesNetDFSNodeFixedInfo nodeFixedInfo) {
		this();
		this.nodeFixedInfo = nodeFixedInfo;
	}
	// 
	BayesNetDFSNode copyExcludingConnections() {
		return new BayesNetDFSNode(nodeFixedInfo);
	}
	// add a neighbor to both connections
	void addNeighborToBothConnections(BayesNetDFSNode neighbor) {
		allConnectedNodelist.add(neighbor);
		remainingConnectedNodelist.add(neighbor);
	}
	// add a fill in
	void addFillinNeighbor(BayesNetDFSNode fillinNeighbor) {
		allConnectedNodelist.add(fillinNeighbor);
	}
	void addRemainingNeighbor(BayesNetDFSNode remainingNeighbor) {
		remainingConnectedNodelist.add(remainingNeighbor);
	}
	// remove a connection from remainingConnectedNodelist
	void removeNeighbor(BayesNetDFSNode neighbor) {
		remainingConnectedNodelist.remove(neighbor);
	}
	
	BayesNetDFSNodeSet getAllNeighborNodeSet() {
		return new BayesNetDFSNodeSet(allConnectedNodelist);
	}
	BayesNetDFSNodeSet getRemainingNeighborNodeSet() {
		return new BayesNetDFSNodeSet(remainingConnectedNodelist);
	}	
	
	boolean isRemainingNeighborOf(BayesNetDFSNode node) {
		if(remainingConnectedNodelist.contains(node)) return true;
		return false;
	}
	String getName() { return nodeFixedInfo.name; }
	int tableSize() {
		return nodeFixedInfo.size;
	}
	// family(thisNode) must be a complete set to be a simplicial.
	boolean isRemainingSimplicial() {
		for(BayesNetDFSNode eachNodeA : remainingConnectedNodelist) {
			for(BayesNetDFSNode eachNodeB : remainingConnectedNodelist) {
				// break instead of if(eachNodeA==eachNodeB) continue;
				// because only half of the two dimensional
				// node connection data needs to be considered
				if(eachNodeA==eachNodeB) break;
				if(!eachNodeA.isRemainingNeighborOf(eachNodeB)) return false;
			}
		}
		return true;
	}
	BayesNetDFSLineSet addAllFillins() {
		//int fillinSize = 0;
		BayesNetDFSLineSet fillinSet = new BayesNetDFSLineSet();
		for(BayesNetDFSNode eachNodeA : remainingConnectedNodelist) {
			for(BayesNetDFSNode eachNodeB : remainingConnectedNodelist) {
				// break instead of if(eachNodeA==eachNodeB) continue;
				// because only half of the two dimensional
				// node connection data needs to be considered
				if(eachNodeA==eachNodeB) break;
				if(!eachNodeA.isRemainingNeighborOf(eachNodeB)) {
					fillinSet.addLine(
							new BayesNetDFSLine(eachNodeA, eachNodeB));					
				}
			}
		}
		for(BayesNetDFSLine eachFillin : fillinSet.getLineList()) {
			BayesNetDFSNode eachNodeA = eachFillin.nodeA;
			BayesNetDFSNode eachNodeB = eachFillin.nodeB;
			//System.out.print(eachNodeA.getName()+"-"+eachNodeB.getName()+" ");
			eachNodeA.addFillinNeighbor(eachNodeB);
			eachNodeB.addFillinNeighbor(eachNodeA);
			eachNodeA.addRemainingNeighbor(eachNodeB);
			eachNodeB.addRemainingNeighbor(eachNodeA);
		} //System.out.println();
		return fillinSet;
	}
	// return the size of fill-ins needed when this node is eliminated
	int fillinSize() {
		int fillinSize = 0;
		for(BayesNetDFSNode eachNodeA : remainingConnectedNodelist) {
			for(BayesNetDFSNode eachNodeB : remainingConnectedNodelist) {
				// break instead of if(eachNodeA==eachNodeB) continue;
				// because only half of the two dimensional
				// node connection data needs to be considered
				if(eachNodeA==eachNodeB) break;
				if(!eachNodeA.isRemainingNeighborOf(eachNodeB)) fillinSize++;
			}
		}
		return fillinSize;
	}
	void print() {
		System.out.print("DFSNode:"+getName()+ " " //+ this 
				+" is connected to org + fill-in ");
		for(BayesNetDFSNode eachNode : allConnectedNodelist) {
			System.out.print(eachNode.getName()+" ");	
		} System.out.print("   ");
		System.out.print("DFSNode:"+getName()+" is connected to remaining ");
		for(BayesNetDFSNode eachNode : remainingConnectedNodelist) {
			System.out.print(eachNode.getName()+" ");	
		}
	}
	void removeAllRemainingConnections() {
		for(int i=0; i<remainingConnectedNodelist.size(); i++) {
			BayesNetDFSNode eachNeighbor = remainingConnectedNodelist.get(i);
			eachNeighbor.removeNeighbor(this);
		}
		remainingConnectedNodelist.clear();
	}
	boolean hasSameNodeFxiedInfo(BayesNetDFSNode node) {
		if(this.nodeFixedInfo==node.nodeFixedInfo) return true;
		return false;
	}
	
	// This class is not hard copied when copy()
	// but BayesNetDFSNode only references this class
	// because the information in this class not change.
	private class BayesNetDFSNodeFixedInfo {
		final String name;
		final int size;
		BayesNetDFSNodeFixedInfo(String name, int size) {
			this.name = name;
			this.size = size;
		}
	}
}

