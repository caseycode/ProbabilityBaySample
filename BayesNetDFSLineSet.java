package bnp;

import java.util.ArrayList;

public class BayesNetDFSLineSet {
	private ArrayList<BayesNetDFSLine> linelist;
	BayesNetDFSLineSet() {
		linelist = new ArrayList<BayesNetDFSLine>();
	}
	void addLine(BayesNetDFSLine line) {
		linelist.add(line);
	}
	void add(BayesNetDFSLineSet lineSet) {
		this.linelist.addAll(lineSet.getLineList());
	}
	ArrayList<BayesNetDFSLine> getLineList() {
		return linelist;
	}
	// return a set nodes that are connected to the lines in this object
	BayesNetDFSNodeSet nodeSet() {
		BayesNetDFSNodeSet nodeSet = new BayesNetDFSNodeSet();
		for(BayesNetDFSLine eachLine : linelist) {
			if(!nodeSet.contains(eachLine.nodeA)) 
				nodeSet.add(eachLine.nodeA);
			if(!nodeSet.contains(eachLine.nodeB)) 
				nodeSet.add(eachLine.nodeB);
		}
		return nodeSet;
	}
	int size() { return linelist.size(); }
}
