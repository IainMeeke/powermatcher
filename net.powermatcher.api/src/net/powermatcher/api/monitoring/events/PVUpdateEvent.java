package net.powermatcher.api.monitoring.events;

import java.util.Date;

import net.powermatcher.api.AgentEndpoint;
import net.powermatcher.api.Session;
import net.powermatcher.api.messages.PVUpdate;

public class PVUpdateEvent
    extends AgentEvent {
    /**
     * The id of the {@link Session} of the {@link AgentEndpoint} subclass sending the UpdateEvent
     */
    private final String sessionId;

    private final PVUpdate pvUpdate;

    public PVUpdateEvent(String clusterId,
                         String agentId,
                         String sessionId,
                         Date timestamp,
                         PVUpdate pvUpdate) {
        super(clusterId, agentId, timestamp);
        this.sessionId = sessionId;
        this.pvUpdate = pvUpdate;
    }

    /**
     * @return the current value of session.
     */
    public String getSessionId() {
        return sessionId;
    }

    public PVUpdate getPvUpdate() {
        return pvUpdate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString()
               + ", sessionId = "
               + sessionId;
    }
}
