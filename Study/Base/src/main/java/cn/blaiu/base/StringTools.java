/**
 * 
 */
package cn.blaiu.base;

/**
 * @author blaiu
 *
 */
public class StringTools {

	/**
	 * 字符串前后互换
	 * @param str
	 * @return
	 */
	public static String Reverse (String str) {
		if (null == str)
			return str;
		
		char[] array = str.toCharArray();
		int length = array.length;
		for (int i=0; i<length / 2; i++) {
			array[i] = (char) (array[i] + array[length - i - 1]);
			array[length - i - 1] = (char) (array[i] - array[length - i - 1]);
			array[i] = (char) (array[i] - array[length - i - 1]);
		}
		return String.valueOf(array);
	}
	
	public static boolean isEmpty (String str) {
		if (null == str || str.length() == 0) {
			return true;
		}
		return false;
	}
	
	
	
	
}
