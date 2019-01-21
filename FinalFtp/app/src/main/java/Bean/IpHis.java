package Bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;



public class IpHis extends LitePalSupport {
    @Column(unique = true)
    private String ipDomain;

    public String getIpDomain() {
        return ipDomain;
    }

    public void setIpDomain(String ipDomain) {
        this.ipDomain = ipDomain;
    }
}
