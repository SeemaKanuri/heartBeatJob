/*
 * All rights reserved.
 */
package com.sdm.hw.heartbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.sdm.hw.exception.services.HwBaseAppException;
import com.sdm.hw.exception.services.HwSystemException;
import com.sdm.hw.logging.intf.HwLogger;
import com.sdm.hw.logging.services.LogManager;

/**
 * The Class CommandInvoker.
 */
public class CommandInvoker
{

    /** The Constant COMMAND_LISTENER_PORT. */
    public static final int COMMAND_LISTENER_PORT = 10111;

    /** The Constant SOCKET_TIMEOUT. */
    public static final int SOCKET_TIMEOUT = 120000;

    /** The Constant EOL_LINUX. */
    public static final String EOL_LINUX = "\n";

    /** The Constant EOL_WIN32. */
    public static final String EOL_WIN32 = "\r\n";

    /** The Constant EOL_MAC_LEGACY. */
    public static final String EOL_MAC_LEGACY = "\n\r";

    /** The Constant LOGGER. */
    private static final HwLogger LOGGER = LogManager
            .getLogger(CommandInvoker.class);
    /** The Constant ZERO. */
    private static final int ZERO=0;
    /** The Constant LINGER. */
    public static final int LINGER=500;

    /**
     * Instantiates a new command invoker.
     */
    private CommandInvoker()
    {
    }

    /**
     * Invoke command.
     * 
     * @param hostName
     *            the host name
     * @param command
     *            the command
     * @return the int
     * @throws HwBaseAppException
     *             the hw base app exception
     */
    public static int invokeCommand(final String hostName, final String command)
        throws HwBaseAppException

    {
        return CommandInvoker.invokeCommand(hostName, command, SOCKET_TIMEOUT);
    }

    /**
     * Invoke command.
     * 
     * @param hostName
     *            the host name
     * @param command
     *            the command
     * @param socketTimeout
     *            the socket timeout
     * @return the int
     * @throws HwBaseAppException
     *             the hw base app exception
     */
    public static int invokeCommand(final String hostName,
        final String command, final int socketTimeout)
        throws HwBaseAppException

    {
        Socket socket = null;
        try
        {
            socket = new Socket(hostName, COMMAND_LISTENER_PORT);
            socket.setSoTimeout(socketTimeout);
            socket.setSoLinger(true, LINGER);

            LOGGER.logInfo("invoking script: host= {}, port= {},command= {} ",
                    new Object[]
                    { hostName, COMMAND_LISTENER_PORT, command });

            final PrintWriter writer = new PrintWriter(socket.getOutputStream());

            final String operatingSystem = System.getProperty("os.name");
            if ((operatingSystem != null) && operatingSystem.equals("linux"))
            {
                writer.print(command);
                writer.print(EOL_LINUX);
            }
            else
            {
                writer.println(command);
            }

            writer.flush();

            /*
             * The hwng_execd returns a line for each command executed,
             * containing the numeric return code of the command. We sent one
             * command, so read the single return code.
             */
            final BufferedReader buffRead = new BufferedReader(new InputStreamReader(
            		socket.getInputStream()));
            final String returnValue = buffRead.readLine();
            LOGGER.logDebug("RETURN VALUE " + returnValue);

            // Let's be really cautious and handle the unlikely case of the
            // socket server crashing without causing any of the code above
            // to throw an exception LAR 2006-03-24.
            if (returnValue == null)
            {
                final String errorMessage = "ERROR: command=\"" + command
                        + "\" did not return a value";
                LogManager.getLogger(CommandInvoker.class).logError(
                        errorMessage);

                throw new HwSystemException(errorMessage);
            }
            final int result = Integer.parseInt(returnValue);
            LogManager.getLogger(CommandInvoker.class).logInfo("act--result:"+result);
            if (result != ZERO)
            {
                /*
                 * In UNIX return codes, top byte holds return code of script.
                 * Use it if non-zero, else use the bottom byte, which will be
                 * non-zero if there was some sort of system error (this info
                 * drawn from the sketchy memory of Ivor Williams).
                 */
                int returnCode = result >> 8;
                if (returnCode == ZERO)
                {
                    returnCode = result & 0xFF;
                }

                LogManager.getLogger(CommandInvoker.class).logError(
                        "command=\"" + command + "\" returnCode=" + returnCode);
            }
            return result;
        }
        catch (IOException exp)
        {

            throw new HwSystemException(exp.getMessage(), exp);
        }
        finally
        {
            if (socket != null)
            {
                try
                {
                    socket.close();
                }
                catch (IOException exp)
                {
                    LOGGER.logError(exp.getMessage(), exp);
                }
            }
        }
    }
}
