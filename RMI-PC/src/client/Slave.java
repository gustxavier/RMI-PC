/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ClientRMI;
import util.ServerRMI;

/**
 *
 * @author jonathan
 */
public class Slave extends UnicastRemoteObject implements ClientRMI {

    private ServerRMI buffer = null;
    private ArrayList<Thread> producers;
    private ArrayList<Thread> consumers;
    private ArrayList<ServerRMI> backupServers;

    public Slave(ServerRMI buffer) throws RemoteException {
        super();
        this.buffer = buffer;
        producers = new ArrayList<>();
        consumers = new ArrayList<>();
    }

    @Override
    public void startProduction(long time, int numberThreads) throws RemoteException {
        for (int i = 0; i < numberThreads; i++) {
            Thread thread = new Thread(new ProducerRotine(this, time));
            thread.start();
            producers.add(thread);
            System.out.println("THREAD PRODUCER STARTED");
        }
    }

    @Override
    public boolean stopProduction() throws RemoteException {
        if (producers.isEmpty()) {
            return false;
        }
        producers.remove(0).stop();
        System.out.println("THREAD PRODUCER STOPED");
        return true;
    }

    @Override
    public void startConsumition(long time, int numberThreads) throws RemoteException {
        for (int i = 0; i < numberThreads; i++) {
            Thread thread = new Thread(new ConsumerRotine(this, time));
            thread.start();
            consumers.add(thread);
            System.out.println("THREAD CONSUMER STARTED");
        }
    }

    @Override
    public boolean stopConsumition() throws RemoteException {
        if (consumers.isEmpty()) {
            return false;
        }
        consumers.remove(0).stop();
        System.out.println("THREAD CONSUMER STOPED");
        return true;
    }

    @Override
    public void setBackupServers(ArrayList<ServerRMI> backup) throws RemoteException {
        backupServers = backup;
        synchronized(this){
            this.notifyAll();
        }
    }

    public ServerRMI getBuffer() {
        return buffer;
    }

    public synchronized void reconnect() {

        if (backupServers.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            buffer.isOnTheLine();
        } catch (RemoteException ex) {
            buffer = backupServers.remove(0);
            System.out.println("BUFFER SERVER CHANGED FOR BACKUP");
        }

    }

    @Override
    public int getProducersSize() throws RemoteException {
        return producers.size();
    }

    @Override
    public int getConsumersSize() throws RemoteException {
        return consumers.size();
    }

    @Override
    public boolean isOnTheLine() throws RemoteException{
        return true;
    }
}

class ProducerRotine implements Runnable {

    private Slave slave;
    private long time;

    public ProducerRotine(Slave slave, long time) {
        this.slave = slave;
        this.time = time;
    }

    @Override
    public void run() {
        while (true) {

            try {
                Thread.sleep(time);
                Object obj = new Object();
                slave.getBuffer().insertItem(obj);
                System.out.println("ITEM PRODUCED AND SENT TO BUFFER");
            } catch (InterruptedException ex) {
                Logger.getLogger(ProducerRotine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(ProducerRotine.class.getName()).log(Level.SEVERE, null, ex);

                slave.reconnect();
                continue;
            }

        }
    }
}

class ConsumerRotine implements Runnable {

    private Slave slave;
    private long time;

    public ConsumerRotine(Slave slave, long time) {
        this.slave = slave;
        this.time = time;
    }

    @Override
    public void run() {
        while (true) {

            try {
                Object obj = slave.getBuffer().removeItem();
                System.out.println("ITEM REMOVED FROM BUFFER AND CONSUMED");
                Thread.sleep(time);

            } catch (InterruptedException ex) {
                Logger.getLogger(ProducerRotine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(ConsumerRotine.class.getName()).log(Level.SEVERE, null, ex);

                slave.reconnect();
                continue;
            }
        }
    }

}
