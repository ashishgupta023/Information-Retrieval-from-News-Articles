package edu.buffalo.cse.irf14.index;


import java.util.Comparator;

class PostDocIdComparable implements Comparator<Posting>{
     @Override
    public int compare(Posting posta, Posting postb) {
        return (posta.getDocId()>postb.getDocId() ? 1 : (posta.getDocId()==postb.getDocId() ? 0 : -1));
    }
} 
