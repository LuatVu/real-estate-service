package com.realestate.utilities;

public class EnumUtils {
    // convert string to enum
    public static <T extends Enum<T>> T fromString(Class<T> enumType, String value) {
        if(value == null){
            return null;
        }
        for (T enumConstant : enumType.getEnumConstants()) {
            if (enumConstant.name().equalsIgnoreCase(value)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value + " for enum " + enumType.getSimpleName());
    }
}
