package org.kit.furia;

import java.io.InputStream;
import java.util.Properties;

import org.ajmm.obsearch.example.HelpException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

public class AbstractFuriaChanCommandLine {

    public static void initLogger() {
        try {
            
            InputStream is = AbstractFuriaChanCommandLine.class
                    .getResourceAsStream("/furiaLog4j.config");
            Properties props = new Properties();
            props.load(is);
            PropertyConfigurator.configure(props);
        } catch (final Exception e) {
            System.err.print("Make sure log4j is configured properly"
                    + e.getMessage());
            e.printStackTrace();
            System.exit(48);
        }
    }

    /**
     * Parses the array of options as received in main() and returns a
     * CommandLine object that makes it easier to analyze the commands.
     * @param options
     *                The options object (generated from initCommandLine(...))
     * @param c
     *                The class that will be used for the name of the program.
     * @param args
     *                Arguments of the command line (as received in main(...))
     * @return A CommandLine object ready to parse commands
     * @throws ParseException
     *                 If the given arguments have syntax errors
     * @throws HelpException
     *                 If the user wants "help" we generate an exception
     */
    public static CommandLine getCommandLine(final Options options,
            final Class c, final String[] args) throws ParseException,
            HelpException {
        final CommandLineParser parser = new GnuParser();
        final Option help = new Option("help", "print this message");
        options.addOption(help);
        final CommandLine line = parser.parse(options, args);
        // add the "help option to the help" :)
        if (line.hasOption("help")) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(c.getName(), options, true);
            throw new HelpException();
        }
        return line;
    }

}
