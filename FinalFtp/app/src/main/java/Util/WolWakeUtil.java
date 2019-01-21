package Util;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class WolWakeUtil extends Thread {
    String ip = null;
    String macAddr = null;

    public WolWakeUtil(String ip, String macAddr) {
        this.ip = ip;
        this.macAddr = macAddr;
    }

    @Override
    public void run() {
        super.run();
        wakeOnLan(ip, macAddr);
    }

    public void wakeOnLan(String ip, String macAddr) {
        DatagramSocket datagramSocket = null;
        try {
            byte[] mac = getMacBytes(macAddr);
            byte[] magic = new byte[6 + 16 * mac.length];
            //1.写入6个FF
            for (int i = 0; i < 6; i++) {
                magic[i] = (byte) 0xff;
            }
            //2.写入16次mac地址
            for (int i = 6; i < magic.length; i += mac.length) {
                System.arraycopy(mac, 0, magic, i, mac.length);
            }
            datagramSocket = new DatagramSocket();
            DatagramPacket datagramPacket = new DatagramPacket(magic, magic.length, InetAddress.getByName(ip), 40000);
            datagramSocket.send(datagramPacket);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (datagramSocket != null)
                datagramSocket.close();
        }
    }

    private byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
}
