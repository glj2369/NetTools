package Bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class TelHis extends LitePalSupport implements Serializable {

    private String ip;
    @Column(defaultValue = "23")
    private String port;

    @Column(defaultValue = "admin")
    private String user;
    @Column(defaultValue = "admin")
    private String psw;

    public TelHis() {
    }

    public TelHis(String ip, String port, String user, String psw) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.psw = psw;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
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
