package com.ericpol.notifier.model;

/**
 * Created by vvai on 3/2/15.
 */
public class User
{

    private int itsId;
    private String itsName;
    private int itsSipNumber;
    private boolean itsNotified;

    public final int getId()
    {
        return itsId;
    }

    public final void setId(final int anId)
    {
        this.itsId = anId;
    }

    public final String getName()
    {
        return itsName;
    }

    public final void setName(final String aName)
    {
        this.itsName = aName;
    }

    public final int getSipNumber()
    {
        return itsSipNumber;
    }

    public final void setSipNumber(final int aSipNumber)
    {
        this.itsSipNumber = aSipNumber;
    }

    public final boolean isNotified()
    {
        return itsNotified;
    }

    public final void setNotified(final boolean aNotified)
    {
        this.itsNotified = aNotified;
    }
}
