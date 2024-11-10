package org.firstinspires.ftc.teamcode.jbbfi;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotserver.internal.webserver.CoreRobotWebServer;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;

import java.net.Socket;

/**
 * script portal (awesome)
 */
public class ScriptingWebPortal extends Thread {

    private static final int PORT = 8082;

    public int PORT_FINAL = 8082;

    private ServerSocket serverSocket;
    private Context context;
    private Handler handler;

    private boolean running = true;

    JBBFI jbbfi;


    public ScriptingWebPortal(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.running = true;
    }

    public void setJbbfi(JBBFI jbbfi){
        this.jbbfi = jbbfi;
    }

    public void stopRunning(){
        this.running = false;
    }

    @Override


    public void run() {
        boolean good = false;
        try {
            while(!good) {
                PORT_FINAL += 1;
                try {
                    serverSocket = new ServerSocket(PORT_FINAL);
                    good = true;
                }catch (Exception e){
                    if(PORT_FINAL > 9000){
                        throw new RuntimeException(e);
                    }
                    good = false;
                }
            }
            while (running) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRequest(Socket clientSocket)
    {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String requestLine = reader.readLine();
            if (requestLine.startsWith("GET /scripting")) {
                String filePath = "/sdcard/scripting/index.html";
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    byte[] fileContent = readFileContent(file);
                    sendResponse(clientSocket, fileContent);
                } else {
                    sendNotFoundResponse(clientSocket);
                }
            } else if(requestLine.startsWith("GET /test.jbbfi")){
                String filePath = "/sdcard/scripting/test.jbbfi";
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    byte[] fileContent = readFileContent(file);
                    sendResponse(clientSocket, fileContent);
                } else {
                    sendNotFoundResponse(clientSocket);
                }
            } else if (requestLine.startsWith("POST /upload")) {
                handleFileUpload(reader, clientSocket);
            } else if (requestLine.startsWith("POST /run")){
                runFunction(reader);
            } else if (requestLine.startsWith("/sdcard")){
                String filePath = "/sdcard/scripting/test.jbbfi";
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    byte[] fileContent = readFileContent(file);
                    sendResponse(clientSocket, fileContent);
                } else {
                    sendNotFoundResponse(clientSocket);
                }
            } else {
                sendMethodNotAllowedResponse(clientSocket);
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runFunction(BufferedReader reader) throws IOException{
        String line;
        StringBuilder requestBody = new StringBuilder();

        boolean read = false;
        // Read the request body
        while (reader.ready()) {
            line = reader.readLine();
            if(read)
                requestBody.append(line).append("\n");
            if(line.isEmpty()) read = true;
        }


        if(jbbfi != null){
            try {
                jbbfi.readFile("/sdcard/scripting/test.jbbfi");
                jbbfi.parse();
                jbbfi.runFunction(requestBody.toString());
            } catch (Exception e){

                throw new RuntimeException(e);
            }
        }
    }

    private void sendResponse(Socket clientSocket, byte[] fileContent) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + fileContent.length + "\r\n" +
                "\r\n";
        clientSocket.getOutputStream().write(response.getBytes());
        clientSocket.getOutputStream().write(fileContent);
    }

    private byte[] readFileContent(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new

                byte[(int) file.length()];
        fis.read(buffer);
        fis.close();
        return buffer;
    }

    private void sendNotFoundResponse(Socket clientSocket) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "File not found";
        clientSocket.getOutputStream().write(response.getBytes());
    }

    private void sendMethodNotAllowedResponse(Socket clientSocket) throws IOException {
        String response = "HTTP/1.1 405 Method Not Allowed\r\n" +
                "Allow: GET\r\n" +
                "\r\n" +
                "Method not allowed";
        clientSocket.getOutputStream().write(response.getBytes());
    }


    private void handleFileUpload(BufferedReader reader, Socket clientSocket) throws IOException {
        String line;
        StringBuilder requestBody = new StringBuilder();

        boolean read = false;
        // Read the request body
        while (reader.ready()) {
            line = reader.readLine();
            if(read)
                requestBody.append(line).append("\n");
            if(line.isEmpty()) read = true;
        }

        //
        String[] parts = requestBody.toString().split("\r\n");
        byte[] fileContent = parts[parts.length - 1].getBytes();

        // Save the file locally
        String filePath = "/sdcard/scripting/test.jbbfi";
        saveFileLocally(filePath, fileContent);

        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "File uploaded successfully";
        clientSocket.getOutputStream().write(response.getBytes());
    }

    private void saveFileLocally(String filePath, byte[] fileContent) throws IOException {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(filePath))) {
            os.write(fileContent);
        }
    }
}