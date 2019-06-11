/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anderson
 */
public class ThreadExecCTR extends Thread {

    public ThreadExecCTR() {
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {

        while (true) {
            try {

                System.out.println("Data e Hora = " + new Date());

                DownloadCTR downloadCTR = new DownloadCTR();
                downloadCTR.downloadEmailAttachments();

                sleep(120000);
//                sleep(120000000);

            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadExecCTR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
