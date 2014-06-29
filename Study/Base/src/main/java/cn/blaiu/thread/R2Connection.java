//package com.mail.util;
//
//import java.sql.CallableStatement;
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.SQLWarning;
//import java.sql.Savepoint;
//import java.sql.Statement;
//import java.util.Map;
///**
// * R2 jdbc connection class, to replace the default jdbc connection and replace the close function
// * R2连接池，链接包装类，处理close方法
// * @author 张人杰 北京师范大学 计算机系
// * alex.zhangrj@hotmail.com
// * alex.zhangrj Beijing Normal University
// * 2011/11/26
// */
//public class R2Connection implements Connection {
//
//	private long lastUsingTime=0;//开始使用此链接的时间
//	private Connection conn=null;
//	private R2Pool pool=null;
//	
//	public void setJdbcConnection(Connection jdbcConn){
//		this.conn=jdbcConn;
//	}
//	public Connection getJdbcConnection(){
//		return this.conn;
//	}
//	public void setPool(R2Pool pool){
//		this.pool=pool;
//	}
//
//	public long getLastUsingTime() {
//		return lastUsingTime;
//	}
//	public void setLastUsingTime(long lastUsingTime) {
//		this.lastUsingTime = lastUsingTime;
//	}
//	public void clearWarnings() throws SQLException {
//		if(conn!=null)conn.clearWarnings();
//	}
//
//	public void close() throws SQLException {//关闭链接时，不实际关闭Connection，而把其放入连接池中
//		synchronized(this){
//			Connection c = conn;//防止一个conn被多个线程使用的情况
//			conn=null;
//			if(c!=null){
//				try {
//					c.rollback();
//				} catch (Exception e) {
//				}
//				pool.releaseConnection(c);
//			}
//		}
//	}
//	/**
//	 * 关闭jdbc真实链接
//	 * @throws SQLException
//	 */
//	public void closeJdbcConnection() throws SQLException {//关闭jdbc真实链接
//		if(conn!=null)conn.close();
//		conn=null;
//	}
//	/**
//	 * 关闭jdbc真实链接并放回池中，保持连接数
//	 * @throws SQLException
//	 */
//	public void closeJdbcConnectionAndRelease() throws SQLException {//关闭jdbc真实链接
//		synchronized(this){
//			Connection c = conn;//防止一个conn被多个线程使用的情况
//			conn=null;
//			if(c!=null){
//				try {
//					c.close();
//				} catch (Exception e) {
//				}
//				pool.newConnection(c);
//			}
//		}
//	}
//
//	public void commit() throws SQLException {
//		if(conn!=null)conn.commit();
//	}
//
////	public Array createArrayOf(String typeName, Object[] elements)
////			throws SQLException {
////		if(conn!=null)return conn.createArrayOf(typeName, elements);
////		return null;
////	}
//
////	public Blob createBlob() throws SQLException {
////		if(conn!=null)return conn.createBlob();
////		return null;
////	}
////
////	public Clob createClob() throws SQLException {
////		if(conn!=null)return conn.createClob();
////		return null;
////	}
////
////	public NClob createNClob() throws SQLException {
////		if(conn!=null)return conn.createNClob();
////		return null;
////	}
////
////	public SQLXML createSQLXML() throws SQLException {
////		if(conn!=null)return conn.createSQLXML();
////		return null;
////	}
//
//	public Statement createStatement() throws SQLException {
//		if(conn!=null)return conn.createStatement();
//		return null;
//	}
//
//	public Statement createStatement(int resultSetType, int resultSetConcurrency)
//			throws SQLException {
//		if(conn!=null)return conn.createStatement(resultSetType,resultSetConcurrency);
//		return null;
//	}
//
//	public Statement createStatement(int resultSetType,
//			int resultSetConcurrency, int resultSetHoldability)
//			throws SQLException {
//		if(conn!=null)return conn.createStatement(resultSetType,resultSetConcurrency,resultSetHoldability);
//		return null;
//	}
//
////	public Struct createStruct(String typeName, Object[] attributes)
////			throws SQLException {
////		if(conn!=null)return conn.createStruct(typeName,attributes);
////		return null;
////	}
//
//	public boolean getAutoCommit() throws SQLException {
//		if(conn!=null)return conn.getAutoCommit();
//		return false;
//	}
//
//	public String getCatalog() throws SQLException {
//		if(conn!=null)return conn.getCatalog();
//		return null;
//	}
//
////	public Properties getClientInfo() throws SQLException {
////		if(conn!=null)return conn.getClientInfo();
////		return null;
////	}
////
////	public String getClientInfo(String name) throws SQLException {
////		if(conn!=null)return conn.getClientInfo(name);
////		return null;
////	}
//
//	public int getHoldability() throws SQLException {
//		if(conn!=null)return conn.getHoldability();
//		return 0;
//	}
//
//	public DatabaseMetaData getMetaData() throws SQLException {
//		if(conn!=null)return conn.getMetaData();
//		return null;
//	}
//
//	public int getTransactionIsolation() throws SQLException {
//		if(conn!=null)return conn.getTransactionIsolation();
//		return 0;
//	}
//
//	public Map<String, Class<?>> getTypeMap() throws SQLException {
//		if(conn!=null)return conn.getTypeMap();
//		return null;
//	}
//
//	public SQLWarning getWarnings() throws SQLException {
//		if(conn!=null)return conn.getWarnings();
//		return null;
//	}
//
//	public boolean isClosed() throws SQLException {
//		if(conn!=null)return conn.isClosed();
//		return false;
//	}
//
//	public boolean isReadOnly() throws SQLException {
//		if(conn!=null)return conn.isReadOnly();
//		return false;
//	}
//
////	public boolean isValid(int timeout) throws SQLException {
////		if(conn!=null)return conn.isValid(timeout);
////		return false;
////	}
//
//	public String nativeSQL(String sql) throws SQLException {
//		if(conn!=null)return conn.nativeSQL(sql);
//		return null;
//	}
//
//	public CallableStatement prepareCall(String sql) throws SQLException {
//		if(conn!=null)return conn.prepareCall(sql);
//		return null;
//	}
//
//	public CallableStatement prepareCall(String sql, int resultSetType,
//			int resultSetConcurrency) throws SQLException {
//		if(conn!=null)return conn.prepareCall(sql,resultSetType,resultSetConcurrency);
//		return null;
//	}
//
//	public CallableStatement prepareCall(String sql, int resultSetType,
//			int resultSetConcurrency, int resultSetHoldability)
//			throws SQLException {
//		if(conn!=null)return conn.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
//		return null;
//	}
//
//	public PreparedStatement prepareStatement(String sql) throws SQLException {
//		if(conn!=null)return conn.prepareStatement(sql);
//		return null;
//	}
//
//	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
//			throws SQLException {
//		if(conn!=null)return conn.prepareStatement(sql, autoGeneratedKeys);
//		return null;
//	}
//
//	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
//			throws SQLException {
//		if(conn!=null)return conn.prepareStatement(sql, columnIndexes);
//		return null;
//	}
//
//	public PreparedStatement prepareStatement(String sql, String[] columnNames)
//			throws SQLException {
//		if(conn!=null)return conn.prepareStatement(sql, columnNames);
//		return null;
//	}
//
//	public PreparedStatement prepareStatement(String sql, int resultSetType,
//			int resultSetConcurrency) throws SQLException {
//		if(conn!=null)return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
//		return null;
//	}
//
//	public PreparedStatement prepareStatement(String sql, int resultSetType,
//			int resultSetConcurrency, int resultSetHoldability)
//			throws SQLException {
//		if(conn!=null)return conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
//		return null;
//	}
//
//	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
//		if(conn!=null)conn.releaseSavepoint(savepoint);
//	}
//
//	public void rollback() throws SQLException {
//		if(conn!=null)conn.rollback();
//	}
//
//	public void rollback(Savepoint savepoint) throws SQLException {
//		if(conn!=null)conn.rollback(savepoint);
//	}
//
//	public void setAutoCommit(boolean autoCommit) throws SQLException {
//		if(conn!=null)conn.setAutoCommit(autoCommit);
//	}
//
//	public void setCatalog(String catalog) throws SQLException {
//		if(conn!=null)conn.setCatalog(catalog);
//	}
//
//	public void setClientInfo(Properties properties)
//			throws SQLClientInfoException {
//		if(conn!=null)conn.setClientInfo(properties);
//	}
//
//	public void setClientInfo(String name, String value)
//			throws SQLClientInfoException {
//		if(conn!=null)conn.setClientInfo(name, value);
//	}
//
//	public void setHoldability(int holdability) throws SQLException {
//		if(conn!=null)conn.setHoldability(holdability);
//	}
//
//	public void setReadOnly(boolean readOnly) throws SQLException {
//		if(conn!=null)conn.setReadOnly(readOnly);
//	}
//
//	public Savepoint setSavepoint() throws SQLException {
//		if(conn!=null)return conn.setSavepoint();
//		return null;
//	}
//
//	public Savepoint setSavepoint(String name) throws SQLException {
//		if(conn!=null)return conn.setSavepoint(name);
//		return null;
//	}
//
//	public void setTransactionIsolation(int level) throws SQLException {
//		if(conn!=null)conn.setTransactionIsolation(level);
//	}
//
//	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
//		if(conn!=null)conn.setTypeMap(map);
//	}
//
//	public boolean isWrapperFor(Class<?> iface) throws SQLException {
//		if(conn!=null)return conn.isWrapperFor(iface);
//		return false;
//	}
//
//	public <T> T unwrap(Class<T> iface) throws SQLException {
//		if(conn!=null)return conn.unwrap(iface);
//		return null;
//	}
//	@Override
//	protected void finalize() throws Throwable {
//		super.finalize();
//		close();
//	}
//}
