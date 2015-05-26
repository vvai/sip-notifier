package com.ericpol.notifier;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

import com.ericpol.notifier.schedule.JobManager;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@SpringBootApplication
@PropertySource("file:main.properties")
public class Application // extends SpringBootServletInitializer
{

    /**
     * slf4j logger.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    
    @Autowired
    JobManager itsJobManager;

    @Autowired
    private Environment itsEnv;
    




    

    @Autowired
    private Constants itsConstants;

    /*
     * @Override protected SpringApplicationBuilder configure(SpringApplicationBuilder application) { return
     * application.sources(Application.class); }
     */

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(Application.class, args);
    }

    /*
     * @PostConstruct private void init() { // File f = new File("default.mid"); //
     * LOGGER.info("file exists: {}",f.exists()); Format format; format = new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8,
     * 1); Processor processor = null; try { processor = Manager.createProcessor(new MediaLocator("file:default.mid"));
     * } catch (IOException e) { e.printStackTrace(); System.exit(-1); } catch (NoProcessorException e) {
     * e.printStackTrace(); System.exit(-1); } // configure the processor processor.configure(); while
     * (processor.getState() != Processor.Configured) { try { Thread.sleep(100); } catch (InterruptedException e) { //
     * e.printStackTrace(); } } processor.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW_RTP));
     * TrackControl track[] = processor.getTrackControls(); boolean encodingOk = false; // Go through the tracks and try
     * to program one of them to // output gsm data. for (int i = 0; i < track.length; i++) { if (!encodingOk &&
     * track[i] instanceof FormatControl) { if (((FormatControl) track[i]).setFormat(format) == null) {
     * track[i].setEnabled(false); } else { encodingOk = true; } } else { // we could not set this track to gsm, so
     * disable it track[i].setEnabled(false); } } // realize the processor processor.realize(); while
     * (processor.getState() != processor.Realized) { try { Thread.sleep(100); } catch (InterruptedException e) {
     * e.printStackTrace(); } } // At this point, we have determined where we can send out // gsm data or not. if
     * (encodingOk) { // get the output datasource of the processor and exit // if we fail
     * javax.media.protocol.DataSource ds = null; try { ds = processor.getDataOutput(); } catch (NotRealizedError e) {
     * e.printStackTrace(); System.exit(-1); } // hand this datasource to manager for creating an RTP // datasink our
     * RTP datasink will multicast the audio try { String url = "rtp://127.0.0.1:8000/audio/1"; MediaLocator m = new
     * MediaLocator(url); DataSink d = Manager.createDataSink(ds, m); d.open(); d.run();
     * System.out.println("Starting processor"); processor.run(); System.out.println("Processor Started");
     * Thread.sleep(30000); } catch (Exception e) { e.printStackTrace(); System.exit(-1); } } }
     */

    @PostConstruct
    private void initApplication() throws SchedulerException, java.text.ParseException, InvalidArgumentException,
            SipException, InterruptedException, IOException
    {
        LOGGER.debug("init application");
        LOGGER.info("MAIN property: {}", itsEnv.getProperty("it.test.prop")); // LOGGER.info("classpath is {}",
                                                                              // System.getProperty("java.class.path"));
        itsJobManager.addDeletePastEventsJob();

        /*RegisterCallFlow register = new RegisterCallFlow(itsNotifier.getMessageMaker());
        LOGGER.info("message maker is {}", itsNotifier.getMessageMaker());
        final Map<String, CallFlow> sessionsState = itsNotifier.getSessionsState();
        final String callId = register.getCallId().getCallId();
        sessionsState.put(callId, register);
        JobDetail jobDetail = JobBuilder.newJob(CallFlowJob.class).withIdentity("register", "sip").build();
        jobDetail.getJobDataMap().put("message", register);
        Date runTime = DateBuilder.evenMinuteDate(new Date());
        final Trigger trigger = TriggerBuilder.newTrigger().withIdentity("register", "sip").startAt(runTime).build();
        itsScheduler.scheduleJob(jobDetail, trigger);*/

        /*final Map<String, CallFlow> sessionsState = itsNotifier.getSessionsState();
        // put Invite job 
        final InviteCallFlow inviteCallFlow = itsCallFlowFactory.createInviteCallFlow("vvai");
        final String inviteCallId = inviteCallFlow.getCallId().getCallId();
        sessionsState.put(inviteCallId, inviteCallFlow);

        JobDetail inviteJobDetail = JobBuilder.newJob(CallFlowJob.class).withIdentity("invite", "vvai").build();
        inviteJobDetail.getJobDataMap().put("message", inviteCallFlow);
        Date inviteRunTime = DateBuilder.evenMinuteDate(new Date());
        final Trigger inviteTrigger =
                TriggerBuilder.newTrigger().withIdentity("invite", "vvai").startAt(inviteRunTime).build();
        itsScheduler.scheduleJob(inviteJobDetail, inviteTrigger);*/

        
        // invokeLotusMethod();

        // sipCall();

        /*
         * final boolean auth = itsAuthenticate.authenticate("uid=vvai,ou=people,dc=ericpol,dc=int",
         * itsConstants.getMailPassword()); LOGGER.info("Authenticate :{}", auth);
         */
    }

    /*private void sipCall() throws ParseException, InvalidArgumentException, SipException, InterruptedException
    {
        itsNotifier.sendRegister();
        Thread.sleep(1000);
        // itsNotifier.startThirdPartyCall("3029", "3258");
        // itsNotifier.sendInvite("3258");
    }*/

}
