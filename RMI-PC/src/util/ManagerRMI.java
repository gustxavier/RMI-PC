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
public interface ManagerRMI extends Remote{
    
    public void update(ArrayList<ServerRMI> bs, ArrayList<Object> b, 
            ArrayList<ClientRMI> c) throws RemoteException;
    public void setBufferSize(int size) throws RemoteException;
    
}
