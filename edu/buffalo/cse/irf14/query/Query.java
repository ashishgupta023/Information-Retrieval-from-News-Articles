package edu.buffalo.cse.irf14.query;

import java.util.LinkedList;

import sun.awt.RepaintArea;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query {
	/**
	 * Method to convert given parsed query into string
	 */
	
	public String representation;
	public LinkedList<String> evaluationOrder;
	
	
	public Query() {
		// TODO Auto-generated constructor stub
		evaluationOrder = new  LinkedList<String>();
	}
	
	public void setString(String representation)
	{
		this.representation =  representation;
	}
	
	public String toString() {
		
		return representation;
	}
	
	public void updateEvalOrder(String node)
	{
		evaluationOrder.add(node);
	}
	
	
	public LinkedList<String> getEvalOrder()
	{
		return evaluationOrder;
	}
	
}
