package org.example.servlet.dto.converter;

public interface Converter<S, T> {
    T convert(S source);
}
