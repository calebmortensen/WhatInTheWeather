package com.calebmortensen.whatintheweather;

/**
 * Created by Admin on 12/17/2017.
 */

public class List {
    String description;

    public List() {
    }

    public List(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "List{" +
                "description='" + description + '\'' +
                '}';
    }
}
