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
    private String prevHeader = "";

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
        int wordCount = wordModel.getWordCount();
        if( wordCount <= 10 ) {
            holder.headerTextView.setText("0 - 10");
            header = "0 - 10";
        }
        else if( wordCount > 10 && wordCount <= 20 ) {
            holder.headerTextView.setText("10 - 20");
            header = "10 - 20";
        }
        else if( wordCount > 20 ) {
            holder.headerTextView.setText("20 and above");
            header = "20 and above";
        }
        if( prevHeader.equals("") ) {
            prevHeader = header;
            holder.headerTextView.setVisibility(View.VISIBLE);
        }
        else if( prevHeader.equals(header) ) {
            holder.headerTextView.setVisibility(View.GONE);
        }
        else if( !prevHeader.equals(header) ) {
            prevHeader = header;
            holder.headerTextView.setVisibility(View.VISIBLE);
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
