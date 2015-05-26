package com.ericpol.notifier;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;

import lotus.domino.NotesException;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ericpol.notifier.lotus.LotusException;
import com.ericpol.notifier.lotus.LotusNotesManager;
import com.ericpol.notifier.shedule.SendingEventsJob;

@SpringBootApplication
public class Application
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Scheduler itsScheduler;

    @Autowired
    private Constants itsConstants;

    public static void main(final String[] anArgs) throws Exception
    {
        SpringApplication.run(Application.class, anArgs);
    }

    @PostConstruct
    private void initApplication() throws SchedulerException, java.text.ParseException, LotusException,
            NotesException, IOException
    {
        LOGGER.debug("init application");

        LOGGER.info("server is {}:{}", itsConstants.getServerHost(), itsConstants.getServerPort());
        LOGGER.info("period is {}", itsConstants.getPeriod());

        itsScheduler.start();

        final LotusNotesManager lotusNotesManager = detectLotusSettings();

        // make job
        JobDetail jobDetail = JobBuilder.newJob(SendingEventsJob.class).withIdentity("sending", "events").build();
        final JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("lotus-pass", itsConstants.getLotusPass());
        jobDataMap.put("mailfile", lotusNotesManager.getMailfile());
        jobDataMap.put("lotus-server", lotusNotesManager.getServer());

        final Trigger trigger =
                TriggerBuilder
                        .newTrigger()
                        .withIdentity("sending", "events")
                        .startAt(new Date())
                        .withSchedule(
                                SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(20).repeatForever())
                        .build();

        itsScheduler.scheduleJob(jobDetail, trigger);
    }

    private LotusNotesManager detectLotusSettings() throws NotesException, LotusException
    {
        final LotusNotesManager lotusNotesManager = new LotusNotesManager();
        lotusNotesManager.detectLotusSettings(itsConstants.getLotusPass());
        return lotusNotesManager;
    }

    /*
     * private void invokeLotusMethod() throws NotesException, IOException, LotusException { LotusNotesManager
     * lotusNotesManager = new LotusNotesManager(); Calendar now = Calendar.getInstance(); now.add(Calendar.DATE, 30 *
     * -1); // Clear out the time portion now.set(Calendar.HOUR_OF_DAY, 0); now.set(Calendar.MINUTE, 0);
     * now.set(Calendar.SECOND, 0); Date startDate = now.getTime(); now = Calendar.getInstance(); now.add(Calendar.DATE,
     * 30); // Set the time portion now.set(Calendar.HOUR_OF_DAY, 23); now.set(Calendar.MINUTE, 59);
     * now.set(Calendar.SECOND, 59); Date endDate = now.getTime(); // Date startDate = call.getTime();
     * LOGGER.info("start date is: {}", startDate); LOGGER.info("end date is: {}", endDate);
     * lotusNotesManager.setServer("LN1-BRS1"); // lotusNotesManager.setServerDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
     * lotusNotesManager.setServerDateFormat("Detect"); lotusNotesManager.setMailFile("mail file");
     * lotusNotesManager.setPassword("my pass"); lotusNotesManager.setMinStartDate(startDate);
     * lotusNotesManager.setMaxEndDate(endDate); lotusNotesManager.setDiagnosticMode(false);
     * lotusNotesManager.setRequiresAuth(true); LOGGER.info("before get calendar entries"); final
     * ArrayList<LotusNotesCalendarEntry> calendarEntries = lotusNotesManager.getCalendarEntries(); for
     * (LotusNotesCalendarEntry eachCalendarEntry : calendarEntries) {
     * LOGGER.info("entry subject: {}, startDate: {}, endDate: {}", eachCalendarEntry.getSubject(),
     * eachCalendarEntry.getStartDateTime(), eachCalendarEntry.getEndDateTime()); }
     * LOGGER.info("after get calendar entries, size: {}", calendarEntries.size()); }
     */

}
