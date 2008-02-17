/**
 *
 */
package org.kit.furia.misc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

/*
    OBSearch: a distributed similarity search engine
    This project is to similarity search what 'bit-torrent' is to downloads.
    Copyright (C)  2007 Arnoldo Jose Muller Molina

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
 * Test utilities.
 * @author Arnoldo Jose Muller Molina
 * @since 0.7
 */
public class FuriaProperties {

    /**
     * Properties loaded from the properties file.
     */
    private static Properties props = null;

    /**
     * Return the properties from the test properties file.
     * @return the properties from the test properties file
     * @throws IOException If the file cannot be read.
     */
    public static Properties getProperties() throws IOException {
        if (props == null) { // load the properties only once
            InputStream is = FuriaProperties.class
                    .getResourceAsStream( "/furia.properties");
            props = new Properties();
            props.load(is);           
        }        
        return props;
    }
    
    
    
    public static String getProperty(String x) throws IOException{
        return getProperties().getProperty(x);
    }

}
