/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import model.domain.Log;

/**
 *
 * @author anderson
 */
public class LogDAO {

    public LogDAO() {
    }

    public int inserirRegBD(Log log) {

        String sql = "INSERT INTO "
                + " PROCNFEE_LOG "
                + " ( "
                + " DATA_HR_PROC "
                + " , DATA_ENCAMINHADO "
                + " , REMETENTE_ENCAMINHADO "
                + " , ASSUNTO_ENCAMINHADO "
                + " , DATA_ORIGINAL "
                + " , REMETENTE_ORIGINAL "
                + " , ASSUNTO_ORIGINAL "
                + " , SIT_PROC "
                + " ) "
                + " VALUES "
                + " ( SYSDATE "
                + " , TO_DATE('" + log.getDtEncaminhado() + "', 'DD/MM/YYYY') "
                + " , '" + log.getRemetenteEncaminhado() + "'"
                + " , '" + log.getAssuntoEncaminhado() + "'"
                + " , TO_DATE('" + log.getDtOriginal() + "', 'DD/MM/YYYY') "
                + " , '" + log.getRemetenteOriginal() + "'"
                + " , '" + log.getAssuntoOriginal() + "'"
                + " , 1)";

        //System.out.println("SQL: " + sql);
        //return 0;
        return Conn.getInstance().manipBDDefault(sql);

    }

}
