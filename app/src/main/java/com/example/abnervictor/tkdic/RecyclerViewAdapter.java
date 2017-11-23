package com.example.abnervictor.tkdic;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class RecyclerViewAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mcontext;
    protected int mlayoutId;
    protected List<T> mdatas;
    private OnItemClickListener monitemclicklistener = null;

    public RecyclerViewAdapter(Context context,int layoutId,List<T> datas){
        mcontext = context;
        mlayoutId = layoutId;
        mdatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent,int viewType){
        ViewHolder viewHolder = ViewHolder.get(mcontext,parent,mlayoutId);
        return viewHolder;
    }


    public interface OnItemClickListener{
        void onClick(int position);
        void onLongClick(int positon);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.monitemclicklistener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,int position){
        convert(holder,mdatas.get(position));

        if (monitemclicklistener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    monitemclicklistener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    monitemclicklistener.onLongClick(holder.getAdapterPosition());
                    return false; //?????
                }
            });
        }
    }

    public abstract void convert(ViewHolder holder,T t);

    @Override
    public int getItemCount() {
        return mdatas.size();
    }

    public void removeItem(int position) {
        mdatas.remove(position);
        notifyItemRemoved(position);
    }

    public void refresh(List<T> datas) {
        mdatas = datas;
        notifyDataSetChanged();
    }


}
