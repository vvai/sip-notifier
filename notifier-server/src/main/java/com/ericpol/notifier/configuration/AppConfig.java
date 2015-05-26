package com.ericpol.notifier.configuration;

import javax.sip.SipFactory;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.ericpol.notifier.Constants;

/**
 * Created by vvai on 2/3/15.
 */
@Configuration
public class AppConfig
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private Environment itsEnv;

    /**
     * Creates sipFactory bean.
     * 
     * @return SipFactory
     */
    @Bean
    public SipFactory sipFactory()
    {
        SipFactory sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        return sipFactory;
    }

    /**
     * Creates constants bean.
     * 
     * @return Constants
     */
    @Bean
    public Constants constants()
    {
        return new Constants(itsEnv.getProperty("com.ericpol.notifier.username"),
                itsEnv.getProperty("com.ericpol.notifier.password"), itsEnv.getProperty("com.ericpol.notifier.host"),
                Integer.valueOf(itsEnv.getProperty("com.ericpol.notifier.local_port")),
                itsEnv.getProperty("com.ericpol.notifier.realm"),
                itsEnv.getProperty("com.ericpol.notifier.server_host"), Integer.valueOf(itsEnv
                        .getProperty("com.ericpol.notifier.server_port")),
                itsEnv.getProperty("com.ericpol.notifier.tag"));
    }

    /**
     * Creates quartz scheduler bean.
     * 
     * @return Scheduler
     * @throws SchedulerException if the scheduler cannot be instantiated
     */
    @Bean
    public Scheduler scheduler() throws SchedulerException
    {
        return new StdSchedulerFactory().getScheduler();
    }

}
