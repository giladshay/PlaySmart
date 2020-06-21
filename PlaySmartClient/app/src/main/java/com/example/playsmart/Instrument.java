/*
  Instrument class file
  Created by: Gil-Ad Shay
  Last edited: 02.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart;

class Instrument {
    // Instrument class

    // properties
    private String name;
    private int number;

    Instrument(String name) { // create new instrument by given name and set number to 0
        this.name = name;
        this.number = 0;
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    int getNumber() {
        return number;
    }

    void setNumber(int number) {
        this.number = number;
    }

    void increase() {
        // increase the number of players
        this.number ++;
    }

    void decrease(int min) {
        // decrease the number of players
        if (this.number > min) // if number is bigger than minimum number df players
            this.number --;
    }
}
