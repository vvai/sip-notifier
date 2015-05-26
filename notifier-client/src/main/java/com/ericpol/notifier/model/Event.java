package com.ericpol.notifier.model;

import java.util.Date;

/**
 * Created by vvai on 2/25/15.
 */
public class Event
{

    private String itsDescription;
    private Date itsDate;
    private String itsUid;

    /**
     * Default constructor.
     */
    public Event()
    {
    }

    /**
     * Constructor with params.
     * @param aUid - universal event id.
     * @param aDescription - description of event
     * @param aDate - date of event
     */
    public Event(final String aUid, final String aDescription, final Date aDate)
    {
        this.itsDescription = aDescription;
        this.itsDate = aDate;
        this.itsUid = aUid;
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
}
