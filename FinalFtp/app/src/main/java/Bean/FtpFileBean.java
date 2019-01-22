package Bean;

public class FtpFileBean {
    private int icon;
    private String name;
    private String time;
    private String size;
    private int dir;
    private long oldSize;

    public FtpFileBean(int icon, String name, String time, String size, int dir, long oldSize) {
        this.icon = icon;
        this.name = name;
        this.time = time;
        this.size = size;
        this.dir = dir;
        this.oldSize = oldSize;
    }

    public long getOldSize() {
        return oldSize;
    }

    public void setOldSize(long oldSize) {
        this.oldSize = oldSize;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }




    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public FtpFileBean() {
    }


}
