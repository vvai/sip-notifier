package com.ericpol.notifier.lotus;

// This source code is released under the GPL v3 license, http://www.gnu.org/licenses/gpl.html.
// This file is part of the LNGS project: http://sourceforge.net/projects/lngooglecalsync.

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LotusNotesCalendarEntry
{

    public LotusNotesCalendarEntry()
    {
        entryType = EntryType.APPOINTMENT;
        appointmentType = AppointmentType.APPOINTMENT;
    }

    /**
     * Create a "deep copy" of this object.
     * 
     * @return The new, cloned object.
     */
    @Override
    public final LotusNotesCalendarEntry clone()
    {
        LotusNotesCalendarEntry cal = new LotusNotesCalendarEntry();

        cal.appointmentType = this.appointmentType;
        cal.entryType = this.entryType;
        cal.startDateTime = this.startDateTime;
        cal.endDateTime = this.endDateTime;
        cal.modifiedDateTime = this.modifiedDateTime;
        cal.subject = this.subject;
        cal.location = this.location;
        cal.room = this.room;
        cal.body = this.body;
        cal.alarm = this.alarm;
        cal.alarmOffsetMins = this.alarmOffsetMins;
        cal.privateEntry = this.privateEntry;
        cal.uid = this.uid;
        cal.requiredAttendees = this.requiredAttendees;
        cal.optionalAttendees = this.optionalAttendees;
        cal.chairperson = this.chairperson;

        return cal;
    }

    public final void setEntryType(final String anEntryType)
    {
        if (anEntryType.equals("Task"))
        {
            this.entryType = EntryType.TASK;
        }
        else
        {
            this.entryType = EntryType.APPOINTMENT;
        }
    }

    public final void setEntryType(final EntryType anEntryType)
    {
        this.entryType = anEntryType;
    }

    public final void setAppointmentType(final String anAppointmentType)
    {
        if (anAppointmentType.equals("0"))
        {
            this.appointmentType = AppointmentType.APPOINTMENT;
        }
        else if (anAppointmentType.equals("1"))
        {
            this.appointmentType = AppointmentType.ANNIVERSARY;
        }
        else if (anAppointmentType.equals("2"))
        {
            this.appointmentType = AppointmentType.ALL_DAY_EVENT;
        }
        else if (anAppointmentType.equals("3"))
        {
            this.appointmentType = AppointmentType.MEETING;
        }
        else if (anAppointmentType.equals("4"))
        {
            this.appointmentType = AppointmentType.REMINDER;
        }
        else
        {
            this.appointmentType = AppointmentType.APPOINTMENT;
        }
    }

    public final void setStartDateTime(final Date aStartDateTime)
    {
        this.startDateTime = aStartDateTime;
    }

    public final void setEndDateTime(final Date anEndDateTime)
    {
        this.endDateTime = anEndDateTime;
    }

    public final void setModifiedDateTime(final Date aModifiedDateTime)
    {
        this.modifiedDateTime = aModifiedDateTime;
    }

    public final void setSubject(final String aSubject)
    {
        this.subject = aSubject;
    }

    public final void setLocation(final String aLocation)
    {
        this.location = aLocation;
    }

    public final void setRoom(final String aRoom)
    {
        this.room = aRoom;
    }

    public final void setBody(final String aValue)
    {
        this.body = aValue;
    }

    public final void setAlarm(final boolean aValue)
    {
        this.alarm = aValue;
    }

    public final void setAlarmOffsetMins(final int aValue)
    {
        this.alarmOffsetMins = aValue;
    }

    public final void setPrivate(final boolean aValue)
    {
        this.privateEntry = aValue;
    }

    public final void setUID(final String aUid)
    {
        this.uid = aUid;
    }

    public final void setRequiredAttendees(final String aNames)
    {
        requiredAttendees = aNames;
    }

    public final void setOptionalAttendees(final String aNames)
    {
        optionalAttendees = aNames;
    }

    public final void setChairperson(final String aName)
    {
        chairperson = aName;
    }

    /**
     * Returns the chairperson name as it was retrieved from Lotus. The standard format is like
     * "CN=John A Smith/US/Acme@MAIL".
     */
    public String getChairperson()
    {
        return chairperson;
    }

    /**
     * Returns the Lotus chairperson name without the Lotus metadata. Given a Lotus format like
     * "CN=John A Smith/US/Acme@MAIL", this method returns "John A Smith".
     */
    public String getChairpersonPlain()
    {
        return getNamePlain(chairperson);
    }

    /**
     * Returns the required attendee list as it was retrieved from Lotus. The standard format is like
     * "John A Smith/US/Acme@MAIL;kelly@gmail.com;Jane B Doe/H9876/Sponge@Bob".
     */
    public String getRequiredAttendees()
    {
        return requiredAttendees;
    }

    /**
     * Returns the attendee list without the Lotus metadata. Given a Lotus list like
     * "John A Smith/US/Acme@MAIL;kelly@gmail.com;Jane B Doe/H9876/Sponge@Bob", this method returns
     * "John A Smith; kelly@gmail.com; Jane B Doe"
     */
    public String getRequiredAttendeesPlain()
    {
        return getNameListPlain(requiredAttendees);
    }

    /**
     * Returns the attendee list without the Lotus metadata. Given a Lotus list like
     * "John A Smith/US/Acme@MAIL;kelly@gmail.com;Jane B Doe/H9876/Sponge@Bob", this method returns
     * "John A Smith; kelly@gmail.com; Jane B Doe"
     */
    public String getOptionalAttendees()
    {
        return getNameListPlain(optionalAttendees);
    }

    /**
     * Given a Lotus format like "CN=John A Smith/US/Acme@MAIL", this method returns "John A Smith".
     */
    protected String getNamePlain(String lotusName)
    {
        if (lotusName == null)
            return null;

        String s = lotusName;

        // Strip off the "CN="
        int i = s.indexOf("=");
        if (i > -1)
        {
            s = s.substring(i + 1);
        }

        // Strip off the "/US/Acme@MAIL"
        i = s.indexOf("/");
        if (i > -1)
        {
            s = s.substring(0, i);
        }

        return s;
    }

    /**
     * Returns a list of names without the Lotus metadata. Given a Lotus list like
     * "John A Smith/US/Acme@MAIL;kelly@gmail.com;Jane B Doe/H9876/Sponge@Bob", this method returns
     * "John A Smith; kelly@gmail.com; Jane B Doe"
     */
    protected String getNameListPlain(String lotusNames)
    {
        if (lotusNames == null)
            return null;

        String[] nameList = lotusNames.split(";");

        StringBuffer sb = new StringBuffer();
        for (String lotusName : nameList)
        {
            if (sb.length() > 0)
                sb.append("; ");
            sb.append(getNamePlain(lotusName));
        }

        return sb.toString();
    }

    public EntryType getEntryType()
    {
        return entryType;
    }

    public AppointmentType getAppointmentType()
    {
        return appointmentType;
    }

    /**
     * @return Returns the run datetime.
     */
    public Date getStartDateTime()
    {
        return startDateTime;
    }

    /**
     * @return Returns the run date (without a time portion).
     * @param addDays Add (or subtract) this many days from the returned value.
     */
    public Date getStartDate(int addDays)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDateTime);
        cal.add(Calendar.DATE, addDays);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public String getStartDateTimeGoogle() throws ParseException
    {
        return getGoogleDateTimeString(startDateTime);
    }

    // Return the run date without time in Google format.
    public String getStartDateGoogle() throws ParseException
    {
        return getGoogleDateString(startDateTime);
    }

    /**
     * Return the run date (without time) in Google format.
     * 
     * @param addDays Add (or subtract) this many days from the returned value.
     */
    public String getStartDateGoogle(int addDays) throws ParseException
    {
        return getGoogleDateString(startDateTime, addDays);
    }

    public Date getEndDateTime()
    {
        return endDateTime;
    }

    /**
     * @return Returns the end date (without a time portion).
     * @param addDays Add (or subtract) this many days from the returned value.
     */
    public Date getEndDate(int addDays)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDateTime);
        cal.add(Calendar.DATE, addDays);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public String getEndDateTimeGoogle() throws ParseException
    {
        return getGoogleDateTimeString(endDateTime);
    }

    /**
     * Return the end date (without time) in Google format.
     * 
     * @param addDays Add (or subtract) this many days from the returned value.
     */
    public String getEndDateGoogle(int addDays) throws ParseException
    {
        return getGoogleDateString(endDateTime, addDays);
    }

    public Date getModifiedDateTime()
    {
        return modifiedDateTime;
    }

    public String getModifiedDateTimeGoogle() throws ParseException
    {
        return getGoogleDateTimeString(modifiedDateTime);
    }

    public String getSubject()
    {
        return subject;
    }

    public String getLocation()
    {
        return location;
    }

    public String getRoom()
    {
        return room;
    }

    /**
     * Lotus has both a Location field and a Room field. Usually only one is filled in. But it is possible to set both
     * fields, e.g. Location = "New York Blg 3" and Room = "Fancy Conf Room". If only one of Location or Room has a
     * value, than that one value is returned. If both fields have values, then the concatinated value is returned. If
     * neither fields have values, null is returned.
     */
    public String getGoogleWhereString()
    {
        String whereStr = null;
        if (location != null && !location.isEmpty())
        {
            whereStr = location;
        }

        if (room != null && !room.isEmpty())
        {
            if (whereStr == null)
                whereStr = room;
            else
                // We have both a Location and Room values
                whereStr = whereStr + " : " + room;
        }

        return whereStr;
    }

    public String getBody()
    {
        return body;
    }

    public boolean getAlarm()
    {
        return alarm;
    }

    public int getAlarmOffsetMins()
    {
        return alarmOffsetMins;
    }

    public boolean getPrivate()
    {
        return privateEntry;
    }

    public int getAlarmOffsetMinsGoogle()
    {
        // Lotus Notes alarms can be before (negative value) or after (positive value)
        // the event. Google only supports alarms before the event and the number
        // is then positive.
        // So, convert to Google as follows: alarms after (positive) are made 0,
        // alarms before (negative) are made positive.
        int alarmMins = 0;
        if (alarmOffsetMins < 0)
            alarmMins = Math.abs(alarmOffsetMins);

        return alarmMins;
    }

    /**
     * Returns the UID stored in Lotus Notes.
     */
    public String getUID()
    {
        return uid;
    }

    /**
     * Returns a UID that includes the Lotus Notes UID and other info to help sync this entry.
     */
    public String getSyncUID()
    {
        // Layout of the SyncUID format:
        // Include a version stamp in case this format changes in the future.
        // Include the Lotus Notes UID.
        // Include the run timestamp. This is needed because we make multiple entries
        // from one repeating event. If we don't include the run timestamp, all the
        // multiple entries would have the same SyncUID.
        // Include the modified timestamp. If the Lotus entry ever changes, this value
        // will change and we'll know to update the sync.
        return currSyncUIDVersion + "-" + uid + "-" + startDateTime.getTime() + "-" + modifiedDateTime.getTime();
    }

    /**
     * Returns true if a provided string matches the format of an LNGS UID.
     */
    public static boolean isLNGSUID(String sample)
    {
        // An LNGS UID is a UUID followed by a colin then a Sync UID. See getSyncUID()
        // for the format of a Sync UID.
        return sample.matches("^[0-9A-Fa-f]{32}:[0-9]-[0-9A-Fa-f]{32}-[0-9]{13}-[0-9]{13}");
    }

    /**
     * Convert from Java Date object to Google format (YYYY-MM-DDTHH:MM:SS). Note: Google format is the same as the XML
     * standard xs:DateTime format.
     * 
     * @param sourceDate The source datetime.
     * @return The datetiem string in Google format.
     */
    private String getGoogleDateTimeString(Date sourceDate) throws ParseException
    {
        DateFormat dfGoogle = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String googleDateTimeFormat = dfGoogle.format(sourceDate);

        return googleDateTimeFormat;
    }

    /**
     * This method calls the overloaded version of this method, and passes zero in for addDays.
     */
    private String getGoogleDateString(Date sourceDate) throws ParseException
    {
        return getGoogleDateString(sourceDate, 0);
    }

    /**
     * Convert from Java Date object to Google string format (YYYY-MM-DD) with no time portion. Note: Google format is
     * the same as the XML standard xs:DateTime format.
     * 
     * @param sourceDate The source datetime.
     * @param addDays Add (or subtract) this many days from the returned value.
     * @return The date string in Google format.
     */
    private String getGoogleDateString(Date sourceDate, int addDays) throws ParseException
    {
        // Add days to the date
        Calendar dateTimeTemp = Calendar.getInstance();
        dateTimeTemp.setTime(sourceDate);
        dateTimeTemp.add(Calendar.DATE, addDays);

        // Convert to Google format
        DateFormat dfGoogle = new SimpleDateFormat("yyyy-MM-dd");
        String googleDateFormat = dfGoogle.format(dateTimeTemp.getTime());

        return googleDateFormat;
    }

    // Version stamp for the SyncUID format
    protected static final int currSyncUIDVersion = 1;

    // The Lotus Notes type for this calendar entry
    public enum EntryType
    {
        NONE, APPOINTMENT, TASK
    };

    // The various sub-types of an appointment
    public enum AppointmentType
    {
        NONE, APPOINTMENT, ANNIVERSARY, ALL_DAY_EVENT, MEETING, REMINDER
    };

    protected EntryType entryType;
    protected AppointmentType appointmentType;
    // DateTime in Lotus Notes format
    protected Date startDateTime = null;
    protected Date endDateTime = null;
    // The last date/time the entry was modified
    protected Date modifiedDateTime = null;
    protected String subject = null;
    protected String location = null;
    protected String room = null;
    // Body is the description for the calendar entry
    protected String body = null;
    // True if the entry has an alarm set
    protected boolean alarm = false;
    // The number of minutes until the alarm goes off. Lotus can set alarms to notify
    // before (default) or after the date. "Before" offsets are negative values and
    // "after" offsets are positive. So, -15 means notify 15 minutes before the event.
    protected int alarmOffsetMins = 0;
    // True if the entry has the Mark Private flag set
    protected boolean privateEntry = false;
    // Unique ID for this calendar entry. This is the value created by Lotus.
    protected String uid = null;
    protected String requiredAttendees = null;
    protected String optionalAttendees = null;
    protected String chairperson = null;
}
