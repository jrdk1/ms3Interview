/*
Assumptions:
Column C is the Unique Constraint
All columns are NOT-NULL
Composite Primary Key for columns A,B,C for guaranteed uniqueness

Assumption: The max number of columns that can be allowed will count up to M(14)
 */
package ms3interview;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author joshr
 */
public class MS3Interview {

    public static List<PersonPayment> personRecordsReceived; // contains all records without checks for integrity
    public static List<PersonPayment> personRecordsSucceeded; // records containing exactly 10 non-null, non-empty records
    public static List<PersonPayment> personRecordsFailed; // record with missing or extraneous data
    public static List<String> temp;
    public static List<String> headers;
    public static boolean isColumnGDouble = false;

    private static Connection connection = null;

    public static void main(String[] args) {
        // TODO code application logic here
        MS3Interview.readData();
        MS3Interview.writeStatisticsFile();
        MS3Interview.writeInvalidRecordsToCSV();
        MS3Interview.writeValidRecordsToDB();

    }

    private static void writeStatisticsFile() {
        try {

            File statsLogFile;
            FileWriter statisticsWriter;
            statsLogFile = new File("src/ms3Interview/ms3Interview.log");
            statisticsWriter = new FileWriter(statsLogFile, true);
            LocalTime localTime = LocalTime.now();

            statisticsWriter.append("--as of: " + Integer.toString(localTime.getHour()) + ":" + Integer.toString(localTime.getMinute()) + ":" + Integer.toString(localTime.getSecond()) + " ---\n");
            statisticsWriter.append("# of records received: " + MS3Interview.personRecordsReceived.size() + "\n");
            statisticsWriter.append("# of records successful: " + MS3Interview.personRecordsSucceeded.size() + "\n");
            statisticsWriter.append("# of records failed: " + MS3Interview.personRecordsFailed.size() + "\n");

            statisticsWriter.flush();
            statisticsWriter.close();

        } catch (IOException | NullPointerException | SecurityException ex) {
            ex.printStackTrace();
        }

        System.out.println("All files accounted for: " + (MS3Interview.personRecordsReceived.size() == (MS3Interview.personRecordsSucceeded.size() + MS3Interview.personRecordsFailed.size())));
    }

    private static void writeInvalidRecordsToCSV() {

        File badRecordsCSVFile = new File("src/ms3Interview/ms3Interview-bad.csv");

        try (PrintWriter pw = new PrintWriter(badRecordsCSVFile)) {
            personRecordsFailed.stream()
                    .map(obj -> MS3Interview.convertToCSV(obj))
                    .forEach(pw::println);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static List<PersonPayment> readData() {
        personRecordsReceived = new ArrayList<>();
        personRecordsSucceeded = new ArrayList<>();
        personRecordsFailed = new ArrayList<>();
        headers = new ArrayList<>();
        temp = new ArrayList<>();

        try {
            InputStream is = new FileInputStream(new File("src/ms3Interview/ms3Interview.csv"));
            BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));

            // gathering the headers for later use. A-J
            csvReader.lines().limit(1).collect(Collectors.toList()).forEach(headerCell -> temp.add(headerCell)); // all headers in A,B,C,D... format at index 0
            String s = temp.get(0).substring(0, temp.get(0).length());
            String[] noComma = s.split(",");
            headers = new ArrayList<>(Arrays.asList(noComma));

            // focusing on just data
            personRecordsReceived = csvReader.lines().skip(1).map(mapper).collect(Collectors.toList());
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return personRecordsReceived;
    }

    private static boolean determineRecordValiditiy(ArrayList<String> pList) {

        boolean basicConditions = false;

        basicConditions = (pList.stream().anyMatch(Objects::nonNull) // if any attribute(non-null) returns false
                && pList.stream().allMatch(x -> x.length() > 0)
                && pList.get(6).startsWith("$")
                && (pList.get(3).equals("Male") || pList.get(3).equals("Female")));

        try {
            if (basicConditions) {
                // turn String to double and parse that
                Double parsedAmount = Double.parseDouble(pList.get(6).replaceAll("[^\\d.]+", ""));
                pList.set(6, String.valueOf(parsedAmount));
                isColumnGDouble = true;
            }

        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            basicConditions = false;
        }

        return basicConditions && isColumnGDouble;
    }

    private static Function<String, PersonPayment> mapper = (line) -> {
        PersonPayment personPayment;
        ArrayList<String> pList = new ArrayList<>();
        String[] p;
        boolean badData = true;

        /* splits string on , followed by an even number of double quotes.
        In other words, it splits on comma outside the double quotes. 
        This will work provided you have balanced quotes in your string
         */
        p = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        pList.addAll(Arrays.asList(p)); // ArrayLists more flexible than arrays

        // check record integrity
        if (determineRecordValiditiy(pList)) {
            badData = false;
        }

        if (!badData) {
            personPayment = new ValidPersonPayment(pList.get(0), pList.get(1), pList.get(2), pList.get(3), pList.get(4).toCharArray(), pList.get(5), pList.get(6), Boolean.valueOf(pList.get(7)), Boolean.valueOf(pList.get(8)), pList.get(9));
            personRecordsSucceeded.add(personPayment);
        } else {
            // future processing for bad data
            pList.replaceAll(x -> (x.isEmpty() || x.equals(null)) ? "" : x);

            // for rows with fewer than 10 rows, the 11 rows comes when we reformat 
            // column E because of the resulting String split on the comma
            while (pList.size() < 10) {
                pList.add((pList.size()), "");
            }

            // correct the monetary amount for nulls
            if (pList.get(6) == null || pList.get(6).equals("")) {
                pList.set(6, "$0.00");
            }

            personPayment = new InValidPersonPayment(pList.get(0), pList.get(1), pList.get(2), pList.get(3), pList.get(4).toCharArray(), pList.get(5), pList.get(6), Boolean.valueOf(pList.get(7)), Boolean.valueOf(pList.get(8)), pList.get(9));
            personRecordsFailed.add(personPayment);
        }

        personRecordsReceived.add(personPayment);
        return personPayment;
    };

    private static Connection connect() {
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

        return connection;
    }

    public static void createNewTable() {

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

    public static void insertValidRecords(String firstName, String lastName, String email, String sex, char[] dataImage, String paymentCard, String paymentAmount, boolean flag1, boolean flag2, String location) {
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

    public static void writeValidRecordsToDB() {
        MS3Interview.connect();
        MS3Interview.createNewTable();

        personRecordsSucceeded.stream().forEach(personPayment
                -> MS3Interview.insertValidRecords(personPayment.getFirstName(), personPayment.getLastName(),
                        personPayment.getEmail(), personPayment.getSex(), personPayment.getDataImage(),
                        personPayment.getPaymentCard(), personPayment.getPaymentAmount(),
                        personPayment.isFlag1(), personPayment.isFlag2(), personPayment.getLocation()));
    }

    // borrowed from Baeldung
    // method for formatting a single line of data
    public static String convertToCSV(PersonPayment data) {
        List<String> convertedDataToList = data.getPersonPaymentAsList();
//        return convertedDataToList.stream().map(stringElement -> MS3Interview.escapeSpecialCharacters(stringElement))
//                .collect(Collectors.joining(","));
        return convertedDataToList.stream().collect(Collectors.joining(","));

    }

    public static byte[] charToBytesASCII(char[] image) {
        byte[] b = new byte[image.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) image[i];
        }
        return b;
    }
}
