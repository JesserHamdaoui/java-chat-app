package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + this.clientUsername + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(clientUsername + ": " + messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for(ClientHandler clientHandler: clientHandlers) {
            try {
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + this.clientUsername + " has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }

            if(bufferedReader != null) {
                bufferedReader.close();
            }

            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
