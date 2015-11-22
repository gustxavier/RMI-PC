/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ServerRMI;
import util.Tasks;

/**
 *
 * @author jonathan
 */
public class Buffer extends UnicastRemoteObject implements ServerRMI {

    private ArrayList<String> buffer;
    private ArrayList<String> clients;
    private int bufferSize;
    private ArrayList<Tasks> tasks;

    public Buffer(int size) throws RemoteException {
        super();

        bufferSize = size;
        clients = new ArrayList<>();
        buffer = new ArrayList<>();
        tasks = new ArrayList<>();

        System.out.println("RUNNING...");
    }

    @Override
    public synchronized void insertItem(String obj, String machineName) throws RemoteException {
        try {
            if (buffer.size() == bufferSize) {
                this.wait();
            }

            buffer.add(obj);
            this.notifyAll();

            System.out.println("ITEM PRODUCED BY "+ machineName);
        } catch (InterruptedException ex) {
            Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized String removeItem(String machineName) throws RemoteException {
        String obj = null;
        try {
            if (buffer.isEmpty()) {
                this.wait();
            }

            obj = buffer.remove(0);
            this.notifyAll();

            System.out.println("ITEM CONSUMED BY "+machineName);
        } catch (InterruptedException ex) {
            Logger.getLogger(Buffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    @Override
    public synchronized String login() throws RemoteException {
        int index = 0;
        while (clients.contains("MACHINE " + index)) {
            index++;
        }

        clients.add("MACHINE " + index);
        System.out.println("MACHINE " + index + " CONNECTED");
        return "MACHINE " + index;
    }

    @Override
    public synchronized void logout(String machineName) throws RemoteException {
        clients.remove(machineName);
        System.out.println(machineName + " DISCONNECTED");

    }

    @Override
    public synchronized void setBufferSize(int size) throws RemoteException {
        this.bufferSize = size;
    }

    @Override
    public synchronized boolean isOnTheLine() {
        return true;
    }

    @Override
    public synchronized ArrayList<String> getClients() throws RemoteException {
        return clients;
    }

    public synchronized void addTasks(Tasks task) throws RemoteException {
        tasks.add(task);
    }

    @Override
    public synchronized Tasks getTasks(String machineName) throws RemoteException {
        for (Iterator<Tasks> iterator = tasks.iterator(); iterator.hasNext();) {
            Tasks t = iterator.next();
            if(t.getMachineName().compareTo(machineName) == 0){
                iterator.remove();
                return t;
            }
        }
        return null;
    }
}
