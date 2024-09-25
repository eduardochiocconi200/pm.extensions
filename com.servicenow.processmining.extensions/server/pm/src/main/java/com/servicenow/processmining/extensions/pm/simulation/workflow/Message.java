package com.servicenow.processmining.extensions.pm.simulation.workflow;

import com.servicenow.processmining.extensions.pm.simulation.core.AbstractSimulator;
import com.servicenow.processmining.extensions.pm.simulation.core.Event;

public class Message
    extends Event
{
    private MessageHandler messageHandler = null;
    private String referenceId = null;
    private String from = null;
    private String to = null;
    private int messageType = REGULAR;
    private AbstractSimulator simulator = null;
    private static long serialMessageNumber = 0;

    public Message(final MessageHandler messageHandler, final String id, final String from, final String to, final double completionTime, final int mType)
    {
        super(completionTime, serialMessageNumber++);
        this.messageHandler = messageHandler;
        this.referenceId = id;
        this.from = from;
        this.to = to;
        this.messageType = mType;
    }

    public MessageHandler getMessageHandler()
    {
        return this.messageHandler;
    }

    public String getReferenceId()
    {
        return this.referenceId;
    }

    public String getFrom()
    {
        return this.from;
    }

    public String getTo()
    {
        return this.to;
    }

    public void updateMessageType(int mType)
    {
        this.messageType = mType;
    }

    public int getType()
    {
        return this.messageType;
    }

    public boolean isRegularMessageType()
    {
        return this.messageType == REGULAR;
    }

    public boolean isEnqueueMessageType()
    {
        return this.messageType == ENQUEUE;
    }

    public boolean isResumedMessageType()
    {
        return this.messageType == RESUME;
    }

    public AbstractSimulator getSimulator()
    {
        return this.simulator;
    }

    public void setDELETE(String referenceId, String from, String to, double completionTime)
    {
        this.referenceId = referenceId;
        this.from = from;
        this.time = completionTime;
    }

    public void execute(AbstractSimulator simulator)
    {
        this.simulator = simulator;
        if (messageHandler != null) {
            messageHandler.handle(this);
        }
    }

    public String toString()
    {
        return "[ Event: WI:[" + getReferenceId() + "] at: '" + getTime() + "' completed Node: '" + getFrom()
                + "' and will be route to Node: '" + getTo() + "'. Type: '" + getType(this.messageType) + "']";
    }

    private String getType(final int mType)
    {
        if (mType == REGULAR) {
            return "REGULAR";
        }
        else if (mType == ENQUEUE) {
            return "ENQUEUE";
        }
        else if (mType == RESUME) {
            return "RESUME";
        }
        else {
            throw new RuntimeException("Invalid Message Type: (" + getType() + ")");
        }
    }

    public static int REGULAR = 1;
    public static int ENQUEUE = 2;
    public static int RESUME = 3;
}