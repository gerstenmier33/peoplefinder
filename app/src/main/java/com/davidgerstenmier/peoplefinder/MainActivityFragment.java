package com.davidgerstenmier.peoplefinder;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidgerstenmier.peoplefinder.Adapters.PeopleRecyclerAdapter;
import com.davidgerstenmier.peoplefinder.Adapters.RecyclerTouchHelper;
import com.davidgerstenmier.peoplefinder.Models.Person;
import com.davidgerstenmier.peoplefinder.utils.MainActivityViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements RecyclerTouchHelper.RecyclerItemTouchHelperListener {

    private Context context;
    private MainActivityViewModel viewModel;
    private Observer observer;
    private PeopleRecyclerAdapter peopleRecyclerAdapter;

    @BindView(R.id.fragmentmain_recyclerview)
    RecyclerView recyclerView;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        this.viewModel = ((MainActivity) context).getMainActivityViewModel();
        initRecycler();
        initObserver();
        this.viewModel.getPeople().observe((LifecycleOwner) context, this.observer);
    }

    @Override
    public void onDestroyView() {

        if (this.observer != null) {
            this.viewModel.getPeople().removeObserver(this.observer);
        }
        super.onDestroyView();
    }

    private void initRecycler() {


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(new DividerItemDecoration(context, 0));
        peopleRecyclerAdapter = new PeopleRecyclerAdapter(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(peopleRecyclerAdapter);
        peopleRecyclerAdapter.updateAdapter(viewModel.filterManager.clearFilters());

        ItemTouchHelper.SimpleCallback itemTouchHelper = new RecyclerTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);


    }

    private void initObserver() {

        this.observer = new Observer<ArrayList<Person>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Person> people) {

                peopleRecyclerAdapter.updateAdapter(people);

            }
        };
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        String name = viewModel.getPeopleFilteredList().get(viewHolder.getAdapterPosition()).getName();


        // backup of removed item for undo purpose
        final Person deletedPerson = viewModel.getPeopleFilteredList().get(viewHolder.getAdapterPosition());
        final int deletedIndex = viewHolder.getAdapterPosition();

        // remove the item from recycler view
        peopleRecyclerAdapter.removeItem(viewHolder.getAdapterPosition());

        this.viewModel.firebaseRepo.removePerson(deletedPerson);
        // showing snack bar with Undo option
        Snackbar snackbar = Snackbar
                .make(viewHolder.itemView, name + " removed from cart!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item

                viewModel.firebaseRepo.addPerson(deletedPerson);
                peopleRecyclerAdapter.restoreItem(deletedPerson, deletedIndex);

            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();

    }
}
