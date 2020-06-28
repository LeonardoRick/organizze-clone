package com.example.organizze_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze_clone.R;
import com.example.organizze_clone.config.FirebaseConfig;
import com.example.organizze_clone.helper.Constants;
import com.example.organizze_clone.helper.CustomDate;
import com.example.organizze_clone.helper.ShowLongToast;
import com.example.organizze_clone.model.Transaction;
import com.example.organizze_clone.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class TransactionActivity extends AppCompatActivity implements ShowLongToast {
    // flag to control if transaction is profit or spending
    private boolean isProfitTransaction = false;

    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private DatabaseReference db = FirebaseConfig.getFirebaseDatabase();

    private EditText textInputValue;
    private TextInputEditText textInputDate, textInputCategory, textInputDesc;
    private FloatingActionButton fabSave;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineViewTheme(); // defining theme before setting contentView
        setContentView(R.layout.activity_transaction);

        // define view elements
        textInputValue = findViewById(R.id.textinputValue);
        textInputDate = findViewById(R.id.textInputDate);
        textInputCategory = findViewById(R.id.textInputCategory);
        textInputDesc = findViewById(R.id.textInputDesc);

        // config data field with acctual date
        configDateLabel();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // recover user info
        recoverUserInfo();
    }

    public void saveTransaction(View view) {
        String stringValue = textInputValue.getText().toString();
        String date = textInputDate.getText().toString();
        String category = textInputCategory.getText().toString();
        String desc = textInputDesc.getText().toString();

        String type = getTextType();

        if(validateRequiredInputFields(stringValue, date, category)) {
            if(CustomDate.validateDate(date)) {
                Double value = Double.parseDouble(stringValue);

                Transaction transaction = new Transaction();
                transaction.setValue(value);
                transaction.setDate(date);
                transaction.setCategory(category);
                transaction.setDesc(desc);
                transaction.setType(type); // "profit" or "spending"
                transaction.saveOnDatabase();

                user.updateUserBalance(transaction); //update user profit or spending values

                finish();
            } else {
                showLongToast("Insira a data no formato: " + CustomDate.pattern);
            }
        }
    }

    private Boolean validateRequiredInputFields(String value, String date, String category) {
        if(value.isEmpty()) {
            showLongToast("Valor não preenchido");
            return false;
        } else if (date.isEmpty()) {
            showLongToast("Data não preenchida");
            return false;
        } else if (category.isEmpty()) {
            showLongToast("categoria não preenchida");
            return false;
        }
        return true;
    }


    public void recoverUserInfo() {
        db.child(Constants.UserNode.KEY)
            .child(auth.getUid())
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


    /**
     * define theme based on with FAB user clicked on HomeActivity
     */
    public void defineViewTheme() {
        // getting keys to access data sent from HomeActivity
        String key              = Constants.KEY_SELECTED_FAB_ID;
        String keyProfitId      = Constants.KEY_PROFIT_FAB_ID;
        String keySpendingId    = Constants.KEY_SPENDING_FAB_ID;

        // setting ids to define theme corectly if its a profit or a spending add of money
        int currentId   = getIntent().getExtras().getInt(key);
        int profitId    = getIntent().getExtras().getInt(keyProfitId);
        int spendingId  = getIntent().getExtras().getInt(keySpendingId);

        if (currentId == profitId) {
            setTheme(R.style.ProfitTheme);
            isProfitTransaction = true;
        } else if (currentId == spendingId) {
            setTheme(R.style.SpendingTheme);
        } else {
            setTheme(R.style.AddMoneyValueTheme);
        }
    }

    /**
     * @return "profit" or "spending" based on isProfitTransaction
     */
    private String getTextType() {
        if(isProfitTransaction) return Constants.TransactionNode.PROFIT;
        return Constants.TransactionNode.SPENDING;
    }

    /**
     * if its current month, label will show today, like 26/07/2020
     * if its other month, lavel will show first day of that month 01/06/2019
     */
    private void configDateLabel() {
        String today = CustomDate.getCurrentDate();
        String selectedDate = getIntent().getExtras().getString(Constants.KEY_CURRENT_MONTH);
        if(CustomDate.verifyEqualMonth(selectedDate, today)) {
            Log.d("ENTREI", "configDateLabel: ");
            textInputDate.setText(today);
        } else {
            textInputDate.setText(selectedDate);
        }
    }

    @Override
    public void showLongToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}