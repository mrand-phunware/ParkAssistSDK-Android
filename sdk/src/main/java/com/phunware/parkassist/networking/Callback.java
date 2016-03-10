package com.phunware.parkassist.networking;

/**
 * Created by mrand on 3/10/16.
 * Communicates responses from the ParkAssist SDK. One and only one method will be invoked in
 * response to a given request.
 * <p>
 * Callbacks are executed on the calling thread.
 *
 * @param <T> expected response type.
 */
public interface Callback<T> {

    /**
     * Successfule SDK response.
     *
     * @param data The response.
     */
    void onSuccess(T data);

    /**
     * Invoked when an unexpected exception occurred during the SDK request.
     * @param e The error received during the request.
     */
    void onFailed(Throwable e);
}


