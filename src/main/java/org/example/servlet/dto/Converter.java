package org.example.servlet.dto;

public interface Converter<S, T> {
    T convert(S source);
}
