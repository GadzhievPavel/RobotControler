package com.example.robotcontroler.view.joystick.net;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class JoysticDatargamClient implements Runnable{

    private Thread thread;
    private DatagramSocket udpSocket;
    private DatagramPacket inputData;
    private byte[] sendMsg;
    private String ip;
    private int port;
    private boolean run, send;
    private int angle;
    private int strength;
    private ByteBuffer byteBuffer;
    private InetAddress inetAddress;
    private Handler handler;
    public JoysticDatargamClient(String ipServer, int portServer) {
        thread = new Thread(this, "sending data thread");
        this.ip = ipServer;
        //Log.e("ip",ip);
        this.port = portServer;
        this.angle = 0;
        this.strength = 0;
    }

    public void startThread(){
        run = true;
        send = true;
        thread.start();
    }

    public void stopThread(){
        run = false;
        send = false;
    }

    public void pauseThread(){
        send = false;
    }

    public void resumeThread(){
        send = true;
    }
    @Override
    public void run() {
        try {
            udpSocket = new DatagramSocket();
            //Log.e("inetAddress","init");
            inetAddress = InetAddress.getByName(ip);
            //Log.e("inetAddress","finish init");
            udpSocket.connect(inetAddress, port);
            run = true;
            while (run){
                if(send){
                    String s = "twist";
                    Log.e("angle st",angle+" "+strength);
                    sendMsg = makeByteArray(s.getBytes(StandardCharsets.UTF_8), angle, strength);
                    //Log.e("sendMsg", String.valueOf(sendMsg.toString()));
                    Log.e("port", String.valueOf(port));
                    DatagramPacket packet = new DatagramPacket(sendMsg, sendMsg.length, inetAddress, port);
                    udpSocket.send(packet);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        udpSocket.close();
    }

    public void setTwist(int angle, int strength){
        this.angle = angle;
        this.strength = strength;
    }

    byte[] toBytes(short i)
    {
        byte[] result = new byte[2];
        result[0] = (byte)(i & 0xff);
        result[1] = (byte)((i >> 8) & 0xff);
        return result;
    }

    private byte[] makeByteArray(byte[] tag, int angle, int strength){
        short a = (short) angle;
        short s = (short) strength;
        byte[] angleByte = toBytes(a);
        byte[] strengthByte = toBytes(s);
        byte b[] = new byte[tag.length+strengthByte.length+angleByte.length];
        int i = 0;
        for (byte b1: tag) {
            b[i++]=b1;
        }
        for (byte b1: angleByte) {
            b[i++]=b1;
        }
        for (byte b1: strengthByte) {
            b[i++]=b1;
        }
        return b;
    }
}
