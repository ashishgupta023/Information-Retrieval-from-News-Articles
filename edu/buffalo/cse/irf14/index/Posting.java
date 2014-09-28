package edu.buffalo.cse.irf14.index;


class Posting {
	Integer docId;
	Integer termFrq;
	Posting() {
		docId = 0;
		termFrq = 1;
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
}
