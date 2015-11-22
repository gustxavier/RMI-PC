/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author jonathan
 */
public class Tasks implements Serializable{
    private String machineName;
    private ArrayList<Long> producersSettings;
    private ArrayList<Long> consumersSettings;

    public Tasks(String machineName, ArrayList<Long> producersSettings, ArrayList<Long> consumersSettings, int removeProducers, int removeConsumers) {
        this.machineName = machineName;
        this.producersSettings = producersSettings;
        this.consumersSettings = consumersSettings;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public ArrayList<Long> getProducersSettings() {
        return producersSettings;
    }

    public void setProducersSettings(ArrayList<Long> producersSettings) {
        this.producersSettings = producersSettings;
    }

    public ArrayList<Long> getConsumersSettings() {
        return consumersSettings;
    }

    public void setConsumersSettings(ArrayList<Long> consumersSettings) {
        this.consumersSettings = consumersSettings;
    }
    
    
}
