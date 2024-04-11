package com.fanyumeta.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 中控指令工具
 * <pre>
 *     1、#parse 解析文本中的指令
 *     2、#generateCommandCacheFile 通过解析 excel 文件，生成指令缓存文件
 *     3、#loadData 加载指令文件
 *     4、initCache 初始化指令缓存
 * </pre>
 */
@Slf4j
public class HardwareControlCommandUtil {

    private static Map<String, String> commandCache = new HashMap<>(256);

    /**
     * 解析文本中的指令
     * @param message 文本信息
     * @return 指令集合
     */
    public static List<String> parse(String message) {
        List<String> command = new CopyOnWriteArrayList<>();
        for (String regStr : commandCache.keySet()) {
            Pattern pattern = Pattern.compile(regStr);
            if (pattern.matcher(message).find()) {
                command.add(commandCache.get(regStr));
            }
        }
        return command;
    }

    /**
     * 通过解析 excel 文件，生成指令缓存文件
     * @param excelFile excel 文件
     * @param commandCacheFile 缓存文件
     */
    public static void generateCommandCacheFile(String excelFile, String commandCacheFile) {
        Map<String, String> data = new HashMap<>();
        try {
            FileInputStream fis = new FileInputStream(excelFile);
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = cell.toString();
                    if ("指令".equals(cellValue)) {
                        int startRowNum = cell.getRowIndex();
                        int startCellNum = cell.getColumnIndex();
                        log.info("指令 row: " + startRowNum + ", 指令 cell: " + startCellNum);
                        for (int i = (startRowNum+1); i <= sheet.getLastRowNum(); i ++) {
                            Row commandRow = sheet.getRow(i);
                            Cell commandColumn = commandRow.getCell(startCellNum);
                            if (!StringUtils.hasText(commandColumn.toString())) {
                                log.info("==================================");
                                break;
                            }
                            int commandValueColumnIndex = commandColumn.getColumnIndex() + 2;
                            String commandKey = getCommandKey(getCommandName(commandColumn, sheet));
                            String commandValue = getColumnValue(sheet, i, commandValueColumnIndex, i);
                            data.put(commandKey, commandValue.replaceFirst("\\.\\d*", ""));
                            System.out.println("rowIndex: " + i + " - " + commandKey + " 指令值：" + commandValue);
                        }
                    }
                }
            }
            workbook.close();
            FileOutputStream fileOutputStream = new FileOutputStream(commandCacheFile);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(data);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载指令文件
     * @param commandCacheFile 缓存文件
     * @return 缓存 map
     */
    public static Map<String,String> loadData(String commandCacheFile) {
//        commandCacheFile = getCommandCacheFile(commandCacheFile);
        Map<String,String> data = null;
        try {
            log.info("开始加载指令缓存文件：{}", commandCacheFile);
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(commandCacheFile));
            data = (Map<String, String>) inputStream.readObject();
            log.info("指令数量：{}\n{}", data.size(), data);
        } catch (Exception e) {
            log.error("指令配置加载异常", e);
        }
        return data;
    }

    /**
     * 更新指令缓存数据
     * @param commandConfigFile excel 指令文件
     */
    public static void initCache(String commandConfigFile) {
        try {
            commandCache.clear();
            commandCache.putAll(loadData(commandConfigFile));
        } catch (Exception e) {
            log.error("加载指令数据异常", e);
        }
    }

//    /**
//     * 获取指令缓存文件
//     * @param commandCacheFile 缓存指令文件
//     * @return 缓存指令文件，如果为空返回默认配置
//     */
//    private static String getCommandCacheFile(String commandCacheFile) {
//        log.info("接收到的缓存文件参数：{}", commandCacheFile);
//        return commandCacheFile;
//    }

    /**
     * 获取指令名称
     * <p>指令名称在 excel 文件中分多列存储，需要分别提取</p>
     * @param commandCell 指令所在 Cell
     * @param sheet 工作簿
     * @return 指令名称集合
     */
    private static List<String> getCommandName(Cell commandCell, Sheet sheet) {
        List<String> commandName = new ArrayList<>();
        commandName.add(commandCell.toString());
        for (int i = 1; i <= 2; i++) {
            int columnIndex = commandCell.getColumnIndex() - i;
            String cellValue = getColumnValue(sheet, commandCell.getRowIndex(), columnIndex, 0);
            if (!StringUtils.hasText(cellValue)) {
                break;
            }
            // 判读指令名称是否重复，重复则丢弃
            if (!commandName.stream().filter(e -> e.contains(cellValue)).findFirst().isPresent()) {
                commandName.add(cellValue);
            }
        }
        return commandName;
    }

    /**
     * 获取指令存入 map 中的 key
     * @param commandList 指令名称集合
     * @return 指令名称集合对应的 key
     */
    private static String getCommandKey(List<String> commandList) {
        String commandKey = null;
        List<String> commandKeyList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(commandList)) {
            int commandListSize = commandList.size();
            if (commandListSize == 3) {
                commandKeyList.add(commandList.get(0) + commandList.get(1) + commandList.get(2));
                commandKeyList.add(commandList.get(2) + commandList.get(1) + commandList.get(0));
            } else if (commandListSize == 2) {
                commandKeyList.add(commandList.get(0) + commandList.get(1));
                commandKeyList.add(commandList.get(1) + commandList.get(0));
            }
        }
        return commandKeyList.stream().collect(Collectors.joining("|"));
    }

    /**
     * 获取单元格中的数据（字符串形式）
     * @param sheet 工作表
     * @param rowIndex 行索引
     * @param columnIndex 列索引
     * @param minRowIndex 最小行索引（合并单元格只有第一行有数据）
     * @return 单元格中的数据
     */
    private static String getColumnValue(Sheet sheet, Integer rowIndex, Integer columnIndex, Integer minRowIndex) {
        if (minRowIndex < 0) {
            minRowIndex = 0;
        }
        Row row = sheet.getRow(rowIndex);
        Cell cell = row.getCell(columnIndex);
        String cellValue = cell.toString();
        if (!StringUtils.hasText(cellValue) && rowIndex > minRowIndex) {
            cellValue = getColumnValue(sheet, (rowIndex - 1), columnIndex, minRowIndex);
        }
        return cellValue;
    }
}
