package com.chizoba.journal.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chizoba.journal.R;
import com.chizoba.journal.database.NoteEntry;

import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.HomeViewHolder> {

    private List<NoteEntry> noteEntries;
    private ItemClickListener itemClickListener;

    public List<NoteEntry> getNotes() {
        return noteEntries;
    }

    public interface ItemClickListener {
        void onItemClick(int itemId);
    }

    public HomeRecyclerViewAdapter(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, parent, false);
        return new HomeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        NoteEntry noteEntry = noteEntries.get(position);
        holder.titleView.setText(noteEntry.getTitle());
        holder.detailView.setText(noteEntry.getBody());
    }

    @Override
    public int getItemCount() {
        return noteEntries == null ? 0 : noteEntries.size();
    }

    public void setData(List<NoteEntry> noteEntries) {
        this.noteEntries = noteEntries;
        notifyDataSetChanged();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleView, detailView;

        HomeViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.itemTitle);
            detailView = itemView.findViewById(R.id.itemDetail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemId = noteEntries.get(getAdapterPosition()).getId();
            itemClickListener.onItemClick(itemId);
        }
    }
}
