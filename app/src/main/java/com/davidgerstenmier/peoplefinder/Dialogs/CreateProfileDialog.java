package com.davidgerstenmier.peoplefinder.Dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.davidgerstenmier.peoplefinder.Adapters.CreateProfileAdapter;
import com.davidgerstenmier.peoplefinder.MainActivity;
import com.davidgerstenmier.peoplefinder.Models.Person;
import com.davidgerstenmier.peoplefinder.R;
import com.davidgerstenmier.peoplefinder.utils.ImageUtils;
import com.davidgerstenmier.peoplefinder.utils.MainActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class CreateProfileDialog extends DialogFragment {

    private final static String TAG = "CreateProfileDialog";
    private MainActivityViewModel viewModel;
    private Observer personObserver;
    private CreateProfileAdapter hobbyAdapter;
    private boolean isEditing = false;

    private Unbinder unbinder;

    private View view;

    //Views
    @BindView(R.id.dialog_createperson_recycler)
    RecyclerView rvHobbies;
    @BindView(R.id.dialogue_button_createhobby)
    AppCompatImageButton createHobby;
    @BindView(R.id.button)
    Button createButton;
    @BindView(R.id.imageView)
    ImageView profileImage;
    @BindView(R.id.dialogue_create_edittext_name)
    TextInputEditText etName;
    @BindView(R.id.dialogue_create_edittext_age)
    TextInputEditText etAge;
    @BindView(R.id.dialogue_create_edittext_hobby)
    TextInputEditText etHobby;
    @BindView(R.id.dialog_createperson_radiogroup)
    RadioGroup rgGender;
    @BindView(R.id.cancel)
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ((MainActivity)getActivity()).getMainActivityViewModel();

        isEditing = getArguments().getBoolean("edit");

        setCancelable(false);
        setRetainInstance(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.dialogue_create_person, container, false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getLivePerson().observe((LifecycleOwner) getActivity(), getPersonObserver());
        updateGender(this.viewModel);
        initPerson(viewModel.getPerson());
        if(isEditing){

            createButton.setText(getActivity().getResources().getString(R.string.save));

        }
    }

    @Override
    public void dismiss() {

        if (this.personObserver != null) {
            this.viewModel.getLivePerson().removeObserver(this.personObserver);
        }
        unbinder.unbind();
        super.dismiss();
    }

    @OnClick(R.id.button)
    public void createPerson() {
        if(this.viewModel.validateNewPerson((Activity)getActivity())) {
            this.viewModel.firebaseRepo.addPerson(viewModel.getPerson());
            this.dismiss();
        }
    }

    @OnClick(R.id.cancel)
    public void cancelDialog(){
        this.dismiss();
    }

    @OnTextChanged(R.id.dialogue_create_edittext_age)
    public void updateAge() {
        if(!etAge.getText().toString().trim().equalsIgnoreCase("")) {
            this.viewModel.getPerson().setAge(Integer.parseInt(etAge.getText().toString().trim()));
        }
    }

    @OnTextChanged(R.id.dialogue_create_edittext_name)
    public void updateName() {
        this.viewModel.getPerson().setName(etName.getText().toString().trim());
    }


    @OnClick(R.id.dialogue_button_createhobby)
    public void createHobby() {

        Boolean hobbyCreated = this.viewModel.createHobbyForPerson(etHobby.getText().toString().trim());
        if (hobbyCreated) {
            etHobby.setText("");
        } else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.hobby_exists), Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.imageView)
    public void chooseMedia() {
        chooseMediaMethod();
    }

    private void initPerson(Person person){

        if (etAge != null && person.getAge() != 0) {
            etAge.setText(String.valueOf(person.getAge()));
        }

        if (etName != null) {
            etName.setText(person.getName());
        }

        if(!person.getImageUri().equalsIgnoreCase("") && profileImage != null){
            Bitmap bitmap = ImageUtils.base64ToBitmap(person.getImageUri());
            Glide.with(getActivity()).load(bitmap).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(profileImage);
        }

        if (hobbyAdapter == null && rvHobbies != null) {

            ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(5);
            hobbyAdapter = new CreateProfileAdapter(getActivity());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvHobbies.setLayoutManager(linearLayoutManager);
            rvHobbies.addItemDecoration(itemOffsetDecoration);
            rvHobbies.setAdapter(hobbyAdapter);
            hobbyAdapter.updateAdapter(person.getHobbies());

        } else if(hobbyAdapter != null) {
            hobbyAdapter.updateAdapter(person.getHobbies());
        }

        if(person.getGender() != null && !person.getGender().equalsIgnoreCase("")){
            if(person.getGender().equalsIgnoreCase(getActivity().getResources().getString(R.string.male))) {
                rgGender.check(R.id.dialog_createperson_radiobutton_male);
            }else{
                rgGender.check(R.id.dialog_createperson_radiobutton_female);
            }
        }
    }

    private Observer getPersonObserver() {

        this.personObserver = new Observer<Person>() {
            @Override
            public void onChanged(@Nullable Person person) {

                initPerson(person);

            }
        };
        return this.personObserver;
    }

    private void updateGender(final MainActivityViewModel viewModel) {

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rgGender.getCheckedRadioButtonId() == R.id.dialog_createperson_radiobutton_male) {
                    viewModel.getPerson().setGender(getActivity().getResources().getString(R.string.male));
                } else {
                    viewModel.getPerson().setGender(getActivity().getResources().getString(R.string.female));
                }
            }
        });

    }

    private void chooseMediaMethod(){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(getActivity().getResources().getString(R.string.media_choice));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getActivity().getResources().getString(R.string.camera),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity)getActivity()).startCameraIntent();
                    }
                });

        builder1.setNegativeButton(
                getActivity().getResources().getString(R.string.gallery),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        ((MainActivity)getActivity()).startGalleryIntent();

                    }
                });
        builder1.setNeutralButton(getActivity().getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){

                        dialog.cancel();

                    }
                });

        AlertDialog alert = builder1.create();
        alert.show();

    }

    //constructor for determining edit or new
    public static CreateProfileDialog createDialog(boolean isEdit){

        CreateProfileDialog createProfileDialog = new CreateProfileDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("edit",isEdit);
        createProfileDialog.setArguments(bundle);

        return createProfileDialog;

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
