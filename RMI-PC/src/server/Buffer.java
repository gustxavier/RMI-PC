/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ClientRMI;
import util.Contants;
import util.ManagerRMI;
import util.ServerRMI;

/**
 *
 * @author jonathan
 */
public class Buffer extends UnicastRemoteObject implements ServerRMI {

    private ArrayList<Object> buffer;
    private ArrayList<ClientRMI> clients;
    private int bufferSize;
    private ArrayList<ServerRMI> backupServers;
    private ArrayList<ManagerRMI> managers;

    public Buffer(int size) throws RemoteException {
        super();

        bufferSize = size;
        backupServers = new ArrayList<>();
        clients = new ArrayList<>();
        buffer = new ArrayList<>();
        managers = new ArrayList<>();

        Thread backupManager = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        synchronized (backupServers) {
                            backupServers.wait();
                            for (Iterator<ServerRMI> iterator = backupServers.iterator(); iterator.hasNext(); ) {
                                ServerRMI s = iterator.next();
                                try {
                                    s.updateBackup(backupServers, buffer, clients, managers);
                                } catch (RemoteException ex) {
                                    iterator.remove();
                                    continue;
                                }
                            }
                            for (Iterator<ManagerRMI> iterator = managers.iterator(); iterator.hasNext();) {
                                ManagerRMI m = iterator.next();
                                try {
                                    m.update(backupServers, buffer, clients);
                                } catch (RemoteException ex) {
                                    iterator.remove();
                                    continue;
                                }
                            }
                        }

                    } catch (InterruptedException ex) {
                        Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        backupManager.start();

        System.out.println("RUNNING...");
    }

    @Override
    public synchronized void insertItem(Object obj) throws RemoteException {
        try {
            if (buffer.size() == bufferSize) {
                this.wait();
            }

            buffer.add(obj);
            synchronized (backupServers) {
                backupServers.notifyAll();
            }
            this.notifyAll();

            System.out.println("ITEM PRODUCED");
        } catch (InterruptedException ex) {
            Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized Object removeItem() throws RemoteException {
        Object obj = null;
        try {
            if (buffer.isEmpty()) {
                this.wait();
            }

            obj = buffer.remove(0);
            synchronized (backupServers) {
                backupServers.notifyAll();
            }
            this.notifyAll();

            System.out.println("ITEM CONSUMED");
        } catch (InterruptedException ex) {
            Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    @Override
    public void login(ClientRMI client) throws RemoteException {
        clients.add(client);
        client.setBackupServers(backupServers);
        System.out.println("NEW MACHINE CONNECTED");
        synchronized (backupServers) {
            backupServers.notifyAll();
        }
    }

    @Override
    public void logout(ClientRMI client) throws RemoteException {
        clients.remove(client);
        System.out.println("MACHINE DISCONNECTED");
        synchronized (backupServers) {
            backupServers.notifyAll();
        }
    }

    @Override
    public ArrayList<ClientRMI> getClients() throws RemoteException {
        return clients;
    }

    @Override
    public void addBackup(ServerRMI backupServer) throws RemoteException {
        backupServers.add(backupServer);
        backupServer.setBufferSize(bufferSize);
        System.out.println("NEW BACKUP STABLISHED");
        for (ClientRMI c : clients) {
            try {
                c.setBackupServers(backupServers);
            } catch (RemoteException ex) {
                clients.remove(c);
                continue;
            }
        }

        synchronized (backupServers) {
            backupServers.notifyAll();
        }
    }

    @Override
    public void setBufferSize(int size) throws RemoteException {
        this.bufferSize = size;
    }

    @Override
    public void updateBackup(ArrayList<ServerRMI> bs, ArrayList<Object> b,
            ArrayList<ClientRMI> c, ArrayList<ManagerRMI> m) throws RemoteException {
        this.backupServers = bs;
        this.backupServers.remove(this);
        this.buffer = b;
        this.clients = c;
        this.managers = m;
    }

    @Override
    public boolean isOnTheLine() {
        return true;
    }

    @Override
    public void addManager(Registry registry) throws RemoteException {
        try {
            ManagerRMI manager = (ManagerRMI) registry.lookup(Contants.RMI_MANAGER_ID);
            
            managers.add(manager);
            manager.setBufferSize(bufferSize);
            System.out.println("MANAGER CONNECTED");
            synchronized (backupServers) {
                backupServers.notifyAll();
            }
        } catch (NotBoundException ex) {
            Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessException ex) {
            Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
