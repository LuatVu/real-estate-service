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

    public static String normalizeVietnameseName(String name) {
        // Remove Vietnamese diacritics and convert to lowercase for proper sorting
        return name.toLowerCase()
                .replace("à", "a").replace("á", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a")
                .replace("ă", "a").replace("ằ", "a").replace("ắ", "a").replace("ẳ", "a").replace("ẵ", "a")
                .replace("ặ", "a")
                .replace("â", "a").replace("ầ", "a").replace("ấ", "a").replace("ẩ", "a").replace("ẫ", "a")
                .replace("ậ", "a")
                .replace("è", "e").replace("é", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e")
                .replace("ê", "e").replace("ề", "e").replace("ế", "e").replace("ể", "e").replace("ễ", "e")
                .replace("ệ", "e")
                .replace("ì", "i").replace("í", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i")
                .replace("ò", "o").replace("ó", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o")
                .replace("ô", "o").replace("ồ", "o").replace("ố", "o").replace("ổ", "o").replace("ỗ", "o")
                .replace("ộ", "o")
                .replace("ơ", "o").replace("ờ", "o").replace("ớ", "o").replace("ở", "o").replace("ỡ", "o")
                .replace("ợ", "o")
                .replace("ù", "u").replace("ú", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u")
                .replace("ư", "u").replace("ừ", "u").replace("ứ", "u").replace("ử", "u").replace("ữ", "u")
                .replace("ự", "u")
                .replace("ỳ", "y").replace("ý", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y")
                .replace("đ", "d");
    }
}
