package Bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class MacHis extends LitePalSupport {
    @Column(unique = true)
    private String mac;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
