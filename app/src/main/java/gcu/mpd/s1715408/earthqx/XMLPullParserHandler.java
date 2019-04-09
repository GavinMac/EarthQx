////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Parsers the XML file given and returns it as a list
 */
public class XMLPullParserHandler {

    private List<Earthquake> earthquakeList;
    private Earthquake currentEarthquake;
    private String text;
    private boolean hitItems = false;

    public XMLPullParserHandler(){
        earthquakeList = new ArrayList<Earthquake>();
    }

    public List<Earthquake> getEarthquakeList(){
        return earthquakeList;
    }

    public List<Earthquake> parse(InputStream inputStream){
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                String parentTag = "channel";
                //Log.d("parser: ", "val: " + tagname);

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("item")) {
                            // create a new instance of currentEarthquake
                            hitItems = true;
                            currentEarthquake = new Earthquake();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if(parser.getText() != null){
                            text = parser.getText();
                        }
                        break;

                    case XmlPullParser.END_TAG:

                        if(tagname != null && hitItems){
                            if (tagname.equalsIgnoreCase("item")) {
                                // add employee object to list
                                earthquakeList.add(currentEarthquake);
                            } else if (tagname.equalsIgnoreCase("title")) {
                                currentEarthquake.setTitle(text);
                            } else if (tagname.equalsIgnoreCase("description")) {
                                currentEarthquake.setDescription(text);
                                String[] stringSplit = text.split(";");
                                String[]values = SplitColons(stringSplit);
                                currentEarthquake.setOriginDate(values[0]);
                                currentEarthquake.setLocation(values[1]);
                                currentEarthquake.setDepth(values[3]);
                                currentEarthquake.setMagnitude(values[4]);

                            } else if (tagname.equalsIgnoreCase("link")) {
                                currentEarthquake.setLink(text);
                            } else if (tagname.equalsIgnoreCase("pubDate")) {
                                currentEarthquake.setPubDate(text);
                                currentEarthquake.setEarthquakeDate(setLocalDate(text));
                            } else if (tagname.equalsIgnoreCase("category")) {
                                currentEarthquake.setCategory(text);
                            }
                            else if (tagname.equalsIgnoreCase("lat")) {
                                currentEarthquake.setGeoLat(text);
                            }
                            else if (tagname.equalsIgnoreCase("long")) {
                                currentEarthquake.setGeoLong(text);
                            }
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("parsedEarthquakeList",""+earthquakeList.size());
        return earthquakeList;
    }

    private String[] SplitColons(String[] array){

        //ArrayList<String>returnArray = new ArrayList<String>();
        String[] returnArray = new String[array.length];
        String tempString;
        for(int i = 0; i< array.length; i++){
            tempString = array[i].replaceAll(".*:\\s+", "");
            returnArray[i] = tempString;
            //Log.e("array["+i+"]", tempString);
        }
        return returnArray;
    }

    public LocalDate setLocalDate(String pubDate){

        //Log.d("pubDate", pubDate);
        Date rawDataAsDate = new Date();

        try{
            rawDataAsDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.UK).parse(pubDate);
        }
        catch(ParseException e){
            Log.e("Date Converting Error", "Can't convert string date to earthquake returnDate");
        }

        LocalDate returnDate = rawDataAsDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //Log.d("returnDate",": " + returnDate);
        return returnDate;
    }


}
