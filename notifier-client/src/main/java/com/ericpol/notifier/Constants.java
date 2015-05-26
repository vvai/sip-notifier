package com.ericpol.notifier;

/**
 * Created by vvai on 2/26/15.
 */
public class Constants
{

    private final String itsServerHost;
    private final int itsServerPort;
    private final String itsLotusPass;
    private final int itsPeriod;

    /**
     * Constructor for constants bean.
     * 
     * @param aServerHost - a host of server
     * @param aServerPort - a port of server
     * @param aLotusPass - a lotus password
     * @param aPeriod - a period between sending of data to the server
     */
    public Constants(final String aServerHost, final int aServerPort, final String aLotusPass, final int aPeriod)
    {
        this.itsServerHost = aServerHost;
        this.itsServerPort = aServerPort;
        this.itsLotusPass = aLotusPass;
        this.itsPeriod = aPeriod;
    }

    public final String getServerHost()
    {
        return itsServerHost;
    }

    public final int getServerPort()
    {
        return itsServerPort;
    }

    public final String getLotusPass()
    {
        return itsLotusPass;
    }

    public final int getPeriod()
    {
        return itsPeriod;
    }
}
