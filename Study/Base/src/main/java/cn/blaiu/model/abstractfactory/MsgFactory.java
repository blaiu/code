/**
 * 
 */
package cn.blaiu.model.abstractfactory;

/**
 * @author blaiu
 *
 */
public class MsgFactory implements Provider {

	@Override
	public Sender produce() {
		return new MsgSender();
	}
	
}
