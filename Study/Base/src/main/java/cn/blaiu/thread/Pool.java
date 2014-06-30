/**
 * 
 */
package cn.blaiu.thread;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author blaiu
 *
 */
public class Pool {

	/** 线程队列池 */
	private ConcurrentLinkedQueue<BConnection> pool = null;
	
	/** 当前使用线程池 */
	private ConcurrentHashMap<Connection, BConnection> usingPool = null;
	
	private AtomNumber currentSize = new AtomNumber(0);
	private String driver = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://192.168.195.57:3306/test";
	private String user = "root";
	private String password = "000000";
	
	/** 最大连接数 */
	private int	maxSize = 5;
	
	/** 最大连接数 */
	private int minSize = 2;
	
	/** 初始化连接数 */
	private int initSize = 2;
	
	/** 增长大小 */
	private int increment = 2;
	
	/** 链接超时时间 */
	private long timeout = 120000;
	
	/** 清理线程的时间间隔 */
	private long idleTestPeriod = 120000;
	
	/** 是否管斌连接 */
	private boolean closeIdleConnection = true;
	
	/** 线程最大空闲时间 */
	private long maxIdleTime = 300000;
	
	/** 重试连接不能访问服务器 */
	private int retryTimesWhileCanNotConnectServer = -1;
	
	/** 保持连接激活的SQL */
	private String keepAliveSql = "select 1;";
	
	/** 最大使用时间 */
	private long maxUsingTime = -1;
	
	private boolean closed = false;
	
	/** 清理线程 */
	private Thread cleanPoolThread;
	
	/** 清理线程池标识 */
	private boolean keepClean = true;
	
	class AtomNumber {
		
		int num = 0;
		
		public AtomNumber() {
			num = 0;
		}
		
		public AtomNumber(int n) {
			num = 0;
		}
		
		public void inc (int n) {
			synchronized (this) {
				num += n;
			}
		}
		
		public void dec (int n) {
			synchronized (this) {
				num -= n;
			}
		}
		
		public int getValue () {
			return num;
		}
		
		public void setValue (int n) {
			synchronized (this) {
				num = n;
			}
		}
	}
	
	/**
	 * 得到当前池中正在使用的链接数
	 * @return
	 */
	public int getCurrentSize () {
		return currentSize.getValue();
	}
	
	/**
	 * 得到当前池中正在使用的连接的数量
	 * @return
	 */
	public int getCurrentUsingPoolSize(){
		return usingPool.size();
	}
	
	/**
	 * 得到当前池中未被使用的连接的数量
	 * @return
	 */
	public int getCurrentFreePoolSize(){
		return pool.size();
	}
	
	
	
	/**
	 * 初始化连接池，增加池中连接，设置清理线程
	 */
	public void init() {
		pool = new ConcurrentLinkedQueue<BConnection>();
		usingPool = new ConcurrentHashMap<Connection, BConnection>();
		closed = false;
		currentSize.setValue(0);
		increasePool(pool, initSize);
		
		//防止重复条用，造成多个清理线程
		if (null != cleanPoolThread) {
			keepClean = false;
			cleanPoolThread.interrupt();	//终短当前线程
			
			try {
				Thread.sleep(idleTestPeriod + 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			keepClean = true;
		}
		
		cleanPoolThread = new Thread("pool cleaner") {

			@Override
			public void run() {
				while (keepClean) {
					try {
						Thread.sleep(idleTestPeriod);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					//清理池中连接
					int availableNum = pool.size();
					BConnection conn = null;
					
					for (int i = 0; i < availableNum; i++) {
						pool.poll();
						if (null == conn) {
							break;	//池中已无队列可用
						}
						
						long now = Calendar.getInstance().getTimeInMillis();
						
						//空闲时间过长时关闭连接
						if (closeIdleConnection && (conn.getLastUsingTime() + maxIdleTime < now) && currentSize.getValue() > minSize) {
							try {
								conn.closeJdbcConnection();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							currentSize.dec(1);
						} else {
							//以下代码保证池中的连接可用
							int j = 0;
							while (j < retryTimesWhileCanNotConnectServer || retryTimesWhileCanNotConnectServer < 0) {
								PreparedStatement pstmt;
								try {
									pstmt = conn.prepareStatement(keepAliveSql);
									pstmt.execute();
									pstmt.close();
									break;
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								DriverManager.setLoginTimeout((int)(timeout / 1000));
								
								try {
									Connection jdbcConn = DriverManager.getConnection(url, user, password);
									conn.setConn(jdbcConn);
									break;
								} catch (SQLException e) {
									e.printStackTrace();
								}
								++j;
							}
							pool.add(conn);
						}
					}
					
					//清理长期为释放的连接
					Iterator<Map.Entry<Connection, BConnection>> it = usingPool.entrySet().iterator();
					while (it.hasNext()) {
						BConnection bconn = it.next().getValue();
						long now = Calendar.getInstance().getTimeInMillis();
						if (maxUsingTime > 0 && bconn.getLastUsingTime() + maxUsingTime < now) {
							try {
								bconn.closeConnectionAndRelease();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		};
		cleanPoolThread.start();
	}
	
	private void increasePool(ConcurrentLinkedQueue<BConnection> pool, int size) {
		try {
			Class.forName(driver);
			for (int i = 0; currentSize.getValue() < maxSize && i < size; i++) {
				Connection jdbcConn = DriverManager.getConnection(url, user, password);
				release(jdbcConn);
				currentSize.inc(1);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void newConnection(Connection jdbcConn) {
		usingPool.remove(jdbcConn);
		BConnection conn = new BConnection();
		conn.setPool(this);
		DriverManager.setLoginTimeout((int)(timeout/1000));
		try {
			jdbcConn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		conn.setConn(jdbcConn);
		conn.setLastUsingTime(Calendar.getInstance().getTimeInMillis());
		pool.add(conn);
	}
	
	
	/**
	 * 释放连接。将jdbc放回连接池,移除已使用的链接
	 * @param conn
	 */
	public void release (Connection conn) {
		usingPool.remove(conn);
		BConnection connection = new BConnection();
		connection.setPool(this);
		connection.setConn(connection);
		connection.setLastUsingTime(Calendar.getInstance().getTimeInMillis());
		pool.add(connection);
	}
	
}
