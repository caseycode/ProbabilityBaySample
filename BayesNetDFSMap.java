package bnp;
import java.util.ArrayList;

public class BayesNetDFSMap {
	private ArrayList<BayesNetDFSGraphListInRLength> graphListInRLength;
	BayesNetDFSMap() {
		graphListInRLength = new ArrayList<BayesNetDFSGraphListInRLength>();
	}
	// try to add graph
	// if added then return true;
	// else return false;
	boolean addIfBetter(BayesNetDFSGraph graph) {
		BayesNetDFSGraphListInRLength bayesNetDFSGraphListInRLength = 
				getBayesNetDFSGraphListInRLength(graph.remainingNodeListSize());
		// no such BayesNetDFSGraphListInRLength
		if(bayesNetDFSGraphListInRLength==null) {
			addBayesNetDFSGraphListInRLength(graph);
			return true;
		} else {
			return bayesNetDFSGraphListInRLength.addIfBetter(graph);
		}
		
	}
	void addBayesNetDFSGraphListInRLength(BayesNetDFSGraph graph) {
		BayesNetDFSGraphListInRLength newList = 
				new BayesNetDFSGraphListInRLength(
								graph.remainingNodeListSize());
		newList.add(graph);
		graphListInRLength.add(newList);		
	}
	// return null, 
	// if there is not such BayesNetDFSGraphListInRLength of such size.
	private BayesNetDFSGraphListInRLength
							getBayesNetDFSGraphListInRLength(int RLength) 
	{
		for(BayesNetDFSGraphListInRLength eachList : graphListInRLength) {
			if(eachList.getRLength()==RLength) return eachList;
		}
		return null;
	}	
	private class BayesNetDFSGraphListInRLength {
		private int RLength;
		ArrayList<BayesNetDFSGraph> graphList;
		BayesNetDFSGraphListInRLength(int RLength) {
			this.RLength = RLength;
			graphList = new ArrayList<BayesNetDFSGraph>();
		}
		int getRLength() {
			return RLength;
		}
		// return null, if there is no such graph
		BayesNetDFSGraph getSameRemainingSetGraph(BayesNetDFSGraph graph) {
			for(BayesNetDFSGraph eachGraph : graphList) {
				if(eachGraph.hasSameRemainingNodeList(graph)) return eachGraph;
			}
			return null;
		}
		void add(BayesNetDFSGraph graph) {
			graphList.add(graph);
		}
		boolean addIfBetter(BayesNetDFSGraph graph) {
			BayesNetDFSGraph equivalentGraph = getSameRemainingSetGraph(graph);
			// if no such graph exists, simply add the new graph
			if(equivalentGraph==null) {
				graphList.add(graph);
				return true;
			}
			else {
				if(graph.totalTableSize()<equivalentGraph.totalTableSize()) {
					graphList.remove(equivalentGraph);
					graphList.add(graph);
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
