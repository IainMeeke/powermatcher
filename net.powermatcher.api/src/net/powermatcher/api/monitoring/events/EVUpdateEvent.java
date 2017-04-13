package net.powermatcher.api.monitoring.events;

import java.util.Date;

import net.powermatcher.api.AgentEndpoint;
import net.powermatcher.api.Session;
import net.powermatcher.api.messages.EVUpdate;

public class EVUpdateEvent
    extends AgentEvent {
    /**
     * The id of the {@link Session} of the {@link AgentEndpoint} subclass sending the UpdateEvent
     */
    private final String sessionId;

    private final EVUpdate evUpdate;

    public EVUpdateEvent(String clusterId,
                         String agentId,
                         String sessionId,
                         Date timestamp,
                         EVUpdate evUpdate) {
        super(clusterId, agentId, timestamp);
        this.sessionId = sessionId;
        this.evUpdate = evUpdate;
    }

    /**
     * @return the current value of session.
     */
    public String getSessionId() {
        return sessionId;
    }

    public EVUpdate getEVUpdate() {
        return evUpdate;
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
