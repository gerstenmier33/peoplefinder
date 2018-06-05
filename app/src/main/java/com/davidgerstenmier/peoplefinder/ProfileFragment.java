package com.davidgerstenmier.peoplefinder;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.davidgerstenmier.peoplefinder.Adapters.ProfileRecyclerAdapter;
import com.davidgerstenmier.peoplefinder.Models.Person;
import com.davidgerstenmier.peoplefinder.utils.ImageUtils;
import com.davidgerstenmier.peoplefinder.utils.MainActivityViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProfileFragment extends Fragment {

    private MainActivityViewModel viewModel;
    private Observer personObserver;
    private ProfileRecyclerAdapter profileRecyclerAdapter;
    private Person person;
    private View view;
    private Unbinder unbinder;

    @BindView(R.id.dialogue_recyclerview_profile)
    RecyclerView recyclerView;
    @BindView(R.id.dialogue_textview_age)
    TextView tvAge;
    @BindView(R.id.dialogue_textview_gender)
    TextView tvGender;
    @BindView(R.id.dialogue_textview_name)
    TextView tvName;
    @BindView(R.id.dialogue_imageview_image)
    ImageView ivProfileImage;
    @BindView(R.id.dialogue_relativelayout_main)
    RelativeLayout main;
    @BindView(R.id.dialogue_edittext_addhobby)
    EditText etCreateHobby;
    @BindView(R.id.dialogue_button_createhobby)
    AppCompatImageButton btn_createHobby;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.viewModel = ((MainActivity) getActivity()).getMainActivityViewModel();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        setRetainInstance(true);
        ((MainActivity) getActivity()).fab.setVisibility(View.GONE);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_profile_view, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setting up the toolbar for profile view
        this.viewModel.getLivePerson().observe((LifecycleOwner) getActivity(), getPersonObserver());
        initPerson();

    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity()).setMenuItems(true);
    }

    @Override
    public void onDestroyView() {
        //setting up the view for main fragment
        ((MainActivity) getActivity()).fab.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).setMenuItems(false);
        this.viewModel.getLivePerson().removeObserver(this.personObserver);

        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.dialogue_button_createhobby)
    public void createHobby(){

        Boolean hobbyCreated = this.viewModel.createHobbyForPerson(etCreateHobby.getText().toString().trim());
        if (hobbyCreated) {
            etCreateHobby.setText("");
            this.viewModel.firebaseRepo.savePersonHobbiesFromProfile();
        } else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.hobby_exists), Toast.LENGTH_SHORT).show();
        }

    }

    private void initPerson() {
        this.person = this.viewModel.getPerson();
        if (tvAge != null) {
            tvAge.setText(String.valueOf(person.getAge()));
        }
        if (tvName != null) {
            tvName.setText(person.getName());
        }

        if (!person.getImageUri().equalsIgnoreCase("") && ivProfileImage != null) {
            Bitmap bitmap = ImageUtils.base64ToBitmap(this.person.getImageUri());
            Glide.with(getActivity()).load(bitmap).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(ivProfileImage);
        }

        if (profileRecyclerAdapter == null && recyclerView != null) {

            ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(5);
            profileRecyclerAdapter = new ProfileRecyclerAdapter(getActivity());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.addItemDecoration(itemOffsetDecoration);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(profileRecyclerAdapter);
            profileRecyclerAdapter.updateAdapter(person.getHobbies());

        } else if (profileRecyclerAdapter != null) {
            profileRecyclerAdapter.updateAdapter(person.getHobbies());
        }

        if (person.getGender() != null) {
            if (person.getGender().equalsIgnoreCase(getActivity().getResources().getString(R.string.male))) {
                main.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBoy));
            } else {
                main.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGirl));
            }

            tvGender.setText(person.getGender());
        }

        profileRecyclerAdapter.updateAdapter(person.getHobbies());

    }

    private Observer getPersonObserver() {

        this.personObserver = new Observer<Person>() {
            @Override
            public void onChanged(@Nullable Person person) {

                initPerson();

            }
        };
        return this.personObserver;
    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }

    }

}
