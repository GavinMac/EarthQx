package gcu.mpd.s1715408.earthqx;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.util.ArrayList;
import java.util.List;

public class XMLPullParserHandler {

    List<Earthquake> earthquakeList;
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
                                currentEarthquake.setOriginDate(stringSplit[0]);
                                currentEarthquake.setLocation(stringSplit[1]);
                                currentEarthquake.setDepth(stringSplit[3]);
                                currentEarthquake.setMagnitude(stringSplit[4]);
                            } else if (tagname.equalsIgnoreCase("link")) {
                                currentEarthquake.setLink(text);
                            } else if (tagname.equalsIgnoreCase("pubDate")) {
                                currentEarthquake.setPubDate(text);
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

        return earthquakeList;
    }
}
