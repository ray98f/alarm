package com.zte.alarm.core.future;

import com.zte.alarm.core.exception.SocketRuntimeException;
import com.zte.alarm.core.exception.SocketTimeoutException;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wshmang@163.com
 * @date 2021/3/9 9:48
 */
@Slf4j
public class InvokeFuture {

    protected Object result;
    protected AtomicBoolean done = new AtomicBoolean(false);
    protected AtomicBoolean success = new AtomicBoolean(false);
    protected Semaphore semaphore = new Semaphore(0);
    protected Throwable cause;
    protected Channel channel;
    protected Object attachment;
    protected List<InvokeFutureListener> listeners = new ArrayList<>();

    public InvokeFuture() {
    }

    public void addListener(InvokeFutureListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can not be null.");
        }
        notifyListener(listener);
        listeners.add(listener);
    }

    private void notifyListeners() {
        if (isDone()) {
            for (InvokeFutureListener listener : listeners) {
                try {
                    listener.operationComplete(this);
                } catch (Exception ex) {
                    log.error("Failed to notify listeners when operation completed.", ex);
                }
            }
        }
    }

    private void notifyListener(InvokeFutureListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can not be null.");
        }

        if (isDone()) {
            try {
                listener.operationComplete(this);
            } catch (Exception ex) {
                log.error("Failed to notify listener when operation completed.", ex);
            }
        }
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return done.get();
    }

    public Object getResult() throws SocketRuntimeException {
        if (!isDone()) {
            try {
                semaphore.acquire();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (cause != null) {
            throw new SocketRuntimeException(cause);
        }
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
        done.set(true);
        success.set(true);

        semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
        notifyListeners();
    }

    public Object getResult(long timeout, TimeUnit unit) {
        if (!isDone()) {
            try {
                if (!semaphore.tryAcquire(timeout, unit)) {
                    setCause(new SocketTimeoutException("time out."));
                }
            } catch (InterruptedException ex) {
                throw new SocketTimeoutException(ex);
            }
        }
        if (cause != null) {
            throw new SocketRuntimeException(cause);
        }
        return this.result;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
        done.set(true);
        success.set(false);
        semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
        notifyListeners();
    }

    public boolean isSuccess() {
        return success.get();
    }

    public Throwable getCause() {
        return cause;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

}
