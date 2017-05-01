package net.powermatcher.api.data;

public class Allocation {

    private final double allocationValue;

    public Allocation(double allocationValue) {
        this.allocationValue = allocationValue;
    }

    public double getAllocationValue() {
        return allocationValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(allocationValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Allocation other = (Allocation) obj;
        if (Double.doubleToLongBits(allocationValue) != Double.doubleToLongBits(other.allocationValue)) {
            return false;
        }
        return true;
    }

}
