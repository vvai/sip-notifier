package com.ericpol.notifier.shedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lotus.domino.NotesException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ericpol.notifier.lotus.LotusException;
import com.ericpol.notifier.lotus.LotusNotesCalendarEntry;
import com.ericpol.notifier.lotus.LotusNotesManager;
import com.ericpol.notifier.model.Event;

/**
 * Created by vvai on 2/26/15.
 */
public class SendingEventsJob implements Job
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(SendingEventsJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        LOGGER.info("sending events job executed!");
        final String pass = (String) jobExecutionContext.getJobDetail().getJobDataMap().get("lotus-pass");
        final String server = (String) jobExecutionContext.getJobDetail().getJobDataMap().get("lotus-server");
        final String mailfile = (String) jobExecutionContext.getJobDetail().getJobDataMap().get("mailfile");

        final LotusNotesManager lotusNotesManager = new LotusNotesManager();
        lotusNotesManager.setMailFile(mailfile);
        lotusNotesManager.setServer(server);
        lotusNotesManager.setPassword(pass);
        List<Event> events = new ArrayList<Event>();
        String user = "";
        try
        {
            // lotusNotesManager.detectLotusSettings(pass);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            // calendar.add(Calendar.DATE, -30);

            lotusNotesManager.setMinStartDate(calendar.getTime());

            calendar.add(Calendar.DATE, 30 + 7);

            lotusNotesManager.setMaxEndDate(calendar.getTime());

            /*
             * LOGGER.info("MAILFILE : {}", lotusNotesManager.getMailfile()); final String mailFile =
             * lotusNotesManager.getMailfile();
             */

            user = mailfile.substring(mailfile.indexOf("\\") + 1, mailfile.indexOf("."));
            final ArrayList<LotusNotesCalendarEntry> calendarEntries = lotusNotesManager.getCalendarEntries();

            for (LotusNotesCalendarEntry eachEntry : calendarEntries)
            {
                Event eachEvent = new Event(eachEntry.getUID(), eachEntry.getSubject(), eachEntry.getStartDateTime());
                events.add(eachEvent);
                //LOGGER.info("UID :{}", eachEntry.getUID());
            }

            LOGGER.info("sending events");
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.put("http://localhost:8080/api/" + user + "/events", events);
        }
        catch (LotusException e)
        {
            e.printStackTrace();
        }
        catch (NotesException e)
        {
            e.printStackTrace();
        }
        catch (RestClientException e)
        {
            LOGGER.info("rest exception. {}", e.getMessage());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
