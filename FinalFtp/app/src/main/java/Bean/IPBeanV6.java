package Bean;

public class IPBeanV6 {

    /**
     * ip : 240e:86:f002:8fad:b7d2:b3bd:6336:7fa9
     * ip_decimal : 47924910695129734634459386596950441897
     * country : China
     * country_eu : false
     * country_iso : CN
     * city : Shenyang
     * latitude : 41.7922
     * longitude : 123.4328
     */

    private String ip;
    private String ip_decimal;
    private String country;
    private boolean country_eu;
    private String country_iso;
    private String city;
    private double latitude;
    private double longitude;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp_decimal() {
        return ip_decimal;
    }

    public void setIp_decimal(String ip_decimal) {
        this.ip_decimal = ip_decimal;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isCountry_eu() {
        return country_eu;
    }

    public void setCountry_eu(boolean country_eu) {
        this.country_eu = country_eu;
    }

    public String getCountry_iso() {
        return country_iso;
    }

    public void setCountry_iso(String country_iso) {
        this.country_iso = country_iso;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
