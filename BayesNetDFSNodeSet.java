package bnp;

import java.util.ArrayList;

class BayesNetDFSNodeSet {
	private ArrayList<BayesNetDFSNode> nodelist;
	BayesNetDFSNodeSet() {
		nodelist = new ArrayList<BayesNetDFSNode>();
	}
	BayesNetDFSNodeSet(ArrayList<BayesNetDFSNode> nodelist) {
		this.nodelist = nodelist;
	}
	private void addAll(BayesNetDFSNodeSet aNodeSet) {
		this.nodelist.addAll(aNodeSet.getNodelist());
	}
	ArrayList<BayesNetDFSNode> getNodelist() {
		return nodelist;
	}
	int size() { return nodelist.size(); }
	BayesNetDFSNode get(int i) {
		return nodelist.get(i);
	}
	BayesNetDFSNodeSet copy() {
		BayesNetDFSNodeSet newNodeSet = new BayesNetDFSNodeSet();
		newNodeSet.addAll(this);
		return newNodeSet;
	}
	void subtract(BayesNetDFSNodeSet nodeSetToSubtract) {
		nodelist.removeAll(nodeSetToSubtract.getNodelist());
	}
	void subtract(BayesNetDFSNode aNode) {
		nodelist.remove(aNode);
	}
	// return a new set
	BayesNetDFSNodeSet intersection(BayesNetDFSNodeSet nodeSet) {
		BayesNetDFSNodeSet newNodeSet = new BayesNetDFSNodeSet();
		for(BayesNetDFSNode eachNode : nodeSet.getNodelist()) {
			if(this.contains(eachNode)) newNodeSet.add(eachNode);
		}
		return newNodeSet;
	}
	boolean contains(BayesNetDFSNode node) {
		return nodelist.contains(node);
	}
	void add(BayesNetDFSNode node) {
		if(!contains(node)) nodelist.add(node);
	}
	BayesNetDFSNodeSet allFamilyNodeSet() {
		BayesNetDFSNodeSet familyNodeSet = new BayesNetDFSNodeSet();
		for(BayesNetDFSNode eachNode : nodelist) {
			// add the family of eachNode. To do that,
			// 1. add eachNode
			if(!familyNodeSet.contains(eachNode)) {
				familyNodeSet.add(eachNode);
			}
			// 2. add neighbors of eachNode
			for(BayesNetDFSNode eachNeighbor : 
							eachNode.getAllNeighborNodeSet().getNodelist()) 
			{
				if(!familyNodeSet.contains(eachNeighbor)) {
					familyNodeSet.add(eachNeighbor);
				}
			}
		}
		return familyNodeSet;
	}
	void print() {
		System.out.print("NodeSet(");
		for(BayesNetDFSNode eachNode : nodelist) {
			System.out.print(eachNode.getName()+ " ");
		}
		System.out.print(")");
	}
}
