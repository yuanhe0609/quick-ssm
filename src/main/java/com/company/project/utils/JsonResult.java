package com.company.project.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonResult {
    private int code;
    private String msg;
    private Object data;

    public JsonResult(int code) {
        this.code = code;
    }

    public JsonResult(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public JsonResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
