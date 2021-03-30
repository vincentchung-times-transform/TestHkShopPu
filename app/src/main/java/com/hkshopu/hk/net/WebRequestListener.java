package com.hkshopu.hk.net;

public interface WebRequestListener<T> {

    //    void Successful(List<T> list);
    void Successful(String string);

    void Fault(String string);
}
