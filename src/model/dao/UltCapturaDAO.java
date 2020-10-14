/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.sql.Connection;
import java.sql.Statement;


/**
 *
 * @author administrador
 */
public class UltCapturaDAO {
    
    public int updateRegBD() {
        
        Connection conn = null;
        Statement stmt = null;
        int r = 0;

        try {
            
            String sql = "UPDATE "
                            + " PROCNFEE_ULT_CAPTURA "
                        + " SET "
                            + " DTHR = SYSDATE, ENVIO_EMAIL = 0 ";
            
            conn = Conn.getInstance().getConnection();
            stmt = conn.createStatement();
            r = stmt.executeUpdate(sql);
            
        } catch (Exception e) {
            System.out.println("Erro1 = " + e);
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception er) {
                System.out.println("Erro2 = " + er);
            }
        } finally{
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("Erro3 = " + e);
            }
        }
        
        return r;

    }
    
}
