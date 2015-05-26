package com.ericpol.notifier.sip;

import java.text.ParseException;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipListener;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericpol.notifier.sip.flows.RegisterCallFlow;

/**
 * Created by vvai on 1/21/15.
 */
@Component
public class Notifier implements SipListener
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(Notifier.class);

    private ConcurrentMap<String, CallFlow> itsSessionsState = new ConcurrentHashMap();

    @Autowired
    private MessageMaker itsMessageMaker;

    @PostConstruct
    private void init() throws TooManyListenersException
    {
        itsMessageMaker.addListener(this);
    }

    public MessageMaker getMessageMaker()
    {
        return itsMessageMaker;
    }

    public Map<String, CallFlow> getSessionsState()
    {
        return itsSessionsState;
    }

    public final void sendMessage(final String aUsername, final String aMessage) throws ParseException, SipException,
            InvalidArgumentException
    {
        itsMessageMaker.sendMessage(aUsername, aMessage);
    }

    public final void sendRegister() throws ParseException, SipException, InvalidArgumentException
    {
        RegisterCallFlow register = new RegisterCallFlow(itsMessageMaker);
        register.run();
        final CallIdHeader callId = register.getCallId();
        itsSessionsState.put(callId.getCallId(), register);
    }

    public final void sendInvite(final String aUsername) throws ParseException, SipException,
            InvalidArgumentException
    {
        itsMessageMaker.sendInvite(aUsername);
    }

    @Override
    public final void processRequest(final RequestEvent aRequestEvent)
    {
        LOGGER.debug("process request {}", aRequestEvent.getRequest().getMethod());
    }

    @Override
    public final void processResponse(final ResponseEvent aResponseEvent)
    {
        LOGGER.info("processResponse :" + aResponseEvent.getResponse().getStatusCode());
        final Response response = aResponseEvent.getResponse();
        CallIdHeader callId = (CallIdHeader) response.getHeader("Call-ID");
        final CallFlow callFlow = itsSessionsState.get(callId.getCallId());
        if (!callFlow.isCompleted())
        {
            callFlow.handleResponse(response);
            if (callFlow.isCompleted())
            {
                itsSessionsState.remove(callId.getCallId());
            }
        }

    }

    public void startThirdPartyCall(final String aFirstParty, final String aSecondParty) throws ParseException,
            InvalidArgumentException, SipException
    {
        // itsMessageMaker.startThirdPartyCall(aFirstParty, aSecondParty);
    }

    @Override
    public final void processTimeout(final TimeoutEvent aTimeoutEvent)
    {
        LOGGER.debug("process timeout");
    }

    @Override
    public final void processIOException(final IOExceptionEvent anIoExceptionEvent)
    {
        LOGGER.debug("process io exception");
    }

    @Override
    public final void processTransactionTerminated(final TransactionTerminatedEvent aTransactionTerminatedEvent)
    {
        LOGGER.debug("process transaction terminated");
    }

    @Override
    public final void processDialogTerminated(final DialogTerminatedEvent aDialogTerminatedEvent)
    {
        LOGGER.debug("process dialog terminated");
    }
}
