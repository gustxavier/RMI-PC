/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.Serializable;

/**
 *
 * @author jonathan
 */
public class Tasks implements Serializable{
    private String machineName;
    private int producersNumber;
    private long producersTime;
    private int consumersNumber;
    private long consumersTime;

    public Tasks(String machineName, int producersNumber, long producersTime, int consumersNumber, long consumersTime) {
        this.machineName = machineName;
        this.producersNumber = producersNumber;
        this.producersTime = producersTime;
        this.consumersNumber = consumersNumber;
        this.consumersTime = consumersTime;
    }

    public int getProducersNumber() {
        return producersNumber;
    }

    public void setProducersNumber(int producersNumber) {
        this.producersNumber = producersNumber;
    }

    public long getProducersTime() {
        return producersTime;
    }

    public void setProducersTime(long producersTime) {
        this.producersTime = producersTime;
    }

    public int getConsumersNumber() {
        return consumersNumber;
    }

    public void setConsumersNumber(int consumersNumber) {
        this.consumersNumber = consumersNumber;
    }

    public long getConsumersTime() {
        return consumersTime;
    }

    public void setConsumersTime(long consumersTime) {
        this.consumersTime = consumersTime;
    }

    
    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    
    
}
