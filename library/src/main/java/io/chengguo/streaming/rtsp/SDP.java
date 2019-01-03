package io.chengguo.streaming.rtsp;

public class SDP extends Response.Body {

    /**
     * Session Description Protocol Version (v)
     */
    private int version;
    /**
     * Owner/Creator
     */
    private Object owner;
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
    private String control;
    /**
     * Session Attribute (a): range
     */
    private String range;
    /**
     * Session Attribute (a): x-qt-text-nam
     */
    private String xQtTextNam;

    /**
     * Session Attribute (a): x-qt-text-inf
     */
    private String xQtTextInf;


    @Override
    public void append(String sLine) {
        super.append(sLine);

    }
}