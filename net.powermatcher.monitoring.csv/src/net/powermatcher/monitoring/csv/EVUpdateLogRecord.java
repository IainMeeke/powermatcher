package net.powermatcher.monitoring.csv;

import java.text.DateFormat;
import java.util.Date;

import net.powermatcher.api.messages.EVUpdate;
import net.powermatcher.api.monitoring.events.EVUpdateEvent;

public class EVUpdateLogRecord
    extends LogRecord {

    private final EVUpdate evUpdate;

    public EVUpdateLogRecord(EVUpdateEvent event, Date logTime, DateFormat dateFormat) {
        super(event.getClusterId(), event.getAgentId(), logTime, event.getTimestamp(), dateFormat);
        evUpdate = event.getEVUpdate();

    }

    public EVUpdate getEvUpdate() {
        return evUpdate;
    }

}
