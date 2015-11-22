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
public interface ServerRMI extends Remote{
    
    public void insertItem(String obj, String machineName) throws RemoteException;
    public String removeItem(String machineName) throws RemoteException;
    public String login() throws RemoteException;
    public void logout(String machineName) throws RemoteException;
    public ArrayList<String> getClients() throws RemoteException;
    public void setBufferSize(int size) throws RemoteException;
    public boolean isOnTheLine() throws RemoteException;
    public void addTasks(Tasks task) throws RemoteException;
    public Tasks getTasks(String machineName) throws RemoteException;
}
