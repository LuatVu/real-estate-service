package com.realestate.utilities;
import java.text.Normalizer;

public class StringUtils {
    public static String removeAccents(String origin){
        if(origin == null) return "";
        String result = Normalizer.normalize(origin, Normalizer.Form.NFKD);
        result = result.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return result;
    }

    public static String createKeyWords(String... content){
        StringBuilder strBuilder = new StringBuilder();
        for(String str : content){
            if(str != null){
                strBuilder.append(" ");
                strBuilder.append(removeAccents(str));
            }            
        }
        return strBuilder.toString();
    }
}
