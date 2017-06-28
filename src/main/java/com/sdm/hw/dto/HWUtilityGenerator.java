package com.sdm.hw.dto;

import java.util.Map;

/**
 * The Class HWUtilityGenerator.
 */
public class HWUtilityGenerator 
{
	private Map<String,String> urlMap;
	private String schedulerInstanceCount;
	private int consecutiveURLHits;
	private int sleepTime;
	private String quartzSchedulerProcess;
	public static final int MILLI_SEC=1000;

	public String getQuartzSchedulerProcess() {
		return quartzSchedulerProcess;
	}

	public void setQuartzSchedulerProcess(final String quartzSchedulerProcess) {
		this.quartzSchedulerProcess = quartzSchedulerProcess;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(final int sleepTime) {
		this.sleepTime = sleepTime*MILLI_SEC;
	}

	public Map<String,String> getUrlMap() {
		return urlMap;
	}

	public void setUrlMap(final Map<String,String> urlMap) {
		this.urlMap = urlMap;
	}

	public String getSchedulerInstanceCount() {
		return schedulerInstanceCount;
	}

	public void setSchedulerInstanceCount(final String schedulerInstanceCount) {
		this.schedulerInstanceCount = schedulerInstanceCount;
	}

	public int getConsecutiveURLHits() {
		return consecutiveURLHits;
	}

	public void setConsecutiveURLHits(final int consecutiveURLHits) {
		this.consecutiveURLHits = consecutiveURLHits;
	}
	
	
}
