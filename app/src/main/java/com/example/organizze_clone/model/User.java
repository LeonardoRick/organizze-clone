package com.example.organizze_clone.model;

import android.content.Context;
import android.widget.Toast;

import com.example.organizze_clone.helper.Constants;
import com.example.organizze_clone.config.FirebaseConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import androidx.annotation.NonNull;

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

    //TODO: lembrar que quando você adiciona ou remove, o processo deveria ser diferente. Ex: quando remove um spending, ta aumentando a divida do cara
    public static void updateUserBalance(Transaction transaction) {
        Double oldValue;
        Double updatedValue;
        String keyToUpdate;
        if(transaction.getType() == Constants.UserNode.TOTAL_PROFIT) {
            oldValue = totalProfit;
            keyToUpdate = Constants.UserNode.TOTAL_PROFIT;
        } else {
            oldValue = totalSpending;
            keyToUpdate = Constants.UserNode.TOTAL_SPENDING;
        }

        updatedValue = oldValue + transaction.getValue();
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.UserNode.KEY)
                .child(FirebaseConfig.getFirebaseAuth().getUid())
                .child(keyToUpdate)
                .setValue(updatedValue)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, "A coisa deu errado :( \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
