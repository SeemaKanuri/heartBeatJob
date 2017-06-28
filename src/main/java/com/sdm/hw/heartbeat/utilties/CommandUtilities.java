package com.sdm.hw.heartbeat.utilties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.sdm.hw.dto.ProcessCheckResponse;
import com.sdm.hw.exception.services.HwBaseAppException;
import com.sdm.hw.heartbeat.CommandInvoker;
import com.sdm.hw.logging.intf.HwLogger;
import com.sdm.hw.logging.services.LogManager;

/**
 * This class provides os call methods to be used by other classes in the
 * package.
 * 
 * @author TCS
 */
public class CommandUtilities {


	/** The Constant LOCALHOST. */
	private static final String LOCALHOST = "localhost";

	/** The Constant LOGGER. */
	private static final HwLogger LOGGER = LogManager
			.getLogger(CommandUtilities.class);

	private static final String QUARTZ_SCRIPT_PATH="/h/silverstream/Batch/";
	private static final String CURRENT_DIR="cd ";
	public static final int RETURN_CODE_VAL=99;
	private static final int ZERO=0;
	
	/**
	 * Generic command.
	 * 
	 * @param commandToSend
	 *            the command to send
	 * @return the int
	 */
	public static int genericCommand(final String commandToSend) {
		int returnCode = RETURN_CODE_VAL;

		try {
			returnCode = CommandInvoker.invokeCommand(LOCALHOST, commandToSend);
		}

		catch (HwBaseAppException exp) {
			LOGGER.logError(exp.getMessage());
		}

		return returnCode;
	}

	/**
	 * Execute ps command.
	 * 
	 * @param source
	 *            the source
	 * @param dest
	 *            the dest
	 * @return the int
	 */
	public static int executePsKillAll(final String processName) {
		String commandToSend;
		int returnCode = RETURN_CODE_VAL;
		try {
			
			/* Comman to execute : ps -ef | grep java | grep
			 com.sdm.hw.batches.tasks.scheduler.MainScheduler | awk '{print
			 $2}' | xargs -i{} kill {} */
			commandToSend = CURRENT_DIR + QUARTZ_SCRIPT_PATH + ";./"
					+ HeartBeatConstants.PROCESS_CHECK_SCRIPT
					+ HeartBeatConstants.KILL_ALL_ARGS;
			returnCode = CommandInvoker.invokeCommand(LOCALHOST, commandToSend);
			LOGGER.logInfo("Return Code is :" + returnCode);
		} catch (HwBaseAppException exp) {
			LOGGER.logError(exp.getMessage(), exp);
		}
		return returnCode;
	}
	
	
	public static int executePsKill(final String processName) {
		String commandToSend;
		int returnCode = RETURN_CODE_VAL;
		try {
			
			/* Comman to execute : ps -ef | grep java | grep
			 com.sdm.hw.batches.tasks.scheduler.MainScheduler | awk '{print
			 $2}' | xargs -i{} kill {} */
			commandToSend = CURRENT_DIR + QUARTZ_SCRIPT_PATH + ";./"
					+ HeartBeatConstants.PROCESS_CHECK_SCRIPT
					+ HeartBeatConstants.KILL_ARGS;
			returnCode = CommandInvoker.invokeCommand(LOCALHOST, commandToSend);
			LOGGER.logInfo("Return Code is :" + returnCode);
		} catch (HwBaseAppException exp) {
			LOGGER.logError(exp.getMessage(), exp);
		}
		return returnCode;
	}

	/**
	 * Execute Batch Script command.
	 * 
	 * @param source
	 *            the source
	 * @param dest
	 *            the dest
	 * @return the int
	 */
	public static int executeBatchScript(final String scriptName) {
		String commandToSend;
		
		int returnCode = RETURN_CODE_VAL;
		try {
			
			commandToSend = CURRENT_DIR+ QUARTZ_SCRIPT_PATH +";.//" + scriptName;
			returnCode = CommandInvoker.invokeCommand(LOCALHOST, commandToSend);
			LOGGER.logInfo("Return Code is :" + returnCode);
		} catch (HwBaseAppException exp) {
			LOGGER.logError(exp.getMessage(), exp);
		}
		return returnCode;
	}
	
	/**
	 * Execute ps command.
	 * 
	 * @param source
	 *            the source
	 * @param dest
	 *            the dest
	 * @return the int
	 */
	public static ProcessCheckResponse executePsCheck(final String processName,String fileName) {
		ProcessCheckResponse processResponse=new ProcessCheckResponse();
		String commandToSend;
		int returnCode = RETURN_CODE_VAL;
		processResponse.setInstanceCountOne(true);
		BufferedReader buffReader = null ;
		try {
						
			commandToSend= CURRENT_DIR+ QUARTZ_SCRIPT_PATH +";.//"+HeartBeatConstants.PROCESS_CHECK_SCRIPT;
			returnCode = CommandInvoker.invokeCommand(LOCALHOST, commandToSend);
			LOGGER.logInfo("Return Code is :" + returnCode);
			if(returnCode==ZERO)
			{
				File file=new File(fileName);
				
				LOGGER.logInfo("Is File Readable:"+file.canRead());
				
				if(file.exists() && file.canRead())
				{
					
					try
					{
					buffReader=new BufferedReader(new FileReader(file));
					int check=Integer.parseInt(buffReader.readLine());
					switch(check)
					{
					case 0:
							processResponse.setInstanceCountZero(true);
							processResponse.setInstanceCountMoreThanOne(false);
							processResponse.setInstanceCountOne(false);
							LOGGER.logInfo("Number of Scheduler Instances from file "
									+ check
									+ " isSchedulerRunning"
									+ processResponse.isInstanceCountZero());
							break;
					case 1:
							processResponse.setInstanceCountOne(true);
							processResponse.setInstanceCountZero(false);
							processResponse.setInstanceCountMoreThanOne(false);
							LOGGER.logInfo("Number of Scheduler Instances from file "
									+ check
									+ " isSchedulerRunning"
									+ processResponse.isInstanceCountOne());
							break;
					default:
							processResponse.setInstanceCountMoreThanOne(true);
							processResponse.setInstanceCountOne(false);
							processResponse.setInstanceCountZero(false);
							LOGGER.logInfo("Number of Scheduler Instances from file "
									+ check
									+ " isSchedulerRunning"
									+ processResponse.isInstanceCountMoreThanOne());
							break;
					}
					//isSchedulerRunning=(check=1)?true:false;
					
						
					return processResponse;
					
					} catch (FileNotFoundException e) {
						LOGGER.logError("File Not found",e);
					} catch (IOException e) {
						LOGGER.logError("IO Problem",e);					}
				}
				else
				{
					LOGGER.logInfo("Something went wrong in command execution");
				}
			}
		} catch (HwBaseAppException exp) {
			LOGGER.logError(exp.getMessage(), exp);
		}
		
		finally
		{
			if (buffReader != null)
			{
				
			try 
			{
				
				buffReader.close();
				
			} catch (IOException e) {
				
				LOGGER.logError("IO Exception",e);
			}
			
			}
		}
		return processResponse;
	}
	
	


}
