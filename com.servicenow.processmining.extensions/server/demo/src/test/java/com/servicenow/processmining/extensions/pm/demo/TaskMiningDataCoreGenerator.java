package com.servicenow.processmining.extensions.pm.demo;

import com.servicenow.processmining.extensions.sn.core.ServiceNowInstance;
import com.servicenow.processmining.extensions.sn.core.ServiceNowRESTService;
import com.servicenow.processmining.extensions.sn.core.ServiceNowTestCredentials;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TaskMiningDataCoreGenerator
{
    private ServiceNowInstance instance = null;

    public static void main(String[] args)
    {
        TaskMiningDataCoreGenerator tm = new TaskMiningDataCoreGenerator();
        //tm.insertTaskMiningCoreDataRecord();
        long timeMillis = 1748779539000L;
        Date d = new Date(timeMillis);
        System.out.println("d: (" + d + ")");
    }

    public TaskMiningDataCoreGenerator()
    {
    }
    
    public boolean insertTaskMiningCoreDataRecord()
    {
        ServiceNowRESTService snrs = new ServiceNowRESTService(getInstance());
		String url = "https://" + getInstance().getInstance() + "/api/sn_tm_core/taskmininginteraction?sysparm_display_value=false";
		String payload = createPayload();
        System.out.println("Sending Payload: (" + payload + ")");
		String response = snrs.executePostRequest(url, payload);
		if (response == null || response != null && response.equals("")) {
			return false;
		}

        return true;
    }

    private String createPayload()
    {
        String payload = "[\n";
        payload += "{\n";
        payload += "\"userId\": \"abraham.lincoln\", \n";
        payload += "\"hostName\": \"servicenowco500\", \n";
        payload += "\"applicationName\": \"com.microsoft.Word\", \n";
        payload += "\"screenName\": \"Document1\", \n";
        payload += "\"url\": \"\", \n";
        DateTime taskDateTimeUTC = new DateTime(2025, 6, 5, 12, 0, 0).withZone(DateTimeZone.UTC);
        DateTime taskDateTime = taskDateTimeUTC;
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"; // Example: 2025-10-20 20:19:00.123
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        String formattedDateTimeUTC = formatter.print(taskDateTimeUTC);
        String formattedDateTimeUTC2 = "2025-06-05T08:00:00.000+07:00";
        String formattedDateTime = formatter.print(taskDateTime);
        System.out.println("startDateTimeUTC: (" + formattedDateTimeUTC + "), TZ: (" + taskDateTimeUTC.getZone() + ")");
        System.out.println("startDateTimeUTC2: (" + formattedDateTimeUTC2 + ")");
        System.out.println("startDateTime: (" + formattedDateTime + ")");
        payload += "\"startDateTime\": \"" + formattedDateTimeUTC2 + "\", \n";
        payload += "\"duration\": 629.212, \n";
        payload += "\"mouseClickCount\": 19 \n";
        payload += "}\n";
        payload += "]";
    
        return payload;
    }

    private ServiceNowInstance getInstance()
    {
        if (this.instance == null) {
            this.instance = new ServiceNowInstance(snInstance, snUser, snPassword);
        }

        return this.instance;
    }

    protected static final String snInstance = ServiceNowTestCredentials.getInstanceName();
    protected static final String snUser = ServiceNowTestCredentials.getUserName();
    protected static final String snPassword = "Galaxy!2390";
}