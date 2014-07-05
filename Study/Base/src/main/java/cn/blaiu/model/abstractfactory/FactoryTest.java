/**
 * 
 */
package cn.blaiu.model.abstractfactory;

/**
 * @author blaiu
 *
 */
public class FactoryTest {

	public static void main(String[] args) {
		MailFactory mailfactory = new MailFactory();
		mailfactory.produce().sender();
		
		MsgFactory msgfactory = new MsgFactory();
		msgfactory.produce().sender();
	}
}
