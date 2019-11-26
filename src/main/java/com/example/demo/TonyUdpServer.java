package com.example.demo;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


@Slf4j
public class TonyUdpServer {

    private static final int MAX_UDP_DATA_SIZE = 4096;
    private DatagramSocket socket;
    private DatagramPacket packet = null;

    private static Logger logger = LoggerFactory.getLogger(TonyUdpServer.class);


    @Autowired
    private SocketIOServer socketIOServer;

    public TonyUdpServer(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        socketIOServer.start();

        while (true) {
            byte[] buffer = new byte[MAX_UDP_DATA_SIZE];
            packet = new DatagramPacket(buffer, buffer.length);
            try {
                logger.info("=======此方法在接收到数据报之前会一直阻塞======");
                socket.receive(packet);
                new Thread(new Process(packet)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Process implements Runnable {
        private Process(DatagramPacket packet) throws UnsupportedEncodingException {
            byte[] buffer = packet.getData();// 接收到的UDP信息，然后解码
            String srt2 = new String(buffer, "UTF-8").trim();
            logger.info("=======接收到的UDP信息======" + srt2);
        }

        @Override
        public void run() {
            try {
                logger.info("====向客户端响应数据=====");
                //1.定义客户端的地址、端口号、数据
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                byte[] data2 = "{'request':'alive','errcode':'0'}".getBytes();
                //2.创建数据报，包含响应的数据信息
                DatagramPacket packet2 = new DatagramPacket(data2, data2.length, address, port);
                //3.响应客户端
                socket.send(packet2);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
