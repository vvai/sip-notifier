package com.ericpol.notifier.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by vvai on 16.2.15.
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(CustomErrorController.class);
    
    /*@RequestMapping("/error")
    public void error(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("/");
    }*/
    @RequestMapping("/error")
    public String error(Exception ex)
    {
        LOGGER.info(ex.getMessage());
        ex.printStackTrace();
        return "/error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
