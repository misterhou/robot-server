package com.fanyumeta;

import com.fanyumeta.utils.HardwareControlCommandUtil;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class RobotServerStartup {

    public static void main(String[] args) {
//        if (args.length > 0) {
//            String command = args[0];
//            if ("1".equals(command)) {
//                log.info("开始执行生成缓存文件功能");
//                if (args.length >= 2) {
//                    String excelFile = args[1];
//                    String commandCacheFile = null;
//                    if (args.length >= 3) {
//                        commandCacheFile = args[2];
//                    }
//                    HardwareControlCommandUtil.generateCommandCacheFile(excelFile, commandCacheFile);
//                } else {
//                    log.info("参数不匹配");
//                }
//            } else if ("2".equals(command)) {
//                log.info("开始执行加载配置文件功能");
//                String commandCacheFile = null;
//                if (args.length >= 2) {
//                    commandCacheFile = args[1];
//                }
//                HardwareControlCommandUtil.loadData(commandCacheFile);
//            } else if ("3".equals(command)) {
//                startup(args);
//            } else {
//                log.info("参数不正确");
//            }
//        } else {
//            log.info("程序启动中...");
//            startup(args);
//        }
        startup(args);
//        try {
//            HardwareControlCommandUtil.generateCommandReceiveCacheFile("src/main/resources/数智人问答指令集0123.xlsx", "ReceiveCommandCache");
//            HardwareControlCommandUtil.generateCommandCacheFile("src/main/resources/中控指令集.xlsx", "RequestCommandCache");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static void startup(String[] args) {
        SpringApplication springApplication = new SpringApplication(RobotServerStartup.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        ApplicationContext ctx = springApplication.run(args);
        Environment environment = ctx.getEnvironment();
        HardwareControlCommandUtil.initCache(
                environment.getProperty("fan-yu.hardware-control.request-command-config-file"),
                environment.getProperty("fan-yu.hardware-control.receive-command-config-file"));
    }
//
//
//
//
//
//
//
//
//    /**
//     * 步骤：：每次递归时，把原始数据和满足条件的工作空间复制一份，所有的操作均在复制文件中进行，目的就是保证不破坏原始数据，
//     * 从而可以让一轮递归结束后可以正常进行下一轮。
//     * 其次，把数据的第一个元素添加到工作空间中，判断工作空间的大小，如果小于k,则需要继续递归，但此时，传入递归函数的
//     * 参数需要注意：假设当前插入的节点的下标是i,因为是顺序插入的,所以i之前的所有数据都应该舍去，只传入i之后的未使用过的数据。
//     * 因此在传参之前，应该对copydata作以处理；当大于k的时候，则表明已经找到满足条件的第一种情况，然后只需修改该情况的最后一个结果即可。
//     * 如：找到abc时，则只需替换c为d即可完成该轮递归。
//     *
//     * @param data      原始数据
//     * @param workSpace 自定义一个临时空间，用来存储每次符合条件的值
//     * @param k         C(n,k)中的k
//     */
//    public static <E> void combinerSelect(List<E> data, List<E> workSpace, int n, int k) {
//        List<E> copyData;
//        List<E> copyWorkSpace;
//
//        if (workSpace.size() == k) {
//            for (Object c : workSpace) {
//                System.out.print(c);
//            }
//            System.out.println();
//        }
//
//        for (int i = 0; i < data.size(); i++) {
//            copyData = new ArrayList<>(data);
//            copyWorkSpace = new ArrayList<>(workSpace);
//
//            copyWorkSpace.add(copyData.get(i));
//            for (int j = i; j >= 0; j--) {
//                copyData.remove(j);
//            }
//            combinerSelect(copyData, copyWorkSpace, n, k);
//        }
//
//    }
//

}
