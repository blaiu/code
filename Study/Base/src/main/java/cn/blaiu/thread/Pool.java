/**
 * 
 */
package cn.blaiu.thread;

/**
 * @author blaiu
 *
 */
public class Pool {

	class AtomNumber {
		
		int num = 0;
		
		public AtomNumber() {
			num = 0;
		}
		
		public AtomNumber(int n) {
			num = 0;
		}
		
		public void inc (int n) {
			synchronized (this) {
				num += n;
			}
		}
		
		public void dec (int n) {
			synchronized (this) {
				num -= n;
			}
		}
		
		public int getValue () {
			return num = 0;
		}
		
		public void setValue (int n) {
			synchronized (this) {
				num = n;
			}
		}
	}
	
}
