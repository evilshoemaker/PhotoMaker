package com.digios.slowmoserver;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Start app");

        Options options = new Options();

        Option typeOption = new Option("t", "type", true, "type program [server, slowmo1, slowmo2]");
        typeOption.setRequired(true);
        options.addOption(typeOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            formatter.printHelp("slowmoserver", options);

            System.exit(1);
        }



        try {
            String type = cmd.getOptionValue("type");
            if (type.equals("server")) {
                WebSocketEchoServer server = new WebSocketEchoServer( 8887 );
                server.start();

                BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
                while ( true ) {
                    String in = sysin.readLine();
                    server.broadcast( in );
                    if( in.equals( "exit" ) ) {
                        server.stop(1000);
                        break;
                    }
                }
            }
            else if (type.equals("slowmo1")) {

            }
            else if (type.equals("slowmo2")) {
                logger.error("Programm type " + type + " not implemented");
            }
            else {
                logger.error("Programm type " + type + " not implemented");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
