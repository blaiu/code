/**
 * 
 */
package cn.blaiu.model.proxy;

/**
 * @author blaiu
 *
 */
public class ProxyTest {

	public static void main(String[] args) {
		Sourceable source = new Proxy();  
        source.method();
	}
}
