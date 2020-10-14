/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.apache.commons.io.FileUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.JavascriptExecutor;

/**
 *
 * @author anderson
 */
public class DownloadCTR {

    private String saveDirecCopia = "C:/Attachment";
//    private String saveDirectory = "C:/Attachment"; //DEV
//    private String saveDirecPrinc = "C:/Attachment";
    private String saveDirecPrinc = "\\\\wntvmsesuite\\Dados_SESuite\\NucleoFiscal\\Captura\\NOTAS-FISCAIS-DE-SERVICO";
    private String saveFilePath;
//    private String saveDirectory = "M:" //QA
//    private String saveDirectory = "N:"; //PROD
//    private String saveDirecPrinc = "N:";
//      private String saveDirectory = "\\\\wntvmsesuite\\Dados_SESuite\\NucleoFiscal\\Captura\\NOTAS-FISCAIS-DE-SERVICO"; //PROD
//      private String saveDirecPrinc = "\\\\wntvmsesuite\\Dados_SESuite\\NucleoFiscal\\Captura\\NOTAS-FISCAIS-DE-SERVICO";
     
    private static final int BUFFER_SIZE = 4096;
//    private static final int BUFFER_SIZE = 20480;
    private int nome = 0;
    private Log log;
    private String fileName;
    private String attachFiles;
    private String sentDate;
    
    public DownloadCTR() {
    }

    public void abreCaixaEmail() {

        Properties properties = new Properties();

        String username = "processamentonfse@usinasantafe.com.br";
        String password = "Sta9900";
//        String username = "impressoras@usinasantafe.com.br";
//        String password = "Sta1234";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", "WNTVMEX10.usinasantafe.com.br");
//        props.put("mail.smtp.host", "WNTVMEX16.usinasantafe.com.br");
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
//            store.connect("WNTVMEX16.usinasantafe.com.br", username, password);

            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);

            lerCaixaEmail(folderInbox.getMessages());

            folderInbox.close(true);

//            Folder folderLix = store.getFolder("Itens Excluídos");
//            folderLix.open(Folder.READ_WRITE);
//
//            Message[] arrayMessagesLix = folderLix.getMessages();
//
//            for (int i = 0; i < arrayMessagesLix.length; i++) {
//                Message message = arrayMessagesLix[i];
//                message.setFlag(Flags.Flag.DELETED, true);
//            }
//
//            folderLix.close(true);

            store.close();

        } catch (Exception ex) {
            System.out.println("" + ex.toString());
        }

    }

    private void lerCaixaEmail(Message[] arrayMessages) {

        String data = "dd/MM/yyyy HH:mm:ss";
        String hora = "HH:mm:ss";

        try {
            
            for (int i = 0; i < arrayMessages.length; i++) {
                
                log = new Log();

                Message message = arrayMessages[i];
                Address[] fromAddress = message.getFrom();
                String from = fromAddress[0].toString();
                String subject = message.getSubject();
                SimpleDateFormat formata = new SimpleDateFormat(data);
                sentDate = formata.format(message.getReceivedDate());

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
                log.setDetalhe("");

                String contentType = message.getContentType();
                String messageContent = "";
                attachFiles = "";
                
                if (contentType.contains("multipart")) {
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            lerParteEmail(part);
                        } else {
                            if (part.getContentType().contains("multipart")) {
                                Multipart multipart = (Multipart) part.getContent();
                                int numberOfParts2 = multipart.getCount();
                                for (int partCount2 = 0; partCount2 < numberOfParts2; partCount2++) {
                                    MimeBodyPart part2 = (MimeBodyPart) multipart.getBodyPart(partCount2);
                                    if (Part.ATTACHMENT.equalsIgnoreCase(part2.getDisposition())) {
                                        lerParteEmail(part2);
                                    } else {
                                        if (part2.getContentType().contains("multipart")) {
                                            Multipart multipart3 = (Multipart) part2.getContent();
                                            int numberOfParts3 = multipart3.getCount();
                                            for (int partCount3 = 0; partCount3 < numberOfParts3; partCount3++) {
                                                MimeBodyPart part3 = (MimeBodyPart) multipart3.getBodyPart(partCount3);
                                                if (Part.ATTACHMENT.equalsIgnoreCase(part3.getDisposition())) {
                                                    lerParteEmail(part3);
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

                log.setDetalhe(log.getDetalhe() + "\nSent Date: " + sentDate);
                System.out.println("Sent Date: " + sentDate);
                log.setDetalhe(log.getDetalhe() + "\nMessage: " + html2text(messageContent));
                System.out.println("Message: " + html2text(messageContent));
                String conteudo = messageContent;
                verLinkEmail(conteudo, sentDate);

                salvaLog();

                message.setFlag(Flags.Flag.DELETED, true);
                
            }
            
        } catch (Exception ex) {
            System.out.println("Erro" + ex.toString());
        }
    }

    private void lerParteEmail(MimeBodyPart part){
        try {
            fileName = "nulo";
            if (part.getFileName() != null) {
                fileName = part.getFileName();
                fileName = fileName.replaceAll(" ", "");
            }
            fileName = removerCaracteresEspeciais(fileName.toLowerCase());
            attachFiles += fileName + ", ";
            log.setDetalhe(log.getDetalhe() + "ContentType Anexo = " + part.getContentType());
            System.out.println("ContentType Anexo = " + part.getContentType());
            if (part.getContentType().contains("application/pdf")
                    || part.getContentType().contains("application/octet-stream")) {
                if (part.getContentType().contains("application/octet-stream")
                        && fileName.contains(".pdf")) {
                    salvaAnexo(part);
                } else if (part.getContentType().contains("application/pdf")) {
                    salvaAnexo(part);
                }
            }
        } catch (Exception ex) {
            System.out.println("" + ex.toString());
        }
    }
    
    private void verLinkEmail(String conteudo, String sentDate){
        
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
                    && (!link.trim().contains("youtu.be"))
                    && (!link.trim().contains("sendgrid.net"))
                    && (!link.trim().contains("fastsolutions.com"))
                    && (!link.trim().contains("usinasantafe.com"))) {

                if (link.trim().contains("ginfes")) {
                    downloadGinfes(link.trim());
                } else if (link.trim().contains("nfe.prefeitura.sp.gov.br/nfe.aspx")) {
                    downloadPrefeituraSP(link.trim());
                } else if (link.trim().contains("e-gov.betha")) {
                    downloadBetha(link.trim());
                } else if (link.trim().contains("simplissweb")) {
                    downloadSimpliss(link.trim());
                } else if (link.trim().contains("www.issnetonline.com.br/ribeiraopreto")){
                    downloadRibeiraoPreto(link.trim());
                }  else {
                    downloadLink(link.trim());
                }

            }

            conteudo = conteudo.substring(posFinal);

        }
    }

    private String removerCaracteresEspeciais(String text) {
        StringBuilder stringBuilder = new StringBuilder(text.replaceAll("[^a-zZ-Z1-9 ]", ""));
        if (!text.equals("")) {
            stringBuilder.insert(text.replaceAll("[^a-zZ-Z1-9 ]", "").length() - 3, '.');
        }
        return stringBuilder.toString();
    }

    private String html2text(String html) {
        return Jsoup.parse(html).text();
    }
    
    private void downloadRibeiraoPreto(String fileURL){
        
        try {
            
            String phantomjsExeutableFilePath = "C:/oracle/phantomjs.exe";
            System.setProperty("phantomjs.binary.path", phantomjsExeutableFilePath);
            
            // Initiate PhantomJSDriver.
            WebDriver driver = new PhantomJSDriver();
            
            Dimension a = new Dimension(700, 800);
            driver.manage().window().setSize(a);
            
            driver.get(fileURL);
            
            
            saveFilePath = File.separator + "integracao_" + nome + ".jpg";
            String saveFilePathGIF = saveDirecCopia + saveFilePath;
            
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(saveFilePathGIF), true);
            
            saveFilePath = File.separator + "integracao_" + nome + ".pdf";
            
            Document document = new Document();
            FileOutputStream fos = new FileOutputStream(saveDirecPrinc + saveFilePath);

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.open();
            document.open();
            Image img = Image.getInstance(saveFilePathGIF);
            img.setAbsolutePosition(-60, -50);
            img.scalePercent(80, 90);
            document.add(img);
            document.close();
            writer.close();
            
        }catch (Exception ex) {
            System.out.println("Falha no execução = " + ex);
            log.setDetalhe(log.getDetalhe() + "\nFalha no execução = " + ex);
        } finally {
        }
                
    }

    private void downloadGinfes(String fileURL) {
        String line = "", all = "";
        BufferedReader in = null;
        try {

            URL urlInicial = new URL(fileURL.replaceAll("amp;", ""));
            log.setDetalhe(log.getDetalhe() + "\nURL " + urlInicial);
            System.out.println("URL = " + urlInicial);
            in = new BufferedReader(new InputStreamReader(urlInicial.openStream()));

            while ((line = in.readLine()) != null) {
                all += line;
            }

            log.setDetalhe(log.getDetalhe() + "\n" + all.substring(all.indexOf("'") + 1, all.lastIndexOf("'")));
            System.out.println("" + all.substring(all.indexOf("'") + 1, all.lastIndexOf("'")));

            String urlPag = all.substring(all.indexOf("'") + 1, all.lastIndexOf("'"));

            in = null;

            URL url2 = new URL(urlPag.replaceAll("amp;", ""));
            log.setDetalhe(log.getDetalhe() + "\nURL2" + url2);
            in = new BufferedReader(new InputStreamReader(url2.openStream(), "ISO-8859-1"));

            boolean verNF = false;
            String nfs = "";
            String nomeRelatorio = "";

            while ((line = in.readLine()) != null) {
                if (line.contains("name=\"nfs\"")) {
                    verNF = true;
                }
                if (verNF) {
                    nfs += line;
                }
                if (line.contains("</input>")) {
                    verNF = false;
                }
                if (line.contains("name=\"nomeRelatorio\"")) {
                    nomeRelatorio = line;
                }
            }

            HttpPost post = new HttpPost("http://visualizar.ginfes.com.br/report/exportacao");

            List<NameValuePair> nameValuePairs
                    = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("nfs", nfs.replaceAll("<input type=\"hidden\" name=\"nfs\" value=\"", "").replaceAll("\"></input>", "")));
            nameValuePairs.add(new BasicNameValuePair("nomeRelatorio", nomeRelatorio.replaceAll("<input type=\"hidden\" name=\"nomeRelatorio\" value=\"", "").replaceAll("\"></input>", "")));
            nameValuePairs.add(new BasicNameValuePair("imprime", "0"));
            nameValuePairs.add(new BasicNameValuePair("tipo", "pdf"));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));

            post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:18.0) Gecko/20100101 Firefox/18.0");
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");

            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);

            log.setDetalhe(log.getDetalhe() + "\nRetorno: " + response.toString());
            System.out.println("Retorno: " + response.toString());

            if (post.getEntity() != null) {
                post.getEntity().consumeContent();
            }

            client.clearResponseInterceptors();
            client.clearRequestInterceptors();
            client.close();

            String ret = response.toString().substring(response.toString().indexOf("Location"));
            ret = ret.substring(0, ret.indexOf(",")).replaceAll("Location: ", "");

            HttpURLConnection httpConn = null;

            URL url = new URL(ret.replaceAll("amp;", ""));
            log.setDetalhe(log.getDetalhe() + "\nURL3 " + url);
            System.out.println("URL = " + url);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(2 * 60 * 1000);
            httpConn.setReadTimeout(2 * 60 * 1000);
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                saveFilePath = File.separator + "integracao_" + nome + ".pdf";
                log.setDetalhe(log.getDetalhe() + "\nFile = " + saveFilePath);

                InputStream inputStream = httpConn.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(saveDirecPrinc + saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                System.out.println("File downloaded");
                log.setSitProc("FOI SALVO ANEXO");
                log.setDescrDownload(log.getDescrDownload() + " - " + saveDirecPrinc +  saveFilePath);
                copiaArq();

            } else {
                log.setDetalhe(log.getDetalhe() + "\nresponseCode: " + responseCode);
                System.out.println("responseCode: " + responseCode);
            }
            httpConn.disconnect();
        } catch (Exception ex) {
            System.out.println("Falha no execução = " + ex);
            log.setDetalhe(log.getDetalhe() + "\nFalha no execução = " + ex);
        } finally {
        }
        
    }

    private int downloadPrefeituraSP(String fileURL) {
        HttpURLConnection httpConn = null;
        try {

            String urlString = fileURL.replaceAll("amp;", "").replaceAll("https://nfe.prefeitura.sp.gov.br/nfe.aspx?", "");
            log.setDetalhe(log.getDetalhe() + "\nURL Cortado = " + urlString);
            System.out.println("URL Cortado = " + urlString);
            urlString = "https://nfe.prefeitura.sp.gov.br/contribuinte/notaprintimg.aspx" + urlString + "&imprimir=1";
            log.setDetalhe(log.getDetalhe() + "\nURL = " + urlString);
            System.out.println("URL = " + urlString);
            URL url = new URL(urlString);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(2 * 60 * 1000);
            httpConn.setReadTimeout(2 * 60 * 1000);
            int responseCode = httpConn.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                
                saveFilePath = File.separator + "integracao_" + nome + ".gif";
                String saveFilePathGIF = saveDirecCopia + saveFilePath;

                InputStream inputStream = httpConn.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(saveFilePathGIF);

                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                log.setDetalhe(log.getDetalhe() + "\nSaveFilePathGIF = " + saveFilePathGIF);
                saveFilePath = File.separator + "integracao_" + nome + ".pdf";
                log.setDetalhe(log.getDetalhe() + "\nFile = " + saveFilePath);
                
                Document document = new Document();
                FileOutputStream fos = new FileOutputStream(saveDirecPrinc + saveFilePath);
                PdfWriter writer = PdfWriter.getInstance(document, fos);
                writer.open();
                document.open();
                Image img = Image.getInstance(saveFilePathGIF);
                img.scalePercent(50, 50);
                document.add(img);
                document.close();
                writer.close();

                System.out.println("File downloaded");
                log.setDetalhe(log.getDetalhe() + "\nFile = " + saveFilePath);
                log.setSitProc("FOI SALVO ANEXO");
                log.setDescrDownload(log.getDescrDownload() + " - " + saveDirecPrinc + saveFilePath);
                copiaArq();

            } else {
                log.setDetalhe(log.getDetalhe() + "\nresponseCode: " + responseCode);
                System.out.println("responseCode: " + responseCode);
            }
            
        } catch (Exception ex) {
            System.out.println("Falha no execução = " + ex);
            log.setDetalhe(log.getDetalhe() + "\nFalha no execução = " + ex);
        }  finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
            return 1;
        }
    }

    private int downloadBetha(String fileURL) {
        return downloadLink(fileURL.replaceAll("amp;", "") + "&local=A&mobile=1");
    }

    private int downloadSimpliss(String fileURL) {

        fileURL = fileURL.replaceAll("amp;", "");

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        dt = c.getTime();
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");

        fileURL = "https:" + fileURL.substring(fileURL.indexOf("//"), fileURL.indexOf("br")) + "br/contrib/app/nfse/rel/rp_nfse_lote?cnpj=45281813000135&cnpjPrestador=" + fileURL.substring(fileURL.indexOf("cnpj=") + 5, fileURL.indexOf("&ser")) + "&im=306170"
                + "&dtini=" + sentDate.substring(0, 10) + "%2000:00:00&dtfim=" + formatador.format(dt) + "%2000:00:00&tipo=4";

        return downloadLink(fileURL);

    }

    private String convert(MimeBodyPart part) {
        String ret = "";
        try {
            ret = part.getContent().toString();
        } catch (Exception ex) {
            System.out.println("Erro = " + ex);
        } finally {
            return ret;
        }
    }

    private void copiaArq() {
        
	try {
            
            InputStream inputStream = new FileInputStream(saveDirecPrinc + saveFilePath);
            FileOutputStream outputStream = new FileOutputStream(saveDirecCopia + saveFilePath);
            
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            
            System.out.println("SALVOU COPIA = " + saveDirecCopia + saveFilePath);
            
	} catch (Exception e) {
            System.out.println("Erro = " + e);
	}
        
    }
    
    private void salvaAnexo(MimeBodyPart part){
        try {
            fileName = ((fileName.length() < 30) ? fileName : (fileName.substring(0, 30) + ".pdf"));
            saveFilePath = File.separator + "integracao_" + fileName;
            log.setDetalhe(log.getDetalhe() + "\nFile = " + saveDirecPrinc + saveFilePath);
            part.saveFile(saveDirecPrinc + saveFilePath);
            System.out.println("Salvou = " + saveDirecPrinc + saveFilePath);
            log.setSitProc("FOI SALVO ANEXO");
            log.setDescrDownload(log.getDescrDownload() + " - " + saveDirecPrinc + saveFilePath);
            copiaArq();
        } catch (Exception ex) {
            System.out.println("Erro = " + ex);
            log.setDetalhe(log.getDetalhe() + "Erro = " + ex);
        } finally{
        }
    }
    
    private void salvaLog(){
        LogDAO logDAO = new LogDAO();
        logDAO.inserirRegBD(log);
    }
    
    private int downloadLink(String fileURL){
        HttpURLConnection httpConn = null;
        try {
            URL url = new URL(fileURL.replaceAll("amp;", ""));
            System.out.println("URL = " + url);
            log.setDetalhe(log.getDetalhe() + "\nURL = " + url);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(2 * 60 * 1000);
            httpConn.setReadTimeout(2 * 60 * 1000);
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                String contentType = httpConn.getContentType();

                if (contentType.contains("application/pdf")) {

                    saveFilePath = File.separator + "integracao_" + nome + ".pdf";
                    log.setDetalhe(log.getDetalhe() + "\nFile = " + saveFilePath);
                    
                    InputStream inputStream = httpConn.getInputStream();
                    FileOutputStream outputStream = new FileOutputStream(saveDirecPrinc + saveFilePath);

                    int bytesRead = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();

                    System.out.println("File downloaded");
                    log.setSitProc("FOI SALVO ANEXO");
                    log.setDescrDownload(log.getDescrDownload() + " - " + saveDirecPrinc + saveFilePath);
                    copiaArq();
                    
                }
                else{
                    log.setDetalhe(log.getDetalhe() + "\ncontentType =  " + contentType);
                    System.out.println("contentType " + contentType);
                }

            } else {
                log.setDetalhe(log.getDetalhe() + "\nresponseCode: " + responseCode);
                System.out.println("responseCode: " + responseCode);
            }
            httpConn.disconnect();
        } catch (Exception ex) {
            System.out.println("Falha no execução = " + ex);
            log.setDetalhe(log.getDetalhe() + "\nFalha no execução = " + ex);
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
            return 1;
        }
    }

}
