package com.example.robotcontroler.view.pointcloud.net;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DatagramClientPointCloud implements Runnable {
    private Thread thread;
    private DatagramSocket udpSocket;
    private DatagramPacket inputData;
    private byte[] inputMsg;
    private Handler handler;
    private byte[] sendMsg = new byte[2];
    private String ip;
    private int port;
    private int input = 0;
    boolean run;
    boolean send = true;
    private ArrayList<Float> dataFloat = new ArrayList<Float>();

    public DatagramClientPointCloud(String ipServer, int portServer) {
        thread = new Thread(this, "sending data thread");
        inputMsg = new byte[1500];
        this.ip = ipServer;
        this.port = portServer;
        thread.start();
    }

    @Override
    public void run() {
        try {
            Log.e("start", "thread");
            run = true;
            send = true;
            InetAddress inetAddress = InetAddress.getByName(ip);
            udpSocket = new DatagramSocket();
            udpSocket.connect(inetAddress, port);
            sendMsg = ("pc").getBytes();
            while (run) {
                if (send) {
                    //Log.e("send packet", "send");
                    DatagramPacket packet = new DatagramPacket(sendMsg, sendMsg.length, inetAddress, port);
                    udpSocket.send(packet);
                    inputData = new DatagramPacket(inputMsg, inputMsg.length);
                    udpSocket.receive(inputData);
                    String data = getStringData(inputData.getData());
                    int countPackage = getCountPackages(data);
                    if (countPackage != -1) {
                        ArrayList<Float> tempFramePoints = new ArrayList<>();
                        //Log.e("countPackage", String.valueOf(countPackage));
                        for (int i = 0; i < countPackage; i++) {
                            udpSocket.receive(inputData);
                            //Log.e("recieve data", String.valueOf(i));
                            ArrayList<Float> temp = getDataPoints(inputData);
                            tempFramePoints.addAll(temp);
                            //Log.e("points info",printPoints(temp));
                        }
                        Log.i("Receive data", String.valueOf(dataFloat.size() / 3) + "points");
                        this.dataFloat = tempFramePoints;
                    }
                }
            }
            udpSocket.close();

        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
            udpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            udpSocket.close();
        }
    }

    private String getStringData(byte[] buffer) {

        byte[] dataIn = new byte[inputData.getLength()];
        for(int i=0; i<inputData.getLength(); i++){
            dataIn[i] = buffer[i];
        }
        String s = new String(dataIn, StandardCharsets.UTF_8);
        return s;
    }

    private String printPoints(ArrayList<Float> data){
        StringBuilder s= new StringBuilder();
        for (int i = 0; i < data.size(); i++ ){
            s.append(data.get(i));
            s.append(" ");
        }
        return s.toString();
    }
    private ArrayList<Float> getDataPoints(DatagramPacket data){
        ArrayList<Float> arrPoints = new ArrayList<>();
        for(int i = 0; i<data.getLength();){
            int size = 2;
            byte[] temp = new byte[size];
            for(int j = 0; j<size;j++){
                temp[j] = data.getData()[i++];
            }
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.put(temp[0]);
            bb.put(temp[1]);
            short shortVal = bb.getShort(0);
            float f = (float)shortVal/(float)100;
            arrPoints.add(f);
        }
        return arrPoints;
    }
    private int getCountPackages(String str) {
        if (str.charAt(0) == 's') {
            int i = 0;
            try{
               i = Integer.parseInt(str.substring(1));
            }catch (Exception e){
                return 0;
            }
            return i;
        }
        return -1;
    }

    public float[] getFloatArray(){
        send = false;
        float[] array = new float[this.dataFloat.size()];
        try {
            for (int i=0; i<dataFloat.size();i++){
                if(i>= dataFloat.size()){
                    break;
                }
                array[i] = dataFloat.get(i);
            }
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e("Array range","index out of range");
            send = true;
        }
        send = true;
        return array;
    }
}
