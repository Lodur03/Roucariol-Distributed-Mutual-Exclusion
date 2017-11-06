/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package printerpoolling;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author felipesilva
 */
public class Server {

    private ServerSocket serverSocket;
    private final int defaultServerPort = 8001;
    private Message lastMessage;

    private void waitForClients() {
        new Thread(
                () -> {
                    try {
                        serverSocket = new ServerSocket(defaultServerPort);
                        handleNewConnection(serverSocket.accept());
                    } catch (Exception ex) {
                        System.out.println("Erro ao conectar   escutar porta"
                                + defaultServerPort + "causa : " + ex.getMessage());
                    }
                }
        ).start();

    }

    private void handleNewConnection(Socket client) {
        new Thread(
                () -> {
                    try {
                        ObjectInputStream ois
                        = new ObjectInputStream(client.getInputStream());
                        Message message = null;
                        while ((message = ((Message) ois.readObject())) != null) {
                            message.setNodeId(client.getInetAddress().toString());
                            lastMessage = message;
                            handleMessage(client, message);
                        }
                    } catch (Exception ex) {
                        System.out.println("Erro ao conectar ao  nó "
                                + client.getInetAddress()
                                + " causa : " + ex.getMessage());
                    }
                }
        ).start();
    }

    private void handleMessage(Socket client, Message message) {
        for (int x = 0; x < 10; x++) {
            System.out.println("Message from : "+ message.getNodeId() + lastMessage.getTimestamp() + x);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
        sendMessage(client,Message.getFinishedMessage());
    }
    
        private void sendMessage(Socket client, Message message) {
        try {
            ObjectOutputStream ois
                    = new ObjectOutputStream(client.getOutputStream());
            ois.writeObject(message);
        } catch (IOException ex) {
            System.out.println("Erro ao enviar msg para "
                    + client.getInetAddress());
        }
    }

}
