package com.example.vipayee;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.payeeName.setText("Payee: " + transaction.getPayeeName());
        holder.amount.setText("₹" + transaction.getAmount());
        holder.transactionDate.setText(transaction.getTransactionDate());  // Left side
        holder.transactionTime.setText(transaction.getTransactionTime());  // Right side

        if (transaction.getTransactionType().equalsIgnoreCase("Debited")) {
            holder.transactionType.setText("Debited");
            holder.transactionType.setTextColor(Color.RED);  // Red for Debit
        } else {
            holder.transactionType.setText("Credited");
            holder.transactionType.setTextColor(Color.GREEN);  // Green for Credit
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView payeeName, amount, transactionDate, transactionTime, transactionType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            payeeName = itemView.findViewById(R.id.payee_name);
            amount = itemView.findViewById(R.id.amount);
            transactionDate = itemView.findViewById(R.id.transaction_date);
            transactionTime = itemView.findViewById(R.id.transaction_time);
            transactionType = itemView.findViewById(R.id.transaction_type);
        }
    }
}
