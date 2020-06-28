package com.example.organizze_clone.model;

import com.example.organizze_clone.config.FirebaseConfig;
import com.example.organizze_clone.helper.Constants;
import com.example.organizze_clone.helper.CustomDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import androidx.annotation.NonNull;

public class Transaction {

    private String id;
    private Double value;
    private String date;
    private String category;
    private String desc;
    private String type;

    public Transaction() {}

    public boolean saveOnDatabase() {
        String monthDatabaseChildNode = CustomDate.getMonthDatabaseChildNode(date);

        DatabaseReference db = FirebaseConfig.getFirebaseDatabase();
        db.child(Constants.TransactionNode.KEY)
            .child(FirebaseConfig.getFirebaseAuth().getUid())
            .child(monthDatabaseChildNode)
            .push() /* create auto id on firebase*/
            .setValue(this)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            });
        return false;
    }


    /**
     *
     * @return Transaction Object with Firebase id that is father key
     */
    public Transaction withId(String id) {
        this.id = id;
        return this;
    }



    /******** getters and setters *********/

    @Exclude
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Double getValue() { return value; }

    public void setValue(Double value) { this.value = value; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getDesc() { return desc; }

    public void setDesc(String desc) { this.desc = desc; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
