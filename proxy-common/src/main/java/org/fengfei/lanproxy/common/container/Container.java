package org.fengfei.lanproxy.common.container;

/**
 * 容器
 */
public interface Container {
    /**
     * 代理服务开启
     */
    void start();

    /**
     * 代理服务关闭
     */
    void stop();
}
