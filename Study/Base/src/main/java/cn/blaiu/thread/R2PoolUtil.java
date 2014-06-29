//package com.mail.util;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//import org.apache.log4j.Logger;
//
///**
// * R2 jdbc connection pool util
// * 连接池工具类
// * @author 张人杰 北京师范大学 计算机系
// * alex.zhangrj@hotmail.com
// * alex.zhangrj Beijing Normal University
// * 2011/11/26
// */
//public class R2PoolUtil {
//	private static R2Pool pool=null;
//	private static Logger logger=Logger.getLogger(R2PoolUtil.class);
//	
//	private static void readConfiguration(R2Pool pool, InputStream is){
//		if(is!=null){
//			Properties prop = new Properties();
//			try {
//				prop.load(is);
//				pool.setDriver(prop.getProperty("driver"));
//				pool.setUrl(prop.getProperty("url"));
//				pool.setUser(prop.getProperty("user"));
//				pool.setPassword(prop.getProperty("password"));
//				pool.setMaxSize(Integer.valueOf(prop.getProperty("maxSize")));
//				pool.setMinSize(Integer.valueOf(prop.getProperty("minSize")));
//				pool.setInitSize(Integer.valueOf(prop.getProperty("initSize")));
//				pool.setIncrement(Integer.valueOf(prop.getProperty("increment")));
//				pool.setTimeout(Long.valueOf(prop.getProperty("timeout")));
//				pool.setIdleTestPeriod(Long.valueOf(prop.getProperty("idleTestPeriod")));
//				pool.setKeepAliveSql(prop.getProperty("keepAliveSql"));
//				pool.setCloseIdleConnection(Boolean.valueOf(prop.getProperty("closeIdleConnection")));
//				pool.setTimeout(Long.valueOf(prop.getProperty("maxIdleTime")));
//				pool.setRetryTimesWhileGetNullConnection(Integer.valueOf(prop.getProperty("retryTimesWhileGetNullConnection")));
//				pool.setRetryDurationDuringGetNullConnection(Long.valueOf(prop.getProperty("retryDurationDuringGetNullConnection")));
//				pool.setRetryTimesWhileCanNotConnectServer(Integer.valueOf(prop.getProperty("retryTimesWhileCanNotConnectServer")));
//				pool.setRetryDurationDuringConnectingServer(Long.valueOf(prop.getProperty("retryDurationDuringConnectingServer")));
//				pool.setMaxUsingTime(Long.valueOf(prop.getProperty("maxUsingTime")));
//			} catch (IOException e) {
//				logger.error("read configuration error", e);
//			}
//		}
//	}
//	/**
//	 * 从默认配置文件中读取配置信息，并建立数据库连接
//	 * 此方法一般用于只有一个连接池的情况
//	 * @return
//	 */
//	synchronized public static R2Pool getStaticPool(){
//		if(pool==null){
//			pool=new R2Pool();
//			InputStream is = R2PoolUtil.class.getResourceAsStream("/r2.properties");
//			readConfiguration(pool, is);
//			try {
//				is.close();
//			} catch (IOException e) {
//				logger.error("close properties file stream error", e);
//			}
//		}
//		return pool;
//	}
//	
//	/**
//	 * 从指定的文件中读取配置信息，并建立数据库连接
//	 * 此方法一般用于存在多个连接池的情况
//	 * @param configureFile
//	 * @return
//	 */
//	public static R2Pool getPool(File configureFile){
//		if(!configureFile.exists()||!configureFile.canRead()){
//			logger.error("can not load configure file:"+configureFile.getAbsolutePath());
//			return null;
//		}
//		R2Pool pool = new R2Pool();
//		try {
//			FileInputStream fis = new FileInputStream(configureFile);
//			readConfiguration(pool, fis);
//			fis.close();
//		} catch (FileNotFoundException e) {
//			logger.error("configure file not found:"+configureFile.getAbsolutePath());
//		} catch (IOException e) {
//			logger.error("close properties file stream error", e);
//		}
//		return pool;
//	}
//	
//}
