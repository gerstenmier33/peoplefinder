package com.davidgerstenmier.peoplefinder.utils.Firebase;

import com.davidgerstenmier.peoplefinder.Models.Person;

import java.util.ArrayList;

public interface FirebaseRepoInterface {

    void addPerson(Person person);
    void removePerson(Person person);

}
