/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Constants;
import util.ServerRMI;

/**
 *
 * @author jonathan
 */
public class SyncBuffer {

    public static void main(String args[]) throws NotBoundException {
        ServerRMI mainBuffer = null, backupBuffer = null;
        try {
            Registry registry = LocateRegistry.getRegistry(Constants.IP_ADRESS_BUFFER_1, Constants.RMI_PORT);
            mainBuffer = (ServerRMI) registry.lookup(Constants.RMI_SERVER_ID);

            registry = LocateRegistry.getRegistry(Constants.IP_ADRESS_BUFFER_2, Constants.RMI_PORT);
            backupBuffer = (ServerRMI) registry.lookup(Constants.RMI_SERVER_ID);
        } catch (RemoteException ex) {
            return;
        }

        while (true) {
            try {
                Thread.sleep(Constants.REQUEST_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(SyncBuffer.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                mainBuffer.isOnTheLine();
            } catch (RemoteException ex) {
                
                try {
                    ServerRMI temp = mainBuffer;
                    mainBuffer = backupBuffer;
                    backupBuffer = temp;
                    if (mainBuffer.getBufferName().compareTo("BUFFER1") == 0) {
                        
                        Registry registry = LocateRegistry.getRegistry(Constants.IP_ADRESS_BUFFER_2, Constants.RMI_PORT);
                        backupBuffer = (ServerRMI) registry.lookup(Constants.RMI_SERVER_ID);
                        
                    } else {
                        Registry registry = LocateRegistry.getRegistry(Constants.IP_ADRESS_BUFFER_1, Constants.RMI_PORT);
                        backupBuffer = (ServerRMI) registry.lookup(Constants.RMI_SERVER_ID);
                        
                    }
                } catch (RemoteException ex1) {
                }

                System.out.println("CHANGING BUFFERS");
                continue;
            }
            try {
                backupBuffer.isOnTheLine();
            } catch (RemoteException ex) {
                continue;
            }

            try {
                backupBuffer.setBackup(mainBuffer.getBuffer(), mainBuffer.getBufferSize(), mainBuffer.getClients());
                System.out.println("BACKUP DONE FROM " + mainBuffer.getBufferName() + " TO " + backupBuffer.getBufferName());
            } catch (RemoteException ex) {
                Logger.getLogger(SyncBuffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
