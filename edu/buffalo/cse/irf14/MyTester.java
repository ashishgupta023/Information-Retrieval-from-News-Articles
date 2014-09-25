package edu.buffalo.cse.irf14;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class  MyTester {
	
	
	public MyTester() {
	}

	

	public static void main(String[] args) {
		
		Map<String , Integer> months = new HashMap<String, Integer>();
		months.put("january", 1);
		months.put("jan", 1);
		months.put("february", 2);
		months.put("feb", 2);
		months.put("march",3);
		months.put("mar", 3);
		months.put("april", 4);
		months.put("apr", 4);
		months.put("may", 5);
		months.put("june", 6);
		months.put("jun", 6);
		months.put("july", 7);
		months.put("jul", 7);
		months.put("august", 8);
		months.put("aug", 8);
		months.put("september", 9);
		months.put("sept", 9);
		months.put("october", 10);
		months.put("oct", 10);
		months.put("november", 11);
		months.put("nov",11);
		months.put("december", 12);
		months.put("dec", 12);
		
		String s[] = {"Vidya", "Balan" , "born" , "1", "January", "1978", "is", "an", "Indian actress." };
		
//		 String nfdNormalizedString = Normalizer.normalize(s, Normalizer.Form.NFD); 
//		    Pattern pattern = Pattern.compile("[\\p{InCombiningDiacriticalMarks}]");
		    //System.out.println( nfdNormalizedString.replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""));
		
		    //System.out.println(s.replaceAll("[^a-zA-Z0-9-.]", ""));
		int month = -1;
		int matchedIndex = -1;
		for(int i =0 ; i < s.length ; i++)
		{
			if(months.containsKey(s[i].toLowerCase()) )
			{
				month = months.get(s[i].toLowerCase());
				matchedIndex = i;
			}
			System.out.println(i);
		}
		
		if(s[matchedIndex-1].matches("^[0-9]{1,2}$") && s[matchedIndex+1].matches("^[0-9]{1,4}$"))
		{
			if(Integer.parseInt(s[matchedIndex-1]) < 31 && ( Integer.parseInt(s[matchedIndex+1])) < 2014)
			{
				SimpleDateFormat matchFormat = new SimpleDateFormat("dd mm yyyy");
				SimpleDateFormat outputFormat = new SimpleDateFormat("HH mm ss");

				//matchFormat.setLenient(false);
				try {
					Date date = matchFormat.parse(s[matchedIndex-1]+ "  " +  month + " " + s[matchedIndex+1]);
					//System.out.println(outputFormat.format(date));

					Calendar d = new GregorianCalendar(Integer.parseInt("84"), Calendar.FEBRUARY ,Integer.parseInt("1"));
					d.set(Calendar.ERA, GregorianCalendar.BC);
					d.set(Calendar.HOUR, 12);
					d.set(Calendar.MINUTE, 56);
					d.set(Calendar.SECOND, 54);
					String dateToken = outputFormat.format(d.getTime());
					System.out.println(dateToken);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			}
		}
		    
		   
		/*    
		String str = "nа̀ра";
		for (char ch : str.toCharArray()) {
			System.out.print((int) ch + " ");
		}
		System.out.println();
		
		String str1 = "napa";
		for (char ch : str1.toCharArray()) {
			System.out.print((int) ch + " ");
		}
		System.out.println();
		System.out.println(str.equals(str1));*/

}
	
	
}

