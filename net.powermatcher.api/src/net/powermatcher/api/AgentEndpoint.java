package net.powermatcher.api;

import net.powermatcher.api.data.Bid;
import net.powermatcher.api.data.Price;
import net.powermatcher.api.messages.AllocationUpdate;
import net.powermatcher.api.messages.PriceUpdate;

/**
 * {@link AgentEndpoint} defines the interface for classes that can receive a {@link PriceUpdate} and send a {@link Bid}
 * , based on the {@link Price} of that {@link PriceUpdate}. An {@link AgentEndpoint} can be linked with zero or one
 * {@link MatcherEndpoint} instances. These are linked by a {@link Session}.
 *
 * @author FAN
 * @version 2.1
 */
public interface AgentEndpoint
    extends Agent {

    /**
     * The {@link Status} object describes the current status and configuration of an {@link AgentEndpoint}. This status
     * can be queried through the {@link AgentEndpoint#getStatus()} method and will give a snapshot of the state at that
     * time.
     */
    interface Status
        extends Agent.Status {
        /**
         * @return the current {@link Session} for the connection from this {@link AgentEndpoint} to the
         *         {@link MatcherEndpoint}.
         * @throws IllegalStateException
         *             when the agent is not connected (see {@link #isConnected()})
         */
        Session getSession();
    }

    /**
     * @return the id of the desired parent {@link Agent}.
     */
    String getDesiredParentId();

    @Override
    Status getStatus();

    /**
     * Connects this {@link AgentEndpoint} instance to a {@link MatcherEndpoint}.
     *
     * @param session
     *            the {@link Session} that will link this {@link AgentEndpoint} with a {@link MatcherEndpoint}.
     */
    void connectToMatcher(Session session);

    /**
     * Notifies the {@link Agent} that this {@link AgentEndpoint} instance is disconnected from the
     * {@link MatcherEndpoint}.
     *
     * @param session
     *            the {@link Session} that used to link the {@link MatcherEndpoint} with this {@link AgentEndpoint}.
     */
    void matcherEndpointDisconnected(Session session);

    /**
     * Called by {@link MatcherEndpoint} via the {@link Session} to update the {@link Price} used by this
     * {@link AgentEndpoint} instance.
     *
     * @param priceUpdate
     *            The new {@link Price}, wrapped in a {@link PriceUpdate}, along with the id of the {@link Bid} it was
     *            based on.
     */
    void handlePriceUpdate(PriceUpdate priceUpdate);

    void handleAllocationUpdate(AllocationUpdate allocationUpdate);
}
