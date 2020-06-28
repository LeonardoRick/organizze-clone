package com.example.organizze_clone.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.organizze_clone.R;
import com.example.organizze_clone.helper.Constants;
import com.example.organizze_clone.model.Transaction;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder> {
    private ArrayList<Transaction> transactionsList;
    private Context context;

    public TransactionsAdapter(Context context, ArrayList<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
        this.context = context;
    }

    /**
     * To create first views
     */
    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transactions_list, parent, false);
        return new TransactionsViewHolder(listItem);
    }

    /**
     * To recycle views and show info
     */
    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {
        // get info from transactions list on specified position
        Transaction transaction = transactionsList.get(position);

        holder.textValue.setText(String.format("%.2f", transaction.getValue()));
        holder.textCategory.setText(transaction.getCategory());
        holder.textDesc.setText(transaction.getDesc());

        if(transaction.getType().equals(Constants.TransactionNode.PROFIT)) {
            holder.textValue.setTextColor(context.getResources().getColor(R.color.colorAccentProfit));
        } else {
            holder.textValue.setTextColor(context.getResources().getColor(R.color.colorAccentSpending));
        }
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    public class TransactionsViewHolder extends RecyclerView.ViewHolder {

        TextView textValue, textCategory, textDesc;
        public TransactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            textValue = itemView.findViewById(R.id.textListValue);
            textCategory = itemView.findViewById(R.id.textListCategory);
            textDesc = itemView.findViewById(R.id.textListDesc);
        }
    }
}
