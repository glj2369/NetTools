package Bean;

public class Ipbean {


    /**
     * ip : 175.164.3.178
     * ip_decimal : 2946761650
     * country : China
     * country_eu : false
     * country_iso : CN
     * city : Shenyang
     * latitude : 41.7922
     * longitude : 123.4328
     */

    private String ip;
    private long ip_decimal;
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

    public long getIp_decimal() {
        return ip_decimal;
    }

    public void setIp_decimal(long ip_decimal) {
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
