package com.igweze.ebi.simplecalladapter;

public interface SimpleHandler<T> {
    void accept(T response, Throwable throwable);
}