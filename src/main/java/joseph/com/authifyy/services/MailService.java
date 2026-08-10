package joseph.com.authifyy.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    // @Value("${spring.mail.username}")
    private String FROMEMAIL = "dt8424423@gmail.com";
    private static final String VERIFICATIONSUBJECT = "Account Verification Email";

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    //Simple text email
    // TODO: implement an HTML email page.
    @Async
    public CompletableFuture<Void> sendEmail(String to, String body) {
        System.out.println("Sending email to " + to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROMEMAIL);
        message.setTo(to);
        message.setSubject(VERIFICATIONSUBJECT);
        message.setText(body);

        javaMailSender.send(message);

        return CompletableFuture.completedFuture(null);
    }
}
