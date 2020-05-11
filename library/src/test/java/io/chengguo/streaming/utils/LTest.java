package io.chengguo.streaming.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LTest {

    @Before
    public void setUp() throws Exception {
        L.setEnabled(true);
    }

    @Test
    public void i() {
        L.i("hello");
    }

    @Test
    public void d() {
    }

    @Test
    public void w() {
    }

    @Test
    public void e() {
    }
}