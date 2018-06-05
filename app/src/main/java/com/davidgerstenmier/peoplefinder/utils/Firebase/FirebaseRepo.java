package com.davidgerstenmier.peoplefinder.utils.Firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.davidgerstenmier.peoplefinder.MainActivity;
import com.davidgerstenmier.peoplefinder.Models.Hobby;
import com.davidgerstenmier.peoplefinder.Models.Person;
import com.davidgerstenmier.peoplefinder.utils.MainActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.UUID;

public class FirebaseRepo implements FirebaseRepoInterface {

    private final String TAG = "FirebaseRepo";

    private final String PEOPLE_OBJECTS = "people";
    private final String HOBBY_OBJECTS = "hobbies";
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference refHobbies;
    private MainActivityViewModel mainActivityViewModel;

    public FirebaseRepo(MainActivityViewModel mainActivityViewModel) {
        this.database = FirebaseDatabase.getInstance();
        this.mainActivityViewModel = mainActivityViewModel;
        this.ref = this.database.getReference().child(PEOPLE_OBJECTS);
        onFirebaseDataChange();
    }


    @Override
    public void addPerson(final Person person) {

        if(person.getFirebaseId().equalsIgnoreCase("")){
            person.setFirebaseId(UUID.randomUUID().toString());
        }
        this.ref.child(person.getFirebaseId()).setValue(person).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mainActivityViewModel.setPerson(person);
                mainActivityViewModel.postPerson();
            }
        });

    }

    @Override
    public void removePerson(Person person) {
        this.ref.child(person.getFirebaseId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


            }
        });
    }

    //People list for Application
    private void onFirebaseDataChange() {

        this.ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mainActivityViewModel.getPeopleList().clear();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {

                    Person person = shot.getValue(Person.class);
                    mainActivityViewModel.getPeopleList().add(person);
                }

                mainActivityViewModel.filterManager.checkFilteredList();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    public void savePersonHobbies(int pos){

        final Person person = mainActivityViewModel.getPerson();

        person.getHobbies().remove(pos);

        this.refHobbies = database.getReference().child(PEOPLE_OBJECTS).child(person.getFirebaseId()).child(HOBBY_OBJECTS);

        this.refHobbies.setValue(person.getHobbies()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mainActivityViewModel.setPerson(person);
            }
        });
    }
    public void savePersonHobbiesFromProfile(){

        final Person person = this.mainActivityViewModel.getPerson();
        this.refHobbies = database.getReference().child(PEOPLE_OBJECTS).child(person.getFirebaseId()).child(HOBBY_OBJECTS);

        this.refHobbies.setValue(person.getHobbies()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mainActivityViewModel.setPerson(person);
            }
        });
    }
}
