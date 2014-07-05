/**
 * 
 */
package cn.blaiu.model.singleton;

/**
 * @author blaiu
 *
 */
public class Singleton {

	private static Singleton instance = null;
	
	private Singleton () {}
	
	public static Singleton getInstance () {
//		if (null == instance) {
//			synchronized (instance) {
//				return new Singleton();
//			}
//		}
//		
//		return instance;
		
		return SingletonFactory.instance;
	} 
	
	/**
	 * 使用静态内部类维护单例
	 * @author blaiu
	 *
	 */
	public static class SingletonFactory {
		private static Singleton instance = new Singleton();
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
    public Object readResolve() {  
        return instance;  
    }
    
    private static synchronized void syncInit() {  
        if (instance == null) {  
            instance = new Singleton();  
        }  
    }  
  
    public static Singleton getInstance2() {  
        if (instance == null) {  
            syncInit();  
        }  
        return instance;  
    }
}
