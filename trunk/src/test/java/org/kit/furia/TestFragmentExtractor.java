package org.kit.furia;


import java.io.File;
import java.io.IOException;

import org.ajmm.obsearch.index.utils.Directory;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.kit.furia.fragment.FragmentBuilderClientAux;
import org.kit.furia.misc.FuriaProperties;

import static org.junit.Assert.*;

public class TestFragmentExtractor {

    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testAll() throws IOException, Exception{
        
       /* try {
            PropertyConfigurator.configure(FuriaProperties.getProperty("log4j.file"));
        } catch (Exception e) {
            System.err.print("Make sure log4j is configured properly"
                    + e.getMessage()); 
            e.printStackTrace();
            assertTrue(false);
        }
        File input = new File(FuriaProperties.getProperty("test.db.input"), "argouml-0.17.5-4jpp.noarch.rpm.jpackage");
        File output = new File( FuriaProperties.getProperty("test.db.output"), "fragmentExtractorTest" );
        String[] params = new String[3];
        params[0]= input.toString();
        params[1] = "100";
        params[2] = output.toString();*/
        
        System.setProperty("log4j.file", "furiaLog4j.config");
        String[] params = new String[3];
        params[0] = "/home/amuller/temp/SmallDataSetJava/JPackageClass/batik-1.6-1jpp.noarch.rpm.jpackage/";
        params[1] = "target/test-classes/extractorTest";
        params[2] = "asm";
        Directory.deleteDirectory(new File(params[1]));
        FragmentBuilderClientAux.main(params);
    }

}
