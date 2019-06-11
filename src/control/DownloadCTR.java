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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import model.dao.LogDAO;
import model.domain.Log;
import org.jsoup.Jsoup;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Random;

/**
 *
 * @author anderson
 */
public class DownloadCTR {

//    private String saveDirectory = "C:/Attachment";
    private String saveDirectory = "M:";
    private static final int BUFFER_SIZE = 4096;
    private int nome = 0;
    private Log log;

    public DownloadCTR() {
    }

    public void downloadEmailAttachments() {

        Properties properties = new Properties();

        final String username = "processamentonfse@usinasantafe.com.br";
        final String password = "Sta9900";

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

            String data = "dd/MM/yyyy HH:mm:ss";
            String hora = "HH:mm:ss";

            for (int i = 0; i < arrayMessages.length; i++) {
//            for (int i = (arrayMessages.length - 1); i >= 0; i--) {
                log = new Log();

                Message message = arrayMessages[i];
                Address[] fromAddress = message.getFrom();
                String from = fromAddress[0].toString();
                String subject = message.getSubject();
                SimpleDateFormat formata = new SimpleDateFormat(data);
                String sentDate = formata.format(message.getSentDate());

                log.setDtEncaminhado(sentDate);
                log.setRemetenteEncaminhado("nfeservico@usinasantafe.com.br");
                log.setAssuntoEncaminhado(subject);

                if (from.contains("<")) {
                    String remEnc = from.substring(from.indexOf("<"));
                    log.setRemetenteOriginal(remEnc.replaceAll(">", "").replaceAll("<", ""));
                } else {
                    log.setRemetenteOriginal(from);
                }

                log.setAssuntoOriginal(subject);
                log.setDtOriginal(sentDate);
                log.setSitProc("NÃO FOI SALVO ANEXO");
                log.setDescrDownload("");

                String contentType = message.getContentType();
                String messageContent = "";
                String attachFiles = "";

                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            String fileName = "nulo";
                            if (part.getFileName() != null) {
                                fileName = part.getFileName();
                                fileName = fileName.replaceAll(" ", "");
                            }
                            fileName = removerCaracteresEspeciais(fileName.toLowerCase());
                            attachFiles += fileName + ", ";
                            System.out.println("ContentType Anexo = " + part.getContentType());
                            if (part.getContentType().contains("application/pdf")
                                    || part.getContentType().contains("application/octet-stream")) {
                                if (part.getContentType().contains("application/octet-stream")
                                        && fileName.contains(".pdf")) {
                                    fileName = ((fileName.length() < 30) ? fileName : (fileName.substring(0, 30) + ".pdf"));
                                    System.out.println("Salvou = " + saveDirectory + File.separator + "integracao_" + fileName);
                                    part.saveFile(saveDirectory + File.separator + "integracao_" + fileName);
                                    log.setSitProc("FOI SALVO ANEXO");
                                    log.setDescrDownload(saveDirectory + File.separator + "integracao_" + fileName);
                                    LogDAO logDAO = new LogDAO();
                                    logDAO.inserirRegBD(log);
                                } else if (part.getContentType().contains("application/pdf")) {
                                    fileName = ((fileName.length() < 30) ? fileName : (fileName.substring(0, 30) + ".pdf"));
                                    System.out.println("Salvou = " + saveDirectory + File.separator + "integracao_" + fileName);
                                    part.saveFile(saveDirectory + File.separator + "integracao_" + fileName);
                                    log.setSitProc("FOI SALVO ANEXO");
                                    log.setDescrDownload(saveDirectory + File.separator + "integracao_" + fileName);
                                    LogDAO logDAO = new LogDAO();
                                    logDAO.inserirRegBD(log);
                                }

                            }
                        } else {
                            // this part may be the message content
                            if (part.getContentType().contains("multipart")) {
                                Multipart multipart = (Multipart) part.getContent();
                                int numberOfParts2 = multipart.getCount();
                                for (int partCount2 = 0; partCount2 < numberOfParts2; partCount2++) {
                                    MimeBodyPart part2 = (MimeBodyPart) multipart.getBodyPart(partCount2);
                                    if (Part.ATTACHMENT.equalsIgnoreCase(part2.getDisposition())) {
                                        // this part is attachment
                                        String fileName = "nulo";
                                        if (part.getFileName() != null) {
                                            fileName = part2.getFileName();
                                            fileName = fileName.replaceAll(" ", "");
                                        }
                                        fileName = removerCaracteresEspeciais(fileName.toLowerCase());
                                        attachFiles += fileName + ", ";
                                        System.out.println("ContentType Anexo = " + part2.getContentType());
                                        if (part2.getContentType().contains("application/pdf")
                                                || part2.getContentType().contains("application/octet-stream")) {
                                            if (part2.getContentType().contains("application/octet-stream")
                                                    && fileName.contains(".pdf")) {
                                                fileName = ((fileName.length() < 30) ? fileName : (fileName.substring(0, 30) + ".pdf"));
                                                System.out.println("Salvou = " + saveDirectory + File.separator + "integracao_" + fileName);
                                                part2.saveFile(saveDirectory + File.separator + "integracao_" + fileName);
                                                log.setSitProc("FOI SALVO ANEXO");
                                                log.setDescrDownload(saveDirectory + File.separator + "integracao_" + fileName);
                                                LogDAO logDAO = new LogDAO();
                                                logDAO.inserirRegBD(log);
                                            } else if (part2.getContentType().contains("application/pdf")) {
                                                fileName = ((fileName.length() < 30) ? fileName : (fileName.substring(0, 30) + ".pdf"));
                                                System.out.println("Salvou = " + saveDirectory + File.separator + "integracao_" + fileName);
                                                part2.saveFile(saveDirectory + File.separator + "integracao_" + fileName);
                                                log.setSitProc("FOI SALVO ANEXO");
                                                log.setDescrDownload(saveDirectory + File.separator + "integracao_" + fileName);
                                                LogDAO logDAO = new LogDAO();
                                                logDAO.inserirRegBD(log);
                                            }

                                        }
                                    } else {
                                        if (part2.getContentType().contains("multipart")) {
                                            Multipart multipart3 = (Multipart) part2.getContent();
                                            int numberOfParts3 = multipart3.getCount();
                                            for (int partCount3 = 0; partCount3 < numberOfParts3; partCount3++) {
                                                MimeBodyPart part3 = (MimeBodyPart) multipart3.getBodyPart(partCount3);
                                                if (Part.ATTACHMENT.equalsIgnoreCase(part3.getDisposition())) {
                                                    // this part is attachment
                                                    String fileName = "nulo";
                                                    if (part.getFileName() != null) {
                                                        fileName = part3.getFileName();
                                                        fileName = fileName.replaceAll(" ", "");
                                                    }
                                                    fileName = removerCaracteresEspeciais(fileName.toLowerCase());
                                                    attachFiles += fileName + ", ";
                                                    System.out.println("ContentType Anexo = " + part3.getContentType());
                                                    if (part3.getContentType().contains("application/pdf")
                                                            || part3.getContentType().contains("application/octet-stream")) {
                                                        if (part3.getContentType().contains("application/octet-stream")
                                                                && fileName.contains(".pdf")) {
                                                            fileName = ((fileName.length() < 30) ? fileName : (fileName.substring(0, 30) + ".pdf"));
                                                            System.out.println("Salvou = " + saveDirectory + File.separator + "integracao_" + fileName);
                                                            part3.saveFile(saveDirectory + File.separator + "integracao_" + fileName);
                                                            log.setSitProc("FOI SALVO ANEXO");
                                                            log.setDescrDownload(saveDirectory + File.separator + "integracao_" + fileName);
                                                            LogDAO logDAO = new LogDAO();
                                                            logDAO.inserirRegBD(log);
                                                        } else if (part3.getContentType().contains("application/pdf")) {
                                                            fileName = ((fileName.length() < 30) ? fileName : (fileName.substring(0, 30) + ".pdf"));
                                                            System.out.println("Salvou = " + saveDirectory + File.separator + "integracao_" + fileName);
                                                            part3.saveFile(saveDirectory + File.separator + "integracao_" + fileName);
                                                            log.setSitProc("FOI SALVO ANEXO");
                                                            log.setDescrDownload(saveDirectory + File.separator + "integracao_" + fileName);
                                                            LogDAO logDAO = new LogDAO();
                                                            logDAO.inserirRegBD(log);
                                                        }

                                                    }
                                                } else {
                                                    if (part3.getContentType().contains("text/plain")
                                                            || part3.getContentType().contains("text/html")) {
                                                        messageContent = messageContent + convert(part3);
                                                    }
                                                }
                                            }
                                        } else {
                                            if (part2.getContentType().contains("text/plain")
                                                    || part2.getContentType().contains("text/html")) {
                                                messageContent = messageContent + convert(part2);
                                            }
                                        }
                                    }

                                }
                            } else {
                                if (part.getContentType().contains("text/plain")
                                        || part.getContentType().contains("text/html")) {
//                                    messageContent = messageContent + part.getContent().toString();
                                    messageContent = messageContent + convert(part);
                                }
                            }
                        }
                    }

                    if (attachFiles.length() > 1) {
                        attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                    }
                } else if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    Object content = message.getContent();
                    if (content != null) {
                        messageContent = messageContent + content.toString();
                    }
                }

                System.out.println("\t Sent Date: " + sentDate);
                System.out.println("\t Message: " + html2text(messageContent));
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

                    int posFinal = 999999999;
                    if ((conteudo.indexOf((char) 10) > 0) && (posFinal >= conteudo.indexOf((char) 10))) {
                        posFinal = conteudo.indexOf((char) 10);
                    }
                    if ((conteudo.indexOf(" ") > 0) && (posFinal >= conteudo.indexOf(" "))) {
                        posFinal = conteudo.indexOf(" ");
                    }
                    if ((conteudo.indexOf("\"") > 0) && (posFinal >= conteudo.indexOf("\""))) {
                        posFinal = conteudo.indexOf("\"");
                    }
                    if ((conteudo.indexOf("<") > 0) && (posFinal >= conteudo.indexOf("<"))) {
                        posFinal = conteudo.indexOf("<");
                    }

                    if (posFinal == 999999999) {
                        posFinal = conteudo.length() - 1;
                    }
                    String link = conteudo.substring(0, posFinal);
//                    System.out.println("Link = " + link);

                    Random gerador = new Random();
                    nome = gerador.nextInt(1000);
                    if ((!link.trim().contains("www.w3.org"))
                            && (!link.trim().contains("schemas.microsoft.com"))
                            && (!link.trim().contains("www.adobe.com"))
                            && (!link.trim().contains("whatsapp"))
                            && (!link.trim().contains("facebook"))
                            && (!link.trim().contains("linkedin"))
                            && (!link.trim().contains("twitter"))
                            && (!link.trim().contains("youtube"))
                            && (!link.trim().contains("outlook"))
                            && (!link.trim().contains("instagram"))
                            && (!link.trim().contains("youtu.be"))) {

                        downloadFile(link.trim(), saveDirectory, nome);

                    }

                    conteudo = conteudo.substring(posFinal);

                }

                if (log.getSitProc().equals("NÃO FOI SALVO ANEXO")) {
                    LogDAO logDAO = new LogDAO();
                    logDAO.inserirRegBD(log);
                }

                message.setFlag(Flags.Flag.DELETED, true);
            }

            folderInbox.close(true);
            store.close();

        } catch (Exception ex) {
            System.out.println("" + ex.toString());
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
            URL url = new URL(stringUrl.replaceAll("amp;", ""));
            InputStream is = url.openStream();
            FileOutputStream fos = new FileOutputStream(saveDirectory + File.separator + "integracao_" + nome + ".pdf");
            int umByte = 0;
            while ((umByte = is.read()) != -1) {
                fos.write(umByte);
            }
            is.close();
            fos.close();
            return new File(saveDirectory + File.separator + "integracao_" + nome + ".pdf");
        } catch (Exception e) {
            //Lembre-se de tratar bem suas excecoes, ou elas tambem lhe tratarão mal!
            //Aqui so vamos mostrar o stack no stderr.
            System.out.println("Erro = " + e.toString());
        }
        return null;
    }

    public String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    public int downloadFile(String fileURL, String saveDir, int nome) {
        HttpURLConnection httpConn = null;
        try {
            URL url = new URL(fileURL.replaceAll("amp;", ""));
            System.out.println("URL = " + url);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(2 * 60 * 1000);
            httpConn.setReadTimeout(2 * 60 * 1000);
            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                fileName = "integracao_" + nome + ".pdf";

                if (contentType.contains("application/pdf")) {

                    System.out.println("URL = " + url + " - PDF");
                    InputStream inputStream = httpConn.getInputStream();
                    String saveFilePath = saveDir + File.separator + fileName;

                    FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                    int bytesRead = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();

                    System.out.println("File downloaded");
                    log.setSitProc("FOI SALVO ANEXO");
                    log.setDescrDownload(saveDirectory + File.separator + "integracao_" + fileName);
                    LogDAO logDAO = new LogDAO();
                    logDAO.inserirRegBD(log);

                }

            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        } catch (Exception ex) {
            System.out.println("Falha no execução = " + ex);
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
            return 1;
        }
    }

    public String convert(MimeBodyPart part) {
        String ret = "";
        try{
            ret = part.getContent().toString();
        } catch (Exception ex) {
            System.out.println("Erro = " + ex);
        } finally {
            return ret;
        }
    }

}
