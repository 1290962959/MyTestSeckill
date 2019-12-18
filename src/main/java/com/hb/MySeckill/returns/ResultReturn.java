package com.hb.MySeckill.returns;

public class ResultReturn<T> {
    private boolean status;
    private T data;

    public ResultReturn(boolean status, T data) {
        this.status = status;
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
