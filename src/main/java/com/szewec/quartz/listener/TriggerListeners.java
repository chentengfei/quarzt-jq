package com.szewec.quartz.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriggerListeners implements TriggerListener{

	private Logger logger = LoggerFactory.getLogger(TriggerListeners.class);
    public String getName() {
        return "TriggerListeners";
    }

    public void triggerFired(Trigger trigger, JobExecutionContext context) {
//    	logger.info("触发器：{},开始执行",trigger.getKey().getName());
    }

    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    public void triggerMisfired(Trigger trigger) {
    	logger.info("触发器：{},偏离计划",trigger.getKey().getName());
    }

    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
//    	logger.info("触发器：{},执行完成",trigger.getKey().getName());
    }
}