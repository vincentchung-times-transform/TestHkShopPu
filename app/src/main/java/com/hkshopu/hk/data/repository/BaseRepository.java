package com.hkshopu.hk.data.repository;


import com.hkshopu.hk.Base.response.function.PageSuccessFunc;
import com.hkshopu.hk.Base.response.function.SuccessFunc;
import com.hkshopu.hk.data.bean.BaseResponse;
import com.hkshopu.hk.data.bean.ListBean;

import java.util.List;

import io.reactivex.ObservableTransformer;

/**
 * @Author: YangYang
 * @Date: 2018/1/10
 * @Version: 1.0.0
 * @Description:
 */
public class BaseRepository {

    //response

    /**
     * 判读请求是否成功+解密+转为List的集合
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseResponse<ListBean<T>>, ListBean<T>> handleListBean(int page, int pageSize) {
        return upstream -> upstream.map(new PageSuccessFunc<>(page, pageSize));
    }

    /**
     * 判读请求是否成功+解密+转为List的集合
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseResponse<List<T>>, List<T>> handleList() {
        return upstream ->
                upstream.map(new SuccessFunc<>());
    }

    /**
     * 判读请求是否成功+解密+转为Bean的集合
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> handleBean() {
        return upstream ->
                upstream.map(new SuccessFunc<>());
    }

    /**
     * 判读请求是否成功+解密+转为Bean的集合
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> handleBean(T defaultValue) {
        return upstream ->
                upstream.map(tBaseResponse -> {
                    tBaseResponse.setRet_val(defaultValue);
                    return tBaseResponse;
                }).map(new SuccessFunc<T>());
    }
//
//    /**
//     * 判读请求是否成功+解密+转为Bean的集合
//     *
//     * @param <T>
//     * @return
//     */
//    public static <T> ObservableTransformer<BaseResponse<T>, T> handleBean() {
//        return upstream -> upstream.map(new SuccessFunc<>());
//    }


}
