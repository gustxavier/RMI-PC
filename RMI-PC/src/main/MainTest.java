/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import client.Manager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import util.Contants;
import util.ServerRMI;

/**
 *
 * @author jonathan
 */
public class MainTest {

    public static void main(String[] args) throws NotBoundException, RemoteException {
        /**
        Registry registry = LocateRegistry.getRegistry(Contants.IP_ADRESS, Contants.RMI_PORT);
        ServerRMI buffer = (ServerRMI) registry.lookup(Contants.RMI_SERVER_ID);

        Manager frame = new Manager(buffer);
        frame.setVisible(true);*/
        System.out.print("Atualizando");

    }

}
