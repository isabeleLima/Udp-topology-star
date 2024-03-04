package Clients;

import java.net.DatagramSocket;
import java.net.SocketException;

public class P4 {


    public static void main(String[] args) {
        int id = 3;
        String name = "P4";
        int configPort = 5003;
        DatagramSocket server;

        try {
            server = new DatagramSocket(configPort);

            Sender sender = new Sender(name, server, 5000);
            Thread s = new Thread(sender);
            s.start();

            Receiver receiver = new Receiver(name, server, 5000);
            Thread r = new Thread(receiver);
            r.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}