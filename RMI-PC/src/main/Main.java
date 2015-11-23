/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import client.Manager;
import client.Slave;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Buffer;
import util.Contants;
import util.ServerRMI;

/**
 *
 * @author jonathan
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.rmi.RemoteException
     */
    public static void main(String[] args) throws RemoteException, 
            AlreadyBoundException, NotBoundException, UnknownHostException {
        // TODO code application logic here

        if (args.length > 0 && args[0].compareTo("buffer") == 0) {
            try {
                int size = Integer.valueOf(args[1]);
                Buffer buffer = new Buffer(size, args[2]);
                
                Registry registry = LocateRegistry.createRegistry(Contants.RMI_PORT);
                registry.rebind(Contants.RMI_SERVER_ID, buffer);

            } catch (NumberFormatException ex) {
                System.out.println("Invalid buffer size.");
                System.exit(0);
            }

            return;
        }

        if (args.length > 0 && args[0].compareTo("manager") == 0) {
            
            Registry registry = LocateRegistry.getRegistry(Contants.IP_ADRESS_BUFFER_1,  Contants.RMI_PORT);
            ServerRMI buffer = (ServerRMI) registry.lookup(Contants.RMI_SERVER_ID);
            
            Manager frame = new Manager(buffer);
            frame.setVisible(true);
        }

        if (args.length > 0 && args[0].compareTo("client") == 0) {
            
            Registry registry = LocateRegistry.getRegistry(Contants.IP_ADRESS_BUFFER_1, Contants.RMI_PORT);
            ServerRMI buffer = (ServerRMI) registry.lookup(Contants.RMI_SERVER_ID);
            
            String machineName = buffer.login();
            System.out.println("CONNECTED AT "+buffer.getBufferName()+" AS "+machineName);
            Slave client = new Slave(buffer, machineName);    
            
            Thread clientThread = new Thread(client);
            clientThread.start();
            
            Thread shutdown = new Thread(new Shutdown(buffer, machineName));
            shutdown.start();
        }
    }
}

class Shutdown implements Runnable {

    private ServerRMI buffer;
    private String machineName;

    public Shutdown(ServerRMI buffer, String machineName) {
        this.buffer = buffer;
        this.machineName = machineName;
    }

    @Override
    public void run() {
        Scanner keyboard = new Scanner(System.in);
        while (keyboard.hasNextLine()) {
            if (keyboard.nextLine().compareTo("exit") == 0) {
                try {
                    buffer.logout(machineName);
                } catch (RemoteException ex) {
                    Logger.getLogger(Shutdown.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }
        }
    }
}
