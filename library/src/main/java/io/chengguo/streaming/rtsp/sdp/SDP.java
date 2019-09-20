package io.chengguo.streaming.rtsp.sdp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

class SDP implements ISerializer {

    private HashMap<String, String> mContents = new HashMap<>();

    /**
     * Session Description Protocol Version (v)
     */
    private int version;
    /**
     * Owner/Creator, Session Id (o): - 1567231587249627 1 IN IP4 172.17.0.2
     */
    private Owner owner;
    /**
     * Session Name (s)
     */
    private String sessionName;
    /**
     * Session Information (i)
     */
    private String sessionInfo;

    private long sessionTimeStart;

    private long sessionTimeStop;
    /**
     * Session Attribute (a): tool
     */
    private String tool;
    /**
     * Session Attribute (a): type
     */
    private String type;
    /**
     * Session Attribute (a): control
     */
    private String mediaControl;
    /**
     * Session Attribute (a): range
     */
    private String range;

    /**
     * Media Description, name and address (m): video 0 RTP/AVP 96
     */
    private MediaDescription mediaDescription;

    public int getVersion() {
        return version;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getSessionInfo() {
        return sessionInfo;
    }

    public long getSessionTimeStart() {
        return sessionTimeStart;
    }

    public long getSessionTimeStop() {
        return sessionTimeStop;
    }

    public String getTool() {
        return tool;
    }

    public String getType() {
        return type;
    }

    public String getMediaControl() {
        return mediaControl;
    }

    public String getRange() {
        return range;
    }

    public MediaDescription getMediaDescription() {
        return mediaDescription;
    }

    @Nullable
    public String query(String key) {
        return mContents.get(key);
    }

    @Override
    public SDP from(String text) {
        String[] lines = text.split("\r\n");
        for (String line : lines) {
            String[] split = line.split("=", 2);
            if (split.length != 2) {
                continue;
            }
            String shortField = split[0];
            String content = split[1];
            mContents.put(shortField, content);
            parseLine(shortField, content);
        }
        return this;
    }

    private void parseLine(String shortField, String content) {
        if ("v".equals(shortField)) {
            version = Integer.parseInt(content);
        } else if ("o".equals(shortField)) {
            this.owner = parseOwner(content);
        } else if ("i".equals(shortField)) {
            sessionInfo = content;
        } else if ("s".equals(shortField)) {
            sessionName = content;
        } else if ("m".equals(shortField)) {
            mediaDescription = parseMediaDes(content);
        } else if ("t".equals(shortField)) {
            parseActiveTime(content);
        } else if ("a".equals(shortField)) {
            parseAttrs(content);
        }
    }

    private void parseActiveTime(String content) {
        String[] times = content.split(" ");
        if (times.length == 2) {
            sessionTimeStart = Long.parseLong(times[0]);
            sessionTimeStart = Long.parseLong(times[1]);
        }
    }

    private void parseAttrs(String content) {
        String[] attrs = content.split(":");
        if (attrs.length == 2) {
            String attrName = attrs[0];
            String attrValue = attrs[1];
            if ("tool".equals(attrName)) {
                tool = attrValue;
            } else if ("control".equals(attrName)) {
                mediaControl = attrValue;
            } else if ("type".equals(attrName)) {
                type = attrValue;
            } else if ("range".equals(attrName)) {
                range = attrValue;
            }else {
                parseAttr(attrName, attrValue);
            }
        }
    }

    protected void parseAttr(String attrName, String attrValue) {
    }

    @NonNull
    private MediaDescription parseMediaDes(String content) {
        MediaDescription md = new MediaDescription();
        String[] values = content.split(" ");
        for (int i = 0; i < values.length; i++) {
            switch (i) {
                case 0:
                    md.type = values[i];
                    break;
                case 1:
                    md.port = Integer.parseInt(values[i]);
                    break;
                case 2:
                    md.protocol = values[i];
                    break;
                case 3:
                    md.format = Integer.parseInt(values[i]);
                    break;
            }
        }
        return md;
    }

    @NonNull
    private Owner parseOwner(String value) {
        Owner owner = new Owner();
        String[] values = value.split(" ");
        for (int i = 0; i < values.length; i++) {
            switch (i) {
                case 0:
                    owner.username = values[i];
                    break;
                case 1:
                    owner.sessionId = values[i];
                    break;
                case 2:
                    owner.sessionVersion = Integer.parseInt(values[i]);
                    break;
                case 3:
                    owner.networkType = values[i];
                    break;
                case 4:
                    owner.addressType = values[i];
                    break;
                case 5:
                    owner.address = values[i];
                    break;
            }
        }
        return owner;
    }

    @Override
    public String toString() {
        return "SDP{" +
                "version=" + version +
                ", owner=" + owner +
                ", sessionName='" + sessionName + '\'' +
                ", sessionInfo='" + sessionInfo + '\'' +
                ", sessionTimeStart=" + sessionTimeStart +
                ", sessionTimeStop=" + sessionTimeStop +
                ", tool='" + tool + '\'' +
                ", type='" + type + '\'' +
                ", mediaControl='" + mediaControl + '\'' +
                ", range='" + range + '\'' +
                ", mediaDescription=" + mediaDescription +
                '}';
    }

    public static class Owner {
        String username;
        String sessionId;
        int sessionVersion;
        String networkType;
        String addressType;
        String address;

        @Override
        public String toString() {
            return "Owner{" +
                    "username='" + username + '\'' +
                    ", sessionId='" + sessionId + '\'' +
                    ", sessionVersion=" + sessionVersion +
                    ", networkType='" + networkType + '\'' +
                    ", addressType='" + addressType + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    public static class MediaDescription {
        /**
         * media type
         * example: video
         */
        String type;

        /**
         * media port
         */
        int port;

        /**
         * media protocol
         * example: RTP/AVP
         */
        String protocol;

        /**
         * media format
         * example: 96
         */
        int format;

        @Override
        public String toString() {
            return "MediaDescription{" +
                    "type='" + type + '\'' +
                    ", port=" + port +
                    ", protocol='" + protocol + '\'' +
                    ", format=" + format +
                    '}';
        }
    }
}