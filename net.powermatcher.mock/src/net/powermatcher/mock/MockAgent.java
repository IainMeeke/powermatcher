package net.powermatcher.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.powermatcher.api.Agent;
import net.powermatcher.api.AgentEndpoint;
import net.powermatcher.api.Session;
import net.powermatcher.api.data.Bid;
import net.powermatcher.api.data.PriceUpdate;
import net.powermatcher.api.monitoring.AgentObserver;
import net.powermatcher.api.monitoring.ObservableAgent;
import net.powermatcher.api.monitoring.events.AgentEvent;

public class MockAgent implements Agent, AgentEndpoint, ObservableAgent {

    private Map<String, Object> agentProperties;
    private PriceUpdate lastPriceUpdate;
    protected Session session;
    private String desiredParentId;

    private String servicePid;

    /**
     * Collection of {@link Observer} services.
     */
    private final Set<AgentObserver> observers = new CopyOnWriteArraySet<AgentObserver>();

    public MockAgent(String agentId) {
        this.agentProperties = new HashMap<String, Object>();
        this.agentProperties.put("agentId", agentId);
    }

    @Override
    public void connectToMatcher(Session session) {
        this.session = session;
    }

    @Override
    public void matcherEndpointDisconnected(Session session) {
        this.session = null;
    }

    @Override
    public void updatePrice(PriceUpdate priceUpdate) {
        this.lastPriceUpdate = priceUpdate;
    }

    public void sendBid(Bid newBid) {
        this.session.updateBid(newBid);
    }

    public PriceUpdate getLastPriceUpdate() {
        return lastPriceUpdate;
    }

    public Map<String, Object> getAgentProperties() {
        return agentProperties;
    }

    @Override
    public String getAgentId() {
        return (String) agentProperties.get("agentId");
    }

    @Override
    public String getClusterId() {
        return session == null ? null : session.getClusterId();
    }

    @Override
    public String getDesiredParentId() {
        return desiredParentId;
    }

    public void setDesiredParentId(String desiredParentId) {
        this.desiredParentId = desiredParentId;
    }

    @Override
    public void addObserver(AgentObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(AgentObserver observer) {
        observers.remove(observer);
    }

    public Session getSession() {
        return this.session;
    }

    public void publishEvent(AgentEvent event) {
        for (AgentObserver observer : observers) {
            observer.update(event);
        }
    }

    @Override
    public String getServicePid() {
        return this.servicePid;
    }
}
