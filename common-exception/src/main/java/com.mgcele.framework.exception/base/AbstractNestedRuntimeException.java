package com.mgcele.framework.exception.base;


import com.mgcele.framework.exception.model.ExceptionInfo;
import com.mgcele.framework.exception.utils.CommonExceptionUtils;
import com.mgcele.framework.exception.utils.ExceptionTraceIdGenerator;
import com.mgcele.framework.system.SystemProperty;

/**
 * @author mgcele on 2017/4/29.
 */
public abstract class AbstractNestedRuntimeException extends RuntimeException implements Traceable, Describable{

    private String traceId;
    private final String code;
    private final transient Traceable parent;
    private transient Throwable cause;
    private ExceptionInfo rootExceptionInfo;
    private final Long timestamp;

    public AbstractNestedRuntimeException(String code)
    {
        this.code = (SystemProperty.getInstance().getSysName() + code);
        this.timestamp = Long.valueOf(System.currentTimeMillis());
        this.parent = null;
        this.traceId = ExceptionTraceIdGenerator.getInstance().getTraceId();
        this.rootExceptionInfo = CommonExceptionUtils.extractExceptionInfo(this);
    }

    public AbstractNestedRuntimeException(String code, String msg)
    {
        super(msg);
        this.code = (SystemProperty.getInstance().getSysName() + code);
        this.traceId = ExceptionTraceIdGenerator.getInstance().getTraceId();
        this.timestamp = Long.valueOf(System.currentTimeMillis());
        this.parent = null;
        this.rootExceptionInfo = CommonExceptionUtils.extractExceptionInfo(this);
    }

    public AbstractNestedRuntimeException(String code, String msg, Throwable e)
    {
        super(msg);
        this.cause = e;
        this.code = (SystemProperty.getInstance().getSysName() + code);
        if ((e instanceof Traceable))
        {
            this.parent = ((Traceable)e);
            this.timestamp = Long.valueOf(((Traceable)e).getTimestamp());
            this.traceId = getTraceId();
            this.rootExceptionInfo = ((Traceable)e).getRootExceptionInfo();
        }
        else
        {
            this.parent = null;
            this.traceId = getTraceId();
            this.timestamp = Long.valueOf(System.currentTimeMillis());
            this.rootExceptionInfo = CommonExceptionUtils.extractExceptionInfo(e);
        }
    }

    public AbstractNestedRuntimeException(String code, Throwable e)
    {
        this.code = (SystemProperty.getInstance().getSysName() + code);
        this.cause = e;
        if ((e instanceof Traceable))
        {
            this.parent = ((Traceable)e);
            this.timestamp = Long.valueOf(((Traceable)e).getTimestamp());
            this.traceId = getTraceId();
            this.rootExceptionInfo = ((Traceable)e).getRootExceptionInfo();
        }
        else
        {
            this.parent = null;
            this.timestamp = Long.valueOf(System.currentTimeMillis());
            this.rootExceptionInfo = CommonExceptionUtils.extractExceptionInfo(e);
        }
    }

    public Throwable getCause()
    {
        return this.cause;
    }

    public String getCode()
    {
        return this.code;
    }

    public String toString()
    {
        String name = getClass().getName();
        String traceId = getTraceId();
        String code = getCode();
        String message = getLocalizedMessage();

        StringBuilder sb = new StringBuilder();
        sb.append(name + ":");

        sb.append("trace[ " + traceId + " ], ");
        sb.append("code[ " + code + " ], ");
        sb.append("msg[ " + message + " ]");

        return sb.toString();
    }

    public String getTraceId()
    {
        if (this.traceId != null) {
            return this.traceId;
        }
        if (this.parent != null)
        {
            this.traceId = this.parent.getTraceId();
            return this.traceId;
        }
        try
        {
            this.traceId = ExceptionTraceIdGenerator.getInstance().getTraceId();
        }
        catch (Exception e)
        {
            this.traceId = null;
        }
        return this.traceId;
    }

    public ExceptionInfo getRootExceptionInfo()
    {
        if (this.rootExceptionInfo != null) {
            return this.rootExceptionInfo;
        }
        if (this.parent != null)
        {
            this.rootExceptionInfo = this.parent.getRootExceptionInfo();
            return this.rootExceptionInfo;
        }
        try
        {
            this.rootExceptionInfo = CommonExceptionUtils.extractExceptionInfo(this);
        }
        catch (Exception e)
        {
            this.rootExceptionInfo = null;
        }
        return this.rootExceptionInfo;
    }

    public long getTimestamp()
    {
        return this.timestamp.longValue();
    }

}
