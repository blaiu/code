/**
 * 
 */
package cn.blaiu.model.factory;

/**
 * @author blaiu
 *
 */
public class FactoryTest {

	public static void main(String[] args) {
		SendFactory factory = new SendFactory();
		factory.produce("msg").sender();
		factory.produce("mail").sender();
		
		factory.produceMail().sender();
		factory.produceMsg().sender();
		
		//静态工厂模式
		SendFactory.produceStaticMail().sender();
		SendFactory.produceStaticMsg().sender();
	}
}
