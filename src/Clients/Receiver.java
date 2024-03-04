package Clients;

import java.net.*;
import java.util.Scanner;
import java.util.StringJoiner;

public class Receiver implements Runnable {
    DatagramSocket server;
    Scanner scanner = new Scanner(System.in);
    String id;
    int connectionPort;

    int[] ports = {5001,5002,5003};
    String name;

    public Receiver(String id, DatagramSocket server, int connectionPort) {
        this.id = id;
        this.server = server;
        this.connectionPort = connectionPort;

    }

    public void run() {
        while(true) {
            try {
                InetAddress address = InetAddress.getByName("localhost");

                //receive
                byte[] receiveBuffer = new byte[1024];

                DatagramPacket receiveDatagram = new DatagramPacket(
                        receiveBuffer,
                        receiveBuffer.length);

                server.receive(receiveDatagram);
                receiveBuffer = receiveDatagram.getData();

                byte[] resultBytes = trimZeros(receiveBuffer);

                String receivedMessage = new String(resultBytes);
                System.out.println(ShowMessageFormat(generateResponse(receivedMessage)));
                //repassando a mensagem
                if(server.getLocalPort() == 5000){
                    String response = generatResponseForSend(receivedMessage);

                    if(response != "") {
                        if(returnType(response).equals("/unicast")){
                            int port = 0;
                            switch (returnDestinatario(response)){
                                case "P1":
                                    port = 5000;
                                    break;
                                case "P2":
                                    port = 5001;
                                    break;
                                case "P3":
                                    port = 5002;
                                    break;
                                case "P4":
                                    port = 5003;
                                    break;
                            }
                            byte[] sendBuffer = response.getBytes();
                            DatagramPacket sendDatagram = new DatagramPacket(sendBuffer,
                                    sendBuffer.length,
                                    address,
                                    port);
                            server.send(sendDatagram);
                        }else if (returnType(response).equals("/broadcast")){

                          for (int i = 0; i < ports.length; i++){
                                  byte[] sendBuffer = response.getBytes();
                                  DatagramPacket sendDatagram = new DatagramPacket(sendBuffer,
                                          sendBuffer.length,
                                          address,
                                          ports[i]);
                                  server.send(sendDatagram);
                          }
                        }
                    }
                }
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String returnType(String msg){
        String[] parts = msg.split(":");
        String[] type = parts[parts.length - 1].split(" ");

        return type[1];
    }
    public String returnDestinatario(String msg){
        String[] parts = msg.split(":");
        return parts[2].trim();
    }
    //esse pra mostrar
    public String generateResponse(String msg) {
        String[] parts = msg.split(":");
        String[] type = parts[parts.length - 1].split(" ");
        String originalSender = parts[0];

        if(type[1].equals("/unicast")){
            String destinatario = parts[2].trim();
            if(id.equals(destinatario)) {
                return msg;
            }else{
                return "";
            }
        }else if(type[1].equals("/broadcast")){
            if(id.equals(originalSender)) {
                return "";
            }
        }
        return msg;
    }

    //esse pra enviar
    public String generatResponseForSend(String msg) {
        String[] parts = msg.split(":");
        String[] type = parts[parts.length - 1].split(" ");
        String originalSender = parts[0];

        if(type[1].equals("/unicast")){
            String destinatario = parts[2].trim();
            if(id.equals(destinatario)) {
                return "";
            }else{
                return msg;
            }
        }else if(type[1].equals("/broadcast")){
            if(id.equals(originalSender)) {
                return "";
            }
        }
        return msg;
    }

    public String ShowMessageFormat(String msg) {
        if(msg!=""){
            String[] parts = msg.split(":");
            String[] message = parts[parts.length - 1].split(" ");
            String originalSender = parts[0];

            if(message[1].equals("/unicast")){
                parts[parts.length - 1] = ": " +  message[3];

                StringJoiner joiner = new StringJoiner("");
                for (String str : parts) {
                    joiner.add(str);
                }
                return joiner.toString();
            }else if(message[1].equals("/broadcast")){
                parts[parts.length - 1] =": " + message[2];

                StringJoiner joiner = new StringJoiner("");
                for (String str : parts) {
                    joiner.add(str);
                }
                return joiner.toString();
            }
        }
        return "";
    }
    public static byte[] trimZeros(byte[] bufer) {
        int nonZeroCount = 0;
        // Contar o número de bytes não nulos
        for (byte b : bufer) {
            if (b != 0) {
                nonZeroCount++;
            }
        }
        // Criar uma nova matriz apenas com bytes não nulos
        byte[] result = new byte[nonZeroCount];
        int resultIndex = 0;

        for (byte b : bufer) {
            if (b != 0) {
                result[resultIndex++] = b;
            }
        }

        return result;
    }
}

