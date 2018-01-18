package com.calebmortensen.whatintheweather;

/**
 * Created by Admin on 12/17/2017.
 */

public class Future {
    String description;
    String dt;

    public Future() {
    }

    /*public Future(String description, String dt) {
        this.description = description;
        this.dt = dt;
    }*/
    // Weather Description Getter/Setter in SUB Array "Weather"
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Epoch Date/Time Getter/Setter in ROOT of "list" Array
    public String getDt(){
        return  dt;
    }

    public void setDt(String dt){
        this.dt = dt;
    }

}
