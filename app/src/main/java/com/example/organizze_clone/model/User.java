package com.example.organizze_clone.model;

import com.example.organizze_clone.helper.Constants;
import com.example.organizze_clone.config.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.text.DecimalFormat;

public class User {

    private static String id;
    private static String name;
    private static String email;
    private static String password;

    private static Double totalProfit = 0.00;
    private static Double totalSpending = 0.00;

    public User() {}

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void saveOnDatabase() {
        DatabaseReference db = FirebaseConfig.getFirebaseDatabase();
        db.child(Constants.UserNode.KEY)
                .child(id)
                .setValue(this);
    }

    /**
     * @param transaction to update user database
     */

    public static void updateUserBalance(Transaction transaction, Boolean removeTransactionFromDatabase) {
        Double oldValue;
        Double updatedValue;
        String keyToUpdate;
        DecimalFormat twoDigitsForm = new DecimalFormat("0.00");

        if(transaction.getType().equals(Constants.TransactionNode.PROFIT)) {
            oldValue = totalProfit;
            keyToUpdate = Constants.UserNode.TOTAL_PROFIT;
        } else {
            oldValue = totalSpending;
            keyToUpdate = Constants.UserNode.TOTAL_SPENDING;
        }

        if(removeTransactionFromDatabase) {
            updatedValue = Double.valueOf(
                    twoDigitsForm.format(oldValue - transaction.getValue()) // format to round to two digits
            );
        } else { // if its not a remove value, we should updateUserBalance adding the value
            updatedValue = Double.valueOf(
                    twoDigitsForm.format(oldValue + transaction.getValue())
            );
        }

        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.UserNode.KEY)
                .child(FirebaseConfig.getFirebaseAuth().getUid())
                .child(keyToUpdate)
                .setValue(updatedValue);
    }

    /******** getters and setters *********/
    @Exclude
    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public Double getTotalProfit() { return totalProfit; }

    public Double getTotalSpending() { return totalSpending; }

    public void setId(String id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTotalProfit(Double totalProfit) { this.totalProfit = totalProfit; }

    public void setTotalSpending(Double totalSpending) { this.totalSpending = totalSpending; }
}
