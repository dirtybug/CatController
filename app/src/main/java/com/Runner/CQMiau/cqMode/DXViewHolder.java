package com.Runner.CQMiau.cqMode;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.Runner.CQMiau.R;

public class DXViewHolder extends RecyclerView.ViewHolder {
    private final TextView callSignView;

    private Dx dx;

    public DXViewHolder(View itemView) {
        super(itemView);
        callSignView = itemView.findViewById(R.id.text_callsign);

        itemView.setOnClickListener(v -> {
            CQModeActivity.getInstance().populateCallSign(dx.getCallSign());

        });
    }

    public void bind(Dx dx) {
        this.dx = dx;
        callSignView.setText(dx.getFlag() + dx.getCallSign() + ":" + dx.getComment());


    }
}