/**
 *******************************************************************************
 *                       Continental Confidential
 *                  Copyright (c) Continental AG. 2017
 *
 *      This software is furnished under license and may be used or
 *      copied only in accordance with the terms of such license.
 *******************************************************************************
* @file  Response.java
* @brief Response define
*******************************************************************************
*/ 

package com.map.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


public class Response {

    private HttpStatus code;
    private String data;
    private HttpHeaders header;

    public Response() {
    }

    public Response(HttpStatus code, String data) {
        this.code = code;
        this.data = data;
    }

    public HttpStatus getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public HttpHeaders getHeader() {
        if (header == null) {
            header = new HttpHeaders();
        }
        return header;
    }

    public void setHeader(HttpHeaders header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "Response [code=" + code + ", data=" + data + ", header=" + header + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + code.value();
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((header == null) ? 0 : header.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Response other = (Response) obj;
        if (code != other.code)
            return false;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (header == null) {
            if (other.header != null)
                return false;
        } else if (!header.equals(other.header))
            return false;
        return true;
    }
}
