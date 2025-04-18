package org.example.listener;


import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {
    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMail(Map<String, Object> data) {
        try{
            String email = (String) data.get("email");
            Integer code = (Integer) data.get("code");
            String type = (String) data.get("type");
            SimpleMailMessage message = switch (type){
                case "register" ->
                        createSimpleMailMessage("欢迎注册我们的网站",
                                "您的邮箱验证码为 "+code+",有效时长三分钟，为了保障您的安全，请勿向他人泄露您的验证码",email);
                case "reset" ->
                        createSimpleMailMessage("密码重置邮件",
                                "您好，您正在进行重置密码操作，验证码："+code+",有效时长三分钟，如非本人操作，请无视",email);
                default -> null;
            };
            if(message == null) return;
            sender.send(message);
        }catch ( Exception e ){
            e.printStackTrace();
        }

    }

    private SimpleMailMessage createSimpleMailMessage(String title, String content, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;

    }
}
