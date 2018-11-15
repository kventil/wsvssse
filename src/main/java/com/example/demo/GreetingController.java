package com.example.demo;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class GreetingController {


    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @RequestMapping("/accounts/alerts")
    public SseEmitter getAccountAlertsNoPathVariable(HttpSession session) {

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        Thread t1 = new Thread(() ->{
            try {
                int i = 0;
                // Send 10000 messages
                while(++i<=10000){
                    Thread.sleep(1000);
                    System.out.println("Sending");
                    try{
                        emitter.send(new Alert((long) i,"Alert message"+i));
                    }catch(ClientAbortException cae){
                        //The client is not there anymore, we get out of the loop
                        i = 10000;
                    }
                }
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();

        return emitter;
    }

}