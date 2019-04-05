/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

/**
 *
 * @author anderson
 */
public class DownloadCTR {

    private String saveDirectory = "C:/Attachment";
    
    public DownloadCTR() {
    }
    
    public void downloadEmailAttachments() {

        Properties properties = new Properties();
        
        final String username = "anderson@usinasantafe.com.br";
        final String password = "Diasmelhores2";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", "WNTVMEX10.usinasantafe.com.br");
        props.put("mail.smtp.port", "25");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Store store = session.getStore("imaps");
            store.connect("WNTVMEX10.usinasantafe.com.br", username, password);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);

            // fetches new messages from server
            Message[] arrayMessages = folderInbox.getMessages();

            int nome = 0;

            for (int i = 0; i < arrayMessages.length; i++) {

                Message message = arrayMessages[i];
                Address[] fromAddress = message.getFrom();
                String from = fromAddress[0].toString();
                String subject = message.getSubject();
                String sentDate = message.getSentDate().toString();

                String contentType = message.getContentType();
                String messageContent = "";

                // store attachment file name, separated by comma
                String attachFiles = "";

                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            String fileName = part.getFileName();
                            fileName = fileName.replaceAll(" ", "");
                            fileName = removerCaracteresEspeciais(fileName.toLowerCase());
                            attachFiles += fileName + ", ";
                            part.saveFile(saveDirectory + File.separator + fileName);
                        } else {
                            // this part may be the message content
                            messageContent = part.getContent().toString();
                        }
                    }

                    if (attachFiles.length() > 1) {
                        attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                    }
                } else if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    Object content = message.getContent();
                    if (content != null) {
                        messageContent = content.toString();
                    }
                }

                // print out details of each message
                System.out.println("Message #" + (i + 1) + ":");
//                System.out.println("\t From: " + from);
//                System.out.println("\t Subject: " + subject);
//                System.out.println("\t Sent Date: " + sentDate);
//                System.out.println("\t Message: " + messageContent);
                String conteudo = messageContent;
                while (conteudo.contains("http://") || conteudo.contains("https://")) {

                    if (!conteudo.contains("http://")) {
                        conteudo = conteudo.substring(conteudo.indexOf("https://"));
                    } else if (!conteudo.contains("https://")) {
                        conteudo = conteudo.substring(conteudo.indexOf("http://"));
                    } else if (conteudo.indexOf("http://") <= conteudo.indexOf("https://")) {
                        conteudo = conteudo.substring(conteudo.indexOf("http://"));
                    } else {
                        conteudo = conteudo.substring(conteudo.indexOf("https://"));
                    }
                    
                    int posFinal = 9999999;
                    if((conteudo.indexOf((char) 10) > 0) && (posFinal >= conteudo.indexOf((char) 10))){
                        posFinal = conteudo.indexOf((char) 10);
                    }
                    if((conteudo.indexOf(" ") > 0) && (posFinal >= conteudo.indexOf(" "))){
                        posFinal = conteudo.indexOf(" ");
                    }
                    if((conteudo.indexOf("\"") > 0) && (posFinal >= conteudo.indexOf("\""))){
                        posFinal = conteudo.indexOf("\"");
                    }
                    if((conteudo.indexOf("<") > 0) && (posFinal >= conteudo.indexOf("<"))){
                        posFinal = conteudo.indexOf("<");
                    }
                    System.out.println("Posicao = " + posFinal);
                    String link = conteudo.substring(0, posFinal);
                    System.out.println("Link = " + link);

                    nome++;
                    if(!link.trim().equals("http://www.w3.org/1999/xhtml")){
                        gravaArquivoDeURL(link.trim(), saveDirectory, nome);
                    }
                    
                    conteudo = conteudo.substring(posFinal);

                }

//                message.setFlag(Flags.Flag.DELETED, true);
            }

            // disconnect
            folderInbox.close(true);
            store.close();
        } catch (IOException | MessagingException ex) {
            System.out.println("Erro = " + ex.toString());
        }
    }
    
    public String removerCaracteresEspeciais(String text) {
        StringBuilder stringBuilder = new StringBuilder(text.replaceAll("[^a-zZ-Z1-9 ]", ""));
        if (!text.equals("")) {
            stringBuilder.insert(text.replaceAll("[^a-zZ-Z1-9 ]", "").length() - 3, '.');
        }
        return stringBuilder.toString();
    }

    public File gravaArquivoDeURL(String stringUrl, String pathLocal, int nome) {
        try {
            URL url = new URL(stringUrl);
//			String nomeArquivoLocal = url.getPath();
            InputStream is = url.openStream();
            FileOutputStream fos = new FileOutputStream(saveDirectory + File.separator + nome + ".pdf");
            int umByte = 0;
            while ((umByte = is.read()) != -1) {
                fos.write(umByte);
            }
            is.close();
            fos.close();
            return new File(saveDirectory + File.separator + nome + ".pdf");
        } catch (Exception e) {
            //Lembre-se de tratar bem suas excecoes, ou elas tambem lhe tratarão mal!
            //Aqui so vamos mostrar o stack no stderr.
            e.printStackTrace();
        }
        return null;
    }
    
}