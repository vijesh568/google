import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlParse implements ContentHandler {
	
	private static XmlParse parser = new XmlParse();
	
	private static Date date = null;
	
	public static void main(String[] args) {
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(parser);
			reader.parse("./details.xml");
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("$$$$$$$$$$$$$$$ COMPLETE $$$$$$$$$$$$$");
		try {
			(new InputStreamReader(System.in)).read();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	int[] LEAP_YEAR = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	int[] NON_LEAP_YEAR = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	int LEAP_DAYS = 366;
	int NON_LEAP_DAYS = 365;
	long DAY = 1000*60*60*24;

	public Date getDate(Date startDate, long offset) {
		int day = startDate.getDate();
		int month = startDate.getMonth();
		int year = startDate.getYear();
		while (true) {
			if (offset > 0) {
				int currentYear = isLeapYear(year) ? LEAP_DAYS : NON_LEAP_DAYS;
				int[] monthArray = isLeapYear(year) ? LEAP_YEAR : NON_LEAP_YEAR;
				int currentMonth = monthArray[month];
				if (offset >= currentYear) {
					year += 1;
					offset -= currentYear;
				} else if (offset >= currentMonth) {
					if (month == 11) {
						month = 0;
						year += 1;
					} else {
						month++;
					}
					offset -= currentMonth;
				} else if ((day + offset) > currentMonth) {
					if (month == 11) {
						month = 0;
						year += 1;
					} else {
						month++;
					}
					day = day + (int) offset - currentMonth;
					offset = 0;
				} else {
					day += offset;
					offset = 0;
				}
			} else if (offset < 0) {
				int currentYear = isLeapYear(year - 1) ? LEAP_DAYS
						: NON_LEAP_DAYS;
				int[] monthArray = isLeapYear(year) ? LEAP_YEAR : NON_LEAP_YEAR;
				int currentMonth = monthArray[(month == 0) ? 11 : (month - 1)];
				offset *= (-1);
				if (offset >= currentYear) {
					year -= 1;
					offset -= currentYear;
				} else if (offset >= currentMonth) {
					if (month == 0) {
						month = 11;
						year -= 1;
					} else {
						month--;
					}
					offset -= currentMonth;
				} else if ((day - offset) <= 0) {
					if (month == 0) {
						month = 11;
						year -= 1;
					} else {
						month--;
					}
					day = day - (int) offset + currentMonth;
					offset = 0;
				} else {
					day -= offset;
					offset = 0;
				}
				offset *= (-1);
			} else {
				break;
			}
		}

		return new Date(year, month, day);
	}
	
	public int getOffset(Date startDate, Date endDate) {
		
		int offset = 0;
		Date beginDate = startDate;
		Date lastDate = endDate;
		if (startDate.after(endDate)) {
			beginDate = endDate;
			lastDate = startDate;
		}
		
		long diff = lastDate.getTime() - beginDate.getTime();
		offset = (int)(diff/DAY);
		
		System.out.println("Offset for startdate:"+startDate +":and enddate:"+endDate+":is:"+offset);
		return offset;
	}
	
	
	
	public boolean isLeapYear(int  year) {
		if (year%4 == 0 && (year%100 !=0 || year%400 ==0)) {
			return true;
		}
		return false;
	}
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		
	}

	public void endDocument() throws SAXException {
		
	}
	
	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		
	}
	public void endPrefixMapping(String arg0) throws SAXException {
		
	}
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		
	}
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
	}
	public void setDocumentLocator(Locator arg0) {
		
	}
	public void skippedEntity(String arg0) throws SAXException {
		
	}
	public void startDocument() throws SAXException {
		
	}
	static String username = null;
	static String password = null;
	static boolean create = true;
	static int hours = 0;
	static int minutes = 0;
	static Date refDate = null;
	static int refDay = -1;
	public void startElement(String arg0, String name, String arg2,
			Attributes attribute) throws SAXException {
		int length = attribute.getLength();
		System.out.println("start element:name:"+name+":attribute:"+attribute.getLength());
		if (name == "date") {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				date = format.parse(attribute.getValue(attribute.getIndex("value")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (name=="service") {
			for (int j = 0; j < length; j++) {
				int offset = Integer.parseInt(attribute.getValue(j));
				Date futureDate = getDate(date, offset);
				futureDate.setHours(hours);
				futureDate.setMinutes(minutes);
				String desc = attribute.getQName(j);
				System.out.println(":"+desc+":"+futureDate);
				Test.createCalendarEntry(futureDate, desc, create);
				
			}
		} else if (name == "train") {

			for (int j = 0; j < length; j++) {
				int diff = getOffset(refDate, date);
				int day = (diff%7+refDay)%7;
				System.out.println("Day of date:"+date+":is:"+day);
				
				ArrayList dateArray = new ArrayList();

				if (day == 0) {
					/*monday*/
					dateArray.add(getDate(date, -3));
					dateArray.add(date);
				} else if (day == 1) {
					/* tuesday*/
					dateArray.add(getDate(date, -4));
					dateArray.add(date);
				} else if (day ==2) {
					/*wednesday*/
					dateArray.add(getDate(date, -5));
					dateArray.add(date);
					dateArray.add(getDate(date, -1));
					dateArray.add(getDate(date, 4));
				} else if (day == 3) {
					/*thursday*/
					dateArray.add(getDate(date, -1));
					dateArray.add(getDate(date, 3));
				} else if (day == 4) {
					/* friday*/
					dateArray.add(getDate(date, -1));
					dateArray.add(getDate(date, 2));
				}
				for (int i = 0; i < (dateArray.size() / 2); i++) {
					Date firstDate = (Date) dateArray.get(i*2);
					Date lastDate = (Date) dateArray.get(i*2+1);
					System.out.println(":TO DATE:"+firstDate);
					System.out.println(":FRO DATE:"+lastDate);
					
					int offset = Integer.parseInt(attribute.getValue(j));
					Date futureDate = getDate(firstDate, offset);
					futureDate.setHours(hours);
					futureDate.setMinutes(minutes);
					String desc = attribute.getQName(j)+"TO TICKET";
					System.out.println(":" + desc + ":" + futureDate);
					 Test.createCalendarEntry(futureDate, desc, create);

					offset = Integer.parseInt(attribute.getValue(j));
					futureDate = getDate(lastDate, offset);
					futureDate.setHours(hours);
					futureDate.setMinutes(minutes);
					desc = attribute.getQName(j)+"FRO TICKET";
					System.out.println(":" + desc + ":" + futureDate);
					 Test.createCalendarEntry(futureDate, desc, create);
				}				
				
			}
		
		} else if (name == "credentials") {
			username = attribute.getValue(attribute.getIndex("username"));
			password = attribute.getValue(attribute.getIndex("password"));
			create = attribute.getValue(attribute.getIndex("action")).equals("create");
		} else if(name == "time") {
			hours = Integer.parseInt(attribute.getValue(attribute.getIndex("hours")));
			minutes = Integer.parseInt(attribute.getValue(attribute.getIndex("minutes")));
		} else if (name == "reference") {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				refDate = format.parse(attribute.getValue(attribute.getIndex("date")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			refDay = Integer.parseInt(attribute.getValue(attribute.getIndex("day")));
			System.out.println("Reference date:"+refDate+":ref day:"+refDay);
		}
		
	}
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
	}

}

