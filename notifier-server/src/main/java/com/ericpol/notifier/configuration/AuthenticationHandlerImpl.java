package com.ericpol.notifier.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ericpol.notifier.data.impl.UserDAOImpl;
import com.ericpol.notifier.model.User;

/**
 * Created by vvai on 3/3/15.
 */
@Component
public class AuthenticationHandlerImpl implements AuthenticationSuccessHandler
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationHandlerImpl.class);

    @Autowired
    private UserDAOImpl itsUserDAO;

    @Override
    public final void onAuthenticationSuccess(final HttpServletRequest aRequest, final HttpServletResponse aResponse,
            final Authentication anAuthentication) throws IOException, ServletException
    {
        LOGGER.info("Authentication handler for user '{}'", anAuthentication.getName());

        String username = anAuthentication.getName();
        final User userFromDB = itsUserDAO.getUser(username);
        if (userFromDB == null)
        {
            final User user = new User();
            user.setName(username);
            itsUserDAO.createUser(user);
        }
        aResponse.sendRedirect("/");
    }
}
