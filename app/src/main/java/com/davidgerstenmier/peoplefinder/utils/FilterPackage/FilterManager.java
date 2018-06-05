package com.davidgerstenmier.peoplefinder.utils.FilterPackage;
import com.davidgerstenmier.peoplefinder.Models.Person;
import com.davidgerstenmier.peoplefinder.utils.MainActivityViewModel;

import java.util.ArrayList;
import java.util.Collections;


public class FilterManager implements FilterManagerInterface {

    public boolean isDescending = false;
    public FilterBy filterBy = FilterBy.NONE;
    public OrderBy orderBy = OrderBy.ID;

    private MainActivityViewModel mainActivityViewModel;

    public FilterManager(MainActivityViewModel mainActivityViewModel) {
        this.mainActivityViewModel = mainActivityViewModel;
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
