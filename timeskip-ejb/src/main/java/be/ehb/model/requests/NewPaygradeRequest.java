package be.ehb.model.requests;

import be.ehb.model.AbstractBaseNoId;

/**
 * @author Guillaume Vandecasteele
 * @since 2017
 */
public class NewPaygradeRequest extends AbstractBaseNoId {

    private Double hourlyRate;

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public String toString() {
        return "NewPaygradeRequest{" +
                "hourlyRate=" + hourlyRate +
                '}';
    }
}