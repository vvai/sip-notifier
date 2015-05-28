package com.ericpol.notifier.data.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.ericpol.notifier.model.Event;

/**
 * Row Mapper for Event class.
 */
public class EventMapper implements RowMapper<Event>
{
    @Override
    public final Event mapRow(final ResultSet aResultSet, final int aRowNum) throws SQLException
    {
        Event event = new Event();
        event.setId(aResultSet.getInt("idevent"));
        event.setIdUser(aResultSet.getInt("iduser"));
        event.setDescription(aResultSet.getString("description"));
        event.setDate(new Date(aResultSet.getTimestamp("date").getTime()));
        event.setUID(aResultSet.getString("uid"));
        event.setNotified(aResultSet.getBoolean("notified"));
        event.setAutoCall(aResultSet.getBoolean("auto_call"));
        event.setConferenceNumber(aResultSet.getInt("conference_number"));
        event.setCustom(aResultSet.getBoolean("custom"));
        return event;
    }
}
