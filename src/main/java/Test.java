import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

public class Test {
	/** Application name. */
	private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";

	/** Directory to store user credentials. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"),
			".credentials/calendar-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart. */
	private static final List<String> SCOPES = Arrays
			.asList(CalendarScopes.CALENDAR_READONLY);

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
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		// Load client secrets.
		InputStream in = CalendarQuickstart.class
				.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
				JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow,
				new LocalServerReceiver()).authorize("user");
		System.out.println("Credentials saved to "
				+ DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Calendar client service.
	 * 
	 * @return an authorized Calendar client service
	 * @throws IOException
	 */
	public static com.google.api.services.calendar.Calendar getCalendarService()
			throws IOException {
		Credential credential = authorize();
		return new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
				APPLICATION_NAME).build();
	}

	public static void createCalendarEntry(Date date, String description,
			boolean isCreate) {
		try {
			com.google.api.services.calendar.Calendar service = getCalendarService();

			Event event = createCalendarEntryEvent(date, description, service,
					isCreate);

			if (null != event) {
				System.out.println("going to insert event:"+event);
				service.events().insert("primary", event).execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("Exception while creating the event :description:"
							+ description + ":date:" + date);
		}

	}

	private static Event createCalendarEntryEvent(Date date, String desc,
			Calendar service, boolean isCreate) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Event myEvent = new Event();
		// Set the title and description
		myEvent.setSummary(desc);
		myEvent.setDescription("I am throwing a Pi Day Party!");


		Events events;
		try {
			Date startTime = format.parse(format.format(date));
			
			events = service
					.events()
					.list("primary")
					.setTimeMin(
							new DateTime(new Date(startTime.getTime()),
									TimeZone.getTimeZone("IST")))
					.setTimeMax(
							new DateTime(new Date(startTime.getTime() + 1000),
									TimeZone.getTimeZone("IST"))).execute();

			List<Event> items = events.getItems();
			System.out.println("items:"+items.size());

			boolean isEventNeeded = true;

			for (Event event : items) {
				if (desc.equals(event.getSummary())) {
					isEventNeeded = false;
					System.out.println("Event already present");
					if (!isCreate) {
						System.out.println("going to delete:"+event.getStart()+":summary:"+event.getSummary());
						service.events().delete("primary", event.getId()).execute();
					}
				}
			}
			if (!isEventNeeded || !isCreate) {
				return null;
			}
			String dateStr = format.format(date);

			myEvent.setStart(new EventDateTime().setDate(new DateTime(dateStr)));
			myEvent.setEnd(new EventDateTime().setDate(new DateTime(dateStr)));


			EventReminder[] reminderOverrides = new EventReminder[] {
					new EventReminder().setMethod("popup").setMinutes(date.getHours() * 60), };
			Event.Reminders reminders = new Event.Reminders().setUseDefault(false)
					.setOverrides(Arrays.asList(reminderOverrides));
			myEvent.setReminders(reminders);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return myEvent;

	}

}
