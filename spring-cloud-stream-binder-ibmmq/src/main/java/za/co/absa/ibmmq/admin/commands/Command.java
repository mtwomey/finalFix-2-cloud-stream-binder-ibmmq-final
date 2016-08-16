/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin.commands;

import za.co.absa.ibmmq.IbmMqException;

/**
 * Encapsulates a command to be executed against IBM MQ instance.
 *
 * @author ahmed.seddiq
 * @version 1.0
 *
 * @param T
 *            the return type of the execution.
 * @param Q
 *            the parameters type that will be passed the the execute method
 */
public interface Command<T, P extends Command.Params> {
    /**
     * Executes this commands.
     *
     * @param params
     *            the parameters of this command.
     * @return the return value for this command
     * @throws IbmMqException
     *             should wrap any error occur during operation.
     */
    T execute(P params) throws IbmMqException;

    /**
     * Creates a new instance of the expected Params class for the execute method.
     *
     * @return a new instance of the Params class.
     */
    P newParams();

    /**
     * This interface should be implemented by any Params class for all concrete Commands
     */
    interface Params {
        /**
         * Validates the parameters for execution
         *
         * @throws IllegalArgumentException
         *             if any parameter is invalid.
         */
        void validate() throws IllegalArgumentException;

        /**
         * A Helper method to safely convert string to upper case.
         *
         * @param str
         *            the string to convert.
         * @return the upper case of the string or null if the original string is null.
         */
        default String safeUpperCase(final String str) {
            return str != null ? str.toUpperCase() : null;
        }

    }
}
