package com.example.hw82;

public class Weather {
    final String location;
    final double tempKelvin;

    public Weather (final String location,
                    final double tempKelvin) {
        this.location = location;
        this.tempKelvin = tempKelvin;
    }

    public int getFahrenheit(){
        return (int) (tempKelvin * 9/5 - 459.67);
    }

}
