package ru.truckfollower.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.truckfollower.repo.AlarmRepo;
import ru.truckfollower.service.SendAlarmMessageService;

import javax.annotation.PostConstruct;

@Controller
@Slf4j
public class TestController {

    @Autowired
    AlarmRepo alarmRepo;

    @Autowired
    SendAlarmMessageService sendAlarmMessageService;

    @Autowired
    MessageConverter messageConverter;

    @PostConstruct
    public void init() throws Exception {
/*

      Alarm a=  alarmRepo.findById(1597136L).get();


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(a);
        objectOutputStream.flush();


        //жсон
      // Message m = messageConverter.toMessage(a,new MessageProperties());


        InetAddress inetAddress =InetAddress.getByName("26.167.224.214");
        int port = 9009;

        DatagramSocket clientSocket = new DatagramSocket();

        DatagramPacket datagramPacket = new DatagramPacket(byteArrayOutputStream.toByteArray(), byteArrayOutputStream.size() ,inetAddress,port);


            clientSocket.send(datagramPacket);
            clientSocket.send(datagramPacket);


        */
/*Socket socket = new Socket("26.167.224.214",9009);
        socket.s
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        for (byte b : s.getBytes()) {
            writer.write(b);
        }
        writer.flush();
        writer.close();*//*



*/


    }
}
