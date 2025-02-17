package com.example.vipayee;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<TransactionModel> transactions;

    public TransactionAdapter(List<TransactionModel> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionModel transaction = transactions.get(position);
        holder.amountTextView.setText("₹ " + transaction.getAmount());

        // Extract formatted date and time from transactionDate String
        String formattedDate = formatDate(transaction.getTransactionDate());
        String formattedTime = formatTime(transaction.getTransactionDate());

        holder.dateTextView.setText(formattedDate);
        holder.timeTextView.setText(formattedTime);

        if ("Credit".equalsIgnoreCase(transaction.getTransactionType())) {
            holder.transactionTypeTextView.setText("Received from: " + transaction.getPayeeName());
            holder.transactionTypeTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else {
            holder.transactionTypeTextView.setText("Sent to: " + transaction.getPayeeName());
            holder.transactionTypeTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }
    }

    // Function to extract and format Date (yyyy-MM-dd)
    private String formatDate(String transactionDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(transactionDate);
            return (date != null) ? outputFormat.format(date) : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Function to extract and format Time (HH:mm)
    private String formatTime(String transactionDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(transactionDate);
            return (date != null) ? outputFormat.format(date) : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionTypeTextView, amountTextView, dateTextView, timeTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionTypeTextView = itemView.findViewById(R.id.transactionType);
            amountTextView = itemView.findViewById(R.id.amount);
            dateTextView = itemView.findViewById(R.id.date);
            timeTextView = itemView.findViewById(R.id.time);
        }
    }

    // Function to format Date (yyyy-MM-dd)
    private String formatDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }

    // Function to format Time (HH:mm)
    private String formatTime(long timestamp) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormat.format(new Date(timestamp));
    }
}
