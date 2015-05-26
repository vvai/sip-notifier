package com.ericpol.notifier.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vvai on 3/2/15.
 */
public class Event implements Comparable<Event>
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(Event.class);

    private int itsId;
    private String itsUid;
    private int itsIdUser;
    private String itsDescription;
    private Date itsDate;
    private boolean itsNotified;
    private boolean itsAutoCall;
    private int itsConferenceNumber;

    public final int getId()
    {
        return itsId;
    }

    public final void setId(final int anId)
    {
        this.itsId = anId;
    }

    public final int getIdUser()
    {
        return itsIdUser;
    }

    public final void setIdUser(final int anIdUser)
    {
        this.itsIdUser = anIdUser;
    }

    public final String getDescription()
    {
        return itsDescription;
    }

    public final void setDescription(final String aDescription)
    {
        this.itsDescription = aDescription;
    }

    public final Date getDate()
    {
        return itsDate;
    }

    public final void setDate(final Date aDate)
    {
        this.itsDate = aDate;
    }

    public final String getUID()
    {
        return itsUid;
    }

    public final void setUID(final String aUid)
    {
        this.itsUid = aUid;
    }

    public final boolean isNotified()
    {
        return itsNotified;
    }

    public final void setNotified(final boolean aNnotified)
    {
        this.itsNotified = aNnotified;
    }

    public final boolean isAutoCall()
    {
        return itsAutoCall;
    }

    public final void setAutoCall(final boolean anAutoCall)
    {
        this.itsAutoCall = anAutoCall;
    }

    public final int getConferenceNumber()
    {
        return this.itsConferenceNumber;
    }

    public final void setConferenceNumber(final int aConferenceNumber)
    {
        this.itsConferenceNumber = aConferenceNumber;
    }

    /**
     * Gets formatted date.
     * 
     * @return string with formatted date
     */
    public final String getFormatDate()
    {
        SimpleDateFormat dt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        return dt.format(itsDate);
    }

    @Override
    public final int compareTo(final Event anEvent)
    {
        return this.getDate().compareTo(anEvent.getDate());
    }
}
