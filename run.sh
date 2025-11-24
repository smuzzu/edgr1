mvn -X clean package
java -cp ".:src/main/resources/javax.mail-api-1.6.2.jar:src/main/resources/javax.mail-1.6.2.jar:src/main/resources/activation-1.1.1.jar:target/classes" MailSenderUtil