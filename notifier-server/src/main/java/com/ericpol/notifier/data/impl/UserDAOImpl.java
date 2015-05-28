package com.ericpol.notifier.data.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ericpol.notifier.data.UserDAO;
import com.ericpol.notifier.data.mapper.EventMapper;
import com.ericpol.notifier.data.mapper.UserMapper;
import com.ericpol.notifier.model.Event;
import com.ericpol.notifier.model.User;

/**
 * Created by vvai on 3/2/15.
 */
@Component
public class UserDAOImpl implements UserDAO
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(UserDAOImpl.class);

    @Autowired
    private JdbcTemplate itsJdbcTemplateObject;

    @Override
    public final User getUser(final String aUser)
    {

        String sql = "select * from user where username = ?";
        try
        {
            return itsJdbcTemplateObject.queryForObject(sql, new Object[] {aUser}, new UserMapper());
        }
        catch (EmptyResultDataAccessException e)
        {
            LOGGER.info("user {} not exist", aUser);
        }
        return null;

    }

    @Override
    public final List<Event> getUserEvents(final User aUser)
    {
        String sql = "select * from event where iduser = ?";
        return itsJdbcTemplateObject.query(sql, new Object[] {aUser.getId()}, new EventMapper());
    }

    @Override
    public final List<User> getUsers()
    {
        String sql = "select * from user";
        return itsJdbcTemplateObject.query(sql, new UserMapper());
    }

    @Override
    public final void updateUser(final User aUser)
    {
        String sql = "update user set sip=?,username=?,notified=? where iduser=?";
        itsJdbcTemplateObject.update(sql, aUser.getSipNumber(), aUser.getName(), aUser.isNotified(), aUser.getId());
    }

    @Override
    public final void createUser(final User aUser)
    {
        String sql = "insert into user (username, sip, notified) values (?, ?, ?)";
        itsJdbcTemplateObject.update(sql, aUser.getName(), aUser.getSipNumber(), aUser.isNotified());
    }

    @Override
    public final void setUserEvents(final User aUser, final List<Event> anEvents)
    {
        final List<Event> userEvents = getUserEvents(aUser);

        for (Event eachEvent : anEvents)
        {
            eachEvent.setIdUser(aUser.getId());
        }

        Map<String, Event> userEventsMap = new HashMap<>();
        for (Event eachEvent : userEvents)
        {
            userEventsMap.put(eachEvent.getUID(), eachEvent);
        }
        for (Event eachEvent : anEvents)
        {
            if (userEventsMap.get(eachEvent.getUID()) == null)
            {
                LOGGER.info("create event: {}", eachEvent.getDescription());
                eachEvent.setNotified(true);
                createEvent(eachEvent);
            }
            else
            {
                userEventsMap.remove(eachEvent.getUID());
            }
        }
        for (Event eachEvent : userEventsMap.values())
        {
            LOGGER.info("delete event: {}", eachEvent.getDescription());
            deleteEvent(eachEvent);
        }

    }

    @Override
    public final Event getEvent(final User aUser, final Date aDate)
    {
        String sql = "select * from event where date = ? and iduser = ?";
        return itsJdbcTemplateObject.queryForObject(sql, new Object[] {aDate, aUser.getId()}, new EventMapper());
    }

    @Override
    public final Event getEvent(final String aUid)
    {
        String sql = "select * from event where uid = ?";
        return itsJdbcTemplateObject.queryForObject(sql, new Object[] {aUid}, new EventMapper());
    }


    @Override
    public final void createEvent(final Event anEvent)
    {
        String sql = "insert into event (uid, description, date, notified, iduser, custom) values (?, ?, ?, ?, ?, ?)";
        itsJdbcTemplateObject.update(sql, anEvent.getUID(), anEvent.getDescription(), anEvent.getDate(),
                anEvent.isNotified(), anEvent.getIdUser(), anEvent.isCustom());
    }

    @Override
    public final void deleteEvent(final Event anEvent)
    {
        String sql = "delete from event where idevent = ?";
        itsJdbcTemplateObject.update(sql, anEvent.getId());
    }

    @Override
    public final void deleteEvent(final String aUid)
    {
        String sql = "delete from event where uid = ?";
        itsJdbcTemplateObject.update(sql, aUid);
    }

    @Override
    public final void deletePastEvents()
    {
        String sql = "delete from event where date < now() ";
        itsJdbcTemplateObject.update(sql);
    }

    @Override
    public final void setEventsSettings(final User aUser, final Map<String, String> aProperties)
    {
        resetUserEventSettings(aUser);
        for (String eachProperty : aProperties.keySet())
        {
            final String eventId = getEventId(eachProperty);
            if (eachProperty.contains("notify"))
            {
                notifyEvent(eventId);
            }
            else if (eachProperty.contains("conf"))
            {
                final String conferenceNumber = aProperties.get(eachProperty);
                setConferenceNumber(eventId, conferenceNumber);
            }
        }
    }

    private final String getEventId(String anParamName)
    {
        return anParamName.substring(anParamName.indexOf('-') + 1);
    }

    private final void resetUserEventSettings(final User aUser)
    {
        String sql = "update event set notified=false, auto_call=false, conference_number=NULL where iduser=?";
        itsJdbcTemplateObject.update(sql, aUser.getId());
    }

    private final void notifyEvent(final String anEventId)
    {
        String sql = "update event set notified=1 where idevent = ?";
        itsJdbcTemplateObject.update(sql, anEventId);
    }

    private final void setConferenceNumber(final String anEventId, final String aConferenceNumber)
    {
        String sql = "update event set auto_call=1, conference_number=? where idevent=?";
        itsJdbcTemplateObject.update(sql, aConferenceNumber, anEventId);
    }
}
