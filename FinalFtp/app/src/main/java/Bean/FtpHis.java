package Bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class FtpHis extends LitePalSupport {
    private String ip;
    @Column(defaultValue = "2222")
    private String port;

    @Column(defaultValue = "admin")
    private String user;
    @Column(defaultValue = "123")
    private String psw;

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
