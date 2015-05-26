package com.ericpol.notifier.sip.flows;

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ericpol.notifier.sip.CallFlow;
import com.ericpol.notifier.sip.MessageMaker;
import gov.nist.javax.sip.header.WWWAuthenticate;

/**
 * Created by vvai on 2/24/15.
 */
public class RegisterCallFlow implements CallFlow
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(RegisterCallFlow.class);

    @Autowired
    private MessageMaker itsMessageMaker;

    private CallIdHeader itsCallId;

    private boolean itsIsCompleted = false;

    public RegisterCallFlow(final MessageMaker aMessageMaker)
    {
        this.itsMessageMaker = aMessageMaker;
        this.itsCallId = itsMessageMaker.getNewCallId();

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
            else if (aResponse.getStatusCode() == 200)
            {
                LOGGER.info("Register completed");
                itsIsCompleted = true;
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

    @Override
    public void run()
    {
        try
        {
            itsMessageMaker.sendRegister(itsCallId);

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
