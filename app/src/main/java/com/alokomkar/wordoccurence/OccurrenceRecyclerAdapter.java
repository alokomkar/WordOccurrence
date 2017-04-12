package com.alokomkar.wordoccurence;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Alok on 12/04/17.
 */
public class OccurrenceRecyclerAdapter extends RecyclerView.Adapter<OccurrenceRecyclerAdapter.ViewHolder> {

    private Map<String, WordModel> wordCountMap;
    private String header = "";
    private ArrayList<WordModel> wordModelArrayList;

    public OccurrenceRecyclerAdapter(Map<String, WordModel> wordCountMap) {
        this.wordModelArrayList = new ArrayList<>();
        this.wordCountMap = wordCountMap;
        wordModelArrayList.addAll(wordCountMap.values());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_count, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        WordModel wordModel = wordModelArrayList.get(position);
        if( header.equals("") ) {
            holder.headerTextView.setText("0 - 10");
            header = holder.headerTextView.getText().toString();
        }
        else {
            holder.headerTextView.setVisibility(header.equals(holder.headerTextView.getText().toString()) ? View.GONE : View.VISIBLE);
        }
        holder.contentTextView.setText(wordModel.toString());

    }

    @Override
    public int getItemCount() {
        return wordCountMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView headerTextView;
        TextView contentTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            headerTextView = (TextView) itemView.findViewById(R.id.headerTextView);
            contentTextView = (TextView) itemView.findViewById(R.id.contentTextView);
        }
    }
}
