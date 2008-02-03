package org.kit.furia.fragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.ajmm.obsearch.asserts.OBAsserts;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kit.furia.misc.FuriaProperties;

/*
 Furia-chan: An Open Source software license violation detector.    
 Copyright (C) 2007 Kyushu Institute of Technology

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * FragmentBuilderClient is in charge of executing soot in a set of java class
 * directories and leaving a "fragments" file in the specified output directory.
 * Since Soot has problems when executed several times during the life of a VM
 * (reset had issues) we call a new VM for each program that will be fragmented.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */

public class FragmentBuilderClient {

    /**
     * Used to stop the threads if an error occurs.
     */
    private Exception exception = null;

    /**
     * Logger.
     */
    private static Logger logger = Logger.getLogger("FragmentBuilderClient");

    /**
     * Files that will be processed.
     */
    private File[] filesToProcess;

    /**
     * Index used to access filesToProcess.
     */
    private AtomicInteger i;

    /**
     * Used to wait until all threads have finished.
     */
    private CountDownLatch join;

    /**
     * Maximum # of expansions performed for each fragment.
     */
    private int maxExpansions;

    /**
     * If the program will receive one directory (that holds in its root a set
     * of java class files) (false) or if the directory received contains
     * directories that contain class files. Each of these directories is
     * treated as a different application.
     */
    private boolean directoryOfDirectoriesMode;

    /**
     * The output directory to use.
     */
    protected File outputDirectory;

    /**
     * Fail if there is an error.
     */
    private boolean failOnError;

    /**
     * Number of cpus.
     */
    private int cpus;

    /**
     * Timeout for subprocesses.
     */
    private long timeout;

    /**
     * The frequency we will check each process to complete.
     */
    private static final long POLL_INTERVAL = 1000 * 5; // every seconds.

    /**
     * Error code returned by the caller if there is a timeout exception.
     */
    private static final int TIMEOUT_ERRORCODE = -20; // every seconds.
    
    /**
     * Fragment engine to employ.
     */
    private String engine;

    /**
     * Takes a directory of a set of directories and generates fragments out of
     * the given folders. An infinite timeout is set. (Not recommended for Soot)
     * @param cpus
     *                The number of CPUS to employ
     * @param directory
     *                The directory that will be opened.
     * @param directoryOfDirectoriesMode
     *                If the program will receive one directory (that holds in
     *                its root a set of java class files) (false) or if the
     *                directory received contains directories that contain class
     *                files. Each of these directories is treated as a different
     *                application.
     * @param outputDirectory
     *                The directory that holds the resulting files from the
     *                operation. If directoryOfDirectoriesMode == false then the
     *                output data files will be copied directory to
     *                outputDirectory. Otherwise a directory outputDirectory/<app>
     *                will be created for each application where <app> is the
     *                application name.
     * @param engine Fragment engine that will be used 'soot' or 'asm'.
     * @param failOnError
     *                If true, stops if there is an error.
     */
    public FragmentBuilderClient(boolean directoryOfDirectoriesMode,
            File directory, int cpus, File outputDirectory, boolean failOnError, String engine)
            throws Exception, InterruptedException {
        this(directoryOfDirectoriesMode, directory, cpus, outputDirectory,
                failOnError, (long) 0, engine);
    }

    /**
     * Takes a directory of a set of directories and generates fragments out of
     * the given folders.
     * @param cpus
     *                The number of CPUS to employ
     * @param directory
     *                The directory that will be opened.
     * @param directoryOfDirectoriesMode
     *                If the program will receive one directory (that holds in
     *                its root a set of java class files) (false) or if the
     *                directory received contains directories that contain class
     *                files. Each of these directories is treated as a different
     *                application.
     * @param outputDirectory
     *                The directory that holds the resulting files from the
     *                operation. If directoryOfDirectoriesMode == false then the
     *                output data files will be copied directory to
     *                outputDirectory. Otherwise a directory outputDirectory/<app>
     *                will be created for each application where <app> is the
     *                application name.
     * @param failOnError
     *                If true, stops if there is an error.
     * @param fragmentEngine The fragment engine to be used, currently soot or ASM.
     * @param timeout
     *                Amount of time in milliseconds to wait until one program
     *                is fragmented.
     */
    public FragmentBuilderClient(boolean directoryOfDirectoriesMode,
            File directory, int cpus, File outputDirectory,
            boolean failOnError, long timeout, String fragmentEngine) throws Exception,
            InterruptedException {
        if(! ( fragmentEngine.equals("asm") ||  fragmentEngine.equals("soot"))){
            throw new IllegalArgumentException("Engines available are 'asm' or 'soot'.");
        }
        this.engine = fragmentEngine;
        this.timeout = timeout;
        this.cpus = cpus;

        if (directoryOfDirectoriesMode) {
            filesToProcess = directory.listFiles();
        } else {
            filesToProcess = new File[1];
            filesToProcess[0] = directory;
        }
        this.directoryOfDirectoriesMode = directoryOfDirectoriesMode;
        this.outputDirectory = outputDirectory;
        this.i = new AtomicInteger(0);
        join = new CountDownLatch(filesToProcess.length);
        this.failOnError = failOnError;
        int i = 0;
        while (i < cpus) {
            new Thread(new FragmentExecutor()).start();
            i++;
        }
        boolean interrupted = true;
        // wait for all the threads to complete
        while (interrupted) {
            try {
                join.await();
                interrupted = false;
            } catch (InterruptedException e) {
                // dancing all alone...
            }
        }

        if (exception != null) {
            throw exception;
        }

    }

    /**
     * Executes a new jvm on each folder and creates the fragments
     * @author Arnoldo Jose Muller Molina
     */
    private class FragmentExecutor implements Runnable {

        public void run() {

            try {

                while (exception == null) {
                    int cx = i.getAndIncrement();
                    if (cx < filesToProcess.length) {
                        File dirToProcess = filesToProcess[cx];
                        logger.info("Processing: " + (cx + 1) + " of "
                                + filesToProcess.length + " % " + dirToProcess
                                + " count: " + join.getCount());
                        execApp(dirToProcess);
                        join.countDown();
                        // if there is an error in the execution, we expect
                    } else {
                        break;
                    }
                }
                if (exception != null) {
                    logger
                            .fatal("Quitting thread because exception was not null "
                                    + exception.toString());
                    while (join.getCount() > 0) {
                        join.countDown();
                    }
                }

                logger.debug("Quitting thread");

            } catch (Exception e) {
                logger.fatal("Caught Exception", e);
            }

        }

        /**
         * Calls a sub-process that will fragment
         * @param dirToProcess
         */
        private void execApp(File dirToProcess) {
            File appOutputDir;
            // fixed the proper output directory
            if (directoryOfDirectoriesMode) {
                appOutputDir = new File(outputDirectory, dirToProcess.getName());
            } else {
                appOutputDir = outputDirectory;
            }

            List < String > command = new LinkedList < String >();
            command.add("java");
            long ramToAllocate = Runtime.getRuntime().maxMemory() / 1024 / 1024
                    / cpus;
            command.add("-Xmx" + (ramToAllocate) + "m");
            command.add(FragmentBuilderClientAux.class.getCanonicalName());
            command.add(dirToProcess.toString());
            command.add(appOutputDir.toString());
            command.add(engine);

            try {
                // create dir if it is not created
                if (!outputDirectory.exists()) {
                    outputDirectory.mkdirs();
                }
                OBAsserts.chkAssert(outputDirectory.exists(), "Directory "
                        + outputDirectory + " was not created succesfully");

                // add the current directory as part of the classpath.
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(new File(System.getProperty("user.dir")));
                Map < String, String > env = pb.environment();

                env.put("CLASSPATH", System.getProperty("java.class.path"));
                env
                        .put("log4j.file", FuriaProperties
                                .getProperty("log4j.file"));
                pb.redirectErrorStream(true);
                long endTime = System.currentTimeMillis() + timeout;
                Process p = pb.start();
                boolean interrupted = true;
                // while (interrupted) {
                StringBuilder x = null;
                try {
                    InputStream in = p.getInputStream();
                    InputStreamReader inR = new InputStreamReader(in);
                    BufferedReader bIn = new BufferedReader(inR);

                    int res = TIMEOUT_ERRORCODE; // code for timeout
                    if (timeout == 0) {
                           res = p.waitFor();
                    } else {
                        boolean finished = false;
                        Object lock = new Object();
                        while (!finished) {
                            try {
                                res = p.exitValue();
                                finished = true;
                            } catch (IllegalThreadStateException notFinished) {
                                try {
                                    synchronized (lock) {
                                        lock.wait(POLL_INTERVAL);
                                    }
                                } catch (InterruptedException waitInterrupted) {
                                    // nothing to do.
                                }
                                if (endTime < System.currentTimeMillis()) {
                                    // we have to stop
                                    finished = true;
                                    res = TIMEOUT_ERRORCODE; // :)
                                }
                            }
                        }
                    }

                    logger.info("Completed app:" + dirToProcess.toString()
                            + " count " + join.getCount());
                 
                    if (res != 0) {

                        // Read the output of the invoked program and
                        // print it to string.
                        String line = bIn.readLine();
                        x = new StringBuilder();
                        while (line != null) {
                            x.append(line + "\n");
                            line = bIn.readLine();
                        }
                        bIn.close();
                        FileWriter result = new FileWriter(new File(
                                appOutputDir, "fatal.txt"));
                        // add the msg timeout to the result.
                        if (res == TIMEOUT_ERRORCODE) {
                            x.append("\n Timeout ! :(\n ");
                        }
                        result.write(x.toString());                                                                          
                        result.close();
                        if (failOnError) {
                            throw new Exception(
                                    "Failed while executing command:"
                                            + command.toString()
                                            + " returned code: " + res + "\n"
                                            + x.toString());
                        } else {
                            logger.warn("Failed to process: "
                                    + dirToProcess
                                    + " check "
                                    + (new File(appOutputDir, "fatal.txt"))
                                            .toString());
                        }
                    }
                    interrupted = false;
                } catch (InterruptedException e) {
                    logger.fatal("Process interrupted");
                } catch (FileNotFoundException e2) { // we could not even
                                                        // create the fatal.txt
                                                        // msg.
                    if (x != null) {
                        throw new Exception("Failed while executing command:"
                                + command.toString() + "\n" + "\n"
                                + x.toString());// + "\n Passed env:\n" +
                        // pb.environment().toString()
                        // + " \nworking dir: " +
                        // pb.directory());
                    }
                }
                // }

            } catch (Exception e) {
                logger.fatal("Error while executing command", e);
                exception = e;
            }
        }

    }

}
