package Bean;

import java.io.Serializable;

public class FtpBean implements Serializable {

    private String url;
    private int port;
    private String user;
    private String psw;

    public FtpBean(String url, int port, String user, String psw) {
        this.url = url;
        this.port = port;
        this.user = user;
        this.psw = psw;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }
}
