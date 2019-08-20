package ms3interview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author joshr
 */
public class MS3Reader implements MyReader {

    private MS3Validator ms3Validator;
    
    public MS3Reader(){
        ms3Validator = new MS3Validator();
    }
    
    private Function<String, PersonPayment> mapper = (line) -> {
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
        if (ms3Validator.determineRecordValiditiy(pList)) {
            badData = false;
        }

        if (!badData) {
            personPayment = new PersonPayment(pList.get(0), pList.get(1), pList.get(2), pList.get(3), pList.get(4).toCharArray(), pList.get(5), pList.get(6), Boolean.valueOf(pList.get(7)), Boolean.valueOf(pList.get(8)), pList.get(9));
            MS3Interview.getInstance().getPersonRecordsSucceeded().add(personPayment);
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

            personPayment = new PersonPayment(pList.get(0), pList.get(1), pList.get(2), pList.get(3), pList.get(4).toCharArray(), pList.get(5), pList.get(6), Boolean.valueOf(pList.get(7)), Boolean.valueOf(pList.get(8)), pList.get(9));
            MS3Interview.getInstance().getPersonRecordsFailed().add(personPayment);
        }

        MS3Interview.getInstance().getPersonRecordsReceived().add(personPayment);
        return personPayment;
    };

    public  List<PersonPayment> readData() {

        try {
            InputStream is = new FileInputStream(new File("src/ms3Interview/ms3Interview.csv"));
            BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));

            // gathering the headers for later use. A-J
            csvReader.lines().limit(1).collect(Collectors.toList()).forEach(headerCell -> MS3Interview.getInstance().getTemp().add(headerCell)); // all headers in A,B,C,D... format at index 0
            String s = MS3Interview.getInstance().getTemp().get(0).substring(0, MS3Interview.getInstance().getTemp().get(0).length());
            String[] noComma = s.split(",");
            MS3Interview.getInstance().setHeaders(new ArrayList<>(Arrays.asList(noComma)));

            // focusing on just data
            MS3Interview.getInstance().setPersonRecordsReceived(csvReader.lines().skip(1).map(mapper).collect(Collectors.toList()));
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return MS3Interview.getInstance().getPersonRecordsReceived();
    }
}
