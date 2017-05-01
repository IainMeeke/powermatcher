package net.powermatcher.api.messages;

import net.powermatcher.api.data.Allocation;

public class AllocationUpdate {

    private final Allocation allocation;
    private final int predictionNumber;

    public AllocationUpdate(Allocation allocation) {
        if (allocation == null) {
            throw new NullPointerException("Allocation");
        }
        this.allocation = allocation;
        predictionNumber = 0;// predictionNumber;
    }

    public Allocation getAllocation() {
        return allocation;
    }

    public int getPredictionNumber() {
        return predictionNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((allocation == null) ? 0 : allocation.hashCode());
        result = prime * result + predictionNumber;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AllocationUpdate other = (AllocationUpdate) obj;
        if (allocation == null) {
            if (other.allocation != null) {
                return false;
            }
        } else if (!allocation.equals(other.allocation)) {
            return false;
        }
        if (predictionNumber != other.predictionNumber) {
            return false;
        }
        return true;
    }
}
