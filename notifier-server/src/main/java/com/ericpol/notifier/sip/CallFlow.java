package com.ericpol.notifier.sip;

import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 * Created by vvai on 2/24/15.
 */
public interface CallFlow {
    
    boolean isCompleted();
    void handleResponse(Response aResponse);
    void run();
}
