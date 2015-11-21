/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 *
 * @author jonathan
 */
public interface ServerRMI extends Remote{
    
    public void insertItem(Object obj) throws RemoteException;
    public Object removeItem() throws RemoteException;
    public void login(ClientRMI client) throws RemoteException;
    public void logout(ClientRMI client) throws RemoteException;
    public ArrayList<ClientRMI> getClients() throws RemoteException;
    public void addBackup(ServerRMI backupBuffer) throws RemoteException;
    public void setBufferSize(int size) throws RemoteException;
    public void updateBackup(ArrayList<ServerRMI> bs, ArrayList<Object> b, 
            ArrayList<ClientRMI> c, ArrayList<ManagerRMI> m) throws RemoteException;
    public boolean isOnTheLine() throws RemoteException;
    public void addManager(Registry manager) throws RemoteException;
}
