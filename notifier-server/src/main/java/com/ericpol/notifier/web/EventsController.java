package com.ericpol.notifier.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.ericpol.notifier.schedule.JobManager;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ericpol.notifier.data.impl.UserDAOImpl;
import com.ericpol.notifier.model.Event;
import com.ericpol.notifier.model.User;

/**
 * Created by vvai on 2/25/15.
 */
@RestController
@RequestMapping(value = "api")
public class EventsController
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(EventsController.class);

    @Autowired
    private UserDAOImpl userDAO;
    
    @Autowired
    private JobManager itsJobManager;

    /*@RequestMapping(value = "/events", method = RequestMethod.GET)
    public CalendarEvent greeting(@RequestParam(value = "name", defaultValue = "World") String name)
    {
        final UserEvents vvai = new UserEvents("vvai");
        List<CalendarEvent> events = new ArrayList<>();
        events.add(new CalendarEvent("This is a test event", new Date()));
        events.add(new CalendarEvent("This is a second test event", new Date()));

        vvai.addEvents(events);

        final CalendarEvent event = new CalendarEvent("This is a test event", new Date());
        return event;
    }*/

    @RequestMapping(value = "/{user}/events", method = RequestMethod.PUT)
    public void putEvents(@RequestBody List<Event> anEvents, @PathVariable("user") String aUser,
            HttpServletResponse response, Model model) throws IOException, SchedulerException {
        LOGGER.info("PUT method, event for: {} - {}", aUser, anEvents.size());

        final User user = userDAO.getUser(aUser);
        if (user != null)
        {
            userDAO.setUserEvents(user, anEvents);
            itsJobManager.rewriteUserJobs(user);
        }
        else
        {
            LOGGER.info("unknown user : {}", aUser);
            model.addAttribute("error", "user not exist");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "user not exist");

        }
    }

}
