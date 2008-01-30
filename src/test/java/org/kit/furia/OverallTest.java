package org.kit.furia;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.ajmm.obsearch.index.utils.Directory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kit.furia.fragment.soot.FragmentBuilderClient;
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
 * OverallTest tests that Furia can run. It fails if Furia cannot detect the
 * given programs within a certain threshold
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */

public class OverallTest {
    private static final Logger logger = Logger.getLogger("OverallTest");
    /**
     * Properties for the test.
     */    
        
    @Before
    public void setUp() throws Exception {
    }


    FuriaChanEngine engine = null;
    @Test
    public void testAll() throws IOException, Exception{
        
        try {
            PropertyConfigurator.configure(FuriaProperties.getProperty("log4j.file"));
        } catch (Exception e) {
            System.err.print("Make sure log4j is configured properly"
                    + e.getMessage()); 
            e.printStackTrace();
            assertTrue(false);
        }
        
        // delete the output directory before starting a new test.
        String output = FuriaProperties.getProperty("test.db.output");
        File outputDir = new File(output);
                     
       /*Directory.deleteDirectory(outputDir); 
       assertTrue(outputDir.mkdirs());
       fragmentDataSet("JPackageClass");
       fragmentDataSet("JPackageClassObfuscatedSandMarkNoClassEnc");
       fragmentDataSet("JPackageClassObfuscatedZelix");
       
       // now we perform matches with IRIndex and the results
       // should be within the top 10.*/
       
       // first we create the database:
       File furiaChanDBDir = new File(outputDir, "FuriaChanDB");
       /*Directory.deleteDirectory(furiaChanDBDir);
        engine = new FuriaChanEngine(furiaChanDBDir);      
       
       engine.insert(new File(outputDir, "JPackageClass"));
       engine.freeze();
       engine.close();*/
       // got to reopen everything or lucene won't have anything refreshed.
       engine = new FuriaChanEngine(furiaChanDBDir);   
       engine.setValidationMode(true);
       engine.setN((short)10);
       engine.setR((short) 3);
       logger.info("*** Matching base ***");
       // performs the search. Makes sure all the items were found in the specified range
       assertEquals(engine.search(new File(outputDir, "JPackageClass")), 1f);       
       logger.info("*** Matching zelix ***");
       assertEquals(engine.search(new File(outputDir, "JPackageClassObfuscatedZelix")), 1f);
       logger.info("*** Matching SandMark ***");
       assertEquals(engine.search(new File(outputDir, "JPackageClassObfuscatedSandMarkNoClassEnc")), 1f);
    }
    
    @After
    public void closeEngine() throws Exception{
        if(engine != null){
            engine.close(); // close the engine so that everything will be refreshed.
        }
    }
    
    /**
     * Fragments the given data set. The dataset name is prepended to the 
     * test variable: test.db.input
     * @param dataset
     */
    private void fragmentDataSet(String dataset) throws Exception{
        String input = FuriaProperties.getProperty("test.db.input") + File.separator + dataset;
        String output = FuriaProperties.getProperty("test.db.output")+ File.separator + dataset ;
        // create the fragments!
        File outputFile = new File(output);
        Directory.deleteDirectory(outputFile);
        FragmentBuilderClient c = new FragmentBuilderClient(true,
                new File(input), Runtime.getRuntime().availableProcessors(), outputFile, true);
        // fragments were created.         
    }

}
