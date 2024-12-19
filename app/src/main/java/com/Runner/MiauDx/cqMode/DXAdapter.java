package com.Runner.MiauDx.cqMode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.Runner.MiauDx.R;

import java.util.List;

public class DXAdapter extends RecyclerView.Adapter<DXViewHolder> {
    private final List<Dx> dxList;

    public DXAdapter(List<Dx> dxList) {
        this.dxList = dxList;
    }

    @Override
    public DXViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dx, parent, false);
        return new DXViewHolder(view);
    }


    @Override
    public void onBindViewHolder(DXViewHolder holder, int position) {
        Dx dx = dxList.get(position);
        holder.bind(dx);
    }

    @Override
    public int getItemCount() {
        return dxList.size();
    }
}
