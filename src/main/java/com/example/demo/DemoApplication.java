package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private static Logger logger = LoggerFactory.getLogger(TonyUdpServer.class);

    @Autowired
    private TonyUdpServer udpServer;

    @Override
    public void run(String... args) throws Exception {
        udpServer.start();
        logger.info("socket.io启动成功！");
    }
}
