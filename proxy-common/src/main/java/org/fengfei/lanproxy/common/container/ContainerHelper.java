package org.fengfei.lanproxy.common.container;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 容器启动工具类.
 *
 * @author fengfei
 */
public class ContainerHelper {

    private static final Logger logger = LoggerFactory.getLogger(ContainerHelper.class);

    private static volatile boolean running = true;

    private static List<Container> cachedContainers;

    /**
     * 开始方法
     * <p>
     * 通过该方法启动容器集合中的所有容器
     *
     * @param containers 容器集合
     */
    public static void start(List<Container> containers) {

        cachedContainers = containers;

        // 启动所有容器
        startContainers();

        // 注册程序关闭时的钩子函数
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                synchronized (ContainerHelper.class) {
                    // 停止所有容器.
                    stopContainers();
                    running = false;
                    //不释放锁
                    ContainerHelper.class.notifyAll();
                }
            }
        });

        synchronized (ContainerHelper.class) {
            while (running) {
                try {
                    //阻塞线程，会将ContainerHelper同步锁释放
                    //notify()方法通知后，被唤醒，并且去参加获取锁的竞争，不是马上得到锁，有一个过程。
                    ContainerHelper.class.wait();
                } catch (Throwable e) {

                }
            }
        }
    }

    /**
     * 开启容器集合中的容器
     */
    private static void startContainers() {
        for (Container container : cachedContainers) {
            logger.info("starting container [{}]", container.getClass().getName());
            container.start();
            logger.info("container [{}] started", container.getClass().getName());
        }
    }

    /**
     * 关闭容器集合中的容器
     */
    private static void stopContainers() {
        for (Container container : cachedContainers) {
            logger.info("stopping container [{}]", container.getClass().getName());
            try {
                container.stop();
                logger.info("container [{}] stopped", container.getClass().getName());
            } catch (Exception ex) {
                logger.warn("container stopped with error", ex);
            }
        }
    }
}
