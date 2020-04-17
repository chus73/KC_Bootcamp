package com.kc.calendar.tasks;

import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kc.calendar.crawler.CalendarHelper;
import com.kc.calendar.model.dto.CalendarEventDto;
import com.kc.calendar.model.dto.CalendarWeekEventsDto;
import com.kc.calendar.model.dto.WeekDto;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CalendarTasks {

	@Autowired
	private CalendarHelper calendarHelper;
	
	@Value("${api.calendar.url}")
	private String apiCalendarUrl;

	@Scheduled(initialDelay = 10000, fixedRateString = "${api.calendar.scheduled.rate}")
	public void search() {

		LinkedList<WeekDto> weeks = calendarHelper.getWeeks();		
		log.info("Detected weeks: {}", weeks.size());
		
		for(WeekDto week : weeks) {
	
			try {
				
				String url = apiCalendarUrl + week.getStartDateFormatted();
				Document doc = Jsoup.connect(url).get();				
				Elements tables = doc.select("table.dfx-calendar-table.tab-pane");
								
				LinkedList<CalendarEventDto> weekEvents = new LinkedList<>();
				
				String parentId = week.getStartYear() + "_" + week.getEndYear() + "_" + week.getWeekOfYear();
								
				for(int i=0; i < tables.size(); i++) {
					
					Element table = tables.get(i);
					LinkedList<CalendarEventDto> dayEvents = calendarHelper.buildDayEvents(table, parentId, i);
					log.info("Day events: {}", dayEvents.size());
					weekEvents.addAll(dayEvents);
				}
				
				/*
				for(Element table: tables) {
					LinkedList<CalendarEventDto> dayEvents = calendarHelper.buildDayEvents(table);
					log.info("Day events: {}", dayEvents.size());
					weekEvents.addAll(dayEvents);
				}				
				*/
				
				
				log.info("Week {} events: {}", parentId, weekEvents.size());				
				CalendarWeekEventsDto weekEventsDto = 
						CalendarWeekEventsDto.builder()
						.id(parentId)
						.startDateFormatted(week.getStartDateFormatted())
						.endDateFormatted(week.getEndDateFormatted())
						.events(weekEvents)
						.build();
				
				sendAction(weekEventsDto);
							
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Error {}", e.getMessage());
			}			
			
		}
	}
	
	private void sendAction(CalendarWeekEventsDto weekEvents) {
		
		log.info(" --------- start sendAction() ----------- ");
		log.info(" --------- {} ----------- ", weekEvents.getId());
		
		try {
			
			String json = calendarHelper.parse(weekEvents);					
			boolean stored = calendarHelper.toDatabase(weekEvents, json);					
			boolean saved  = calendarHelper.toFile(weekEvents, json);		
			
			boolean sent = false;
			if(calendarHelper.shouldSendToKafka(stored, saved)) {
				sent  = calendarHelper.toKafka(json);
			}
			
			log.debug("Stored in DB: {}", stored);
			log.debug("Saved in file: {}", saved);
			log.debug("Sent to Kafka: {}", sent);		
			
		} catch (Exception e) {
			log.error("Error sending : {}", e.getMessage());
		}
		
		log.info(" --------- end sendAction() ----------- ");
	}
}
