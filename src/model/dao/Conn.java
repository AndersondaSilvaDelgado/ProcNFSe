/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OraclePreparedStatement;

/**
 *
 * @author anderson
 */
public class Conn {

    private int BD = 2;
    public final static String driver = "oracle.jdbc.driver.OracleDriver";
    public final static String url_prod = "jdbc:oracle:thin:@(DESCRIPTION = (ENABLE = BROKEN)(FAILOVER = ON)(LOAD_BALANCE = YES)"
            + "    (ADDRESS = (PROTOCOL = TCP)(HOST = stafe-scan)(PORT = 1521))"
            + "    (CONNECT_DATA ="
            + "      (SERVER = DEDICATED)"
            + "      (SERVICE_NAME = STAFE)"
            + "      (FAILOVER_MODE ="
            + "        (TYPE = SELECT)"
            + "        (METHOD = BASIC)"
            + "        (RETRIES = 180)"
            + "        (DELAY = 5)"
            + "       )"
            + "    )"
            + "  )";

    public final static String url_qa = "jdbc:oracle:thin:@(DESCRIPTION = (ENABLE = BROKEN)(FAILOVER = ON)(LOAD_BALANCE = YES)"
            + "    (ADDRESS = (PROTOCOL = TCP)(HOST = stafe-scan)(PORT = 1521))"
            + "    (CONNECT_DATA ="
            + "      (SERVER = DEDICATED)"
            + "      (SERVICE_NAME = STAFEQA)"
            + "      (FAILOVER_MODE ="
            + "        (TYPE = SELECT)"
            + "        (METHOD = BASIC)"
            + "        (RETRIES = 180)"
            + "        (DELAY = 5)"
            + "       )"
            + "    )"
            + "  )";

    public final static String url_dev = "jdbc:oracle:thin:@(DESCRIPTION = (ENABLE = BROKEN)(FAILOVER = ON)(LOAD_BALANCE = YES)"
            + " (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.2.15)(PORT = 1521)) "
            + " (CONNECT_DATA = "
            + "      (SERVER = DEDICATED) "
            + "      (SERVICE_NAME = STAFEDEV) "
            + "      (FAILOVER_MODE = "
            + "        (TYPE = SELECT) "
            + "        (METHOD = BASIC) "
            + "        (RETRIES = 180) "
            + "        (DELAY = 5) "
            + "       )"
            + "    )"
            + "  )";

    public final static String usuario = "INTERFACE";
    public final static String senha = "FGBNY946";

    private Connection connection = null;

    private static Conn instance = null;

    private Conn() {
        inicializar();
    }

    public static Conn getInstance() {
        if (instance == null) {
            instance = new Conn();
        }
        return instance;
    }

    private void inicializar() {
        try {
            Class.forName(driver);
            String url = "";
            switch (BD) {
                case 1:
                    url = url_prod;
                    break;
                case 2:
                    url = url_qa;
                    break;
                case 3:
                    url = url_dev;
                    break;
                default:
                    break;
            }

            connection = DriverManager.getConnection(url, usuario, senha);
        } catch (ClassNotFoundException e) {
            System.out.println("O driver nao pode ser carregado ["
                    + e.getMessage() + "]");
        } catch (SQLException e) {
            System.out.println("A conexao nao pode ser estabelecida ["
                    + e.getMessage() + "]");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao fechar a conexao ["
                    + e.getMessage() + "]");
        }
    }

    public int manipBDDefault(String sql) {
        int r = 0;
        try {
            Statement stmt = getConnection().createStatement();
            r = stmt.executeUpdate(sql);

            if (stmt != null) {
                stmt.close();
            }
            
            closeConnection();
            instance = null;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return r;
    }

    public int manipBDClob(String sql, String txt) {
        int r = 0;
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ((OraclePreparedStatement) ps).setStringForClob(1, txt);
            ps.execute();
            
            r = 1;
            
            if (ps != null) {
                ps.close();
            }
            
            closeConnection();
            instance = null;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            
        }
        return r;
    }
    
}
