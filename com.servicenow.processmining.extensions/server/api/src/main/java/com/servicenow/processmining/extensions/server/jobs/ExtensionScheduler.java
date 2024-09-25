package com.servicenow.processmining.extensions.server.jobs;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.TriggerBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class ExtensionScheduler
{
    private static ExtensionScheduler singleton = null;
    private Scheduler scheduler = null;

    public static ExtensionScheduler getScheduler()
    {
        if (singleton == null) {
            singleton = new ExtensionScheduler();
            singleton.start();
        }

        return singleton;
    }

    private ExtensionScheduler()
    {
    }

    private boolean start()
    {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            scheduler = sf.getScheduler();
            scheduler.start();
        }
        catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String scheduleJob(final String jobId, final String jobGroup, final Class<? extends Job> jobClass)
    {
        JobDetail job = null;

        try {
            job = JobBuilder.newJob(jobClass)
                .withIdentity("pm", jobGroup)
                .usingJobData("key", jobId)
                .build();

            SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity("pmtrigger", jobGroup)
                .startNow()
                .build();

            scheduler.scheduleJob(job, trigger);
        }
        catch (SchedulerException e) {
            e.printStackTrace();
            return null;
        }

        return job.getKey().toString();
    }

    public String scheduleProcessAuditLogsSyncJob(final String id)
    {
        return scheduleJob(id, "sync", SyncWorkflowAuditTrailJob.class);
    }

    public String scheduleProcessMineJob(final String id)
    {
        return scheduleJob(id, "mine", ProcessMineWorkflowJob.class);
    }
}
