package com.accelerator.signbar.service;

import java.util.Map;
import java.util.Map.Entry;

public interface SignbarService {

    Map<String, String> getBars();

    String getTbs();

    boolean doSign(Entry<String, String> bar);

}
