package ms3interview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author joshr
 */
// POJO class
public class PersonPayment {

    private String firstName; // Column A
    private String lastName;  // Column B
    private String email;     // Column C

    private String sex;             // Column D
    private char[] dataImage;       // Column E
    private String paymentCard;     // Column F
    private String paymentAmount;   // Column G
    private boolean flag1;          // Column H
    private boolean flag2;          // Column I
    private String location;        // Column J 

    public PersonPayment(String firstName, String lastName, String email, String sex, char[] dataImage, String paymentCard, String paymentAmount, boolean flag1, boolean flag2, String location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.sex = sex;
        this.dataImage = dataImage;
        this.paymentCard = paymentCard;
        this.paymentAmount = paymentAmount;
        this.flag1 = flag1;
        this.flag2 = flag2;
        this.location = location;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public char[] getDataImage() {
        return dataImage;
    }

    public void setDataImage(char[] dataImage) {
        this.dataImage = dataImage;
    }

    public String getPaymentCard() {
        return paymentCard;
    }

    public void setPaymentCard(String paymentCard) {
        this.paymentCard = paymentCard;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public boolean isFlag1() {
        return flag1;
    }

    public void setFlag1(boolean flag1) {
        this.flag1 = flag1;
    }

    public boolean isFlag2() {
        return flag2;
    }

    public void setFlag2(boolean flag2) {
        this.flag2 = flag2;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public List<String> getPersonPaymentAsList(){
        return new ArrayList<String>(Arrays.asList(this.getFirstName(), 
        this.getLastName(), this.getEmail(), this.getSex(), 
        String.copyValueOf(this.getDataImage()), this.getPaymentCard(),
        this.getPaymentAmount(), String.valueOf(this.isFlag1()), 
        String.valueOf(this.isFlag2()), this.getLocation()));
    }
}
