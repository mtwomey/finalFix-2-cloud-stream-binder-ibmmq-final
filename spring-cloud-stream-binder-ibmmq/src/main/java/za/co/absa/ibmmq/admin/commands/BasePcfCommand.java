/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin.commands;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import za.co.absa.ibmmq.IbmMqException;
import za.co.absa.ibmmq.admin.IbmMqNativeClient;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFMessage;

/**
 * Base class for all PCF Commands.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public abstract class BasePcfCommand<T, P extends BasePcfCommand.Params> implements Command<T, P> {
    @Autowired
    private IbmMqNativeClient ibmMqNativeClient;

    /**
     * Checks whether the command result is a success response.
     *
     * @param res
     *            the result PCFMessages
     * @return whether it is a success response.
     */
    protected final boolean isSuccessfulResponse(final PCFMessage[] res) {
        return res.length >= 1 && res[0].getCompCode() == 0;
    }

    /**
     * A template method that will executes the command, It will delegate the request message creation to the
     * params.toRequest() method and will delegate handling success/error responses to corresponding methods.
     *
     * @param params
     *            the parameters of this command.
     * @return the execution result.
     * @throws IbmMqException
     *             if any error occurs during operation .
     */
    @Override
    public final T execute(final P params) throws IbmMqException {
        params.validate();

        try {
            final PCFMessage[] res = ibmMqNativeClient.getPcfMessageAgent().send(params.toRequest());
            if (isSuccessfulResponse(res)) {
                return handleSuccessResponse(res, params);
            } else {
                throw new IbmMqException(String.format("PCF Command Failed, Reason Code (%d) (%s)", res[0].getReason(),
                        MQConstants.lookupReasonCode(res[0].getReason())));
            }
        } catch (final MQException e) {
            return handleErrorResponse(e.getReason());
        } catch (final IOException e) {
            throw new IbmMqException(String.format("Error executing command (%s)", this.getClass().getSimpleName()), e);
        }

    }

    /**
     * Default implementation for error responses, it will throw IbmMqException resolving the given error code.
     *
     * @param reason
     *            the reason code.
     * @return The execution result in case of error response.
     * @throws IbmMqException
     *             an Exception wrapping the given reason code.
     */
    protected T handleErrorResponse(final int reason) throws IbmMqException {
        throw new IbmMqException(
                String.format("PCF Command Failed, Reason Code (%s)", MQConstants.lookupReasonCode(reason)));
    }

    /**
     * A hook for subclasses to handle the success response and create an instance of "T" class.
     *
     * @param res
     *            the result PCFMessages
     * @param params
     *            the passed parameters to the execute method.
     * @return The execution result in case of success response.
     * @throws IbmMqException
     */
    protected abstract T handleSuccessResponse(PCFMessage[] res, P params) throws IbmMqException;

    /**
     * The base Params interface for all PCF Commands
     */
    public interface Params extends Command.Params {
        PCFMessage toRequest();

    }
}
