package controllers.stock;
import java.io.UnsupportedEncodingException;  
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.avaje.ebean.Ebean;

import play.mvc.Result;
import controllers.AppController;
import models.stock.Stock;
  
/** 
 * 取得给定汉字串的首字母串,即声母串 
 * Title: ChineseCharToEn 
 * @date 2004-02-19 注：只支持GB2312字符集中的汉字 
 */  
public final class StockChineseToPinyin extends AppController{  
//    private final static int[] li_SecPosValue = { 1601, 1637, 1833, 2078, 2274,  
//            2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,  
//            4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590 };  
//    private final static String[] lc_FirstLetter = { "a", "b", "c", "d", "e",  
//            "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",  
//            "t", "w", "x", "y", "z" };  
//  
//    /** 
//     * 取得给定汉字串的首字母串,即声母串 
//     * @param str 给定汉字串 
//     * @return 声母串 
//     */  
//    public String getAllFirstLetter(String str) {  
//        if (str == null || str.trim().length() == 0) {  
//            return "";  
//        }  
//  
//        String _str = "";  
//        for (int i = 0; i < str.length(); i++) {  
//            _str = _str + this.getFirstLetter(str.substring(i, i + 1));  
//        }  
//  
//        return _str;  
//    }  
//  
//    /** 
//     * 取得给定汉字的首字母,即声母 
//     * @param chinese 给定的汉字 
//     * @return 给定汉字的声母 
//     */  
//    public String getFirstLetter(String chinese) {  
//        if (chinese == null || chinese.trim().length() == 0) {  
//            return "";  
//        }  
//        chinese = this.conversionStr(chinese, "GB2312", "ISO8859-1");  
//  
//        if (chinese.length() > 1) // 判断是不是汉字  
//        {  
//            int li_SectorCode = (int) chinese.charAt(0); // 汉字区码  
//            int li_PositionCode = (int) chinese.charAt(1); // 汉字位码  
//            li_SectorCode = li_SectorCode - 160;  
//            li_PositionCode = li_PositionCode - 160;  
//            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; // 汉字区位码  
//            if (li_SecPosCode > 1600 && li_SecPosCode < 5590) {  
//                for (int i = 0; i < 23; i++) {  
//                    if (li_SecPosCode >= li_SecPosValue[i]  
//                            && li_SecPosCode < li_SecPosValue[i + 1]) {  
//                        chinese = lc_FirstLetter[i];  
//                        break;  
//                    }  
//                }  
//            } else // 非汉字字符,如图形符号或ASCII码  
//            {  
//                chinese = this.conversionStr(chinese, "ISO8859-1", "GB2312");  
//                chinese = chinese.substring(0, 1);  
//            }  
//        }  
//  
//        return chinese;  
//    }  
//  
//    /** 
//     * 字符串编码转换 
//     * @param str 要转换编码的字符串 
//     * @param charsetName 原来的编码 
//     * @param toCharsetName 转换后的编码 
//     * @return 经过编码转换后的字符串 
//     */  
//    private String conversionStr(String str, String charsetName,String toCharsetName) {  
//        try {  
//            str = new String(str.getBytes(charsetName), toCharsetName);  
//        } catch (UnsupportedEncodingException ex) {  
//            System.out.println("字符串编码转换异常：" + ex.getMessage());  
//        }  
//        return str;  
//    }  
//  
//    public Result pinyin(){  
//    	StockChineseToPinyin cte = new StockChineseToPinyin();  
//    	List<Stock> allList = Stock.find.all();
//    	int size = allList.size();
//    	for(int i=0; i<size; i++ ){
//      //  System.out.println("获取拼音首字母："+ cte.getAllFirstLetter(allList.get(i).stockName));  
//        allList.get(i).pinyin = cte.getAllFirstLetter(allList.get(i).stockName);
//        Ebean.update(allList.get(i));
//    	}
//      return ok();	
//    }  
 //   鑫 孚  圳  钴  钽 晖     鹭 獐 锝  螳螂 浔 钛 	濮  琚  柘 钼 怡 昊 宸 睿 岘 癀 琪岷
	public static String getPingYin(String src) {
        char[] t1 = null;
        t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                if (java.lang.Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    t4 += t2[0];
                } else {
                    t4 += java.lang.Character.toString(t1[i]);
                }
            }
            return t4;
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
        }
        return t4;
    }

    /**
     * 得到中文首字母
     * 
     * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str) {

        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }

    /**
     * 将字符串转移为ASCII码
     * 
     * @param cnStr
     * @return
     */
    public static String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            // System.out.println(Integer.toHexString(bGBK[i]&0xff));
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }

    public Result newChinese() {
        List<Stock> allList = Stock.find.all();
        int size = allList.size();
        for(int i=0; i<size; i++){
        String cnStr = allList.get(i).stockName;
       // System.out.println(getPingYin(cnStr));
        allList.get(i).pinyin = getPinYinHeadChar(cnStr);
        Ebean.update(allList.get(i));

        }
        return ok();
        }
}  

