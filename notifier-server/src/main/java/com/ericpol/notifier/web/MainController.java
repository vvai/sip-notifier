/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ericpol.notifier.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ericpol.notifier.data.impl.UserDAOImpl;
import com.ericpol.notifier.model.Event;
import com.ericpol.notifier.model.User;

@Controller
public class MainController
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Value("${application.message:Hello World}")
    private String message = "Hello World";

    @Autowired
    private UserDAOImpl userDAO;

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(ModelMap map)
    {
        return "login";
    }

    @RequestMapping("/")
    public String welcome(ModelMap aMap) throws IOException
    {
        // model.put("time", new Date());
        // model.put("message", this.message);

        /*
         * LotusNotesManager lotusNotesManager = new LotusNotesManager(); Calendar now = Calendar.getInstance();
         * now.add(Calendar.DATE, 30 * -1); // Clear out the time portion now.set(Calendar.HOUR_OF_DAY, 0);
         * now.set(Calendar.MINUTE, 0); now.set(Calendar.SECOND, 0); Date startDate = now.getTime(); now =
         * Calendar.getInstance(); now.add(Calendar.DATE, 30); // Set the time portion now.set(Calendar.HOUR_OF_DAY,
         * 23); now.set(Calendar.MINUTE, 59); now.set(Calendar.SECOND, 59); Date endDate = now.getTime(); // Date
         * startDate = call.getTime(); LOGGER.info("run date is: {}", startDate); LOGGER.info("end date is: {}",
         * endDate); lotusNotesManager.setServer("LN1-BRS1"); //
         * lotusNotesManager.setServerDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
         * lotusNotesManager.setServerDateFormat("Detect"); lotusNotesManager.setMailFile("mail\\vvai.nsf");
         * lotusNotesManager.setPassword(""); lotusNotesManager.setMinStartDate(startDate);
         * lotusNotesManager.setMaxEndDate(endDate); lotusNotesManager.setDiagnosticMode(false);
         * lotusNotesManager.setRequiresAuth(true); LOGGER.info("before get calendar entries"); final
         * ArrayList<LotusNotesCalendarEntry> calendarEntries = lotusNotesManager.getCalendarEntries(); for
         * (LotusNotesCalendarEntry eachCalendarEntry : calendarEntries) {
         * LOGGER.info("entry subject: {}, startDate: {}, endDate: {}", eachCalendarEntry.getSubject(),
         * eachCalendarEntry.getStartDateTime(), eachCalendarEntry.getEndDateTime()); }
         * LOGGER.info("after get calendar entries, size: {}", calendarEntries.size());
         */

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        LOGGER.info("user is {}", name);

        final User user = userDAO.getUser(name);
        aMap.addAttribute("user", user);
        if (user != null)
        {
            final List<Event> userEvents = userDAO.getUserEvents(user);
            Collections.sort(userEvents);
            aMap.addAttribute("events", userEvents);
        }

        return "index";
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    public String settings(@RequestParam(value = "sip-number", defaultValue = "none") String aSipNumber,
            @RequestParam(value = "notified", defaultValue = "false") boolean notified, Map<String, Object> model)
    {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        LOGGER.info("settings controller for {}, sip is {}, notified is {}", name, aSipNumber, notified);

        final User user = userDAO.getUser(name);
        user.setSipNumber(Integer.parseInt(aSipNumber));
        user.setNotified(notified);
        userDAO.updateUser(user);

        return "redirect:/";
    }

    @RequestMapping(value = "/new-event", method = RequestMethod.POST)
    public String newEvent(@RequestParam(value = "description", defaultValue = "none") String aDescription,
                           @RequestParam(value = "date-time") String aDate, Map<String, Object> model) throws ParseException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Date date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(aDate);

        final User user = userDAO.getUser(name);
        final Event newEvent = new Event();
        newEvent.setUID("custom");
        newEvent.setDescription(aDescription);
        newEvent.setDate(date);
        newEvent.setNotified(true);
        newEvent.setCustom(true);
        newEvent.setIdUser(user.getId());
        userDAO.createEvent(newEvent);


        LOGGER.info("new event {}, date {}", aDescription, aDate);

        return "redirect:/";
    }

    @RequestMapping(value = "/save-events", method = RequestMethod.POST)
    public String saveEvents(@RequestParam Map<String, String> allRequestParams, Model model) throws IOException,
            SchedulerException
    {
        LOGGER.info("save events");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();

        final User user = userDAO.getUser(name);
        userDAO.setEventsSettings(user, allRequestParams);

        return "redirect:/";
    }

}
