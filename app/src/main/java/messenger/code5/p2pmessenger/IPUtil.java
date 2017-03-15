package messenger.code5.p2pmessenger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Bastian Wieck on 3/15/2017.
 */

public class IPUtil {
    private final static String p2pInt = "p2p-p2p0";

    public static String getIpFromMac(String macAddress){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine())!=null){
                String[] splitted = line.split(" +");
                if(splitted!=null && splitted.length >= 4){
                    String device = splitted[5];
                    if(device.matches(".*"+p2pInt+".*")){
                        String mac = splitted[3];
                        if(mac.matches(macAddress)){
                            return splitted[0];
                        }
                    }
                }
            }
        }catch (FileNotFoundException e){

        }catch (IOException e){

        }finally {
            try {
                br.close();
            }catch (IOException e){

            }
        }
        return null;
    }

    public static String getLocalIPAddress(){
        try {
            for (Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();en.hasMoreElements();){
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress>enIpAddress = networkInterface.getInetAddresses();enIpAddress.hasMoreElements();){
                    InetAddress inetAddress = enIpAddress.nextElement();

                    String interfaceName = networkInterface.getName();
                    if(interfaceName.matches(".*"+p2pInt+".*")){
                        if(inetAddress instanceof Inet4Address){
                            return getDottedDecimalIP(inetAddress.getAddress());
                        }
                    }
                }
            }
        }catch (SocketException e){

        }catch (NullPointerException e){

        }
        return null;
    }

    private static String getDottedDecimalIP(byte[] ipAddress){
        String ipAddrStr = "";
        for(int i = 0;i<ipAddress.length;i++){
            if(i>0)ipAddrStr+=".";
            ipAddrStr+=ipAddress[i]&0xFF;
        }
        return ipAddrStr;
    }
}
