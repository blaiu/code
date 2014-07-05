/**
 * 
 */
package cn.blaiu.model.adapter;

/**
 * @author blaiu
 *
 */
public class Wrapper implements Targetable {

	private Source source;
	
	public Wrapper(Source source) {
		this.source = source;
	}
	
	@Override
	public void method1() {
		System.out.println("this is the targetable method!");
		
	}

	@Override
	public void method2() {
		source.method1();
		
	}

}
