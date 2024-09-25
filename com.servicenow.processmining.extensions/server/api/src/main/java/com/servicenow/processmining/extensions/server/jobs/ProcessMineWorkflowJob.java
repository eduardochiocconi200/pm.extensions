package com.servicenow.processmining.extensions.server.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMineWorkflowJob
    implements Job
{
    public void execute(JobExecutionContext context)
        throws JobExecutionException
    {
        logger.debug("Job is running process mine job for workflow with id: (" + context.getMergedJobDataMap().get("key") + ") ...");
    }

    private static final Logger logger = LoggerFactory.getLogger(ProcessMineWorkflowJob.class);
}
