/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ms3interview;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;

/**
 *
 * @author joshr
 */
public class MS3Writer implements MyWriter {

    File badRecordsCSVFile;
    File statsLogFile;
    FileWriter statisticsWriter;

    public void writeInvalidRecordsToCSV() {

        badRecordsCSVFile = new File("src/ms3Interview/ms3Interview-bad.csv");

        try (PrintWriter pw = new PrintWriter(badRecordsCSVFile)) {
            MS3Interview.getInstance().getPersonRecordsFailed().stream()
                    .map(obj -> MS3Interview.convertToCSV(obj))
                    .forEach(pw::println);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void writeStatisticsFile() {
        try {

            statsLogFile = new File("src/ms3Interview/ms3Interview.log");
            statisticsWriter = new FileWriter(statsLogFile, true);
            LocalTime localTime = LocalTime.now();

            statisticsWriter.append("--as of: " + Integer.toString(localTime.getHour()) + ":" + Integer.toString(localTime.getMinute()) + ":" + Integer.toString(localTime.getSecond()) + " ---\n");
            statisticsWriter.append("# of records received: " + MS3Interview.getInstance().getPersonRecordsReceived().size() + "\n");
            statisticsWriter.append("# of records successful: " + MS3Interview.getInstance().getPersonRecordsSucceeded().size() + "\n");
            statisticsWriter.append("# of records failed: " + MS3Interview.getInstance().getPersonRecordsFailed().size() + "\n");

            statisticsWriter.flush();
            statisticsWriter.close();

        } catch (IOException | NullPointerException | SecurityException ex) {
            ex.printStackTrace();
        }

        System.out.println("All files accounted for: " + (MS3Interview.getInstance().getPersonRecordsReceived().size() == (MS3Interview.getInstance().getPersonRecordsSucceeded().size() + MS3Interview.getInstance().getPersonRecordsFailed().size())));
    }

    public void writeValidRecordsToDB() {

        MS3Interview.getInstance().getPersonRecordsSucceeded().stream().forEach(personPayment
                -> insertValidRecords(personPayment.getFirstName(), personPayment.getLastName(),
                        personPayment.getEmail(), personPayment.getSex(), personPayment.getDataImage(),
                        personPayment.getPaymentCard(), personPayment.getPaymentAmount(),
                        personPayment.isFlag1(), personPayment.isFlag2(), personPayment.getLocation()));
    }

    public void insertValidRecords(String firstName, String lastName, String email, String sex, char[] dataImage, String paymentCard, String paymentAmount, boolean flag1, boolean flag2, String location) {
        String sql = "INSERT INTO ValidPersonPayments(first_Name, last_Name, email, sex, data_Image, payment_card, payment_Amount, flag1, flag2, location) VALUES(?,?,?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement pstmt = MS3DB.connection.prepareStatement(sql);

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, sex);

            // stored dataImage in blob column
            pstmt.setBytes(5, MS3Interview.getInstance().charToBytesASCII(dataImage));

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
