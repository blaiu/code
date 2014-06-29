//package com.mail.util;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.Calendar;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//import org.apache.log4j.Logger;
//
///**
// * R2 jdbc connection pool
// * R2连接池，连接池类
// * @author 张人杰 北京师范大学 计算机系
// * alex.zhangrj@hotmail.com
// * alex.zhangrj Beijing Normal University
// * 2011/11/26
// */
//public class R2Pool {
//	private ConcurrentLinkedQueue<R2Connection> pool=null;
//	private ConcurrentHashMap<Connection,R2Connection> usingPool=null;
//	private String driver="oracle.jdbc.driver.OracleDriver";
//	private String url="jdbc:oracle:thin:@172.29.128.77:1521:orcl";
//	private String user="hisapectest";
//	private String password="hisapectest";
//	private int	maxSize=5;
//	private int minSize=2;
//	private int initSize=2;
//	private int increment=2;
//	private long timeout=120000;
//	private long idleTestPeriod=120000;
//	private String keepAliveSql="select 1;";
//	private boolean closeIdleConnection=true;
//	private long maxIdleTime=300000;
//	private int retryTimesWhileGetNullConnection=-1;
//	private long retryDurationDuringGetNullConnection=1000;
//	private int retryTimesWhileCanNotConnectServer=-1;
//	private long retryDurationDuringConnectingServer=1000;
//	private long maxUsingTime=-1;
//	
//	private AtomNumber currentSize=new AtomNumber(0); 
//	private Thread cleanPoolThread;
//	private boolean keepClean=true;
//	private boolean closed=false;
//	private Logger logger=Logger.getLogger(R2Pool.class);
//	
//	
//	class AtomNumber{
//		int num=0;
//		public AtomNumber(){
//			num=0;
//		}
//		public AtomNumber(int n){
//			num=n;
//		}
//		public void inc(int n){
//			synchronized (this) {
//				num+=n;
//			}
//		}
//		public void dec(int n){
//			synchronized (this) {
//				num-=n;
//			}
//		}
//		public int getValue(){
//			return num;
//		}
//		public void setValue(int n){
//			synchronized (this) {
//				num=n;
//			}
//		}
//	}
//	/**
//	 * 得到当前池中的连接数量
//	 * @return
//	 */
//	public int getCurrentSize(){
//		return currentSize.getValue();
//	}
//	
//	/**
//	 * 得到当前池中正在使用的连接的数量
//	 * @return
//	 */
//	public int getCurrentUsingPoolSize(){
//		return usingPool.size();
//	}
//	/**
//	 * 得到当前池中未被使用的连接的数量
//	 * @return
//	 */
//	public int getCurrentFreePoolSize(){
//		return pool.size();
//	}
//	
//	/**
//	 * 初始化R2连接池，增加池中链接，设置清理线程。
//	 * 注意：
//	 * 1、 此方法只可调用一次,若意外被调用多次，则原有jdbc连接会在Connection对象被gc时释放，释放时间较长。
//	 * 2、在开始使用连接池后调用此方法，则可能造成连接数超过配置项最大连接数maxSize
//	 */
//	public void init(){
//		pool=new ConcurrentLinkedQueue<R2Connection>();
//		usingPool=new ConcurrentHashMap<Connection, R2Connection>();
//		closed=false;
//		currentSize.setValue(0);
//		increasePool(pool, initSize);
//		if(cleanPoolThread!=null){//防止重复调用，造成多个清理线程
//			keepClean=false;
//			cleanPoolThread.interrupt();
//			try {
//				Thread.sleep(idleTestPeriod+1000);
//			} catch (InterruptedException e) {
//				logger.info("reinitial pool error,interrupted while waiting the old clean thread to close.", e);
//			}
//			logger.info("the old cleanPoolThread is closed.");
//			keepClean=true;
//		}
//		cleanPoolThread=new Thread("R2Pool cleaner"){
//			@Override
//			public void run() {
//				while(keepClean){
//					logger.info("clean...");
//					try {
//						Thread.sleep(idleTestPeriod);
//					} catch (InterruptedException e) {
//						logger.info("clean pool thread sleep error", e);
//					}
//					//清理池中连接
//					int availableNum=pool.size();//目前池中拥有的连接数
//					R2Connection conn=null;
//					for(int i=0;i<availableNum;i++){
//						conn=pool.poll();
//						if(conn==null)break;//池中已无可用连接
//						long now = Calendar.getInstance().getTimeInMillis();
//						if(closeIdleConnection&&(conn.getLastUsingTime()+maxIdleTime<now)&&currentSize.getValue()>minSize){//空闲时间过长则关闭连接
//							try {
//								conn.closeJdbcConnection();
//								conn=null;
//							} catch (Exception e) {
//								logger.info("close the real jdbc connection error", e);
//							}
//							currentSize.dec(1);
//						}else{
//							//以下代码保证池中的连接可用
//							int j=0;
//							while(j<retryTimesWhileCanNotConnectServer||retryTimesWhileCanNotConnectServer<=0){
//								PreparedStatement pstmt;
//								try {//检查连接是否正常打开
//									pstmt = conn.prepareStatement(keepAliveSql);
//									pstmt.execute();
//									pstmt.close();
//									break;
//								} catch (Exception e) {
//									logger.info("execute keepAliveSql error", e);
//								}//若未正常打开，则重新链接数据库，以保证返回的数据库连接可用
//								DriverManager.setLoginTimeout((int)(timeout/1000));
//								try {
//									Connection jdbcConn = DriverManager.getConnection(url, user, password);
//									conn.setJdbcConnection(jdbcConn);
//									break;
//								} catch (Exception e) {
//									logger.info("connecting server error,maybe user or password or url wrong,or the db is not available (timeout) at this time.", e);
//								}
//								try {
//									Thread.sleep(retryDurationDuringConnectingServer);
//								} catch (InterruptedException e) {
//									logger.info("retry sleep error while connectiing server.", e);
//								}
//								++j;
//							}
//							pool.add(conn);
//						}
//					}
//					logger.info("begin clean long time using connection...");
//					//清理长期未释放的连接
//					Set<Connection> keys = usingPool.keySet();
//					for(Connection key:keys){
//						R2Connection r2conn = usingPool.get(key);
//						long now = Calendar.getInstance().getTimeInMillis();
//						if(maxUsingTime>0&&r2conn.getLastUsingTime()+maxUsingTime<now){
//							try {
//								r2conn.closeJdbcConnectionAndRelease();
//								logger.info("a connection returned to the pool.");
//							} catch (SQLException e) {
//								logger.error("release connection error.");
//							}
//						}
//					}
//				}
//			}
//		};
//		cleanPoolThread.start();
//	}
//
//	/**
//	 * 增加R2连接池中连接的数量（由调用此方法的方法控制同步锁）
//	 * @param pool
//	 * @param size
//	 */
//	private void increasePool(ConcurrentLinkedQueue<R2Connection> pool,int size){
//		try {
//			Class.forName(driver);
//			for(int i=0;currentSize.getValue()<maxSize&&i<size;i++){
//				DriverManager.setLoginTimeout((int)(timeout/1000));
//				try {
//					Connection jdbcConn = DriverManager.getConnection(url, user, password);
//					releaseConnection(jdbcConn);
//					currentSize.inc(1);
//				} catch (Exception e) {
//					e.printStackTrace();
//					logger.info("getConnection error,maybe user or password or url wrong,or the db is not available (timeout) at this time.", e);
//				}
//			}
//		} catch (ClassNotFoundException e) {
//			logger.info("jdbc driver error", e);
//		}
//	}
//	/**
//	 * 释放连接（将jdbc连接放回R2连接池中）
//	 * @param jdbcConn
//	 */
//	public void releaseConnection(Connection jdbcConn){
//		usingPool.remove(jdbcConn);
//		R2Connection conn = new R2Connection();
//		conn.setPool(this);
//		conn.setJdbcConnection(jdbcConn);
//		conn.setLastUsingTime(Calendar.getInstance().getTimeInMillis());
//		pool.add(conn);
//	}
//	/**
//	 * 新建连接（用于长期占用的链接强制关闭后保持连接池中的链接数量）
//	 * @param jdbcConn
//	 */
//	public void	newConnection(Connection jdbcConn){
//		usingPool.remove(jdbcConn);
//		R2Connection conn = new R2Connection();
//		conn.setPool(this);
//		DriverManager.setLoginTimeout((int)(timeout/1000));
//		try {
//			jdbcConn = DriverManager.getConnection(url, user, password);
//		} catch (Exception e) {
//			logger.info("connecting server error,maybe user or password or url wrong,or the db is not available (timeout) at this time.", e);
//		}
//		conn.setJdbcConnection(jdbcConn);
//		conn.setLastUsingTime(Calendar.getInstance().getTimeInMillis());
//		pool.add(conn);
//	}
//	/**
//	 * 从R2连接池中获得连接
//	 * @return 经过包装处理close方法后的Connection对象
//	 */
//	public Connection getConnection(){
//		if(closed){//连接池已关闭
//			return null;
//		}
//		synchronized(this){//若连接池未初始化，则初始化
//			if(pool==null){
//				init();
//			}
//		}
//		R2Connection conn = pool.poll();
//		int i=0;
//		while(conn==null&&(i<retryTimesWhileGetNullConnection||retryTimesWhileGetNullConnection<=0)){//当retryTimes为-1时，保持重试
//			synchronized(this){//若连接池中连接已用完（返回的wrap为null），则增加连接池中连接数量
//				if(currentSize.getValue()<maxSize){
//					increasePool(pool, increment);
//				}
//			}
//			conn = pool.poll();
//			if(conn!=null)break;
//			try {
//				Thread.sleep(retryDurationDuringGetNullConnection);
//			} catch (InterruptedException e) {
//				logger.info("retry sleep error",e);
//			}
//			++i;
//		}
//		if(conn!=null){
//			int j=0;
//			while(j<retryTimesWhileCanNotConnectServer||retryTimesWhileCanNotConnectServer<=0){
//				PreparedStatement pstmt;
//				try {//检查连接是否正常打开
//					pstmt = conn.prepareStatement(keepAliveSql);
//					pstmt.execute();
//					pstmt.close();
//					break;
//				} catch (Exception e) {
//					logger.info("execute keepAliveSql error", e);
//				}//若未正常打开，则重新链接数据库，以保证返回的数据库连接可用
//				DriverManager.setLoginTimeout((int)(timeout/1000));
//				try {
//					Connection jdbcConn = DriverManager.getConnection(url, user, password);
//					conn.setJdbcConnection(jdbcConn);
//				} catch (Exception e) {
//					logger.info("connecting server error,maybe user or password or url wrong,or the db is not available (timeout) at this time.", e);
//				}
//				try {
//					Thread.sleep(retryDurationDuringConnectingServer);
//				} catch (InterruptedException e) {
//					logger.info("retry sleep error while connectiing server.", e);
//				}
//				++j;
//			}
//			try {
//				conn.setAutoCommit(true);
//			} catch (SQLException e) {
//				logger.error("connection set autocommit error");
//			}
//			usingPool.put(conn.getJdbcConnection(), conn);
//			return conn;
//		}else{
//			return null;
//		}
//	}
//	public void close(){
//		keepClean=false;
//		closed=true;
//	}
//
//	//以下为set，get方法
//	public String getDriver() {
//		return driver;
//	}
//
//	public void setDriver(String driver) {
//		this.driver = driver;
//	}
//
//	public String getUrl() {
//		return url;
//	}
//
//	public void setUrl(String url) {
//		this.url = url;
//	}
//
//	public String getUser() {
//		return user;
//	}
//
//	public void setUser(String user) {
//		this.user = user;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public int getMaxSize() {
//		return maxSize;
//	}
//
//	public void setMaxSize(int maxSize) {
//		this.maxSize = maxSize;
//	}
//
//	public int getMinSize() {
//		return minSize;
//	}
//
//	public void setMinSize(int minSize) {
//		this.minSize = minSize;
//	}
//
//	public int getInitSize() {
//		return initSize;
//	}
//
//	public void setInitSize(int initSize) {
//		this.initSize = initSize;
//	}
//
//	public int getIncrement() {
//		return increment;
//	}
//
//	public void setIncrement(int increment) {
//		this.increment = increment;
//	}
//
//	public long getTimeout() {
//		return timeout;
//	}
//
//	public void setTimeout(long timeout) {
//		this.timeout = timeout;
//	}
//
//	public long getIdleTestPeriod() {
//		return idleTestPeriod;
//	}
//
//	public void setIdleTestPeriod(long idleTestPeriod) {
//		this.idleTestPeriod = idleTestPeriod;
//	}
//
//	public String getKeepAliveSql() {
//		return keepAliveSql;
//	}
//
//	public void setKeepAliveSql(String keepAliveSql) {
//		this.keepAliveSql = keepAliveSql;
//	}
//
//	public boolean isCloseIdleConnection() {
//		return closeIdleConnection;
//	}
//
//	public void setCloseIdleConnection(boolean closeIdleConnection) {
//		this.closeIdleConnection = closeIdleConnection;
//	}
//
//	public long getMaxIdleTime() {
//		return maxIdleTime;
//	}
//
//	public void setMaxIdleTime(long maxIdleTime) {
//		this.maxIdleTime = maxIdleTime;
//	}
//
//	public int getRetryTimesWhileGetNullConnection() {
//		return retryTimesWhileGetNullConnection;
//	}
//
//	public void setRetryTimesWhileGetNullConnection(
//			int retryTimesWhileGetNullConnection) {
//		this.retryTimesWhileGetNullConnection = retryTimesWhileGetNullConnection;
//	}
//
//	public long getRetryDurationDuringGetNullConnection() {
//		return retryDurationDuringGetNullConnection;
//	}
//
//	public void setRetryDurationDuringGetNullConnection(
//			long retryDurationDuringGetNullConnection) {
//		this.retryDurationDuringGetNullConnection = retryDurationDuringGetNullConnection;
//	}
//
//	public int getRetryTimesWhileCanNotConnectServer() {
//		return retryTimesWhileCanNotConnectServer;
//	}
//
//	public void setRetryTimesWhileCanNotConnectServer(
//			int retryTimesWhileCanNotConnectServer) {
//		this.retryTimesWhileCanNotConnectServer = retryTimesWhileCanNotConnectServer;
//	}
//
//	public long getRetryDurationDuringConnectingServer() {
//		return retryDurationDuringConnectingServer;
//	}
//
//	public void setRetryDurationDuringConnectingServer(
//			long retryDurationDuringConnectingServer) {
//		this.retryDurationDuringConnectingServer = retryDurationDuringConnectingServer;
//	}
//
//	public long getMaxUsingTime() {
//		return maxUsingTime;
//	}
//
//	public void setMaxUsingTime(long maxUsingTime) {
//		this.maxUsingTime = maxUsingTime;
//	}
//}
