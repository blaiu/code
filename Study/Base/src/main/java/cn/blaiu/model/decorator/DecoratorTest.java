/**
 * 
 */
package cn.blaiu.model.decorator;

/**
 * @author blaiu
 *
 */
public class DecoratorTest {

	public static void main(String[] args) {
		Sourceable source = new Source();  
        Sourceable obj = new Decorator(source);  
        obj.method();
	}
}
