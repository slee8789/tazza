package com.leesc.tazza.ui.roominfo;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {
    private ServerSocket serverSocket;
    private Socket socket;

    private DataInputStream streamByClient = null;
    private DataOutputStream streamToClient = null;
    private DataInputStream streamByServer = null;
    private DataOutputStream streamToServer = null;


    public ServerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket = serverSocket.accept();
                streamByClient = new DataInputStream(socket.getInputStream());
                streamToClient = new DataOutputStream(socket.getOutputStream());
                Log.d("lsc", "클라이언트로부터 받은 메세지 " + streamByClient.readUTF());
            } catch (IOException e) {
                Log.e("lsc", "serversocket io exception " + e.getMessage());
            }
        }
    }

}