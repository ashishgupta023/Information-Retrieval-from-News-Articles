/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
	
	public QueryParser() {
		// TODO Auto-generated constructor stub
	}
	
	private static boolean hasPrecedence(String op1, String op2 )
	{
		 if (op2.equals("(") || op2.equals(")"))
	            return false;
	     if ( ( op1.equals("#") || op1.equals("*")) && op2.equals("+")   )
	        return false;
	     else
	        return true;
	}
	
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator) {
		//TODO: YOU MUST IMPLEMENT THIS METHOD
		
		userQuery = userQuery.trim();
		
		defaultOperator = defaultOperator.replaceAll("AND", "+");
		defaultOperator = defaultOperator.replaceAll("OR", "#");
		defaultOperator = defaultOperator.replaceAll("NOT", "*");

		String[] userQueryVals = userQuery.split("AND");
		String joinOpearator = "+";
		String checkNotContains = "OR";
		if(userQueryVals.length == 1)
		{
			 userQueryVals = userQuery.split("OR");
			 joinOpearator = "#";
			 checkNotContains = "AND";
		}
		for(int i = 0 ; i< userQueryVals.length ; i++)
		{
			Pattern pattern = Pattern.compile("[A-Z][a-z]*:[(]");
			Matcher matcher = pattern.matcher(userQueryVals[i]);
			if(matcher.find())
			{
				String indexInUse = matcher.group(0).replaceAll(":[(]", "");
				userQueryVals[i] = userQueryVals[i].replaceAll("[A-Z][a-z]*:", "");
				 pattern = Pattern.compile("[A-Z]*[a-z]+");
				 matcher = pattern.matcher(userQueryVals[i].trim());
				 if(matcher.find())
				 {
					 String output = matcher.replaceFirst(indexInUse.concat(":"+matcher.group(0)));

					 pattern = Pattern.compile("\\s[A-Z]*[a-z]+");
					 matcher = pattern.matcher(output);
					 boolean val = matcher.find();
					while(val)
					 {	
					  output = matcher.replaceAll(" "+indexInUse.concat(":"+matcher.group(0).trim()));
					  val = matcher.find();
					 }
					userQueryVals[i] = output;
				 }
				 
			}
				String[] splitViaSpaces = userQueryVals[i].trim().split(" ");

				if(splitViaSpaces.length > 1 && !userQueryVals[i].contains(checkNotContains) && !userQueryVals[i].contains("NOT") && !userQueryVals[i].contains("\""))
				{
					userQueryVals[i]  = "";

					for(int j = 0 ; j < splitViaSpaces.length ; j++)
					{
						if(userQueryVals[i] == "")
						{
							userQueryVals[i] = splitViaSpaces[j];
						}
						else
						{
							userQueryVals[i]  = userQueryVals[i].concat(defaultOperator).concat(splitViaSpaces[j]);
						}

					}
					if(!userQuery.startsWith(splitViaSpaces[0]))
					userQueryVals[i] = " ( "+userQueryVals[i]+" ) ";
				}
			
		}
		
		userQuery = "";

		for(int i = 0 ; i < userQueryVals.length ; i++)
		{
			if(userQuery == "")
				userQuery = userQueryVals[i];
			else
			{
				userQuery = userQuery.concat(joinOpearator).concat(userQueryVals[i]);
			}
		}
		
		
		
		userQuery = userQuery.replaceAll("AND", "+");
		userQuery = userQuery.replaceAll("OR", "#");
		userQuery = userQuery.replaceAll("NOT", "*");
		
		char[] query = userQuery.toCharArray();
		
		Stack<String> queryStack = new Stack<String>();
		Stack<Character> opsStack = new Stack<Character>();
		boolean processingQuotes = false;
	
		for(int i = 0; i< query.length; i++)
		{
            StringBuffer buffer = new StringBuffer();

			
				if(query[i] == ' ')
					continue;
			
				if(query[i] == '\"')
				{
	                		buffer.append(query[i++]);
							while(i<query.length && query[i] != '\"')
							{
				                buffer.append(query[i++]);
							}
							String pushValue = buffer.toString();
							if(!pushValue.contains(":"))
							{
								pushValue = "TERM:".concat(pushValue);
							}
							queryStack.push(pushValue);
							
		                
				}
			
		
                
                
                if(query[i] != '+' && query[i] != '#' && query[i] != '*' && query[i] != '(' && query[i] != ')' && query[i]!= ' ' )
                {
					while(i<query.length && query[i] != '+' && query[i] != '#' && query[i] != '*' & query[i] != '(' && query[i] != ')' && query[i]!= ' ')
					{
		                buffer.append(query[i++]);
					}
					i--;
					String pushValue = buffer.toString();
					if(!pushValue.contains(":"))
					{
						pushValue = "TERM:".concat(pushValue);
					}
					queryStack.push(pushValue);
                }
                else if(query[i] == '(' )
                {
                	opsStack.push(query[i]);
                }
                else if(query[i] == ')'  )
                {
                	while(opsStack.peek() != '(')
                	{
                		String topVal = queryStack.pop();
                		String pushValue = queryStack.pop().concat(Character.toString(opsStack.pop())).concat(topVal);
                		queryStack.push("[".concat(pushValue).concat("]"));

                	}
                	opsStack.pop();
                }
                else if(query[i] == '+' || query[i] == '#' || query[i] == '*')
                {
                	while(!opsStack.isEmpty() && hasPrecedence(Character.toString(query[i]) , Character.toString(opsStack.peek())) )
                	{
                		if(!queryStack.isEmpty())
                		{
                		String topVal = queryStack.pop();
                		if(!queryStack.isEmpty())
                		{

                		String pushValue = queryStack.pop().concat(Character.toString(opsStack.pop())).concat(topVal);
                		
                		queryStack.push(pushValue);
                		}
                		}
                	}
                	
                	opsStack.push(query[i]);
                }		
		}
		
		while(!opsStack.isEmpty())
    	{
			
			if(!queryStack.isEmpty())
			{
			String topVal = queryStack.pop();
			if(!queryStack.isEmpty())
			{
				String pushValue = queryStack.pop().concat(Character.toString(opsStack.pop())).concat(topVal);
	    		queryStack.push(pushValue);
			}
			else
			{
	    		queryStack.push(topVal);
	    		opsStack.pop();
			}
			}
			
    	}
		
		System.out.println("{" + queryStack.pop()+"}" );
		return null;
	}
	
	
}

