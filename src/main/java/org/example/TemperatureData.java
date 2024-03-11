package org.example;

import java.util.Objects;


public class TemperatureData {

    private String sensorId;
    private Double temperatureC;

    public TemperatureData(String sensorId, Double temperatureC) {
        this.sensorId = sensorId;
        this.temperatureC = temperatureC;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setTemperatureC(Double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public String getSensorId() {
        return sensorId;
    }

    public Double getTemperatureC() {
        return temperatureC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemperatureData that = (TemperatureData) o;
        return Objects.equals(sensorId, that.sensorId) && Objects.equals(temperatureC, that.temperatureC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, temperatureC);
    }

    @Override
    public String toString() {
        return "TemperatureData{" +
                "sensorId='" + sensorId + '\'' +
                ", temperatureC=" + temperatureC +
                '}';
    }
}
