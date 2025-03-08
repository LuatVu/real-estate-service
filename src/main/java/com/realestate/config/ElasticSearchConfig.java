package com.realestate.config;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;



@Configuration
public class ElasticSearchConfig {
    @Bean
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(
            Arrays.asList(
                // new BigDecimalToStringConverter(),
                // new StringToBigDecimalConverter(),
                new LongToLocalDateTimeConverter(),
                new LocalDateTimeConverterToLong()
            )
        );
    }

    // Custom converters for BigDecimal
    public static class BigDecimalToStringConverter implements Converter<BigDecimal, String> {
        @Override
        public String convert(BigDecimal source) {
            return source.toString();
        }
    }

    public static class StringToBigDecimalConverter implements Converter<String, BigDecimal> {
        @Override
        public BigDecimal convert(String source) {
            return new BigDecimal(source);
        }
    }

    public static class LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime>{
        @Override
        public LocalDateTime convert(Long source){
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(source), 
                                TimeZone.getDefault().toZoneId());  
        }
    }

    public static class LocalDateTimeConverterToLong implements Converter<LocalDateTime, Long>{
        @Override
        public Long convert(LocalDateTime source){
            return source.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
        }
    }
}
