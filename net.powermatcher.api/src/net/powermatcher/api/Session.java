package net.powermatcher.api;

import net.powermatcher.api.data.Bid;
import net.powermatcher.api.data.MarketBasis;
import net.powermatcher.api.messages.BidUpdate;
import net.powermatcher.api.messages.PredictionUpdate;
import net.powermatcher.api.messages.PriceUpdate;

/**
 * {@link Session} defines the interface for a link between an {@link AgentEndpoint} with a {@link MatcherEndpoint} in a
 * Powermatcher cluster.
 *
 * @author FAN
 * @version 2.1
 */
public interface Session {

    /**
     * @return the agentId of the {@link AgentEndpoint} of this {@link Session}.
     */
    String getAgentId();

    /**
     * @return the agentId of the {@link MatcherEndpoint} of this {@link Session}.
     */
    String getMatcherId();

    /**
     * @return the id of the cluster this {@link Session} is active in.
     */
    String getClusterId();

    /**
     * @return the id of this {@link Session} instance.
     */
    String getSessionId();

    /**
     * @return the {@link MarketBasis} used in this {@link Session}.
     */
    MarketBasis getMarketBasis();

    /**
     * Sets the {@link MarketBasis} for this session. This should be set by the {@link MatcherEndpoint} when its
     * {@link MatcherEndpoint#connectToAgent(Session)} method is called. This makes sure that the {@link AgentEndpoint}
     * knows which {@link MarketBasis} is used for bidding.
     *
     * @param marketBasis
     *            the new marketBasis used in this {@link Session}.
     */
    void setMarketBasis(MarketBasis marketBasis);

    /**
     * Passes the {@link PriceUpdate} sent by the {@link MatcherEndpoint} to the {@link AgentEndpoint} of this
     * {@link Session}. It calls {@link AgentEndpoint#handlePriceUpdate(PriceUpdate)}.
     *
     * @param priceUpdate
     *            The {@link PriceUpdate} passed by the {@link MatcherEndpoint} of this {@link Session}.
     */

    void updatePrice(PriceUpdate priceUpdate);

    /**
     * Passes the {@link Bid} sent by the {@link AgentEndpoint} to the {@link MatcherEndpoint} of this session.
     *
     * @param newBid
     *            The {@link Bid} passed by the {@link AgentEndpoint} of this {@link Session}.
     * @see MatcherEndpoint#handleBidUpdate(Session, BidUpdate)
     */
    void updateBid(BidUpdate newBid);

    /**
     * Disconnect the {@link AgentEndpoint} from the {@link MatcherEndpoint}. This method should always call both the
     * {@link AgentEndpoint#matcherEndpointDisconnected(Session)} and the
     * {@link MatcherEndpoint#agentEndpointDisconnected(Session)} methods.
     *
     * This method can be called by any object, event the {@link AgentEndpoint} or {@link MatcherEndpoint} themselves.
     */
    void disconnect();

    void updatePrediction(PredictionUpdate update);
}
