package com.digios.slowmoserver;

import com.digios.slowmoserver.gui.MainForm;
import com.digios.slowmoserver.websocketserver.WebSocketServer;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class Main {
    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Start app");

        try {
            //WebSocketServer server = new WebSocketServer(8887);
            //server.start();

            new MainForm();
        }
        catch (Exception ex) {
            logger.error(ex);
        }

        //new MainForm();

        /*Options options = new Options();

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
                WebSocketServer server = new WebSocketServer( 8887 );
                server.start();

                BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
                while ( true ) {
                    String in = sysin.readLine();
                    server.broadcast( in );
                    if( in.equals( "exit" ) ) {
                        server.stop(1000);
                        System.exit(0);
                    }
                }
            }
            else if (type.equals("slowmo1")) {
                PhotoMakerAlgoritm1 pm = new PhotoMakerAlgoritm1(new URI(Config.INSTANCE.host()));
                pm.connect();

                BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
                while ( true ) {
                    String in = sysin.readLine();
                    if( in.equals( "exit" ) ) {
                        pm.close();
                        System.exit(0);
                    }
                }
            }
            else if (type.equals("slowmo2")) {
                PhotoMakerAlgoritm2 pm = new PhotoMakerAlgoritm2(new URI(Config.INSTANCE.host()));
                pm.connect();

                BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
                while ( true ) {
                    String in = sysin.readLine();
                    if( in.equals( "exit" ) ) {
                        pm.close();
                        System.exit(0);
                    }
                }
            }
            else {
                //logger.error("Programm type " + type + " not implemented");
                new MainForm();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //System.exit(0);
    }
}
