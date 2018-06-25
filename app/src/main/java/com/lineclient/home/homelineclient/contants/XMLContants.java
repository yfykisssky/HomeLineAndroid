package com.lineclient.home.homelineclient.contants;

public class XMLContants {

    public static String[] getAllFilesName(){
        return new String[]{USER_XML,
                REFRESH_XML,
                AUTO_EXECUTE,
                AUTO_EXECUTE_KEY.RANGE_WIFI,AUTO_EXECUTE_KEY.WIFI_INFO,
                AUTO_EXECUTE_KEY.RANGE_GPS,AUTO_EXECUTE_KEY.GPS_INFO};
    }

    public static final String USER_XML = "USER_XML";

    public static final class USER_KEY {
        public static final String USER_NAME = "USER_NAME";
        public static final String TOKEN = "TOKEN";
        public static final String AESKEY = "AESKEY";
    }

    public static final String REFRESH_XML = "REFRESH_XML";

    public static final class REFRESH_DATA {
        public static final String REFRESH_KIND = "REFRESH_KIND";
        public static final String REFRESH_TIME = "REFRESH_TIME";
        public static final String REFRESH_ALL = "REFRESH_TIME";
    }

    public static final String AUTO_EXECUTE = "AUTO_EXECUTE";

    public static final class AUTO_EXECUTE_KEY {
        public static final String RANGE_WIFI = "RANGE_WIFI";
        public static final String WIFI_INFO = "WIFI_INFO";

        public static class WIFI_INFO {
            private String WIFI_SSID;
            private String WIFI_IP;
            private String WIFI_MAC;
            private String WIFI_BSSID;

            public String getWIFI_SSID() {
                return WIFI_SSID;
            }

            public void setWIFI_SSID(String WIFI_SSID) {
                this.WIFI_SSID = WIFI_SSID;
            }

            public String getWIFI_IP() {
                return WIFI_IP;
            }

            public void setWIFI_IP(String WIFI_IP) {
                this.WIFI_IP = WIFI_IP;
            }

            public String getWIFI_MAC() {
                return WIFI_MAC;
            }

            public void setWIFI_MAC(String WIFI_MAC) {
                this.WIFI_MAC = WIFI_MAC;
            }

            public String getWIFI_BSSID() {
                return WIFI_BSSID;
            }

            public void setWIFI_BSSID(String WIFI_BSSID) {
                this.WIFI_BSSID = WIFI_BSSID;
            }
        }

        public static final String RANGE_GPS = "RANGE_GPS";
        public static final String GPS_INFO="GPS_INFO";

        public class GPS_INFO {
            private String GPS_LAT;
            private String GPS_LNG;
            private String GPS_RANGE;

            public String getGPS_LAT() {
                return GPS_LAT;
            }

            public void setGPS_LAT(String GPS_LAT) {
                this.GPS_LAT = GPS_LAT;
            }

            public String getGPS_LNG() {
                return GPS_LNG;
            }

            public void setGPS_LNG(String GPS_LNG) {
                this.GPS_LNG = GPS_LNG;
            }

            public String getGPS_RANGE() {
                return GPS_RANGE;
            }

            public void setGPS_RANGE(String GPS_RANGE) {
                this.GPS_RANGE = GPS_RANGE;
            }
        }

    }

}
