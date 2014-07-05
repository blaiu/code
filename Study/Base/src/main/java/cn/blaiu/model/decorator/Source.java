/**
 * 
 */
package cn.blaiu.model.decorator;

/**
 * @author blaiu
 *
 */
public class Source implements Sourceable {

	@Override
	public void method() {
		System.out.println("the original method!");
	}

}
