package net.powermatcher.integration.test;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.zip.DataFormatException;

import net.powermatcher.integration.base.AuctioneerResilienceTest;

import org.junit.Test;

public class SendReceiveBidTestCBQ1 extends AuctioneerResilienceTest {

    /**
     * Sending an invalid bid in an agent hierarchy is difficult because the creation of a bid info instance prohibits
     * this.
     * 
     * Test 1: Create a bid with a demand array that is too long.
     * 
     * Expected result: Bid constructor generates an InvalidParameterException
     * 
     * @throws IOException
     * @throws DataFormatException
     */
    @Test(expected = InvalidParameterException.class)
    public void createInvalidBid1CPF1() throws IOException, DataFormatException {
        // Prepare the test for reading test input
        prepareTest("CBQ/CBQ1/Test1", null);

        this.bidReader.nextBid();
    }

    /**
     * Sending an invalid bid in an agent hierarchy is difficult because the creation of a bid info instance prohibits
     * this.
     * 
     * Test 2: Create a bid with a demand array that is too short.
     * 
     * Expected result: Bid constructor generates an InvalidParameterException
     * 
     * @throws IOException
     * @throws DataFormatException
     */
    @Test(expected = InvalidParameterException.class)
    public void createInvalidBid2CPF1() throws IOException, DataFormatException {
        // Prepare the test for reading test input
        prepareTest("CBQ/CBQ1/Test2", null);

        this.bidReader.nextBid();
    }

    /**
     * Sending an invalid bid in an agent hierarchy is difficult because the creation of a bid info instance prohibits
     * this.
     * 
     * Test 3: Create a bid with a demand array that is too short.
     * 
     * Expected result: Bid constructor generates an InvalidParameterException
     * 
     * @throws IOException
     * @throws DataFormatException
     */
    @Test(expected = InvalidParameterException.class)
    public void createInvalidBid3CPF1() throws IOException, DataFormatException {
        // Prepare the test for reading test input
        prepareTest("CBQ/CBQ1/Test3", null);

        this.bidReader.nextBid();
    }
}
