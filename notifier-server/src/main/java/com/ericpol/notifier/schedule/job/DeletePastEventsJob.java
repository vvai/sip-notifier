package com.ericpol.notifier.schedule.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericpol.notifier.data.UserDAO;

/**
 * Created by vvai on 3/6/15.
 */
public class DeletePastEventsJob implements Job
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(DeletePastEventsJob.class);

    @Override
    public final void execute(final JobExecutionContext aJobExecutionContext) throws JobExecutionException
    {

        LOGGER.info("delete past events job");
        final JobDataMap jobDataMap = aJobExecutionContext.getJobDetail().getJobDataMap();
        final UserDAO userDAO = (UserDAO) jobDataMap.get("user-dao");
        userDAO.deletePastEvents();
    }
}
