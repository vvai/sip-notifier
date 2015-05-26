package com.ericpol.notifier.configuration;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.ericpol.notifier.Constants;

/**
 * Created by vvai on 2/3/15.
 */
@Configuration
@PropertySource("file:app.properties")
public class AppConfig
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private Environment itsEnv;

    @Bean
    public Constants constants()
    {
        int period = 1;
        if (itsEnv.getProperty("com.ericpol.notifier.client.period") != null)
        {
            period = Integer.valueOf(itsEnv.getProperty("com.ericpol.notifier.client.period"));
        }
        return new Constants(itsEnv.getProperty("com.ericpol.notifier.client.server_host"), Integer.valueOf(itsEnv
                .getProperty("com.ericpol.notifier.client.server_port")),
                itsEnv.getProperty("com.ericpol.notifier.client.lotus_pass"), period);
    }

    @Bean
    public Scheduler scheduler() throws SchedulerException
    {
        return new StdSchedulerFactory().getScheduler();
    }

}
