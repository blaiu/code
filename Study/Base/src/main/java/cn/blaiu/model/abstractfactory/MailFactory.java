/**
 * 
 */
package cn.blaiu.model.abstractfactory;

/**
 * @author blaiu
 *
 */
public class MailFactory implements Provider {

	@Override
	public Sender produce() {
		return new MailSender();
	}
	
}
