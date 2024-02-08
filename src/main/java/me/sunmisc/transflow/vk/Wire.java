package me.sunmisc.transflow.vk;

import me.sunmisc.transflow.vk.requests.Request;

public interface Wire {

    Response get(Request request) throws Exception;

    Response post(Request request) throws Exception;

    Response delete(Request request) throws Exception;

    Response patch(Request request) throws Exception;
}
