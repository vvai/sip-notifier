package com.ericpol.notifier.sip;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.annotation.PostConstruct;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import com.ericpol.notifier.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericpol.notifier.sip.auth.DigestClientAuthenticationMethod;
import gov.nist.javax.sip.SipStackExt;

/**
 * Class which make and send requests.
 */
@Component
public class MessageMaker
{

    // @Autowired
    private SipProvider itsSipProvider;
    // @Autowired
    private SipStackExt itsSipStack;
    // @Autowired
    private HeaderFactory itsHeaderFactory;
    // @Autowired
    private AddressFactory itsAddressFactory;
    // @Autowired
    private MessageFactory itsMessageFactory;

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(MessageMaker.class);
    @Autowired
    private SipFactory itsSipFactory;
    @Autowired
    private DigestClientAuthenticationMethod itsDigestClientAuthenticationMethod;
    @Autowired
    private Constants itsConstants;



    /**
     * initialization.
     * 
     * @throws PeerUnavailableException
     * @throws InvalidArgumentException
     * @throws TransportNotSupportedException
     * @throws ObjectInUseException
     */
    @PostConstruct
    public final void init() throws PeerUnavailableException, InvalidArgumentException,
            TransportNotSupportedException, ObjectInUseException
    {
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "Notifier");
        properties.setProperty("javax.sip.IP_ADDRESS", itsConstants.getHost());

        properties.setProperty("javax.sip.OUTBOUND_PROXY",
                itsConstants.getServerHost() + ":" + itsConstants.getServerPort() + "/udp");
        // properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        // properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "textclient.txt");
        // properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "textclientdebug.log");

        itsSipStack = (SipStackExt) itsSipFactory.createSipStack(properties);
        itsHeaderFactory = itsSipFactory.createHeaderFactory();
        itsAddressFactory = itsSipFactory.createAddressFactory();
        itsMessageFactory = itsSipFactory.createMessageFactory();

        ListeningPoint tcp = itsSipStack.createListeningPoint(itsConstants.getPort(), "tcp");
        ListeningPoint udp = itsSipStack.createListeningPoint(itsConstants.getPort(), "udp");
        itsSipProvider = itsSipStack.createSipProvider(udp);
    }

    /**
     * Adds listener for sip provider.
     * 
     * @param aListener - a listener
     * @throws TooManyListenersException if sip provider has too many listeners
     */
    public final void addListener(final SipListener aListener) throws TooManyListenersException
    {
        itsSipProvider.addSipListener(aListener);
    }

    public FromHeader getFromHeader() throws ParseException
    {
        SipURI from =
                itsAddressFactory.createSipURI(itsConstants.getUsername(), itsConstants.getHost() + ":"
                        + itsConstants.getPort());
        Address fromNameAddress = itsAddressFactory.createAddress(from);
        fromNameAddress.setDisplayName(itsConstants.getUsername());
        return itsHeaderFactory.createFromHeader(fromNameAddress, itsConstants.getTag());
    }

    public ToHeader getToHeader(final String aUsername) throws ParseException
    {
        SipURI toAddress = itsAddressFactory.createSipURI(aUsername, itsConstants.getServerHost());
        Address toNameAddress = itsAddressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(aUsername);
        return itsHeaderFactory.createToHeader(toNameAddress, null);
    }

    private ToHeader getToHeader(final Address aToAdress) throws ParseException
    {
        return itsHeaderFactory.createToHeader(aToAdress, null);

    }
    
    public CallIdHeader getNewCallId()
    {
        return itsSipProvider.getNewCallId();
        
    }

    public ViaHeader getViaHeader() throws ParseException, InvalidArgumentException
    {
        return itsHeaderFactory
                .createViaHeader(itsConstants.getHost(), itsConstants.getPort(), "udp", null /* "branch1" */);

    }

    public ContactHeader getContactHeader() throws ParseException
    {
        SipURI contactURI = itsAddressFactory.createSipURI(itsConstants.getUsername(), itsConstants.getHost());
        contactURI.setPort(itsConstants.getPort());
        Address contactAddress = itsAddressFactory.createAddress(contactURI);
        contactAddress.setDisplayName(itsConstants.getUsername());
        return itsHeaderFactory.createContactHeader(contactAddress);
    }

    /**
     * Sends a sip MESSAGE request.
     * 
     * @param aUsername name of user
     * @param aMessage text of message
     * @throws ParseException
     * @throws InvalidArgumentException
     * @throws SipException
     */
    public final void sendMessage(final String aUsername, final String aMessage) throws ParseException,
            InvalidArgumentException, SipException
    {
        FromHeader fromHeader = getFromHeader();
        ToHeader toHeader = getToHeader(aUsername);

        SipURI requestURI = itsAddressFactory.createSipURI(aUsername, itsConstants.getServerHost());
        requestURI.setTransportParam("udp");

        ArrayList viaHeaders = new ArrayList();
        viaHeaders.add(getViaHeader());

        CallIdHeader callIdHeader = itsSipProvider.getNewCallId();

        CSeqHeader cSeqHeader = itsHeaderFactory.createCSeqHeader(1, Request.MESSAGE);

        MaxForwardsHeader maxForwards = itsHeaderFactory.createMaxForwardsHeader(70);

        Request request =
                itsMessageFactory.createRequest(requestURI, Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                        toHeader, viaHeaders, maxForwards);

        request.addHeader(getContactHeader());
        ContentTypeHeader contentTypeHeader = itsHeaderFactory.createContentTypeHeader("text", "plain");
        request.setContent(aMessage, contentTypeHeader);

        itsSipProvider.sendRequest(request);
    }

    /**
     * Sends sip INVITE request.
     * 
     * @param aUsername name of user
     * @throws ParseException
     * @throws InvalidArgumentException
     * @throws SipException
     */
    public final void sendInvite(final String aUsername) throws ParseException, InvalidArgumentException,
            SipException
    {
        final Request request = prepareInvite(aUsername, null, 0);

        // ContentTypeHeader contentTypeHeader = itsHeaderFactory.createContentTypeHeader("text", "plain");
        // request.setContent(aMessage, contentTypeHeader);
        // LOGGER.info("reguqes is {}", request);
        // itsSipProvider.sendRequest(request);
        itsSipProvider.sendRequest(request);
        LOGGER.info("invite send");
    }

    public final void sendInvite(final String aUsername, final CallIdHeader aCallId) throws ParseException,
            InvalidArgumentException, SipException
    {
        final Request request = prepareInvite(aUsername, aCallId, 0);
        itsSipProvider.sendRequest(request);
    }

    /**
     * Sends sip INVITE request with Proxy-Authorization header.
     * 
     * @param aNonce nonce for authorization
     * @param aCallId callID
     * @param aCSeq CSeq previous message
     * @param aUsername a name of user
     * @throws ParseException
     * @throws InvalidArgumentException
     * @throws SipException
     */
    public final void sendInvite(final String aNonce, final CallIdHeader aCallId, final long aCSeq,
            final String aUsername, final Object aSdpContent) throws ParseException, InvalidArgumentException,
            SipException
    {

        Request request = prepareInvite(aUsername, aCallId, aCSeq);
        if (aNonce != null)
        {
            request.addHeader(getAuthenticateHeader(aNonce, "Proxy-Authorization", "INVITE"));
        }
        if (aSdpContent != null)
        {
            request.setContent(aSdpContent, getSdpTypeHeader("application", "sdp"));
        }
        // ContentTypeHeader contentTypeHeader = itsHeaderFactory.createContentTypeHeader("text", "plain");
        // request.setContent(aMessage, contentTypeHeader);

        itsSipProvider.sendRequest(request);
        LOGGER.info("message send");
    }

    public final void sendRequest(final Request aRequest) throws SipException
    {
        LOGGER.info("send request");
        itsSipProvider.sendRequest(aRequest);
    }
    
    public final SipURI createSipURI(final String aUsername, final String aHost) throws ParseException {
        return itsAddressFactory.createSipURI(aUsername, aHost);
    }
    
    public final CSeqHeader createCSeqHeader(final long aNumber, final String aMethod) throws ParseException, InvalidArgumentException {
        return itsHeaderFactory.createCSeqHeader(aNumber, aMethod);
        
    }
    
    public final MaxForwardsHeader createMaxForwardsHeader(final int aCountOfForwards) throws InvalidArgumentException {
        return itsHeaderFactory.createMaxForwardsHeader(aCountOfForwards);
        
    }
    
    public final Request createRequest(final SipURI requestURI, final String aMethod,final CallIdHeader aCallId,
                                       final CSeqHeader aCSeqHeader,final FromHeader aFromHeader,
                                       final ToHeader aToHeader,final List<ViaHeader> aViaHeaders, final MaxForwardsHeader aMaxForwards) throws ParseException {
        return itsMessageFactory.createRequest(requestURI, aMethod, aCallId, aCSeqHeader, aFromHeader,
                aToHeader, aViaHeaders, aMaxForwards);
        
    }

    /**
     * Sends register request.
     * 
     * @throws ParseException
     * @throws InvalidArgumentException
     * @throws SipException
     */
    public void sendRegister(final CallIdHeader aCallIdHeader) throws ParseException, InvalidArgumentException, SipException
    {
        LOGGER.info("send register without auth");
        final Request request = prepareRegister(aCallIdHeader, 0);
        itsSipProvider.sendRequest(request);
    }

    /**
     * Sends register request with authorization header.
     * 
     * @param aNonce nonce
     * @param aCallId CallID
     * @param aCSeq CSeq previous message
     * @throws ParseException
     * @throws InvalidArgumentException
     * @throws SipException
     */
    public final void sendRegister(final String aNonce, final CallIdHeader aCallId, final long aCSeq)
            throws ParseException, InvalidArgumentException, SipException
    {
        LOGGER.info("send register with auth");
        final Request request = prepareRegister(aCallId, aCSeq);
        request.addHeader(getAuthenticateHeader(aNonce, "Authorization", "REGISTER"));
        itsSipProvider.sendRequest(request);

    }

    public Request prepareInvite(final String aUsername, final CallIdHeader aCallId, final long aCSeq)
            throws ParseException, InvalidArgumentException
    {
        FromHeader fromHeader = getFromHeader();
        ToHeader toHeader = getToHeader(aUsername);

        SipURI requestURI = itsAddressFactory.createSipURI(aUsername, itsConstants.getServerHost());
        requestURI.setTransportParam("udp");

        ArrayList viaHeaders = new ArrayList();
        viaHeaders.add(getViaHeader());

        CallIdHeader callIdHeader;
        if (aCallId == null)
        {
            callIdHeader = itsSipProvider.getNewCallId();
        }
        else
        {
            callIdHeader = aCallId;
        }

        CSeqHeader cSeqHeader = itsHeaderFactory.createCSeqHeader(aCSeq + 1, Request.INVITE);
        MaxForwardsHeader maxForwards = itsHeaderFactory.createMaxForwardsHeader(70);

        Request request =
                itsMessageFactory.createRequest(requestURI, Request.INVITE, callIdHeader, cSeqHeader, fromHeader,
                        toHeader, viaHeaders, maxForwards);

        request.addHeader(getContactHeader());

        /*
         * request.addHeader(itsHeaderFactory.createHeader("P-Asserted-Identity", "\"James Bond\" <sip:" +
         * itsConstants.getUsername() + "@" + itsConstants.getServerHost() + ">"));
         */
        return request;
    }

    private Request prepareRegister(final CallIdHeader aCallId, final long aCSeq) throws ParseException,
            InvalidArgumentException
    {
        FromHeader fromHeader = getFromHeader();
        ToHeader toHeader = getToHeader(itsConstants.getUsername());

        final URI requestUri = itsAddressFactory.createURI("sip:" + itsConstants.getServerHost());

        ArrayList viaHeaders = new ArrayList();
        viaHeaders.add(getViaHeader());

        CallIdHeader callIdHeader;
        if (aCallId == null)
        {
            callIdHeader = itsSipProvider.getNewCallId();
        }
        else
        {
            callIdHeader = aCallId;
        }

        CSeqHeader cSeqHeader = itsHeaderFactory.createCSeqHeader(aCSeq + 1, Request.INVITE);
        MaxForwardsHeader maxForwards = itsHeaderFactory.createMaxForwardsHeader(70);

        Request request =
                itsMessageFactory.createRequest(requestUri, Request.REGISTER, callIdHeader, cSeqHeader, fromHeader,
                        toHeader, viaHeaders, maxForwards);

        request.addHeader(getContactHeader());
        final ExpiresHeader expiresHeader = itsHeaderFactory.createExpiresHeader(60);
        request.addHeader(expiresHeader);

        /*
         * request.addHeader(itsHeaderFactory.createHeader("P-Asserted-Identity", "\"James Bond\" <sip:" +
         * itsConstants.getUsername() + "@" + itsConstants.getServerHost() + ">"));
         */

        return request;
    }

    private Header getAuthenticateHeader(final String aNonce, final String aType, final String aMethod)
            throws ParseException
    {
        try
        {
            itsDigestClientAuthenticationMethod.initialize(itsConstants.getRealm(), itsConstants.getUsername(),
                    "sip:" + itsConstants.getServerHost(), aNonce, itsConstants.getPassword(), aMethod, null, "MD5");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOGGER.warn("error {}", e);
        }
        final String response = itsDigestClientAuthenticationMethod.generateResponse();

        String valueOfHeader =
                "Digest username=\"" + itsConstants.getUsername() + "\", realm=\"" + itsConstants.getRealm()
                        + "\", nonce=\"" + aNonce + "\", uri=\"sip:" + itsConstants.getServerHost()
                        + "\", response=\"" + response + "\", algorithm=MD5";

        return itsHeaderFactory.createHeader(aType, valueOfHeader);

    }

    public Request prepareAck(Address aToAdress, long aCSeq, CallIdHeader aCallId) throws ParseException,
            InvalidArgumentException
    {
        FromHeader fromHeader = getFromHeader();
        ToHeader toHeader = getToHeader(aToAdress);
        ArrayList viaHeaders = new ArrayList();
        viaHeaders.add(getViaHeader());
        final CSeqHeader cSeqHeader = itsHeaderFactory.createCSeqHeader(aCSeq, Request.ACK);
        final ContactHeader contactHeader = getContactHeader();
        final MaxForwardsHeader maxForwardsHeader = itsHeaderFactory.createMaxForwardsHeader(70);

        /*
         * SipURI requestURI = itsAddressFactory.createSipURI(, itsConstants.getServerHost());
         * requestURI.setTransportParam("udp");
         */

        Request request =
                itsMessageFactory.createRequest(aToAdress.getURI(), Request.ACK, aCallId, cSeqHeader, fromHeader,
                        toHeader, viaHeaders, maxForwardsHeader);

        return request;
    }

    /*public void startThirdPartyCall(final String aFirstParticipant, final String aSecondParticipant)
            throws ParseException, InvalidArgumentException, SipException
    {
        final CallFlowFirst callFlow = new CallFlowFirst(aFirstParticipant, aSecondParticipant);
        final CallIdHeader firstCallIdHeader = itsSipProvider.getNewCallId();
        final CallIdHeader secondCallIdHeader = itsSipProvider.getNewCallId();
        
        callFlow.setFirstCallId(firstCallIdHeader);
        callFlow.setSecondCallId(secondCallIdHeader);

        LOGGER.info("build first call-id: `{}`",firstCallIdHeader.getCallId());
        LOGGER.info("build second call-id: `{}`",secondCallIdHeader.getCallId());
        
        itsCallFlows.put(firstCallIdHeader.getCallId(), callFlow);
        itsCallFlows.put(secondCallIdHeader.getCallId(), callFlow);
        final Request request = prepareInvite(aFirstParticipant, firstCallIdHeader, 0);

        final ContentTypeHeader contentTypeHeader = itsHeaderFactory.createContentTypeHeader("application", "sdp");
        final String sdpContent =
                "v=0\n" + "o=3273 200230668 157256245 IN IP4 172.17.151.95\n" + "s=A conversation\n"
                        + "c=IN IP4 172.17.151.95\n" + "t=0 0\n";
        request.setContent(sdpContent, contentTypeHeader);
        itsSipProvider.sendRequest(request);
    }

    public CallFlowFirst getCallFlow(final String aCallId)
    {
        return itsCallFlows.get(aCallId);

    }*/

    public ContentTypeHeader getSdpTypeHeader(final String aType, final String aType2) throws ParseException
    {
        return itsHeaderFactory.createContentTypeHeader(aType, aType2);
    }

    public Address getAdressForUser(String username) throws ParseException
    {
        return itsAddressFactory.createAddress("sip:" + username + "@"
                + itsConstants.getServerHost());
    }

}
