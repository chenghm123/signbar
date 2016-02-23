package com.accelerator.signbar;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public interface Constants {

    AtomicBoolean COOKIE_EXPIRED = new AtomicBoolean(false);

    AtomicInteger CURRENT_SIGN_TIMES = new AtomicInteger(0);

    Map<String, Boolean> SIGN_STATUS = Maps.newConcurrentMap();

}
