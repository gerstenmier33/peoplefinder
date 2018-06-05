package com.davidgerstenmier.peoplefinder.utils;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.widget.Toast;
import com.davidgerstenmier.peoplefinder.Models.Hobby;
import com.davidgerstenmier.peoplefinder.Models.Person;
import com.davidgerstenmier.peoplefinder.R;
import com.davidgerstenmier.peoplefinder.utils.FilterPackage.FilterManager;
import com.davidgerstenmier.peoplefinder.utils.Firebase.FirebaseRepo;

import java.util.ArrayList;

public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = MainActivityViewModel.class.getName();
    private MutableLiveData<ArrayList<Person>> people = new MutableLiveData<>();
    private MutableLiveData<Person> livePerson = new MutableLiveData<>();
    private ArrayList<Person> peopleList = new ArrayList<>();
    private ArrayList<Person> peopleFilteredList = new ArrayList<>();
    public FirebaseRepo firebaseRepo;
    public FilterManager filterManager;
    private Person person;
    private Menu mMenu;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.filterManager = new FilterManager(this);
        this.firebaseRepo = new FirebaseRepo(this);

    }

    //methods for creating and updating person object
    public void createNewPerson(){
        Person person = new Person();
        person.setId(peopleList.size()+1);
        this.person = person;
        postPerson();
    }

    //setting LiveData
    public void postPerson(){

        this.livePerson.postValue(this.person);

    }

    public void postPeople(@Nullable ArrayList<Person> people){

        // Call filtering/sorting from here

        if(people != null){
            this.people.postValue(people);
        }else {
            this.people.postValue(this.peopleFilteredList);
        }

    }

    //getters and setters
    public void setPerson(Person person) {

        this.person = person;
        postPerson();

    }

    public Menu getmMenu() {
        return mMenu;
    }

    public void setmMenu(Menu mMenu) {
        this.mMenu = mMenu;
    }

    public MutableLiveData<ArrayList<Person>> getPeople() {
        return people;
    }

    public ArrayList<Person> getPeopleList() {
        return peopleList;
    }

    public ArrayList<Person> getPeopleFilteredList() {
        return peopleFilteredList;
    }

    public void setPeopleFilteredList(ArrayList<Person> peopleFilteredList) {
        this.peopleFilteredList = peopleFilteredList;
    }

    public MutableLiveData<Person> getLivePerson() {
        return livePerson;
    }

    public Person getPerson(){
        return this.person;
    }

    public boolean createHobbyForPerson(String name){
        if(this.person.getHobbies() != null) {
            for (Hobby h : this.person.getHobbies()) {
                if(h.getName().equalsIgnoreCase(name)){
                    return false;
                }
            }
        }else{
            this.person.setHobbies(new ArrayList<Hobby>());
        }
        Hobby hobby = new Hobby();
        hobby.setName(name);
        this.person.getHobbies().add(hobby);
        this.livePerson.postValue(this.person);
        return true;
    }

    public void deleteHobbyFromPerson(int pos){

        //TODO: needs to be deleting from Firebase and then updating here
        this.person.getHobbies().remove(pos);

        livePerson.postValue(this.person);

    }
    public boolean validateNewPerson(Activity activity){

        if(this.person.getName().equalsIgnoreCase("")){

            Toast.makeText(activity,activity.getResources().getString(R.string.name_validation), Toast.LENGTH_LONG).show();
            return false;
        }
        if(this.person.getGender().equalsIgnoreCase("")){
            Toast.makeText(activity,activity.getResources().getString(R.string.gender_validation), Toast.LENGTH_LONG).show();
            return false;
        }
        if(this.person.getAge() == 0){
            Toast.makeText(activity,activity.getResources().getString(R.string.age_validation), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }

}
