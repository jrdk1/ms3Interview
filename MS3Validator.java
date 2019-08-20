/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ms3interview;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author joshr
 */
public class MS3Validator implements MyValidator {
    
    private boolean isColumnGDouble = false;
    
    public boolean determineRecordValiditiy(ArrayList<String> pList) {

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
}
