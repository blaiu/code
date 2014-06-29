/**
 * 
 */
package cn.blaiu.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * @author blaiu
 *
 */
public class PropertiesUtil {

	private static Logger logger = Logger.getLogger(PropertiesUtil.class);
	
	public static Properties getProperties (InputStream is) {
		Properties prop = null;
		try {
			prop = new Properties();
			prop.load(is);
		} catch (IOException e) {
			logger.error("read configuration error", e);
		}
		return prop;
	}
	
}
