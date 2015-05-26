package com.ericpol.notifier.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.ericpol.notifier.schedule.job.CallFlowJob;
import com.ericpol.notifier.schedule.job.DeletePastEventsJob;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericpol.notifier.data.impl.UserDAOImpl;
import com.ericpol.notifier.model.Event;
import com.ericpol.notifier.model.User;
import com.ericpol.notifier.sip.CallFlow;
import com.ericpol.notifier.sip.CallFlowFactory;
import com.ericpol.notifier.sip.Notifier;
import com.ericpol.notifier.sip.flows.InviteCallFlow;

/**
 * Created by vvai on 3/6/15.
 */
@Component
public class JobManager
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(JobManager.class);

    @Autowired
    private UserDAOImpl userDAO;

    @Autowired
    private Scheduler itsScheduler;

    @Autowired
    private Notifier itsNotifier;

    @Autowired
    private CallFlowFactory itsCallFlowFactory;

    @PostConstruct
    private void init() throws SchedulerException
    {
        itsScheduler.start();

    }

    public void rewriteUserJobs(final User user) throws SchedulerException
    {
        // final User user = userDAO.getUser(username);
        final List<Event> userEvents = userDAO.getUserEvents(user);

        for (JobKey jobKey : itsScheduler.getJobKeys(GroupMatcher.<JobKey> groupEquals(user.getName())))
        {
            LOGGER.info("job is {}", jobKey);
            itsScheduler.deleteJob(jobKey);

        }

        for (Event eachEvent : userEvents)
        {
            LOGGER.info("add event {}", eachEvent.getUID());

            final Map<String, CallFlow> sessionsState = itsNotifier.getSessionsState();
            // put Invite job
            final InviteCallFlow inviteCallFlow =
                    itsCallFlowFactory.createInviteCallFlow(user.getName(), eachEvent.getUID());
            final String inviteCallId = inviteCallFlow.getCallId().getCallId();
            sessionsState.put(inviteCallId, inviteCallFlow);

            JobDetail inviteJobDetail =
                    JobBuilder.newJob(CallFlowJob.class).withIdentity(eachEvent.getUID(), user.getName()).build();
            inviteJobDetail.getJobDataMap().put("message", inviteCallFlow);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(eachEvent.getDate());
            calendar.add(Calendar.MINUTE, -5);
            Date triggerTime = calendar.getTime();
            LOGGER.info("trigger time is {}", triggerTime);
            // Date inviteRunTime = DateBuilder.evenMinuteDate(new Date());

            final Trigger inviteTrigger =
                    TriggerBuilder.newTrigger().withIdentity(eachEvent.getUID(), user.getName()).startAt(triggerTime)
                            .build();
            if (triggerTime.after(new Date()))
            {
                addJob(inviteJobDetail, inviteTrigger);
            }

        }

    }

    private void addJob(JobDetail aJobDetail, Trigger aTrigger) throws SchedulerException
    {
        itsScheduler.scheduleJob(aJobDetail, aTrigger);

    }

    public void addDeletePastEventsJob() throws SchedulerException
    {
        JobDetail jobDetail =
                JobBuilder.newJob(DeletePastEventsJob.class).withIdentity("delete-past", "events").build();
        final JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("user-dao", userDAO);

        final Trigger trigger =
                TriggerBuilder
                        .newTrigger()
                        .withIdentity("delete-past", "events")
                        .startAt(new Date())
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(1).repeatForever())
                        .build();

        itsScheduler.scheduleJob(jobDetail, trigger);

    }
}
