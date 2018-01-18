package com.calebmortensen.whatintheweather;

/**
 * Created by Admin on 12/17/2017.
 */

public class WeatherInfo {
    public final Coord coord;
    public final Weather[] weather;
    public final Main main;
    public final Wind wind;
    public final String name;
    public final String dt;
    public final Sys sys;



    public WeatherInfo(Coord coord, Weather[] weather, Main main, Wind wind, String name, String dt, Sys sys) {
        this.coord = coord;
        this.weather = weather;
        this.main = main;
        this.wind = wind;
        this.name = name;
        this.dt = dt;
        this.sys = sys;
    }


    public class Coord {

        public final double lat;
        public final double lon;
        Coord(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    public class Weather {
        public final String description;
        Weather(String description){
            this.description = description;
        }
    }


    public class Main{
        public final double temp;
        public final double humidity;
        Main(double temp, double humidity) {
            this.temp = temp;
            this.humidity = humidity;
        }
    }

    public class Wind{
        public final double speed;
        Wind(double speed) {
            this.speed = speed;
        }
    }

    public class Sys {
        public final String country;
        Sys(String country) {
            this.country = country;
        }
    }
}

