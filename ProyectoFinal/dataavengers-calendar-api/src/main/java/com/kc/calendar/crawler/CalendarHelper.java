package com.kc.calendar.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kc.calendar.model.dto.CalendarEventDto;
import com.kc.calendar.model.dto.CalendarWeekEventsDto;
import com.kc.calendar.model.dto.WeekDto;
import com.kc.calendar.model.entities.CalendarEntity;
import com.kc.calendar.model.repositories.CalendarRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CalendarHelper {

	private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	private File file;
	
	@Value("${api.file.export.path}")
	private String filePath;

	@Value("${api.calendar.date.output}")
	private String apiDateFormatOutput;
	
	@Value("${api.calendar.date.from}")
	private String apiCalendarFrom;
	
	@Value("${api.calendar.date.to}")
	private String apiCalendarTo;
		
	@Value("${app.enable.toDatabase}")
	private Boolean enabledToDatabase;
	
	@Value("${app.enable.toFile}")
	private Boolean enabledToFile;
	
	@Value("${app.enable.toKafka}")
	private Boolean enabledToKafka;
	
	@Autowired
	private CalendarRepository calendarRepository;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@PostConstruct
	public void setUp() {
		file = new File(filePath);
	}
	
	public LinkedList<WeekDto> getWeeks() {
		
		LinkedList<WeekDto> weeks = new LinkedList<WeekDto>();
		
		String[] fromParts = apiCalendarFrom.split("-");
		String[] toParts = apiCalendarTo.split("-");
				
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MMdd");
		Period weekPeriod = new Period().withWeeks(1);
		DateTime startDate = new DateTime(
				Integer.parseInt(fromParts[2]), 
				Integer.parseInt(fromParts[1]), 
				Integer.parseInt(fromParts[0]), 0, 0, 0, 0 );
		while(startDate.getDayOfWeek() != DateTimeConstants.SUNDAY) {
		    startDate = startDate.minusDays(1);
		}
		
		DateTime endDate = new DateTime(
				Integer.parseInt(toParts[2]), 
				Integer.parseInt(toParts[1]), 
				Integer.parseInt(toParts[0]), 0, 0, 0, 0 );
		Interval i = new Interval(startDate, weekPeriod );
		while(i.getEnd().isBefore( endDate)) {
			
			WeekDto dto = WeekDto.builder()
				.weekOfYear(i.getStart().getWeekOfWeekyear())
				.startDate(i.getStart().toDate())
				.startDateFormatted(df.format( i.getStart().toDate() ))
				.endDate(i.getEnd().minusMillis(1).toDate())
				.endDateFormatted(df.format( i.getEnd().minusMillis(1).toDate()))
				.startYear(i.getStart().getYear())
				.endYear(i.getEnd().getYear())
				.build();
			
			weeks.add(dto);

		    i = new Interval(i.getStart().plus(weekPeriod), weekPeriod);
		}  
		
		return weeks;
	}
	
	public LinkedList<CalendarEventDto> buildDayEvents(Element table, String parentId, int pos) {
		
		LinkedList<CalendarEventDto> events = new LinkedList<>();
		
		Elements rows = table.select("tr.jsdfx-searched-item.event");
		
		String rawDate = table.selectFirst("div").text().trim();
		
		int acc = 0;
		for(Element row: rows) {
			
			Elements tds = row.getAllElements();

			String hour      = tds.get(1).text().trim();
			String currency  = tds.get(3).selectFirst("div").attr("data-filter").toUpperCase().trim();
			
			String event;
			if(!StringUtils.isEmpty(hour)) {
				event = tds.get(5).text().substring(tds.get(5).text().indexOf(' ') + 1).trim();
			} else {
				event = tds.get(5).text().trim();
			}
			
			String importance = tds.get(9).text().trim();
			String actual 	  = tds.get(11).text().trim();
			String forecast   = tds.get(12).text().trim();
			String previous   = tds.get(13).text().trim();
			
			events.add(buildCalendarEvent(parentId, pos, acc, hour, currency, event, importance, actual, forecast, previous, rawDate));	
			acc++;
		}
		
		return events;
	}
	
	public CalendarEventDto buildCalendarEvent(String parent, int pos, int acc, String hour, String currency, String event, String importance, String actual, 
			String forecast, String previous, String rawDate) {
		
		String formattedDate = formatDate(rawDate);
		log.debug("Building: hour<{}>, currency<{}>, event<{}>, importance<{}>, actual<{}>, "
				   + "forecast<{}>, previous<{}>, rawDate<{}>, formattedDate<{}>", 
				    hour, currency, event, importance, actual, 
				    forecast, previous, rawDate, formattedDate);
		
		String id = parent + "_" + pos + "_" + acc;
		
		return CalendarEventDto.builder()
				.id(id)
				.eventDateFormatted(formattedDate)
				.rawEventDate(rawDate)
				.hour(hour)
				.currency(currency)
				.event(event)
				.importance(importance)
				.actual(actual)
				.forecast(forecast)
				.previous(previous).build();
	}
	
	private String formatDate(String calendarDate) {

		SimpleDateFormat sdfSource = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH);
		SimpleDateFormat sdfTarget = new SimpleDateFormat(apiDateFormatOutput, Locale.ENGLISH);

		try {
			Date date = sdfSource.parse(calendarDate);
			return sdfTarget.format(date);
		} catch (ParseException e) {
			return calendarDate;
		}
	}
		
	public String parse(CalendarWeekEventsDto dto) {
		try {
			return CalendarHelper.JSON_MAPPER.writeValueAsString(dto);		
		} catch (Exception e) {
			log.error("Error parsing json: {}", e.getMessage());
		}
		return "";
	}
	
	public boolean toFile(CalendarWeekEventsDto dto, String json) {
		
		boolean saved = false;
		if(enabledToFile) {
			try {
						
				if(!findInFile(dto.getId())) {
					
					try {
						FileUtils.writeStringToFile(file, json+ "\r\n", StandardCharsets.UTF_8, true);
						saved = true;
					} catch (IOException e) {
						log.error("Error writing to file: {}", e.getMessage());
					}
					
				} else {
					log.debug("Repeated in File! {}", dto.getId());
				}
			} catch (Exception e) {
				log.error("Error saving to file: {}", e.getMessage());
			}			
		}
		return saved;
	}
	
	private boolean findInFile(String id) throws Exception {
		
		String currentLine = null;

		try {

			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((currentLine = br.readLine()) != null) {
				if(currentLine.contains(id)) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}	
	
	public boolean toDatabase(CalendarWeekEventsDto dto, String json) {
		
		boolean stored = false;
		if(enabledToDatabase) {
			try {
				String databaseId = dto.getId();
				
				Optional<CalendarEntity> found = calendarRepository.findById(databaseId);						
				if(!found.isPresent()) {
					
					CalendarEntity entity = new CalendarEntity(
							databaseId,
							json);
				
					calendarRepository.save(entity);
					stored = true;
					
				} else {
					log.debug("Repeated in DB! {}", databaseId);
				}
				
			} catch (Exception e) {
				log.error("Error saving to db: {}", e.getMessage());
			}		
		}	
		return stored;
	}
	
	public boolean shouldSendToKafka(boolean stored, boolean saved) {
		
		if(!enabledToFile && !enabledToDatabase && enabledToKafka) {
			return true;
		} else if((enabledToFile || enabledToDatabase) && (stored || saved) && enabledToKafka)  {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean toKafka(String json) {
		
		if(enabledToKafka) {
			kafkaTemplate.sendDefault(json);
			return true;
		} else {
			return false;
		}
	    
	}

}