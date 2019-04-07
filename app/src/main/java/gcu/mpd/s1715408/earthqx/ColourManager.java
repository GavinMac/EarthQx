////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.graphics.Color;

/**
 * Colour managment
 * this class will return values such as string colour codes or certain coloured resources to set the colours based on input values
 */
public class ColourManager {

    private Earthquake earthquake;
    private double magnitude;

    public ColourManager(double magnitude) {
        this.magnitude = magnitude;
    }

    /**
     * Returns a colour code as string based on the double magnitude value passed in by the constructor
     * @return String colourString
     */
    public String GetMagColour(){

        String colourString;

        if(magnitude >=3){
            colourString = "#FFFF0000";
        }
        else if(magnitude >=2){
            colourString = "#FFFF9800";
        }
        else if (magnitude >=1){
            colourString = "#FFFFEB3B";
        }
        else{
            colourString = "#909090";
        }

        return colourString;
    }

    /**
     * Returns an int which represents the resource from R.drawable/.. based on the magnitude double passed in by constructor
     * @return int index
     */
    public int GetMarkerResourceIndex(){

        int index;

        if(magnitude >=3){
            index = R.drawable.marker_circle_red;
        }
        else if(magnitude >=2 && magnitude <3){
            index = R.drawable.marker_circle_orange;
        }
        else if (magnitude >=1 && magnitude <2){
            index = R.drawable.marker_circle_yellow;
        }
        else{
            index = R.drawable.marker_circle_white;
        }

        return index;

    }

}
