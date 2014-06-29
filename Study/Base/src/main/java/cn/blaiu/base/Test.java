/**
 * 
 */
package cn.blaiu.base;

/**
 * @author blaiu
 *
 */
public class Test {

	public static void main(String[] args) {
		
//		int i = 0;
//		for (;;) {
//			i++;
//			System.out.println(i);
//		}
		
		String a = "asdksksos";
		String b = "";
		String c = " ";
		String d = null;
		
		System.out.println(StringTools.Reverse(a));
		System.out.println(StringTools.isEmpty(b));
		System.out.println(StringTools.isEmpty(c));
		System.out.println(StringTools.isEmpty(d));
		
	}
	
}
