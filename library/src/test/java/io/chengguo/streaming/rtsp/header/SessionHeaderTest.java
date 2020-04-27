package io.chengguo.streaming.rtsp.header;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SessionHeaderTest {
    @Test
    public void testConstructor_raw() {
        SessionHeader sessionHeader = new SessionHeader("Session: E7E25DEC;timeout=65");
        assertThat(sessionHeader.getSession(), is("E7E25DEC"));
        assertThat(sessionHeader.getTimeout(), is(65));
    }

    @Test
    public void testChange() {
        SessionHeader sessionHeader = new SessionHeader("Session: E7E25DEC;timeout=65");
        sessionHeader.setTimeout(18);
        System.out.println(sessionHeader.toString());
    }
}