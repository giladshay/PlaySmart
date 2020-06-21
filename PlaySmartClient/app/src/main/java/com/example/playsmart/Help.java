/*
  Help class file
  Created by: Gil-Ad Shay
  Last edited: 02.05.2020
  Version: 'with documentation'
 */

package com.example.playsmart;

class Help {
    // Help class

    // properties
    private Instrument[] instrumentArray;

    Help() { // create new Help
    }

    void createInstrumentArray(String[] resources) {
        // create instrument array using resources
        this.instrumentArray = new Instrument[resources.length];
        for (int i = 0; i < this.instrumentArray.length; i ++) {
            this.instrumentArray[i] = new Instrument(resources[i]);
        }
    }
    Instrument[] getInstrumentArray() {
        // return instrument array
        return this.instrumentArray;
    }

}
