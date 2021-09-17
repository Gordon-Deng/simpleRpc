package com.gordon.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerUtils {

    public static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger("orc.rpc");
    }

}
