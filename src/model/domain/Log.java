/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.domain;

/**
 *
 * @author anderson
 */
public class Log {
    
    private String dtEncaminhado;
    private String remetenteEncaminhado;
    private String assuntoEncaminhado;
    private String dtOriginal;
    private String remetenteOriginal;
    private String assuntoOriginal;
    private String sitProc;
    private String descrDownload;

    public Log() {
    }

    public String getDtEncaminhado() {
        return dtEncaminhado;
    }

    public void setDtEncaminhado(String dtEncaminhado) {
        this.dtEncaminhado = dtEncaminhado;
    }

    public String getRemetenteEncaminhado() {
        return remetenteEncaminhado;
    }

    public void setRemetenteEncaminhado(String remetenteEncaminhado) {
        this.remetenteEncaminhado = remetenteEncaminhado;
    }

    public String getAssuntoEncaminhado() {
        return assuntoEncaminhado;
    }

    public void setAssuntoEncaminhado(String assuntoEncaminhado) {
        this.assuntoEncaminhado = assuntoEncaminhado;
    }

    public String getDtOriginal() {
        return dtOriginal;
    }

    public void setDtOriginal(String dtOriginal) {
        this.dtOriginal = dtOriginal;
    }

    public String getRemetenteOriginal() {
        return remetenteOriginal;
    }

    public void setRemetenteOriginal(String remetenteOriginal) {
        this.remetenteOriginal = remetenteOriginal;
    }

    public String getAssuntoOriginal() {
        return assuntoOriginal;
    }

    public void setAssuntoOriginal(String assuntoOriginal) {
        this.assuntoOriginal = assuntoOriginal;
    }

    public String getSitProc() {
        return sitProc;
    }

    public void setSitProc(String sitProc) {
        this.sitProc = sitProc;
    }

    public String getDescrDownload() {
        return descrDownload;
    }

    public void setDescrDownload(String descrDownload) {
        this.descrDownload = descrDownload;
    }
}
