package tcd.iainmeeke.battery;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.flexiblepower.context.FlexiblePowerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;
import net.powermatcher.api.AgentEndpoint;
import net.powermatcher.api.data.Bid;
import net.powermatcher.api.data.MarketBasis;
import net.powermatcher.api.data.PointBidBuilder;
import net.powermatcher.api.messages.BidUpdate;
import net.powermatcher.api.messages.PriceUpdate;
import net.powermatcher.api.monitoring.ObservableAgent;
import net.powermatcher.core.BaseAgentEndpoint;

/**
 * {@link PVPanelAgent} is a implementation of a {@link BaseAgentEndpoint}. It represents a dummy wind turbine.
 * {@link PVPanelAgent} creates a {@link PointBid} with random {@link PricePoint}s at a set interval. It does nothing
 * with the returned {@link Price}.
 * 
 * @author FAN
 * @version 2.0
 */
@Component(
        designateFactory = Battery.Config.class,
        immediate = true,
        provide = { ObservableAgent.class, AgentEndpoint.class })
public class Battery extends BaseAgentEndpoint implements AgentEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(Battery.class);
    private final Object lock = new Object();
    private Config config;

    public static interface Config {
        @Meta.AD(deflt = "bat", description = "The unique identifier of the agent")
        String agentId();

        @Meta.AD(
                deflt = "concentrator",
                description = "The agent identifier of the parent matcher to which this agent should be connected")
        public String desiredParentId();

        @Meta.AD(deflt = "30", description = "Number of seconds between bid updates")
        long bidUpdateRate();

        @Meta.AD(deflt = "13500", description = "Capacity of the battery.")
        double batteryCapacity();

        @Meta.AD(deflt = "2000", description = "The Battery Charge power")
        double batteryCharge();

        @Meta.AD(deflt = "2000", description = "The Battery Output power")
        double batteryOutput();
    }

    /**
     * A delayed result-bearing action that can be cancelled.
     */
    private ScheduledFuture<?> scheduledFuture;

    private double batteryCapacity; //the size of the battery
    private double chargePower; //the power the battery can charge at
    private double outputPower;

    private BatterySimulation battery;

    /**
     * OSGi calls this method to activate a managed service.
     * 
     * @param properties
     *            the configuration properties
     */
    @Activate
    public void activate(Map<String, Object> properties) {
        config = Configurable.createConfigurable(Config.class, properties);
        init(config.agentId(), config.desiredParentId());

        batteryCapacity = config.batteryCapacity();
        chargePower = config.batteryCharge();
        outputPower = config.batteryOutput();

        LOGGER.info("Agent [{}], activated", config.agentId());
    }

    /**
     * OSGi calls this method to deactivate a managed service.
     */
    @Override
    @Deactivate
    public void deactivate() {
        super.deactivate();
        scheduledFuture.cancel(false);
        LOGGER.info("Agent [{}], deactivated", getAgentId());
    }

    /**
     * {@inheritDoc}
     */
    void doBidUpdate() {
        AgentEndpoint.Status currentStatus = getStatus();

        if (currentStatus.isConnected()) {
            battery.updateStatus();
            MarketBasis mb = currentStatus.getMarketBasis();
            double batteryLevel = battery.getCurrentChargeLevel();
            double demand = battery.getChargePower();
            double output = battery.getOutputPower();
            
            synchronized (lock) {
                if (batteryLevel == 0) { 
                    publishBid(Bid.flatDemand(currentStatus.getMarketBasis(), demand));
                } else if (batteryLevel == 1) {
                    publishBid(Bid.flatDemand(currentStatus.getMarketBasis(), -output));
                } else {
                    publishBid(new PointBidBuilder(mb).add(mb.getMaximumPrice() * (1-batteryLevel), demand)
                            .add(mb.getMaximumPrice() * (1-batteryLevel), -output).build());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void handlePriceUpdate(PriceUpdate priceUpdate) {
        super.handlePriceUpdate(priceUpdate);
        BidUpdate lastBidUpdate = getLastBidUpdate();
        double demandForCurrentPrice = 0; //set to zero initially and then overwrite it if we need to
        if (lastBidUpdate == null) {
            LOGGER.info("Ignoring price update while no bid has been sent");
        } else if (lastBidUpdate.getBidNumber() != priceUpdate.getBidNumber()) {
            LOGGER.info("Ignoring price update on old bid (lastBid={} priceUpdate={})", lastBidUpdate.getBidNumber(),
                    priceUpdate.getBidNumber());
        } else {
            demandForCurrentPrice = getLastBidUpdate().getBid().getDemandAt(priceUpdate.getPrice()); // the demand at that price
            synchronized (lock) {
                battery.setChargingAt(demandForCurrentPrice); // set the battery to charge at this power
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContext(FlexiblePowerContext context) {
        super.setContext(context);
        battery = new BatterySimulation(super.context, outputPower, batteryCapacity, chargePower);
        scheduledFuture = context.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                doBidUpdate();
            }
        }, Measure.valueOf(0, SI.SECOND), Measure.valueOf(config.bidUpdateRate(), SI.SECOND));
    }
}
