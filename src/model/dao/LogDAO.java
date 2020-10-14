/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import model.domain.Log;
import oracle.jdbc.OraclePreparedStatement;

/**
 *
 * @author anderson
 */
public class LogDAO {

    public LogDAO() {
    }

    public int inserirRegBD(Log log) {
        
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            
            String sql = "INSERT INTO "
                + " PROCNFSE_LOG "
                + " ( "
                + " DATA_HR_PROC "
                + " , DATA_ENCAMINHADO "
                + " , REMETENTE_ENCAMINHADO "
                + " , ASSUNTO_ENCAMINHADO "
                + " , DATA_ORIGINAL "
                + " , REMETENTE_ORIGINAL "
                + " , ASSUNTO_ORIGINAL "
                + " , SIT_PROC "
                + " , DESCR_ARQUIVO "
                + " , DETALHES "
                + " ) "
                + " VALUES "
                + " ( SYSDATE "
                + " , TO_DATE('" + log.getDtEncaminhado() + "', 'DD/MM/YYYY HH24:MI:SS') "
                + " , '" + log.getRemetenteEncaminhado() + "'"
                + " , '" + log.getAssuntoEncaminhado() + "'"
                + " , TO_DATE('" + log.getDtOriginal() + "', 'DD/MM/YYYY HH24:MI:SS') "
                + " , '" + log.getRemetenteOriginal() + "'"
                + " , '" + log.getAssuntoOriginal() + "'"
                + " , '" + log.getSitProc() + "'"
                + " , '" + log.getDescrDownload() + "'"
                + " , ?)";

            System.out.println("SQL: " + sql);
        
            conn = Conn.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ((OraclePreparedStatement) ps).setStringForClob(1, log.getDetalhe());
            ps.execute();
            
        } catch (Exception e) {
            System.out.println("Erro1 = " + e);
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception er) {
                System.out.println("Erro2 = " + er);
            }
        } finally{
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("Erro3 = " + e);
            }
        }
        
        return 1;

    }

}
