package com.davidgerstenmier.peoplefinder.utils.FilterPackage;

import android.os.Build;

import com.davidgerstenmier.peoplefinder.Models.Person;
import com.davidgerstenmier.peoplefinder.utils.MainActivityViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterManager implements FilterManagerInterface {

    public boolean isDescending = false;
    public FilterBy filterBy = FilterBy.NONE;
    public OrderBy orderBy = OrderBy.ID;

    public boolean isDescending() {
        return this.isDescending;
    }


    private MainActivityViewModel mainActivityViewModel;

    public FilterManager(MainActivityViewModel mainActivityViewModel) {
        this.mainActivityViewModel = mainActivityViewModel;
    }

    @Override
    public ArrayList<Person> filterGender(String gender) {
        return null;
    }

    @Override
    public ArrayList<Person> sortByAge() {
        return null;
    }

    @Override
    public ArrayList<Person> sortByName() {
        return null;
    }

    @Override
    public ArrayList<Person> filterByName() {
        return null;
    }

    @Override
    public ArrayList<Person> clearFilters() {

        mainActivityViewModel.setPeopleFilteredList(mainActivityViewModel.getPeopleList());
        mainActivityViewModel.postPeople(null);
        return null;
    }

    public void checkFilteredList() {

        ArrayList<Person> people = mainActivityViewModel.getPeopleList();

        ArrayList<Person> filtered = new ArrayList<>();


        //ArrayList<Person> removed = new ArrayList<>();

        /*for (Person p:filtered) {
            boolean isChecked = true;
            for (Person check:peopleCheck) {

                if(p.getFirebaseId().equalsIgnoreCase(check.getFirebaseId())){
                    isChecked = false;
                }

            }
            if(isChecked){
                removed.add(p);
            }
        }*/
        //filtered.removeAll(removed);

        if (filterBy == FilterBy.NONE) {
            filtered = people;
        }else{
            for (Person person : people) {
                switch (filterBy) {
                    case MALE:
                        if(person.getGender().equalsIgnoreCase("male")) {
                            filtered.add(person);
                        }
                        break;
                    case FEMALE:
                        if(person.getGender().equalsIgnoreCase("female")) {
                            filtered.add(person);
                        }
                        break;
                }
            }
        }

        switch (orderBy) {
            case ID:
                Collections.sort(filtered, Person.personIdComparator);
                break;
            case AGE:
                Collections.sort(filtered, Person.personAgeComparator);
                break;
            case NAME:
                Collections.sort(filtered, Person.personNameComparator);
                break;

        }

        if (isDescending) {
            Collections.reverse(filtered);

        }


        mainActivityViewModel.setPeopleFilteredList(filtered);
        mainActivityViewModel.postPeople(null);

    }

    Predicate<Person> isMalePredicate = new Predicate<Person>() {
        @Override
        public boolean test(Person person) {
            return person.getGender().equalsIgnoreCase("Male");
        }
    };

    Predicate<Person> isFemalePredicate = new Predicate<Person>() {
        @Override
        public boolean test(Person person) {
            return person.getGender().equalsIgnoreCase("Female");
        }
    };

    public enum FilterBy {
        NONE,
        MALE,
        FEMALE
    }

    public enum OrderBy {
        ID,
        AGE,
        NAME
    }
}
