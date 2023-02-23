package com.xiaojukeji.know.streaming.km.rebalance.utils;

import joptsimple.OptionParser;

import java.io.IOException;

public class CommandLineUtils {

    /**
     * Print usage and exit
     */
    public static void printUsageAndDie(OptionParser parser, String message) {
        try {
            System.err.println(message);
            parser.printHelpOn(System.err);
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
