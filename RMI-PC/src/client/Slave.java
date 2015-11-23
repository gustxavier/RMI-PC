/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ServerRMI;
import util.Tasks;
import util.Contants;

/**
 *
 * @author jonathan
 */
public class Slave implements Runnable {

    private ServerRMI buffer = null;
    private String machineName;
    private ArrayList<Thread> producers;
    private ArrayList<Thread> consumers;

    public Slave(ServerRMI buffer, String machineName) throws RemoteException {
        
        this.buffer = buffer;
        producers = new ArrayList<>();
        consumers = new ArrayList<>();
        this.machineName = machineName;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(Contants.REQUEST_TIME);
                System.out.println("REQUEST FOR TASKS");
                Tasks t = getBuffer().getTasks(machineName);
                if (t != null) {
                    for (Thread p : producers) {
                        p.stop();
                    }
                    for (Thread p : consumers) {
                        p.stop();
                    }
                    producers = new ArrayList<>();
                    consumers = new ArrayList<>();
                    for (int i = 0; i < t.getProducersNumber(); i++) {
                        startProduction(t.getProducersTime());
                    }
                    for (int i = 0; i < t.getConsumersNumber(); i++) {
                        startConsumition(t.getConsumersTime());
                    }
                }
            } catch (RemoteException ex) {
                Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void startProduction(long time) throws RemoteException {

        Thread thread = new Thread(new ProducerRotine(this, time));
        thread.start();
        producers.add(thread);
        System.out.println("THREAD PRODUCER STARTED");
    }


    public void startConsumition(long time) throws RemoteException {

        Thread thread = new Thread(new ConsumerRotine(this, time));
        thread.start();
        consumers.add(thread);
        System.out.println("THREAD CONSUMER STARTED");
    }
    
    public synchronized ServerRMI getBuffer() {
        try {
            buffer.isOnTheLine();
            
        } catch (RemoteException ex) {
            try {
                Registry registry = LocateRegistry.getRegistry(Contants.IP_ADRESS_BUFFER_1,  Contants.RMI_PORT);
                buffer = (ServerRMI) registry.lookup(Contants.RMI_SERVER_ID);
                System.out.println("CHANGE TO "+buffer.getBufferName());
            } catch (RemoteException ex1) {
                try {
                    Registry registry = LocateRegistry.getRegistry(Contants.IP_ADRESS_BUFFER_2,  Contants.RMI_PORT);
                    buffer = (ServerRMI) registry.lookup(Contants.RMI_SERVER_ID);
                    System.out.println("CHANGE TO "+buffer.getBufferName());
                } catch (RemoteException ex2) {
                    try {
                        Thread.sleep(Contants.REQUEST_TIME);
                        getBuffer();
                    } catch (InterruptedException ex3) {
                        Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex3);
                    }
                } catch (NotBoundException ex2) {
                    
                }
            } catch (NotBoundException ex1) {
                
            }     
        }  
        return buffer;
    }

    public String getMachineName(){
        return machineName;
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
                String obj = "item";
                slave.getBuffer().insertItem(obj, slave.getMachineName());
                System.out.println("ITEM PRODUCED AND SENT TO BUFFER");
            } catch (InterruptedException ex) {
                Logger.getLogger(ProducerRotine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(ProducerRotine.class.getName()).log(Level.SEVERE, null, ex);

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
                String obj = slave.getBuffer().removeItem(slave.getMachineName());
                System.out.println("ITEM REMOVED FROM BUFFER AND CONSUMED");
                Thread.sleep(time);

            } catch (InterruptedException ex) {
                Logger.getLogger(ProducerRotine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(ConsumerRotine.class.getName()).log(Level.SEVERE, null, ex);

                continue;
            }
        }
    }
}
