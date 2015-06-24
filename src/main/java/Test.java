import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.meta.When;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class Test {
	/** Application name. */
    private static final String APPLICATION_NAME =
        "Google Calendar API Java Quickstart";

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/calendar-api-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            CalendarQuickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
        getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

	
	public static void createCalendarEntry(Date date, String description,
			boolean isCreate) {
		try {
			com.google.api.services.calendar.Calendar service = getCalendarService();
			
			Event event = createCalendarEntryEvent(date, description, service, isCreate);
			
			if (null != event) {
				try {
					CalendarEventEntry insertedEntry = service.insert(postURL,
							event);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			System.out
					.println("Exception while creating the event :description:"
							+ description + ":date:" + date);
		}
		

	}
	private static Event createCalendarEntryEvent(Date date,
			String desc, Calendar service, boolean isCreate) {
		

		Event myEvent = new Event();
		// Set the title and description
		myEvent.setSummary(desc);
		myEvent.setDescription("I am throwing a Pi Day Party!");

		// Create DateTime events and create a When object to hold them, then
		// add
		// the When event to the event
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
//		Date dat1 = null;
//		Date dat2 = null;
//		try {
//			dat1 = format.parse(date);
//			dat2 = format.parse(date);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		DateTime startTime = new DateTime(date, TimeZone.getTimeZone("IST"));
		System.out.println("start date:" + startTime);
		DateTime endTime = new DateTime(date, TimeZone.getTimeZone("IST"));
		System.out.println("endTime:" + endTime);
		
		Events events = service.events()
				.list("primary")
				.setTimeMin(
						new DateTime(new Date(startTime.getValue() - 10),
								TimeZone.getTimeZone("IST")))
				.setTimeMax(
						new DateTime(new Date(startTime.getValue() + 1000),
								TimeZone.getTimeZone("IST"))).execute();
		

		List<Event> items = events.getItems();
		
		
		for (Event event : items) {
			System.out.println("event at["+i+"]:"+event.getTitle().getPlainText());
			if (desc.equals(event.get) {
				isEventNeeded = false;
				if(!isCreate) {
					try {
						event.delete();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ServiceException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (!isEventNeeded) {
			return null;
		}
		
		// DateTime startTime =
		// DateTime.parseDateTime("2012-11-28T12:41:00-08:00");
		// DateTime endTime =
		// DateTime.parseDateTime("2012-11-28T12:42:00-08:00");
		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEvent.addTime(eventTimes);
		Reminder reminder = new Reminder();
		reminder.setMethod(Reminder.Method.SMS);
		reminder.setDays(new Integer(0));
		reminder.setHours(new Integer(0));
		reminder.setMinutes(new Integer(0));
		reminder.setAbsoluteTime(startTime);

		myEvent.getReminder().add(reminder);
		// myEvent.addExtension(reminder);
		System.out.println("days:" + reminder.getDays() + ":min:"
				+ reminder.getHours());

		return myEvent;

	}

}
