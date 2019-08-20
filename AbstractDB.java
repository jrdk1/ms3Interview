/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ms3interview;

/**
 *
 * @author joshr
 */
public abstract class AbstractDB {
    
    abstract public void insertValidRecords(String firstName, String lastName, String email, String sex, char[] dataImage, String paymentCard, String paymentAmount, boolean flag1, boolean flag2, String location);
    
}
