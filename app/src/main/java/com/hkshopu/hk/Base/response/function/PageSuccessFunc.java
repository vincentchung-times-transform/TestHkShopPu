package com.hkshopu.hk.Base.response.function;

import com.hkshopu.hk.data.bean.BaseResponse;
import com.hkshopu.hk.data.bean.ListBean;
import com.hkshopu.hk.data.exception.RequestException;

import io.reactivex.functions.Function;

public class PageSuccessFunc<T> implements Function<BaseResponse<T>, T> {
    private int page;
    private int pageSize;

    public PageSuccessFunc(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    @Override
    public T apply(BaseResponse<T> tBaseResponse) throws Exception {
        if (tBaseResponse.getStatus() == 0) {
            T data = tBaseResponse.getRet_val();
            if (data == null){
                data = (T) new Object();
            }

            if (tBaseResponse.getRet_val() instanceof ListBean){
                ListBean<T> listBean = (ListBean<T>) tBaseResponse.getRet_val();
                listBean.setPageSize(pageSize);
                listBean.setPage(page);
            }
            return data;
        } else {
            throw (new RequestException(tBaseResponse.getStatus(), tBaseResponse.getMsg()));
        }
    }
}
