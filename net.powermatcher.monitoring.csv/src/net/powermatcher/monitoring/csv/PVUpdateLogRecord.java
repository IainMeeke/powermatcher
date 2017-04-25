package net.powermatcher.monitoring.csv;

import java.text.DateFormat;
import java.util.Date;

import net.powermatcher.api.messages.PVUpdate;
import net.powermatcher.api.monitoring.events.PVUpdateEvent;

public class PVUpdateLogRecord
    extends LogRecord {

    private final PVUpdate pvUpdate;

    public PVUpdateLogRecord(PVUpdateEvent event, Date logTime, DateFormat dateFormat) {
        super(event.getClusterId(), event.getAgentId(), logTime, event.getTimestamp(), dateFormat);
        pvUpdate = event.getPvUpdate();

    }

    public PVUpdate getPvUpdate() {
        return pvUpdate;
    }

}
