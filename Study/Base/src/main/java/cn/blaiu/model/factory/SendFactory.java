/**
 * 
 */
package cn.blaiu.model.factory;

/**
 * @author blaiu
 * <P> 就是建立一个工厂类，对实现了同一接口的一些类进行实例的创建
 */
public class SendFactory {

	public Sender produce (String type) {
		if ("mail".equals(type)) {
			return new MailSender();
		} else if ("msg".equals(type)){
			return new MsgSender();
		} else {
			System.out.println("sorry， I don't know. ");
			return null;
		}
	}
	
	public Sender produceMail () {
		return new MailSender();
	}
	
	public Sender produceMsg () {
		return new MsgSender();
	}
	
	
	public static Sender produceStaticMail () {
		return new MailSender();
	}
	
	public static Sender produceStaticMsg () {
		return new MsgSender();
	}
	
}
