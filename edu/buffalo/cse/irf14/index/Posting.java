package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;


class Posting implements Serializable {
	Integer docId;
	Integer termFrq;
	ArrayList<Integer> posIndex ;
	Posting() {
		docId = 0;
		termFrq = 1;
		posIndex = new ArrayList<Integer>()  ;
	}
	Integer getDocId() {
		return docId;
	}
	
	Integer getTermFrq() {
		return termFrq;
	}
	void setDocId(Integer docId) {
		this.docId = docId;
	}
	
	void setTermFrq(Integer termFrq) {
		this.termFrq = termFrq;
	}
	void incrTermFrq() {
		this.termFrq++;
	}
	
	void setPosIndex(Integer pos)
	{
		this.posIndex.add(pos);
	}
	
	ArrayList<Integer> getPosIndex()
	{
		return this.posIndex;
	}
	
	void mergePosIndex(ArrayList<Integer> posIndexToMerge)
	{
		ListIterator<Integer> iter = posIndexToMerge.listIterator();
		while(iter.hasNext())
		{
			this.posIndex.add(iter.next());
		}
	}
}
