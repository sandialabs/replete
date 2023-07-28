package replete.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import replete.numbers.BitUtil;
import replete.numbers.NumUtil;

public class IpUtil {

    public static int ipV4StrToInt(String ip) {
        String[] octets = ip.split("\\.");
        int i = Integer.parseInt(octets[3]) & 0xFF;
        i |= ((Integer.parseInt(octets[2]) << 8) & 0xFF00);
        i |= ((Integer.parseInt(octets[1]) << 16) & 0xFF0000);
        i |= ((Integer.parseInt(octets[0]) << 24) & 0xFF000000);
        return i;
    }

    public static String ipV4IntToStr(int ip) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(ip >>> 24);        // Right shift without sign extension
        buffer.append('.');
        buffer.append((ip >>> 16) & 0xFF);
        buffer.append('.');
        buffer.append((ip >>> 8) & 0xFF);
        buffer.append('.');
        buffer.append(ip & 0xFF);
        return buffer.toString();
    }

    public static long[] ipV6StrToLongs(String ip) {
        String[] groups = ip.split(":", Integer.MAX_VALUE);

        int nonBlank = 0;
        for(String group : groups) {
            if(!group.isEmpty()) {
                nonBlank++;
            }
        }

        long up = 0L;
        long lo = 0L;

        int groupsSet = 0;
        boolean blankGroupsSet = false;

        for(String group : groups) {
            if(!group.isEmpty()) {
                int i = Integer.parseInt(group, 16);
                if(groupsSet < 4) {
                    up <<= 16;
                    up |= i;
                } else {
                    lo <<= 16;
                    lo |= i;
                }
                groupsSet++;

            } else if(!blankGroupsSet) {
                for(int b = 0; b < 8 - nonBlank; b++) {
                    if(groupsSet < 4) {
                        up <<= 16;
                    } else {
                        lo <<= 16;
                    }
                    groupsSet++;
                }
                blankGroupsSet = true;
            }
        }

        return new long[] {up, lo};
    }

    public static String ipV6LongsToStr(long[] ip) {
        StringBuilder buffer = new StringBuilder();
        long up = ip[0];
        for(int i = 0; i < 4; i++) {
            buffer.append(Integer.toHexString((int) ((up >>> (48 - i * 16)) & 0xFFFFL)));
            buffer.append(":");
        }
        long lo = ip[1];
        for(int i = 0; i < 4; i++) {
            buffer.append(Integer.toHexString((int) ((lo >>> (48 - i * 16)) & 0xFFFFL)));
            buffer.append(":");
        }

        // Can implement IP textual representation normalization if desired.
//        int start = -1;
//        for(int i = 0; i < buffer.length(); i++) {
//            char ch = buffer.charAt(i);
//            if(ch == '0') {
//                if(buffer.charAt(i + 1) == ':') {
//                    start = i;
//                    int end = x;
//                    for(int j = i; j < buffer.length(); j++) {
//                        char ch2 = buffer.charAt(j);
//                        if(ch2 == '0' && ch == ':') {
//
//                        }
//                    }
//                }
//            }
//        }
        return buffer.toString();
    }

    public static List<String> findKnownIps(String url) throws UnknownHostException {
        List<String> knownIps = new ArrayList<>();
        String host = UrlUtil.url(url).getHost();
        if(host != null) {
            InetAddress[] addrs = InetAddress.getAllByName(host);
            if(addrs.length != 0) {
                for(InetAddress addr : addrs) {
                    knownIps.add(addr.toString());
                }
            }
        }
        return knownIps;
    }


    public static void main(String[] args) {
        String xip = "2001:371::";
        long[] xcheckIp = ipV6StrToLongs(xip);
        String xip2 = ipV6LongsToStr(xcheckIp);
        System.out.println(xip);
        System.out.println(xip2);
        if(true) {
            return;
        }

        String[] ipv6 = {
            "2001:0db8:85a3:0000:0000:8a2e:0370:7334",
            "2001:db8:85a3:0:0:8a2e:370:7334",
            "2001:db8:85a3::8a2e:370:7334",
            "::ffff",
            "2::ffff",
            "::1",
            "::"
        };

        for(String ip : ipv6) {
            long[] longs = ipV6StrToLongs(ip);
            System.out.printf("%39s => %s%n", ip, BitUtil.markupBinaryString(BitUtil.toBinaryString(longs[0]) + BitUtil.toBinaryString(longs[1])));
            String ip2 = ipV6LongsToStr(longs);
            System.out.printf("%39s%n", ip2);
        }

        int TRIALS = 10_000_000;
        Random R = new Random();
        for(int i = 0; i < TRIALS; i++) {
            long up = R.nextLong();
            long lo = R.nextLong();
            long[] randomIp = {up, lo};
            String ip = ipV6LongsToStr(randomIp);
            long[] checkIp = ipV6StrToLongs(ip);
            if(randomIp[0] != checkIp[0] || randomIp[1] != checkIp[1]) {
                throw new RuntimeException(ip);
            }
            NumUtil.printProg("IP Trial", 10_000, i, TRIALS);
        }
        NumUtil.printProg("IP Trial", 10_000, TRIALS, TRIALS);
    }
}
