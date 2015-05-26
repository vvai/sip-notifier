package com.ericpol.notifier.sip.flows;

import java.text.ParseException;
import java.util.ArrayList;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ProxyAuthenticateHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ericpol.notifier.sip.CallFlow;
import com.ericpol.notifier.Constants;
import com.ericpol.notifier.sip.MessageMaker;
import gov.nist.javax.sip.header.WWWAuthenticate;

/**
 * Created by vvai on 2/24/15.
 */
public class FirstThirdPartyCallFlow implements CallFlow
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(FirstThirdPartyCallFlow.class);

    @Autowired
    MessageMaker itsMessageMaker;

    @Autowired
    Constants itsConstants;

    boolean itsIsCompleted;

    private final String itsFirstParticipant;
    private final String itsSecondParticipant;
    private final CallIdHeader itsFirstCallIdHeader;
    private final CallIdHeader itsSecondCallIdHeader;

    public FirstThirdPartyCallFlow(final String aFirstParticipant, final String aSecondParticipant,
            final CallIdHeader aFirstCallId, final CallIdHeader aSecondCallId)
    {
        this.itsFirstParticipant = aFirstParticipant;
        this.itsSecondParticipant = aSecondParticipant;
        this.itsFirstCallIdHeader = aFirstCallId;
        this.itsSecondCallIdHeader = aSecondCallId;
    }

    @Override
    public boolean isCompleted()
    {
        return itsIsCompleted;
    }

    @Override
    public void run()
    {
        try
        {
            final Request request = prepareInvite(itsFirstParticipant, itsFirstCallIdHeader, 0);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (InvalidArgumentException e)
        {
            e.printStackTrace();
        }
    }

    private Request prepareInvite(final String aUsername, final CallIdHeader aCallId, final long aCSeq)
            throws ParseException, InvalidArgumentException
    {
        FromHeader fromHeader = itsMessageMaker.getFromHeader();
        ToHeader toHeader = itsMessageMaker.getToHeader(aUsername);

        SipURI requestURI = itsMessageMaker.createSipURI(aUsername, itsConstants.getServerHost());
        requestURI.setTransportParam("udp");

        ArrayList viaHeaders = new ArrayList();
        viaHeaders.add(itsMessageMaker.getViaHeader());

        CSeqHeader cSeqHeader = itsMessageMaker.createCSeqHeader(aCSeq + 1, Request.INVITE);
        MaxForwardsHeader maxForwards = itsMessageMaker.createMaxForwardsHeader(70);

        Request request =
                itsMessageMaker.createRequest(requestURI, Request.INVITE, aCallId, cSeqHeader, fromHeader, toHeader,
                        viaHeaders, maxForwards);

        request.addHeader(itsMessageMaker.getContactHeader());
        return request;
    }

    @Override
    public void handleResponse(Response aResponse)
    {
        // LOGGER.info("processResponse :" + aResponse.getStatusCode());
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
                LOGGER.info("Response 407");

                final ProxyAuthenticateHeader proxyAuthenticateHeader =
                        (ProxyAuthenticateHeader) aResponse.getHeader("Proxy-Authenticate");
                final CSeqHeader cSeqHeader = (CSeqHeader) aResponse.getHeader("CSeq");
                final CallIdHeader callIdHeader = (CallIdHeader) aResponse.getHeader("Call-ID");
                final ToHeader toHeader = (ToHeader) aResponse.getHeader("To");
                final String aUsername = toHeader.getAddress().getDisplayName();
                Object sdpContent = aResponse.getContent();
                itsMessageMaker.sendInvite(proxyAuthenticateHeader.getNonce(), callIdHeader,
                        cSeqHeader.getSeqNumber(), aUsername, sdpContent);

            }
            else if (aResponse.getStatusCode() == 200)
            {
                /*final CallIdHeader callId = (CallIdHeader) aResponse.getHeader("Call-ID");
                final FromHeader from = (FromHeader) aResponse.getHeader("From");
                final ToHeader to = (ToHeader) aResponse.getHeader("To");
                final CSeqHeader cSeq = (CSeqHeader) aResponse.getHeader("CSeq");
                final CallFlowFirst callFlow = itsMessageMaker.getCallFlow(callId.getCallId());
                if (callFlow.getFirstPartySdp() != null && callFlow.getSecondPartySdp() != null)
                {
                    return;
                }
                if (to.getAddress().getURI().toString().contains(callFlow.getFirstParty()))
                {
                    LOGGER.info("OK from first party");
                    callFlow.setFirstPartySdp(aResponse.getContent());
                    if (!callFlow.isInviteForSecondSend())
                    {
                        final Request request =
                                itsMessageMaker.prepareInvite(callFlow.getSecondParty(), callFlow.getSecondCallId(),
                                        cSeq.getSeqNumber());
                        LOGGER.info("content is {}", aResponse.getContent().toString());
                        request.setContent(aResponse.getContent(),
                                itsMessageMaker.getSdpTypeHeader("application", "sdp"));
                        itsMessageMaker.sendRequest(request);
                        callFlow.setInviteForSecondSend(true);
                        LOGGER.info("invite for second sending...");
                    }
                }
                else if (to.getAddress().getURI().toString().contains(callFlow.getSecondParty()))
                {
                    LOGGER.info("OK from second party");
                    callFlow.setSecondPartySdp(aResponse.getContent());
                    final Request ackForSecond =
                            itsMessageMaker.prepareAck(itsMessageMaker.getAdressForUser(callFlow.getSecondParty()),
                                    cSeq.getSeqNumber(), callFlow.getSecondCallId());
                    itsMessageMaker.sendRequest(ackForSecond);
                    final Request ackForFirst =
                            itsMessageMaker.prepareAck(itsMessageMaker.getAdressForUser(callFlow.getFirstParty()),
                                    2 *//* cSeq.getSeqNumber() *//*, callFlow.getFirstCallId());
                    ackForFirst.setContent(aResponse.getContent(),
                            itsMessageMaker.getSdpTypeHeader("application", "sdp"));
                    itsMessageMaker.sendRequest(ackForFirst);
                    LOGGER.info("ack sending...");
                }
                else
                {
                    LOGGER.info("interesting...");
                }*/
            }
        }
        catch (InvalidArgumentException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (SipException e)
        {
            e.printStackTrace();
        }
    }

}
