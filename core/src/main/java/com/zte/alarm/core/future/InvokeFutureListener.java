package com.zte.alarm.core.future;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 9:49
 */
public interface InvokeFutureListener {
    /**
     * 完成操作
     *
     * @param future
     * @throws Exception
     */
    void operationComplete(InvokeFuture future) throws Exception;
}
