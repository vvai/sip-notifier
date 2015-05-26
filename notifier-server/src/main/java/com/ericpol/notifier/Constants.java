package com.ericpol.notifier;

/**
 * Created by vvai on 4.2.15.
 */
public class Constants
{

    private final String itsPassword;
    private final String itsUsername;
    private final String itsHost;
    private final int itsPort;
    private final String itsRealm;
    private final String itsServerHost;
    private final int itsServerPort;
    private final String itsTag;

    /**
     * Constructor of bean.
     * 
     * @param aUsername - name of sip user
     * @param aPassword - password of sip user
     * @param aHost - ip of uac
     * @param aPort - port of uac
     * @param aRealm - realm for authentication
     * @param aServerHost - ip of sip server (asterisk for example)
     * @param aServerPort - port of sip server
     * @param aTag - tag of uac
     */
    public Constants(final String aUsername, final String aPassword, final String aHost, final int aPort,
            final String aRealm, final String aServerHost, final int aServerPort, final String aTag)
    {
        this.itsUsername = aUsername;
        this.itsPassword = aPassword;
        this.itsHost = aHost;
        this.itsPort = aPort;
        this.itsRealm = aRealm;
        this.itsServerHost = aServerHost;
        this.itsServerPort = aServerPort;
        this.itsTag = aTag;
    }

    public final String getTag()
    {
        return itsTag;
    }

    public final int getServerPort()
    {
        return itsServerPort;
    }

    public final String getServerHost()
    {
        return itsServerHost;
    }

    public final String getRealm()
    {
        return itsRealm;
    }

    public final int getPort()
    {
        return itsPort;
    }

    public final String getPassword()
    {
        return itsPassword;
    }

    public final String getUsername()
    {
        return itsUsername;
    }

    public final String getHost()
    {
        return itsHost;
    }

}
