package com.ericpol.notifier.sip.flows;

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ProxyAuthenticateHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import com.ericpol.notifier.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericpol.notifier.data.UserDAO;
import com.ericpol.notifier.model.User;
import com.ericpol.notifier.sip.CallFlow;
import com.ericpol.notifier.sip.MessageMaker;
import gov.nist.javax.sip.header.WWWAuthenticate;

/**
 * Created by vvai on 3/3/15.
 */
public class InviteCallFlow implements CallFlow
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(InviteCallFlow.class);

    private MessageMaker itsMessageMaker;

    private UserDAO itsUserDao;

    private CallIdHeader itsCallId;

    private boolean itsIsCompleted = false;

    private final String itsUser;

    private final String itEventUID;

    public InviteCallFlow(final MessageMaker aMessageMaker, final UserDAO aUserDao, final String aUser,
            final String anEventUid)
    {
        this.itsMessageMaker = aMessageMaker;
        this.itsUserDao = aUserDao;
        this.itsCallId = itsMessageMaker.getNewCallId();
        this.itsUser = aUser;
        this.itEventUID = anEventUid;
    }

    @Override
    public boolean isCompleted()
    {
        return itsIsCompleted;
    }

    public CallIdHeader getCallId()
    {
        return itsCallId;
    }

    @Override
    public void handleResponse(Response aResponse)
    {
        final User user = itsUserDao.getUser(itsUser);
        if (user.isNotified())
        {
            try
            {
                if (aResponse.getStatusCode() == 401)
                {
                    LOGGER.info("Response 401");
                    final WWWAuthenticate wwwAuth = (WWWAuthenticate) aResponse.getHeader("WWW-Authenticate");
                    final CSeqHeader cSeqHeader = (CSeqHeader) aResponse.getHeader("CSeq");
                    final CallIdHeader callIdHeader = (CallIdHeader) aResponse.getHeader("Call-ID");
                    if (cSeqHeader.getSeqNumber() < 2)
                    {
                        itsMessageMaker.sendRegister(wwwAuth.getNonce(), callIdHeader, cSeqHeader.getSeqNumber());
                    }
                }
                else if (aResponse.getStatusCode() == 407)
                {
                    LOGGER.info("proxy authentication required");
                    final ProxyAuthenticateHeader proxyAuthenticateHeader =
                            (ProxyAuthenticateHeader) aResponse.getHeader("Proxy-Authenticate");
                    final CSeqHeader cSeqHeader = (CSeqHeader) aResponse.getHeader("CSeq");
                    final CallIdHeader callIdHeader = (CallIdHeader) aResponse.getHeader("Call-ID");
                    final ToHeader toHeader = (ToHeader) aResponse.getHeader("To");
                    final String aUsername = toHeader.getAddress().getDisplayName();
                    if (cSeqHeader.getSeqNumber() < 2)
                    {
                        itsMessageMaker.sendInvite(proxyAuthenticateHeader.getNonce(), callIdHeader,
                                cSeqHeader.getSeqNumber(), aUsername, aResponse.getContent());
                    }
                }
                else if (aResponse.getStatusCode() == 200)
                {
                    final CSeqHeader cSeqHeader = (CSeqHeader) aResponse.getHeader("CSeq");
                    if (Request.REGISTER.equals(cSeqHeader.getMethod()))
                    {
                        itsMessageMaker.sendInvite("" + user.getSipNumber(), itsCallId);
                    }
                    else
                    {
                        LOGGER.info("Invite completed");
                        itsMessageMaker.prepareAck(itsMessageMaker.getAdressForUser("" + user.getSipNumber()),
                                cSeqHeader.getSeqNumber(), itsCallId);
                        itsIsCompleted = true;
                    }
                }
            }
            catch (InvalidArgumentException e)
            {
                e.printStackTrace();
            }
            catch (SipException e)
            {
                e.printStackTrace();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run()
    {
        final User user = itsUserDao.getUser(itsUser);
        final Event event = itsUserDao.getEvent(itEventUID);
        if (user.isNotified() && event.isNotified())
        {
            try
            {
                itsMessageMaker.sendInvite("" + user.getSipNumber(), itsCallId);

            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            catch (InvalidArgumentException e)
            {
                e.printStackTrace();
            }
            catch (SipException e)
            {
                e.printStackTrace();
            }
        }
    }
}
