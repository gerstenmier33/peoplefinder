package com.davidgerstenmier.peoplefinder.Models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Comparator;

public class Person implements Parcelable{

    private int id = 0;
    private String gender = "";
    private String name = "";
    private int age = 0;
    private String imageUri = "";
    private ArrayList<Hobby> hobbies = new ArrayList<>();
    private String firebaseId = "";


    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public ArrayList<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(ArrayList<Hobby> hobbies) {
        this.hobbies = hobbies;
    }

    public Person() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.gender);
        dest.writeString(this.name);
        dest.writeInt(this.age);
        dest.writeString(this.imageUri);
        dest.writeTypedList(this.hobbies);
        dest.writeString(this.firebaseId);
    }

    protected Person(Parcel in) {
        this.id = in.readInt();
        this.gender = in.readString();
        this.name = in.readString();
        this.age = in.readInt();
        this.imageUri = in.readString();
        this.hobbies = in.createTypedArrayList(Hobby.CREATOR);
        this.firebaseId = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public static Comparator<Person> personNameComparator = new Comparator<Person>() {
        public int compare(Person s1, Person s2) {
            String StudentName1 = s1.getName().toUpperCase();
            String StudentName2 = s2.getName().toUpperCase();
            return StudentName1.compareTo(StudentName2);
        }
    };

    /*Comparator for sorting the list by Student Name*/
    public static Comparator<Person> personAgeComparator = new Comparator<Person>() {
        public int compare(Person s1, Person s2) {
            return Integer.compare(s1.getAge(), s2.getAge());
        }
    };

    public static Comparator<Person> personIdComparator = new Comparator<Person>() {
        public int compare(Person s1, Person s2) {
            return Integer.compare(s1.getId(), s2.getId());
        }
    };

}
