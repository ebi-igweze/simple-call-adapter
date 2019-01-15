package com.igweze.ebi.simplecalladapter;

public interface Handler<T> {
    void accept(T response, Throwable throwable);
}