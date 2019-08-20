/*
Assumptions:
Column C is the Unique Constraint
All columns are NOT-NULL
Composite Primary Key for columns A,B,C for guaranteed uniqueness

Assumption: The max number of columns that can be allowed will count up to M(14)
 */
package ms3interview;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 *
 * @author joshr
 */
public final class MS3Interview {

    private static final MS3Interview instance = new MS3Interview();
    private List<PersonPayment> personRecordsReceived; // contains all records without checks for integrity
    private List<PersonPayment> personRecordsSucceeded; // records containing exactly 10 non-null, non-empty records
    private List<PersonPayment> personRecordsFailed; // record with missing or extraneous data
    private List<String> temp;
    private List<String> headers;

    // OLD ABOVE
    // NEW BELOW
    private static MS3DB ms3DB;
    private MS3Reader ms3Reader;
    private MS3Writer ms3Writer;

    private MS3Interview() {
        personRecordsReceived = new ArrayList<>();
        personRecordsSucceeded = new ArrayList<>();
        personRecordsFailed = new ArrayList<>();
        temp = new ArrayList<>();
        headers = new ArrayList<>();
        ms3Reader = new MS3Reader();
        ms3Writer = new MS3Writer();
    }
    
    public static MS3Interview getInstance(){
        return instance;
    }

    public void runApplication() {
        ms3Reader.readData();
        ms3Writer.writeStatisticsFile();
        ms3Writer.writeInvalidRecordsToCSV();
        ms3DB = MS3DB.getInstance();
        ms3DB.createNewTable();
        ms3Writer.writeValidRecordsToDB();
    }

    // borrowed from Baeldung
    // method for formatting a single line of data
    public static String convertToCSV(PersonPayment data) {
        List<String> convertedDataToList = data.getPersonPaymentAsList();
        return convertedDataToList.stream().collect(Collectors.joining(","));

    }

    public static byte[] charToBytesASCII(char[] image) {
        byte[] b = new byte[image.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) image[i];
        }
        return b;
    }

    public List<PersonPayment> getPersonRecordsReceived() {
        return personRecordsReceived;
    }

    public void setPersonRecordsReceived(List<PersonPayment> personRecordsReceived) {
        this.personRecordsReceived = personRecordsReceived;
    }

    public List<PersonPayment> getPersonRecordsSucceeded() {
        return personRecordsSucceeded;
    }

    public void setPersonRecordsSucceeded(List<PersonPayment> personRecordsSucceeded) {
        this.personRecordsSucceeded = personRecordsSucceeded;
    }

    public List<PersonPayment> getPersonRecordsFailed() {
        return personRecordsFailed;
    }

    public void setPersonRecordsFailed(List<PersonPayment> personRecordsFailed) {
        this.personRecordsFailed = personRecordsFailed;
    }

    public List<String> getTemp() {
        return temp;
    }

    public void setTemp(List<String> temp) {
        this.temp = temp;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public MS3DB getMs3DB() {
        return ms3DB;
    }

    public void setMs3DB(MS3DB ms3DB) {
        this.ms3DB = ms3DB;
    }

    public MS3Reader getMs3Reader() {
        return ms3Reader;
    }

    public void setMs3Reader(MS3Reader ms3Reader) {
        this.ms3Reader = ms3Reader;
    }

    public MS3Writer getMs3Writer() {
        return ms3Writer;
    }

    public void setMs3Writer(MS3Writer ms3Writer) {
        this.ms3Writer = ms3Writer;
    }
    
    
}
