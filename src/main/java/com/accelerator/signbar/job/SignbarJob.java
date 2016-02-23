package com.accelerator.signbar.job;

import com.accelerator.signbar.Constants;
import com.accelerator.signbar.service.SignbarService;
import com.accelerator.signbar.util.MailUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class SignbarJob implements Constants {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SignbarService signbarService;

    @Value("${baidu.signTimes}")
    private int signTimes;

    public void startSign() throws InterruptedException {
        if (COOKIE_EXPIRED.get())
            return;
        int currentTimes = CURRENT_SIGN_TIMES.incrementAndGet();
        logger.info("开始签到，第({})次签到", currentTimes);
        Map<String, String> bars = signbarService.getBars();
        for (Map.Entry<String, String> bar : bars.entrySet()) {
            String balvid = bar.getKey();
            if (BooleanUtils.isTrue(SIGN_STATUS.get(balvid)))
                continue;
            boolean status = signbarService.doSign(bar);
            SIGN_STATUS.put(balvid, status);
        }
        if (CURRENT_SIGN_TIMES.compareAndSet(signTimes, 0)) {
            Map<String, Boolean> signInfo = Maps.newIdentityHashMap();
            for (Map.Entry<String, Boolean> signStatusEntry : SIGN_STATUS.entrySet()) {
                String balvid = signStatusEntry.getKey();
                String title = bars.get(balvid);
                signInfo.put(title, signStatusEntry.getValue());
            }
            SIGN_STATUS.clear();
            MailUtils.sendSignbarStatus(signInfo);
        }
        logger.info("结束签到，第({})次签到", currentTimes);
    }
}
