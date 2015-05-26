package com.ericpol.notifier.sip;

import com.ericpol.notifier.data.UserDAO;
import com.ericpol.notifier.sip.flows.InviteCallFlow;
import com.ericpol.notifier.sip.flows.RegisterCallFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by vvai on 3/3/15.
 */
@Component
public class CallFlowFactory {
    
    @Autowired
    private MessageMaker itsMessageMaker;
    
    @Autowired
    private UserDAO itsUserDao;
    
    public RegisterCallFlow createRegisterCallFlow()
    {
        return new RegisterCallFlow(itsMessageMaker);
    }
    
    public InviteCallFlow createInviteCallFlow(final String aNumber, final String anEventUid)
    {
        return new InviteCallFlow(itsMessageMaker, itsUserDao, aNumber, anEventUid);
    }
    
}
