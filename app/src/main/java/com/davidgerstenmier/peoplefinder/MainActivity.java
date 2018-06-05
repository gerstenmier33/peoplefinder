package com.davidgerstenmier.peoplefinder;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import com.davidgerstenmier.peoplefinder.Dialogs.CreateProfileDialog;

import com.davidgerstenmier.peoplefinder.utils.FilterPackage.FilterManager;
import com.davidgerstenmier.peoplefinder.utils.ImageUtils;
import com.davidgerstenmier.peoplefinder.utils.MainActivityViewModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private MainActivityViewModel mainActivityViewModel;
    private Context context;
    private FragmentTransaction trans;
    private FragmentManager fragmentManager;
    public FloatingActionButton fab;
    public Toolbar toolbar;
    private MenuItem editItem;
    private MenuItem filterItem;
    private MenuItem deleteItem;

    @BindView(R.id.filter_sheet_btn_clear_filters)
    AppCompatImageButton btnClearFilters;
    @BindView(R.id.filter_sheet_men)
    AppCompatImageButton btnMenFilter;
    @BindView(R.id.filter_sheet_women)
    AppCompatImageButton btnWomenFilter;
    @BindView(R.id.filter_sheet_order)
    AppCompatImageButton btnOrderBy;
    @BindView(R.id.filter_sheet_age_sort)
    AppCompatImageButton btnAgeSort;
    @BindView(R.id.filter_sheet_name_sort)
    AppCompatImageButton btnNameSort;



    public static final int ACTION_IMAGE_CAPTURE = 7007;
    public static final int ACTION_GALLERY = 4004;

    public LinearLayout mediaSnackBar;

    @BindView(R.id.filter_sheet)
    LinearLayout FilterSheet;

    public BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        ButterKnife.bind(this);

        invalidateOptionsMenu();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomSheetBehavior = BottomSheetBehavior.from(FilterSheet);
        //initFilterSheet(bottomSheetBehavior);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivityViewModel.createNewPerson();
                CreateProfileDialog dialog = CreateProfileDialog.createDialog(true);
                dialog.show(getFragmentManager(), null);

            }
        });
        requestAllPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        mainActivityViewModel.setmMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        setMenuItems(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_filter) {

            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            return true;
        }
        if (id == R.id.action_edit) {

            CreateProfileDialog dialog = CreateProfileDialog.createDialog(true);
            dialog.show(getFragmentManager(), null);

            return true;
        }
        if(id == R.id.action_delete){

            createAlertDialog(getResources().getString(R.string.delete_alert));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public MainActivityViewModel getMainActivityViewModel() {
        return mainActivityViewModel;
    }

    public void switchFragment(Fragment frag) {

        fragmentManager = getSupportFragmentManager();
        if (Build.VERSION.SDK_INT >= 23) {
            trans = fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_left).replace(R.id.main_framelayout, frag);
            trans.addToBackStack("tag");
        } else {

            trans = fragmentManager.beginTransaction().add(R.id.main_framelayout, frag); //.commit();
        }
        trans.commit();

        fragmentManager.executePendingTransactions();

    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().findFragmentByTag("tag") != null) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        if (hasOpenedDialogs(this)) {
            return;
        }

        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTION_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {

                    Bitmap bitmap = ImageUtils.getCaptureImage(imageFilePath);

                    if (bitmap != null) {
                        String base64 = ImageUtils.bitmapToBase64(bitmap);
                        mainActivityViewModel.getPerson().setImageUri(base64);
                        mainActivityViewModel.postPerson();
                    }

                }
                break;
            case ACTION_GALLERY:
                if (resultCode == Activity.RESULT_OK) {

                    if (data != null && data.getData() != null) {

                        String uri = ImageUtils.fileFromUri(this, data.getData());
                        Bitmap bitmap = ImageUtils.getCaptureImage(uri);

                        if (bitmap != null) {
                            String base64 = ImageUtils.bitmapToBase64(bitmap);
                            mainActivityViewModel.getPerson().setImageUri(base64);
                            mainActivityViewModel.postPerson();
                        }

                    }

                }
                break;
            default:
                break;
        }

    }

    @OnClick(R.id.filter_sheet_btn_clear_filters)
    public void clearAllFilters(){
        this.mainActivityViewModel.filterManager.filterBy = FilterManager.FilterBy.NONE;
        this.mainActivityViewModel.filterManager.orderBy = FilterManager.OrderBy.ID;
        this.mainActivityViewModel.filterManager.isDescending = false;
        this.mainActivityViewModel.filterManager.checkFilteredList();
    }

    @OnClick(R.id.filter_sheet_men)
    public void filterByMen(){
        this.mainActivityViewModel.filterManager.filterBy = FilterManager.FilterBy.MALE;
        this.mainActivityViewModel.filterManager.checkFilteredList();
    }
    @OnClick(R.id.filter_sheet_women)
    public void filterByWomen(){
        this.mainActivityViewModel.filterManager.filterBy = FilterManager.FilterBy.FEMALE;
        this.mainActivityViewModel.filterManager.checkFilteredList();
    }
    @OnClick(R.id.filter_sheet_age_sort)
    public void orderByAge(){
        this.mainActivityViewModel.filterManager.orderBy = FilterManager.OrderBy.AGE;
        this.mainActivityViewModel.filterManager.checkFilteredList();
    }
    @OnClick(R.id.filter_sheet_name_sort)
    public void orderByName(){
        this.mainActivityViewModel.filterManager.orderBy = FilterManager.OrderBy.NAME;
        this.mainActivityViewModel.filterManager.checkFilteredList();
    }
    @OnClick(R.id.filter_sheet_order)
    public void orderByAscendingOrDescending(){

        boolean cur = this.mainActivityViewModel.filterManager.isDescending;
        this.mainActivityViewModel.filterManager.isDescending = !cur;

        this.mainActivityViewModel.filterManager.checkFilteredList();
    }




    public void startCameraIntent() {

        if (checkExternalStoragePermissions()) {
            Intent pictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = ImageUtils.createImageFile(context);
                    imageFilePath = photoFile.getAbsolutePath();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.davidgerstenmier.peoplefinder.provider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(pictureIntent, ACTION_IMAGE_CAPTURE);
                }
            }
        }
    }

    public void startGalleryIntent() {

        if (checkExternalStoragePermissions()) {
            Intent intent = new Intent();

            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTION_GALLERY);
        }

    }

    public boolean hasOpenedDialogs(Activity activity) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof DialogFragment) {
                    ((DialogFragment) fragment).dismiss();
                    return true;
                }
            }
        }

        return false;
    }



    public void setMenuItems(boolean isProfileView){

        Menu menu = this.mainActivityViewModel.getmMenu();
        if(deleteItem == null){

            this.editItem = (MenuItem)menu.findItem(R.id.action_edit);
            this.filterItem = (MenuItem)menu.findItem(R.id.action_filter);
            this.deleteItem = (MenuItem)menu.findItem(R.id.action_delete);
            //return;
        }
        if(isProfileView){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(this.mainActivityViewModel.getPerson().getName());

            this.deleteItem.setVisible(true);
            this.editItem.setVisible(true);
            this.filterItem.setVisible(false);

        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            this.deleteItem.setVisible(false);
            this.editItem.setVisible(false);
            this.filterItem.setVisible(true);

        }

    }

    public void createAlertDialog(String message) {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(getResources().getString(R.string.alert))
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivityViewModel.firebaseRepo.removePerson(mainActivityViewModel.getPerson());
                        onBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
