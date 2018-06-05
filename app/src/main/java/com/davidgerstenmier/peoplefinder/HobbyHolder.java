package com.davidgerstenmier.peoplefinder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HobbyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;

    @BindView(R.id.item_hobby_name_textview)
    public TextView name;
    @BindView(R.id.item_hobby_delete_imageview)
    public ImageView delete;
    @BindView(R.id.linearlayoutholder)
    public LinearLayout main;



    public HobbyHolder(View itemView, Context context) {
        super(itemView);

        this.context = context;
        ButterKnife.bind(this,itemView);
        main.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d("HOBBYHOLDER","FIRED ONCLICK");
        if(delete.getVisibility() == View.VISIBLE){
            Log.d("HOBBYHOLDER","FIRED ONCLICK");
            ((MainActivity)context).getMainActivityViewModel().deleteHobbyFromPerson(getAdapterPosition());
        }else{
            ((MainActivity)context).getMainActivityViewModel().firebaseRepo.savePersonHobbies(getAdapterPosition());
        }

    }
}
