package com.davidgerstenmier.peoplefinder.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.davidgerstenmier.peoplefinder.HobbyHolder;
import com.davidgerstenmier.peoplefinder.MainActivity;
import com.davidgerstenmier.peoplefinder.Models.Person;
import com.davidgerstenmier.peoplefinder.ProfileFragment;
import com.davidgerstenmier.peoplefinder.R;
import com.davidgerstenmier.peoplefinder.utils.ImageUtils;

import java.awt.font.TextAttribute;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PeopleRecyclerAdapter extends RecyclerView.Adapter<PeopleRecyclerAdapter.Holder>{

    private Context context;
    public ArrayList<Person> people;


    public PeopleRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        if(people != null){
            return people.size();
        }
        return 0;
    }

    @Override
    public PeopleRecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_person,parent,false);

        return new Holder(view);
    }

    @Override
    public void onViewRecycled(@NonNull Holder holder) {
        super.onViewRecycled(holder);
        holder.ivImage.setImageDrawable(null);
        holder.tvName.setText("");

    }

    @Override
    public void onBindViewHolder(PeopleRecyclerAdapter.Holder holder, int position) {

        Person person = people.get(position);
        if(!person.getImageUri().equalsIgnoreCase("")) {
            holder.ivImage.setVisibility(View.VISIBLE);
            Bitmap bitmap = ImageUtils.base64ToBitmap(people.get(position).getImageUri());
            Glide.with(context).load(bitmap).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.ivImage);
        }else{
            holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_portrait_black_24dp));
        }

        holder.tvName.setText(person.getName());

        String idVal = "#" + String.valueOf(person.getId()) + ": ";
        holder.tvId.setText(idVal);

        if(person.getGender() != null){
            if(person.getGender().equalsIgnoreCase(context.getResources().getString(R.string.male))){
                holder.cardViewBackground.setBackgroundColor(ContextCompat.getColor(context,R.color.colorBoy));
            }else{
                holder.cardViewBackground.setBackgroundColor(ContextCompat.getColor(context,R.color.colorGirl));
            }
        }

    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.item_person_cardview)
        public CardView cardView;
        @BindView(R.id.item_person_cardview_image)
        public AppCompatImageView ivImage;
        @BindView(R.id.item_person_cardview_name)
        public TextView tvName;
        @BindView(R.id.item_cardview_holder)
        public LinearLayout cardViewHolder;
        @BindView(R.id.item_cardview_background)
        public RelativeLayout cardViewBackground;
        @BindView(R.id.item_person_cardview_id)
        public TextView tvId;


        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            //Launch profile view fragment
            ((MainActivity)context).bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            ((MainActivity)context).getMainActivityViewModel().setPerson(people.get(getAdapterPosition()));
            ((MainActivity)context).switchFragment(new ProfileFragment());
        }
    }

    public void updateAdapter(ArrayList<Person> people){

        this.people = people;
        notifyDataSetChanged();

    }
    public void removeItem(int position) {
        this.people.remove(position);

        notifyItemRemoved(position);
    }

    public void restoreItem(Person person, int position) {
        people.add(position, person);

        notifyItemInserted(position);
    }

}
