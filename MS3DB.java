/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ms3interview;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import static ms3interview.MS3Interview.charToBytesASCII;

/**
 *
 * @author joshr
 */
public class MS3DB extends AbstractDB implements MyDBInfo {

    private static final MS3DB instance = new MS3DB();
    
    private MS3DB(){ 
    }
    
    public static MS3DB getInstance(){
        return instance;
    }
    
    static Connection connection;

    // Connection created when MS3DB class is loaded into memory
    static{
        // Connecting to SQLite
        String url = "jdbc:sqlite:C:\\sqlite\\ms3Interview.db";

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);

            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("Driver Name: " + meta.toString());
            }

            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException | ClassNotFoundException ex) {
            ex.getMessage();
        }

    }

    public void createNewTable() {

        String sql = "CREATE TABLE IF NOT EXISTS ValidPersonPayments(\n"
                + "FIRST_NAME VARCHAR(30),\n"
                + "LAST_NAME VARCHAR(30),\n"
                + "EMAIL VARCHAR(50),\n"
                + "SEX VARCHAR(1),\n"
                + "DATA_IMAGE BLOB,\n"
                + "PAYMENT_CARD VARCHAR(20),\n"
                + "PAYMENT_AMOUNT DECIMAL(3,2),\n"
                + "FLAG1 INTEGER,\n"
                + "FLAG2 INTEGER,\n"
                + "LOCATION VARCHAR(100)\n"
                + ");";
        try {
            Statement statement = connection.createStatement();

            //creating new ValidPersonPayment table
            statement.execute(sql);
        } catch (SQLException sqlEx) {
            System.out.println(sqlEx.getMessage());
        }
    }

    @Override
    public void insertValidRecords(String firstName, String lastName, String email, String sex, char[] dataImage, String paymentCard, String paymentAmount, boolean flag1, boolean flag2, String location) {
        String sql = "INSERT INTO ValidPersonPayments(first_Name, last_Name, email, sex, data_Image, payment_card, payment_Amount, flag1, flag2, location) VALUES(?,?,?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, sex);

            // stored dataImage in blob column
            pstmt.setBytes(5, charToBytesASCII(dataImage));

            pstmt.setString(6, paymentCard);
            pstmt.setString(7, paymentAmount);
            pstmt.setBoolean(8, flag1);
            pstmt.setBoolean(9, flag2);
            pstmt.setString(10, location);

            pstmt.execute();
        } catch (SQLException sqlEx) {
            System.out.println(sqlEx.getMessage());
        }
    }

}
