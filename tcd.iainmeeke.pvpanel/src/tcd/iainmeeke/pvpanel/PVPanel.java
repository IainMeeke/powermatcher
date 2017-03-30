package tcd.iainmeeke.pvpanel;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.io.IOException;
import java.util.Date;

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.apache.http.client.ClientProtocolException;
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
import net.powermatcher.api.messages.PriceUpdate;
import net.powermatcher.api.monitoring.ObservableAgent;
import net.powermatcher.core.BaseAgentEndpoint;

/**
 * {@link PVPanelAgent} is a implementation of a {@link BaseAgentEndpoint}. It represents a dummy pv panel.
 * {@link PVPanelAgent} creates a {@link PointBid} with random {@link PricePoint}s at a set interval. It does nothing
 * with the returned {@link Price}.
 * 
 * @author FAN
 * @version 2.0
 */
@Component(designateFactory = PVPanel.Config.class,
           immediate = true,
           provide = { ObservableAgent.class, AgentEndpoint.class })
public class PVPanel
    extends BaseAgentEndpoint
    implements AgentEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(PVPanel.class);

    private Config config;

    public static interface Config {
        @Meta.AD(deflt = "pvpanel", description = "The unique identifier of the agent")
        String agentId();

        @Meta.AD(deflt = "concentrator",
                 description = "The agent identifier of the parent matcher to which this agent should be connected")
        public String desiredParentId();

        @Meta.AD(deflt = "5", description = "Number of seconds between bid updates")
        long bidUpdateRate();

        @Meta.AD(deflt = "53", description = "latitude of the solar panel")
        double latitude();
        
        @Meta.AD(deflt = "-6", description = "longitude of the solar panel")
        double longitude();
        
        @Meta.AD(deflt = "10", description = "system loss of the solar panel by percentage")
        double sysLoss();
        
        @Meta.AD(deflt = "10", description = "Capacity of the solar panel in kW")
        double capacity();
        
        @Meta.AD(deflt = "0", description = "Does the solar panel have tracking? 0=no tracking, 1=Azimuth Tracking, 2=Azimuth and Tilt Tracking")
        int tracking();
        
        @Meta.AD(deflt = "35", description = "The angle the panel is at relative to horizontal (0 is facing directly upwards, 90 is vertically installed)")
        int tilt();
        
        @Meta.AD(deflt = "180", description = "Compass direction of the panel. For latitude >= 0, 180 degrees is south facing")
        int azim();
        
    }
    /**
     * A delayed result-bearing action that can be cancelled.
     */
    private ScheduledFuture<?> scheduledFuture;		
    PowerOutput pv;
    
    

    /**
     * OSGi calls this method to activate a managed service.
     * 
     * @param properties
     *            the configuration properties
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    @Activate
    public void activate(Map<String, Object> properties) throws ClientProtocolException, IOException {
        config = Configurable.createConfigurable(Config.class, properties);
        init(config.agentId(), config.desiredParentId());

        pv = new PowerOutput(config.latitude(), config.longitude(), config.sysLoss(), config.tracking(), config.capacity(), config.tilt(), config.azim());
        double demand = pv.getDemand(new Date());//need to actually get a bid from somewhere// minimumDemand + (maximumDemand - minimumDemand) * generator.nextDouble();
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
            double demand = pv.getDemand(new Date());//get the demand for the current hour
            publishBid(Bid.flatDemand(currentStatus.getMarketBasis(), demand)); //flat is ok for a pvPanel
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void handlePriceUpdate(PriceUpdate priceUpdate) {
        super.handlePriceUpdate(priceUpdate);
        // Nothing to control for a PVPanel
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContext(FlexiblePowerContext context) {
        super.setContext(context);
        scheduledFuture = context.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                doBidUpdate();
            }
        }, Measure.valueOf(0, SI.SECOND), Measure.valueOf(config.bidUpdateRate(), SI.SECOND));
    }
}
