/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ms3interview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author joshr
 */
public class InValidPersonPayment extends PersonPayment {


    public InValidPersonPayment(String firstName, String lastName, String email, String sex, char[] dataImage, String paymentCard, String paymentAmount, boolean flag1, boolean flag2, String location) {
        super(firstName, lastName, email, sex, dataImage, paymentCard, paymentAmount, flag1, flag2, location);
    }

    @Override
    public List<String> getPersonPaymentAsList(){
        return new ArrayList<String>(Arrays.asList(this.getFirstName(), 
        this.getLastName(), this.getEmail(), this.getSex(), 
        String.copyValueOf(this.getDataImage()), this.getPaymentCard(),
        this.getPaymentAmount(), String.valueOf(this.isFlag1()), 
        String.valueOf(this.isFlag2()), this.getLocation()));
    }

}
