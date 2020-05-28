/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;


/**
 *
 * @author administrador
 */
public class UltCapturaDAO {
    
    public int updateRegBD() {

        String sql = "UPDATE PROCNFEE_ULT_CAPTURA SET DTHR = SYSDATE, ENVIO_EMAIL = 0 ";

//        System.out.println("SQL: " + sql);
        return Conn.getInstance().manipBDDefault(sql);

    }
    
}
