/**
 * 
 */
package cn.blaiu.model.factory;

/**
 * @author blaiu
 *
 */
public class MsgSender implements Sender {

	/* (non-Javadoc)
	 * @see cn.blaiu.model.factory.Sender#sender()
	 */
	@Override
	public void sender() {
		System.out.println("this is msg");
	}

}
