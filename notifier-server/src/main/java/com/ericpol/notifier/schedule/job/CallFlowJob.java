package com.ericpol.notifier.schedule.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericpol.notifier.sip.CallFlow;

/**
 * Created by vvai on 2/25/15.
 */
public class CallFlowJob implements Job
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(CallFlowJob.class);

    @Override
    public final void execute(final JobExecutionContext aJobExecutionContext) throws JobExecutionException
    {

        LOGGER.info("schedule call flow job");
        final JobDataMap jobDataMap = aJobExecutionContext.getJobDetail().getJobDataMap();
        final CallFlow message = (CallFlow) jobDataMap.get("message");
        LOGGER.info("job key is {}", aJobExecutionContext.getJobDetail().getKey());
        message.run();
    }

}
