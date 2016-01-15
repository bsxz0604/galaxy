/** Author: Michael Wang
 * Date: 2015-06-26
 * Description: this class generates different code including uuid and md5.
 */

package controllers.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CodeGenerator {
	
	/* Function: GenerateUUId
	 * Parameters: None
	 * Description: returns a random uuid.
	 */
	
	public static java.security.SecureRandom random = new java.security.SecureRandom();
	
	private static long getBaseTime() {
		Calendar baseDate = Calendar.getInstance();
		baseDate.set(2015, 1, 1, 0, 0, 0);
		baseDate.set(Calendar.MILLISECOND, 0);		
		return baseDate.getTimeInMillis();
	}
	
	public static String GenerateUUId() {
		UUID uuid = UUID.randomUUID();
		
		return uuid.toString();
	}
	
	/* Function: GenerateUUId
	 * Parameters: str - a string that will convert to md5.
	 * Description: returns an md5 string
	 */
	public static String GenerateMD5(String str) throws NoSuchAlgorithmException {
		byte [] buf = str.getBytes();
        
		MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(buf);

        byte [] tmp = md5.digest();
        StringBuilder sb = new StringBuilder();

        for (byte b:tmp) {
        	sb.append(Integer.toHexString(b&0xff));
        }
        
        return sb.toString();        
	}
	
	public static String GenerateRandomNumber() throws NoSuchAlgorithmException {	
		int randomNum = (int)(random.nextDouble()*1000); 
		
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		long timeDiff = new Date().getTime() - getBaseTime();
		return String.format("%d%03d", timeDiff, randomNum);
	}
}
