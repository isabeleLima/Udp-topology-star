package Clients;

import java.net.*;
import java.util.Scanner;

public class Sender implements Runnable {
    Scanner scanner = new Scanner(System.in);
    String name;
    DatagramSocket server;
    int connectionPort;



    public Sender(String id, DatagramSocket server, int connectionPort) {
        this.name = id;
        this.server = server;
        this.connectionPort = connectionPort;
    }

    public void run() {
        System.out.println("Processo - " + name + " " + " rodando em: localhost" + ":" + server.getLocalPort());
        if(connectionPort == 500){
            System.out.println("Esse é o processo centro da estrela");
        }
        while(true) {
            try {
                InetAddress address = InetAddress.getByName("localhost");
                String message = scanner.nextLine();
                if(message != null) {
                    if (message.startsWith("/broadcast")) {
                        message = name + ": " + "envia uma mensagem para todos : " + message;
                        byte[] sendBuffer = message.getBytes();

                        DatagramPacket sendDatagram = new DatagramPacket(sendBuffer,
                                sendBuffer.length,
                                address,
                                connectionPort);

                        server.send(sendDatagram);

                        System.out.println("Enviando mensagem...");
                    }else if(message.startsWith("/unicast")){
                        String[] parts = message.split(" ");
                        String destinatario = parts[1];
                        message = name + ": " + "envia uma mensagem para : "+ destinatario + " : " + message;
                        byte[] sendBuffer = message.getBytes();

                        DatagramPacket sendDatagram = new DatagramPacket(sendBuffer,
                                sendBuffer.length,
                                address,
                                connectionPort);

                        server.send(sendDatagram);

                        System.out.println("Enviando mensagem...");
                    }else{
                        System.out.println("Formato invalido! suas mensagens devem seguir os seguintes padrões:");
                        System.out.println("/broadcast + mensagem");
                        System.out.println("/unicast + destinatario + mensagem");
                    }
                }

            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
