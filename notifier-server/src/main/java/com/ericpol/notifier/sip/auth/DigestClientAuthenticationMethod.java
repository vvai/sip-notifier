package com.ericpol.notifier.sip.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Get this interface from the nist-sip IM.
 * 
 * @author olivier deruelle
 */
@Component
public class DigestClientAuthenticationMethod
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(DigestClientAuthenticationMethod.class);

    private String itsRealm;
    private String itsUserName;
    private String itsUri;
    private String itsNonce;
    private String itsPassword;
    private String itsMethod;
    private String itsCnonce;
    private String itsAlgorithm;
    private MessageDigest itsMessageDigest;

    /**
     * to hex converter.
     */
    private static final char[] TO_HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f'};

    /**
     * convert an array of bytes to an hexadecimal string.
     * 
     * @return a string
     * @param aBytes bytes array to convert to a hexadecimal string
     */

    public static String toHexString(final byte[] aBytes)
    {
        int pos = 0;
        char[] c = new char[aBytes.length * 2];
        for (int i = 0; i < aBytes.length; i++)
        {
            c[pos++] = TO_HEX[(aBytes[i] >> 4) & 0x0F];
            c[pos++] = TO_HEX[aBytes[i] & 0x0f];
        }
        return new String(c);
    }

    public final void initialize(final String aRealm, final String aUserName, final String aUri, final String aNonce,
            final String aPassword, final String aMethod, final String aCnonce, final String anAlgorithm)
            throws Exception
    {
        if (aRealm == null)
        {
            throw new Exception("The realm parameter is null");
        }
        this.itsRealm = aRealm;
        if (aUserName == null)
        {
            throw new Exception("The itsUserName parameter is null");
        }
        this.itsUserName = aUserName;
        if (aUri == null)
        {
            throw new Exception("The itsUri parameter is null");
        }
        this.itsUri = aUri;
        if (aNonce == null)
        {
            throw new Exception("The itsNonce parameter is null");
        }
        this.itsNonce = aNonce;
        if (aPassword == null)
        {
            throw new Exception("The itsPassword parameter is null");
        }
        this.itsPassword = aPassword;
        if (aMethod == null)
        {
            throw new Exception("The itsMethod parameter is null");
        }
        this.itsMethod = aMethod;
        this.itsCnonce = aCnonce;
        if (anAlgorithm == null)
        {
            throw new Exception("The itsAlgorithm parameter is null");
        }
        this.itsAlgorithm = anAlgorithm;

        try
        {
            itsMessageDigest = MessageDigest.getInstance(anAlgorithm);
        }
        catch (NoSuchAlgorithmException ex)
        {
            LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, initialize(): "
                    + "ERROR: Digest itsAlgorithm does not exist.");
            throw new Exception("ERROR: Digest itsAlgorithm does not exist.");
        }
    }

    /**
     * generate the response.
     * 
     * @return response
     */
    public final String generateResponse()
    {
        if (itsUserName == null)
        {
            LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(): "
                    + "ERROR: no itsUserName parameter");
            return null;
        }
        if (itsRealm == null)
        {
            LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(): "
                    + "ERROR: no itsRealm parameter");
            return null;
        }

        LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(): "
                + "Trying to generate a response for the user: " + itsUserName + " , with " + "the itsRealm: "
                + itsRealm);

        if (itsPassword == null)
        {
            LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(): "
                    + "ERROR: no itsPassword parameter");
            return null;
        }
        if (itsMethod == null)
        {
            LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(): "
                    + "ERROR: no itsMethod parameter");
            return null;
        }
        if (itsUri == null)
        {
            LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(): "
                    + "ERROR: no itsUri parameter");
            return null;
        }
        if (itsNonce == null)
        {
            LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(): "
                    + "ERROR: no itsNonce parameter");
            return null;
        }
        if (itsMessageDigest == null)
        {
            LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(): "
                    + "ERROR: the itsAlgorithm is not set");
            return null;
        }

        /******* GENERATE RESPONSE ************************************/
        LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(), itsUserName:" + itsUserName + "!");
        LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(), itsRealm:" + itsRealm + "!");
        LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(), itsPassword:" + itsPassword + "!");
        LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(), itsUri:" + itsUri + "!");
        LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(), itsNonce:" + itsNonce + "!");
        LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(), itsMethod:" + itsMethod + "!");
        final String a1 = itsUserName + ":" + itsRealm + ":" + itsPassword;
        final String a2 = itsMethod.toUpperCase() + ":" + itsUri;
        byte[] mdbytes = itsMessageDigest.digest(a1.getBytes());
        final String ha1 = toHexString(mdbytes);
        LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(), HA1:" + ha1 + "!");
        mdbytes = itsMessageDigest.digest(a2.getBytes());
        final String ha2 = toHexString(mdbytes);
        LOGGER.debug("DEBUG, DigestClientAuthenticationMethod, generateResponse(), HA2:" + ha2 + "!");
        String kd = ha1 + ":" + itsNonce;
        if (itsCnonce != null)
        {
            kd += ":" + itsCnonce;
        }
        kd += ":" + ha2;
        mdbytes = itsMessageDigest.digest(kd.getBytes());
        String response = toHexString(mdbytes);

        LOGGER.debug("DEBUG, DigestClientAlgorithm, generateResponse():" + " response generated: " + response);

        return response;
    }

}
