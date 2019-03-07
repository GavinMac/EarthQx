package gcu.mpd.s1715408.earthqx;

public class Earthquake {

    private String title;
    private String description;
    private String link;
    private String pubDate;
    private String category;
    private String geoLat;
    private String geoLong;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGeoLat() { return geoLat; }

    public void setGeoLat(String geoLat) {
        this.geoLat = geoLat;
    }

    public String getGeoLong() { return geoLong; }

    public void setGeoLong(String geoLong) {
        this.geoLong = geoLong;
    }


    @Override
    public String toString(){
        return title + "\n" + description + "\n" + link + "\n" + pubDate + "\n" + category + "\n" + geoLat + "\n" + geoLong;
    }

}
