package com.example.organizze_clone.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizze_clone.MainActivity;
import com.example.organizze_clone.R;
import com.example.organizze_clone.adapter.TransactionsAdapter;
import com.example.organizze_clone.config.FirebaseConfig;
import com.example.organizze_clone.helper.Constants;
import com.example.organizze_clone.helper.CustomDate;
import com.example.organizze_clone.model.Transaction;
import com.example.organizze_clone.model.User;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private DatabaseReference db = FirebaseConfig.getFirebaseDatabase();

    private ValueEventListener userEventListener;
    private ValueEventListener transactionEventListener;

    // transaction list
    ArrayList<Transaction> transactionsList = new ArrayList<>();
    private String currentDate; // 072020 format to access database
    TransactionsAdapter transactionsAdapter;


    private TextView textHello, textBalance;
    private FloatingActionButton fabProfit, fabSpending;
    private FloatingActionMenu mainFab;

    private MaterialCalendarView calendarView;
    private RecyclerView recyclerTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0); // remove shadow above

        textHello = findViewById(R.id.textHello);
        textBalance = findViewById(R.id.textBalance);

        fabProfit = findViewById(R.id.fabProfit);
        fabSpending = findViewById(R.id.fabSpending);
        mainFab = findViewById(R.id.mainFab);

        setFABicons();

        calendarViewConfig();
        initTransactionMonthRecyclerView();
        swipe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menuLogOut:
                auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userInfoListener(); // to update everytime user open screen
        getMonthTransactions(currentDate); // update transactions list each  time month is changed
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.removeEventListener(userEventListener);
        db.removeEventListener(transactionEventListener);
    }

    public void userInfoListener() {
        userEventListener = db.child(Constants.UserNode.KEY)
                .child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        textHello.setText("Olá, " + user.getName());

                        String balance = String.format("%.2f", user.getTotalProfit() - user.getTotalSpending());
                        textBalance.setText("R$ " + balance);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    public void addNewTransaction(View view) {
        // constant keys to use on TransactionActivity to know it its a profit or a spending add
        String key              = Constants.KEY_SELECTED_FAB_ID;
        String keyProfitId      = Constants.KEY_PROFIT_FAB_ID;
        String keySpendingId    = Constants.KEY_SPENDING_FAB_ID;
        String keyCurrentMonth = Constants.KEY_CURRENT_MONTH;

        // using id of both FABs and repeated id from clicked FAB
        int currentId   = view.getId();
        int profitId    = fabProfit.getId();
        int spendingId  = fabSpending.getId();

        Intent intent = new Intent(this, TransactionActivity.class);
        intent.putExtra(key, currentId);
        intent.putExtra(keyProfitId, profitId);
        intent.putExtra(keySpendingId, spendingId);
        intent.putExtra(keyCurrentMonth, currentDate);
        startActivity(intent);
    }

    public void removeTransaction(final RecyclerView.ViewHolder viewHolder) {

        // config confirmationDialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir Transação da Conta");
        alertDialog.setMessage("Tem certeza que deseja excluir essa transação da sua conta?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int transactionPosition = viewHolder.getAdapterPosition();
                removeTransactionFromDatabase(transactionsList.get(transactionPosition));
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                transactionsAdapter.notifyDataSetChanged();
            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    private void removeTransactionFromDatabase(Transaction transaction) {
        db.child(Constants.TransactionNode.KEY)
                .child(auth.getUid())
                .child(CustomDate.getMonthDatabaseChildNode(transaction.getDate()))
                .child(transaction.getId())
                .removeValue();

        transactionsAdapter.notifyDataSetChanged();

        User.updateUserBalance(transaction, true);
    }


    public void initTransactionMonthRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerTransactions = findViewById(R.id.recyclerTransactions);
        recyclerTransactions.setLayoutManager(layoutManager);
        recyclerTransactions.setHasFixedSize(true);

        // hide FAB Menu (mainFab) when list is scrolled until the end
        recyclerTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy >  0) {
                    mainFab.hideMenu(true);
                }
                if(dy < 0) {
                    mainFab.showMenu(true);
                }
            }
        });

        // Set adapter
        transactionsAdapter = new TransactionsAdapter(getApplicationContext(), transactionsList);
        recyclerTransactions.setAdapter(transactionsAdapter);
    }

    /**
     *
     * @param date as 01/07/2020
     * @return list of transactions of this month
     */
    public ArrayList<Transaction> getMonthTransactions(String date) {

        transactionEventListener =  db.child(Constants.TransactionNode.KEY)
            .child(auth.getUid())
            .child(CustomDate.getMonthDatabaseChildNode(date)) //date as 072020 to use as database node
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        transactionsList.clear();
                        if(dataSnapshot.exists()) {
                            for(DataSnapshot data: dataSnapshot.getChildren()) {
                                Transaction transaction = data.getValue(Transaction.class).withId(data.getKey());
                                transactionsList.add(transaction);
                            }
                        }
                        transactionsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return null;
    }

    /************ view elements config ************/
    public void calendarViewConfig() {
        String[] months = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho",
                "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        String[] days = {"seg", "ter", "quar", "quin", "sex", "sab", "dom"};
        calendarView = findViewById(R.id.calendarView);
        calendarView.setTitleMonths(months);
        calendarView.setWeekDayLabels(days);
        currentDate = CustomDate.convertCalendarViewDate(calendarView.getCurrentDate().toString());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                // because lister is going to be recriated on getMonthTransactions
                // and we don't want to create mutiple
                db.removeEventListener(transactionEventListener);

                currentDate = CustomDate.convertCalendarViewDate(date.toString());
                getMonthTransactions(currentDate); // update transactions list each  time month is changed

            }
        });
    }

    /**
     * Used to set drawble compat icons since component doesn't accept android:src
     */
    public void setFABicons() {
        fabProfit.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add));
        fabSpending.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add));
    }

    /**
     * Create swipe funcion to delete transaction
     */
    public void swipe() {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE; // disable dragging
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END; // enable swipe from both sides
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeTransaction(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerTransactions);
    }

}