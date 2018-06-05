package com.davidgerstenmier.peoplefinder.utils.FilterPackage;

import com.davidgerstenmier.peoplefinder.Models.Person;

import java.util.ArrayList;

public interface FilterManagerInterface {

    ArrayList<Person>filterGender(String gender);
    ArrayList<Person>sortByAge();
    ArrayList<Person>sortByName();
    ArrayList<Person>filterByName();
    ArrayList<Person>clearFilters();

}
