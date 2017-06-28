package com.sdm.hw.heartbeat;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sdm.hw.dto.HWUtilityGenerator;
import com.sdm.hw.dto.ProcessCheckResponse;
import com.sdm.hw.exception.services.HwBaseAppException;
import com.sdm.hw.heartbeat.utilties.CommandUtilities;
import com.sdm.hw.heartbeat.utilties.HeartBeatConstants;
import com.sdm.hw.logging.intf.HwLogger;
import com.sdm.hw.logging.services.LogManager;
import com.sdm.hw.serviceinvocation.exceptions.ServiceNotSupportedException;
import com.sdm.hw.store.delegate.HwStoreServiceDelegate;

public class HwHeartbeatMain 
{

	/*private static URL url;
	private static URLConnection connection;
	private static HttpURLConnection httpConnection;
	private static int responseCode;*/
	private static boolean serviceStatus=false;

	static final HwLogger LOGGER = LogManager.getLogger(HwHeartbeatMain.class);

	public static void main(String[] args) 
	{
		ApplicationContext context = new ClassPathXmlApplicationContext(HeartBeatConstants.APPLICATION_CONTEXT);
		HWUtilityGenerator heartBeatUtil = (HWUtilityGenerator) context.getBean("HWGenerateUrlMap");
		//Map<String, String> map = heartBeatUtil.getUrlMap();
		int hitCount=heartBeatUtil.getConsecutiveURLHits();

		//Collection<String> urlList =map.values();
		//LOGGER.logInfo("checking URL ::" + urlList);
		try {
			//	Object[] urls=urlList.toArray();
			//	url = new URL(HeartBeatConstants.HTTP + String.valueOf(urls[0]));
			//	connection = url.openConnection();
			//	httpConnection = (HttpURLConnection) connection;
				for(int i=1;i<=hitCount&&!serviceStatus; i++)
					{
					try
					{
						HwStoreServiceDelegate.getInstance().isStoreServerUp();
/*						HwReportServiceDelegate.getInstance().isReportServerUp();*/
						serviceStatus=true;
					}
					catch(HwBaseAppException e)
					{
						serviceStatus=false;
						if(e instanceof ServiceNotSupportedException)
						{
							LOGGER.logInfo("Server is Stopped!!HeartBeat could not connect to Server");
							ProcessCheckResponse processResponse = CommandUtilities.executePsCheck(
									heartBeatUtil.getQuartzSchedulerProcess(),
									heartBeatUtil.getSchedulerInstanceCount());
							if ((processResponse.isInstanceCountMoreThanOne() || processResponse.isInstanceCountOne()))
							{
								LOGGER.logInfo("Quartz is Running, kill Running process");
								CommandUtilities.executePsKillAll(heartBeatUtil.getQuartzSchedulerProcess());	
							}
						}
						else
						{
							LOGGER.logError("Some IO Exception other than Connection Exception");
						}
					}
				
					//responseCode = httpConnection.getResponseCode();
					/*	if (responseCode == HeartBeatConstants.ERROR_RESPONSE_CODE) // service is stopped. stop scheduler
						{
							LOGGER.logInfo("Service is Stopped!!");
							serviceStatus=false;
						} 
						else 
						{
							serviceStatus=true;
							break;
						}*/
						Thread.sleep(heartBeatUtil.getSleepTime());
						LOGGER.logInfo("Awake from sleep for time"+i);
					}
				if (!serviceStatus) // service is stopped. stop scheduler
				{
					LOGGER.logInfo("Service is Stopped!! Checking QuartzScheduler process count..");
					ProcessCheckResponse processResponse = CommandUtilities
						.executePsCheck(
								heartBeatUtil.getQuartzSchedulerProcess(),
								heartBeatUtil.getSchedulerInstanceCount());
				
					if ((processResponse.isInstanceCountMoreThanOne() || processResponse.isInstanceCountOne()))	
					{
						LOGGER.logInfo("One or more QuartzScheduler is Running, kill Running process");
						CommandUtilities.executePsKillAll((heartBeatUtil.getQuartzSchedulerProcess()));	
					}
				} 
				else // service is started. check scheduler ,if stopped,start scheduler
				{
					LOGGER.logInfo("Service is Started!!");
					ProcessCheckResponse processResponse = CommandUtilities
						.executePsCheck(
								heartBeatUtil.getQuartzSchedulerProcess(),
								heartBeatUtil.getSchedulerInstanceCount());
					if (!(processResponse.isInstanceCountMoreThanOne() || processResponse.isInstanceCountOne()))
					{
						LOGGER.logInfo("QuartzScheduler is Stopped... Starting...!!");
					
						CommandUtilities.executeBatchScript(HeartBeatConstants.QUARTZ_BATCHES_SCRIPT);
					
						LOGGER.logInfo("QuartzScheduler is started by HeartBeat!!");
					}
					else
					{
						LOGGER.logInfo("Single or Multiple Instances of QuartzScheduler may be running... Monitoring and removing multiple instances...!!");
						CommandUtilities.executePsKill((heartBeatUtil.getQuartzSchedulerProcess()));
					}
				}

			} 
			catch (InterruptedException e) 
			{
				LOGGER.logError("InterruptedException");
			}
		}

}
