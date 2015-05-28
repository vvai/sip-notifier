package com.ericpol.notifier.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ericpol.notifier.model.Event;
import com.ericpol.notifier.model.User;

/**
 * Created by vvai on 3/2/15.
 */
public interface UserDAO
{

    User getUser(String aUser);

    List<Event> getUserEvents(User aUser);

    List<User> getUsers();

    void updateUser(User aUser);

    void createUser(User aUser);

    void setUserEvents(User aUser, List<Event> anEvents);

    Event getEvent(User aUser, Date aDate);

    Event getEvent(String aUid);

    void createEvent(Event anEvent);

    void deleteEvent(Event anEvent);

    void deleteEvent(String aUid);

    void deletePastEvents();

    void setEventsSettings(User aUser, Map<String, String> aNotifiedUids);
}
