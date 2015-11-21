/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author jonathan
 */
public interface ClientRMI extends Remote{
    
    public void startProduction(long time, int numberThreads) throws RemoteException;
    public boolean stopProduction() throws RemoteException;
    public void startConsumition(long time, int numberThreads) throws RemoteException;
    public boolean stopConsumition() throws RemoteException;
    public void setBackupServers(ArrayList<ServerRMI> backup) throws RemoteException;
    public int getProducersSize() throws RemoteException;
    public int getConsumersSize() throws RemoteException;
    public boolean isOnTheLine() throws RemoteException;
}
