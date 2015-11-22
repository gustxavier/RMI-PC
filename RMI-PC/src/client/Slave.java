/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.rmi.RemoteException;
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
        super();
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
                    for (int i = 0; i < t.getProducersSettings().size(); i++) {
                        startProduction(t.getProducersSettings().get(i));
                    }
                    for (int i = 0; i < t.getConsumersSettings().size(); i++) {
                        startConsumition(t.getConsumersSettings().get(i));
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
    
    public ServerRMI getBuffer() {
        try {
            buffer.isOnTheLine();
            
        } catch (RemoteException ex) {
            //change buffer
            
        }
        
        return buffer;
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
                slave.getBuffer().insertItem(obj);
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
                String obj = slave.getBuffer().removeItem();
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
