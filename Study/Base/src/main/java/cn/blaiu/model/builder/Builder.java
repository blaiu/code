/**
 * 
 */
package cn.blaiu.model.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author blaiu
 *
 */
public class Builder {

	public List<Sender> list = new ArrayList<Sender>();
	
	public void produceMailSender(int count) {
		for (int i=0; i<50; i++) {
			list.add(new MailSender());
		}
	}
	
	public void produceMsgSender(int count) {
		for (int i=0; i<50; i++) {
			list.add(new MsgSender());
		}
	}
	
}
