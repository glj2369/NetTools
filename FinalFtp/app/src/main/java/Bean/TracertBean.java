package Bean;

public class TracertBean {

    private String hostname;
    private String ip;
    private float elapsedtime;

    public TracertBean(String s, String s1, float elapsedTime) {
        hostname = s;
        ip = s1;
        this.elapsedtime = elapsedTime;
    }


    public String getIp() {
        return ip;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        String res = hostname + " " + ip + " " + elapsedtime;
        return res;
    }
}
