package com.khaliuk.controller;

import com.khaliuk.web.Request;
import com.khaliuk.web.ViewModel;

public interface Controller {
    ViewModel process(Request req);
}
