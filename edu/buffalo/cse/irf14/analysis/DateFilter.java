package edu.buffalo.cse.irf14.analysis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sun.org.apache.regexp.internal.recompile;
import com.sun.xml.internal.ws.api.pipe.NextAction;


public class DateFilter extends TokenFilter {
	String matchedMonth;
	String matchedDate;
	String matchedYear;
	String matchedHour;
	String matchedMin;
	String matchedSecond;
	String matchedEra;
	String punctReq;
	String  timeOfDay;
	
	public DateFilter(TokenStream stream) {
		super(stream);
		matchedMonth = null;
		matchedDate = null;
		matchedHour = null;
		matchedMin = null;
		matchedYear = null;
		matchedSecond = null;
		punctReq = null;
	}
	
	
	public void nulledDate()
	{
		matchedMonth = null;
		matchedDate = null;
		matchedHour = null;
		matchedMin = null;
		matchedYear = null;
		matchedSecond = null;
		punctReq = null;
	}
	
	
	public void searchTime(Token token) 
	{
		Token testToken = token;
		String probableTime = testToken.getTermText().toLowerCase();
		if(probableTime.matches("\\.*[0-9]{1,2}[:][0-9]{1,2}.*"))
		{
			
			String[] time =  probableTime.replaceAll("[a-zA-Z]*[.]*", "").split(":");
			matchedHour = time[0];
			matchedMin = time[1];
			if(time.length == 3)
			{
				matchedSecond = time[2];
			}
			if(probableTime.contains(","))
			{
				punctReq = ",";
			}
			else if(probableTime.contains("."))
			{
				punctReq = ".";
			}
			if(probableTime.replaceAll("[.]*", "").matches(".*am"))
			{
				timeOfDay = "am";
			}
			else 
			{
				timeOfDay = "pm";
			}
		}

		
		if(matchedHour != null)
		{
			if(filterStream.hasNext())
				testToken = filterStream.next();
			
			if(testToken.getTermText().toLowerCase().replaceAll("[.]*", "").matches("am|pm"))
			{
				if(testToken.getTermText().toLowerCase().contains(","))
				{
					punctReq = ",";
				}
				else if(testToken.getTermText().toLowerCase().contains("."))
				{
					punctReq = ".";
				}
				timeOfDay = testToken.getTermText().toLowerCase().replaceAll("[.]*", "");
				filterStream.remove();

			}
		
			
		}
		
		
	}
	public void searchMonth(Token token)
	{
		Token testToken = token;

		if(months.containsKey(testToken.getTermText().toLowerCase().replaceAll(",", "")) )
		{
			matchedMonth = months.get(testToken.getTermText().toLowerCase().replaceAll(",", "")).toString();
		}
	}
	
	public void searchDate(Token token)
	{
		Token testToken = token;
		if(testToken.getTermText().toLowerCase().matches("^[0-9]{1,2}[,]{0,1}$"))
		{
			if(Integer.parseInt(testToken.getTermText().toLowerCase().replaceAll(",", "")) <= 31)
			{
				matchedDate = testToken.getTermText().toLowerCase().replace(",", "");
			}
		}
	}
	
	public void searchYear(Token token)
	{
		Token testToken = token;
		String probableYear = testToken.getTermText().toLowerCase();
		if(probableYear.matches("^[0-9]{2,4}[.,]{0,1}$"))
		{
			if(probableYear.contains(","))
			{
				punctReq = ",";
			}
			else if(probableYear.contains("."))
			{
				punctReq = ".";
			}
			
			matchedYear = probableYear.replaceAll("[,.]*", "");

		}
		else if(probableYear.matches("^[0-9]{2,4}[.,]{0,1}[ad|bc].*$"))
		{
			if(probableYear.contains(","))
			{
				punctReq = ",";
			}
			else if(probableYear.contains("."))
			{
				punctReq = ".";
			}
			
			if(probableYear.replaceAll("[.]", "").contains("bc"))
			{
				matchedEra = testToken.getTermText().replaceAll("[.]", "");
				
			}
			
			if(probableYear.replaceAll("[.]", "").contains("ad"))
			{
				matchedEra = testToken.getTermText().replaceAll("[.]", "");
			}

			matchedYear = probableYear.replaceAll("[a-z]*[.]*", "");

		}
	}
	
	public void searchEra(Token token)
	{
		Token testToken = token;
		if(testToken.getTermText().toLowerCase().replaceAll("[.]", "").contains("bc"))
		{
			matchedEra = testToken.getTermText().replaceAll("[.]", "");
			
		}
		
		if(testToken.getTermText().toLowerCase().replaceAll("[.]", "").contains("ad"))
		{
			matchedEra = testToken.getTermText().replaceAll("[.]", "");
		}

	}
	
	public void createDateToken(Token token)
	{
		Token testToken = token;
		String dateToken = null;
		if(matchedDate == null)
		{
			matchedDate = "01";
		}
		if(matchedMonth == null)
		{
			matchedMonth = "01";
		}
		
		if(matchedYear == null)
		{
			matchedYear = "1900";
		}
		

		if(matchedHour == null)
		{
			matchedHour = "00";
			matchedMin = "00";
			matchedSecond = "00";
		}

		
		Calendar d = new GregorianCalendar(Integer.parseInt(matchedYear), Integer.parseInt(matchedMonth) - 1 ,Integer.parseInt(matchedDate));
		SimpleDateFormat outputFormat  = new SimpleDateFormat("yyyyMMdd");
		if(!matchedHour.equals("00") )
		{
			if(timeOfDay != null)
			{
				if(timeOfDay.toLowerCase().equals("am"))
				{
					d.set(Calendar.AM_PM, Calendar.AM);
					
				}
				else if(timeOfDay.toLowerCase().equals("pm"))
				{
					d.set(Calendar.AM_PM, Calendar.PM);
	
				}
			}
			outputFormat = new SimpleDateFormat("HH:mm:ss");
			d.set(Calendar.HOUR,Integer.parseInt(matchedHour));
			d.set(Calendar.MINUTE,Integer.parseInt(matchedMin));
			
			if(matchedSecond != null)
				d.set(Calendar.SECOND,Integer.parseInt(matchedSecond));
			dateToken =   punctReq == null ? outputFormat.format(d.getTime()) : outputFormat.format(d.getTime()).concat(punctReq);
		}
		
		if(matchedHour.equals("00"))
		{
			dateToken =   punctReq == null ? outputFormat.format(d.getTime()) : outputFormat.format(d.getTime()).concat(punctReq);
			if(matchedEra!= null && matchedEra.toLowerCase().equals("bc"))
			{
				dateToken = "-"+dateToken;
			}
		}
		testToken.setTermText(dateToken);
		testToken.setTermBuffer(dateToken.toCharArray());
		testToken.setIsDate(true);
	}
	
	public void checkDatePattern(Token token)
	{
		if(token.getTermText().toLowerCase().trim().matches("^[0-9]{1,2}[./-][0-9]{1,2}[./-](19|20)\\d\\d$"))
		{
			String[] probableDate = token.getTermText().trim().split("[\\.-]");
			matchedYear = probableDate[2];
			matchedDate = probableDate[1];
			matchedMonth = probableDate[0];
		}
	}
	
	@Override
	public boolean increment() throws TokenizerException {
		try
		{
		
		if(filterStream.hasNext())
		{
			currToken = filterStream.next();

			
			searchTime(currToken);
			if(matchedHour != null)
			{
				createDateToken(currToken);
				nulledDate();
				return filterStream.hasNext();

			}

			if(matchedYear == null)
			{
				checkDatePattern(currToken);
				if(matchedYear != null)
				{
					createDateToken(currToken);
					nulledDate();
					return filterStream.hasNext();

				}
			}
			
			
			//December 1, 1948
			if(matchedMonth ==  null || matchedMonth.isEmpty() )
			{
				searchMonth(currToken);
				if(matchedMonth != null)
				{
					if(filterStream.hasNext())
					{
						
						currToken = filterStream.next();
						searchDate(currToken);
						if(matchedDate == null)
							currToken = filterStream.previous();
							
					}
					
					if(matchedDate != null)
					{

						filterStream.remove();

						if(filterStream.hasNext())
						{
							currToken = filterStream.next();
							searchYear(currToken);
						
						
							if(matchedYear != null)
							{
								filterStream.remove();
							}
							
							currToken = filterStream.previous();
						}
						
						if(matchedYear == null)
							currToken = filterStream.previous();
						
						createDateToken(currToken);


					}
					nulledDate();

					return filterStream.hasNext();
				}
			}
			
			//1 December, 1948
			if(matchedDate == null )
			{
				
				searchDate(currToken);
				if(matchedDate != null)
				{
					filterStream.remove();
					if(filterStream.hasNext())
					{
						currToken = filterStream.next();
						searchMonth(currToken);
					}
					
					if(matchedMonth != null)
					{
						filterStream.remove();

						if(filterStream.hasNext())
						{
							currToken = filterStream.next();
							searchYear(currToken);
							createDateToken(currToken);
						}
					}
					nulledDate();

					return filterStream.hasNext();
				}
			}
			
		
			
			
			//1948 , 84 BC or AD
			if(matchedYear == null)
			{
				searchYear(currToken);
				if(matchedYear != null)
				{
					if(filterStream.hasNext())
					{
						currToken = filterStream.next();
						searchEra(currToken);
					
						if(matchedEra != null)
						{
							filterStream.remove();
						}
						
						currToken =  filterStream.previous();

					}
					if(matchedEra == null)
						currToken = filterStream.previous();
					createDateToken(currToken);
					nulledDate();
					return filterStream.hasNext();


				}
			}
				
			}
		}
		catch(Exception e)
		{
		}
		
		return filterStream.hasNext();

	}

	@Override
	public TokenStream getStream() {
		
		return filterStream;
	}

}
