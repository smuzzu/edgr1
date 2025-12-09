import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart; 
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import java.io.UnsupportedEncodingException;

public class MailSenderUtil {


    private static final String username="beatrizdassieu@gmail.com";
    private static final String password="svbskofrfnfoslef";
    private static final String destinationAddresses="sebamuzzu@gmail.com,edgar@qubitsuite.com";

    public static boolean sendMail(String subject, String text,String destinationAddress, String []attachmets){
        if (destinationAddress==null || destinationAddress.isEmpty()){
            destinationAddress=destinationAddresses;
        }
        Properties prop = new Properties();
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2"); //nuevo
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS


        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            MimeMessage message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(username,"Portal Statistics"));
            } catch (UnsupportedEncodingException e) {
                String errorMsg="UnsupportedEncodingException sending email=" + subject;
                System.out.println(errorMsg);
                e.printStackTrace();
            }

            InternetAddress from = null;
            try {
                from = new InternetAddress(username, "Joseph Mailer");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            message.setFrom(from);
            message.setRecipients(
                    MimeMessage.RecipientType.TO,
                    InternetAddress.parse(destinationAddress)
            );
            message.setSubject(subject);

// Create a multipar message
            Multipart multipart = new MimeMultipart();

// body
            BodyPart messageBodyPart = new MimeBodyPart();
//messageBodyPart.setText(text);
            messageBodyPart.setContent(text,"text/html");
            multipart.addBodyPart(messageBodyPart);

            if (attachmets!=null){
                for (int i=0; i<attachmets.length; i++){
                    String attachment=attachmets[i];
                    if (attachment!=null && !attachment.isEmpty()){
                        messageBodyPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(attachment);
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(attachment);
                        multipart.addBodyPart(messageBodyPart);
                    }
                }
            }

// Send the complete message parts
            message.setContent(multipart);

            Transport.send(message);


        } catch (MessagingException e) {
            String errorMsg="exception sending email=" + subject;
            System.out.println(errorMsg);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void sendMail(String subject, String text, String destinationAddress){
        sendMail(subject,text,destinationAddress,null);
    }


    public static void main(String[] args) {
        String subject = "Reporte de test automáticos";
        String body="";

        Path filePath = Paths.get("target/surefire-reports/emailable-report.html");
        try {
            body= Files.readString(filePath);
        }catch (Exception e){
            e.printStackTrace();
        }
        sendMail(subject,body,null);
    }

}
