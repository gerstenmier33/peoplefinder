package com.davidgerstenmier.peoplefinder.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidgerstenmier.peoplefinder.HobbyHolder;
import com.davidgerstenmier.peoplefinder.Models.Hobby;
import com.davidgerstenmier.peoplefinder.R;

import java.util.List;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<HobbyHolder> {

    private List<Hobby> hobbies;
    private Context context;

    public ProfileRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public HobbyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_hobby,parent,false);

        return new HobbyHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull HobbyHolder holder, int position) {

        holder.name.setText(hobbies.get(position).getName());

        holder.delete.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {

        if(hobbies != null){
            return hobbies.size();
        }else {
            return 0;
        }
    }

    public void updateAdapter(List<Hobby> hobbies){

        this.hobbies = hobbies;
        notifyDataSetChanged();

    }
}
