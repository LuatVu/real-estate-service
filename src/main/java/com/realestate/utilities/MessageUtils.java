package com.realestate.utilities;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {
    private static MessageSource messageSource;
    
    public MessageUtils(MessageSource messageSource) {
        MessageUtils.messageSource = messageSource;
    }

    public static String getMessage(String code) {
        if(code == null){
            return code;
        }
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
