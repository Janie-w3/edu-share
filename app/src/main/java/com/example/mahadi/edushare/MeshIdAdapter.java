/*
 *  ****************************************************************************
 *  * Created by : Roman on 11/16/2016 at 1:40 PM.
 *  * Email : roman@w3engineers.com
 *  * 
 *  * Last edited by : Roman on 11/16/2016.
 *  * 
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
 *  ****************************************************************************
 */
package com.example.mahadi.edushare;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MeshIdAdapter extends RecyclerView.Adapter<MeshIdAdapter.IdViewHolder> {

    private final List<String> meshIds;
    private final SparseBooleanArray selectedItems;
    private View.OnClickListener clickListener;

    public MeshIdAdapter(View.OnClickListener clickListener) {
        meshIds = new ArrayList<>();
        selectedItems = new SparseBooleanArray();
        this.clickListener = clickListener;
    }

    @Override
    public IdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meshid, parent, false);
        return new IdViewHolder(itemView, clickListener);
    }

    @Override
    public void onBindViewHolder(IdViewHolder holder, int position) {

        holder.viewId.setText(getItem(position));
        holder.itemView.setTag(position);

        if (isSelected(position)) {
            holder.viewId.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccent));
        } else {
            holder.viewId.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
        }
    }

    public String getItem(int position) {
        if (position < 0 || position >= meshIds.size()) return null;
        return meshIds.get(position);
    }

    public void clear() {
        meshIds.clear();
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void addItem(String meshId) {
        meshIds.add(0, meshId);
        notifyItemInserted(0);
    }


    public void targetSelection(String meshId) {
        targetSelection(meshIds.indexOf(meshId));
    }

    public void targetSelection(int position) {
        clearSelection();
        selectedItems.put(position, true);
        notifyItemChanged(position);
    }

    public String getSelectedItem() {
        if (selectedItems.size() > 0) {
            int position = selectedItems.keyAt(0);
            return getItem(position);
        }
        return null;
    }

    public boolean isSelected(int position) {
        return selectedItems.get(position, false);
    }

    public boolean clearSelection() {
        boolean cleared = selectedItems.size() > 0;
        for (int i = 0; i < selectedItems.size(); ) {
            int position = selectedItems.keyAt(i);
            selectedItems.delete(position);
            notifyItemChanged(position);
        }
        return cleared;
    }

    @Override
    public int getItemCount() {
        return meshIds.size();
    }

    static final class IdViewHolder extends RecyclerView.ViewHolder {

        final TextView viewId;

        IdViewHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);
            itemView.setOnClickListener(clickListener);
            viewId = (TextView) itemView.findViewById(R.id.viewId);
        }
    }
}
