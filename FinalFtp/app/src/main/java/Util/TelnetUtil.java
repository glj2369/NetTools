package Util;

import android.util.Log;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * Telnet操作类
 * Created by fblife on 2017/3/28.
 */

public class TelnetUtil {
    private static final String TAG = TelnetUtil.class.getSimpleName();
    private TelnetClient client;
    private BufferedInputStream bis;
    private PrintStream ps;
    private char prompt = '$';
    private GetMessageListener mListener;

    public TelnetUtil() {
        client = new TelnetClient("VT220");
    }

    /**
     * 连接及登录
     *
     * @param ip       目标主机IP
     * @param port     端口号（Telnet 默认 23）
     * @param user     登录用户名
     * @param password 登录密码
     */
    public void connect(String ip, int port, String user, String password) {
        try {
            client.connect(ip, port);
            //client.setCharset(Charset.forName("UTF-8"));
            bis = new BufferedInputStream(client.getInputStream());
            ps = new PrintStream(client.getOutputStream());
            //this.prompt = user.equals("admin") ? '#' : '$';
            this.prompt = '#';
            login(user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     *
     * @param user
     * @param password
     */
    public void login(String user, String password) {
        read("login:");
        write(user);
        read("Password:");
        write(password);
        read(prompt + " ");
    }

    /**
     * 读取返回来的数据
     *
     * @param info
     * @return
     */
    public String read(String info) {
        try {
            char lastChar = info.charAt(info.length() - 1);
            Log.e("111111", "lastChar=" + lastChar);
            StringBuffer sb = new StringBuffer();
            char ch = (char) bis.read();
            Log.e("111111", "while外 ch=" + ch);

            while (true) {
                sb.append(ch);
                Log.e("111111", " sb=" + sb.toString());
                if (ch == lastChar) {
                    Log.e("111111", " while内ch=" + ch);
                    if (sb.toString().endsWith(info)) {
                        if (mListener != null) {
                            mListener.onMessage(sb.toString());
                            Log.v("11111111111111111111", sb.toString());
                        } else {
                            Log.v("11111111111111111111", sb.toString());
                        }
                        return sb.toString();
                    }
                }
                ch = (char) bis.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写指令
     *
     * @param instruction
     */
    public void write(final String instruction) {
        try {
            ps.println(instruction);
            ps.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向目标发送命令字符串
     *
     * @param command
     * @return
     */
    public String sendCommand(String command) {
        try {
            write(command);
            return read(prompt + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        try {
            if (client != null)
                client.disconnect();
            if (bis != null)
                bis.close();
            if (ps != null)
                ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean isComm() {
        return client.isConnected();
    }

    public Boolean isAvali() {
        return client.isAvailable();
    }


    public interface GetMessageListener {
        void onMessage(String info);
    }

    public void setListener(GetMessageListener mListener) {
        this.mListener = mListener;
    }
}
