package com.fanyumeta.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

    /**
     * 请求指令缓存
     */
    private static final Map<String, String> REQUEST_COMMAND_CACHE = new HashMap<>(256);

    /**
     * 响应指令缓存
     */
    private static final Map<String, String> RECEIVE_COMMAND_CACHE = new HashMap<>(256);

    /**
     * 解析文本中的指令
     * @param message 文本信息
     * @return 指令集合
     */
    public static List<String> parse(String message) {
        List<String> command = new CopyOnWriteArrayList<>();
        for (String regStr : REQUEST_COMMAND_CACHE.keySet()) {
            Pattern pattern = Pattern.compile(regStr);
            if (pattern.matcher(message).find()) {
                command.add(REQUEST_COMMAND_CACHE.get(regStr));
            }
        }
        return command;
    }

    /**
     * 获取响应指令描述
     * @param commandValue 响应指令
     * @return 响应指令描述
     */
    public static String getReceiveCommandDescription(String commandValue) {
        return RECEIVE_COMMAND_CACHE.get(commandValue);
    }

    /**
     * 通过解析 excel 文件，生成指令缓存文件
     * @param excelFile excel 文件
     * @param commandCacheFile 缓存文件
     */
    public static void generateCommandCacheFile(String excelFile, String commandCacheFile) throws IOException {
//        Map<String, String> data = new HashMap<>();
//        try {
//            FileInputStream fis = new FileInputStream(excelFile);
//            Workbook workbook = WorkbookFactory.create(fis);
//            Sheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
//                for (Cell cell : row) {
//                    String cellValue = cell.toString();
//                    if ("指令".equals(cellValue)) {
//                        int startRowNum = cell.getRowIndex();
//                        int startCellNum = cell.getColumnIndex();
//                        log.info("指令 row: " + startRowNum + ", 指令 cell: " + startCellNum);
//                        for (int i = (startRowNum+1); i <= sheet.getLastRowNum(); i ++) {
//                            Row commandRow = sheet.getRow(i);
//                            Cell commandColumn = commandRow.getCell(startCellNum);
//                            if (!StringUtils.hasText(commandColumn.toString())) {
//                                log.info("==================================");
//                                break;
//                            }
//                            int commandValueColumnIndex = commandColumn.getColumnIndex() + 2;
//                            String commandKey = getCommandKey(getCommandName(commandColumn, sheet));
//                            String commandValue = getColumnValue(sheet, i, commandValueColumnIndex, i);
//                            data.put(commandKey, commandValue.replaceFirst("\\.\\d*", ""));
//                            System.out.println("rowIndex: " + i + " - " + commandKey + " 指令值：" + commandValue);
//                        }
//                    }
//                }
//            }
//            workbook.close();
//            FileOutputStream fileOutputStream = new FileOutputStream(commandCacheFile);
//            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
//            outputStream.writeObject(data);
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        int sheetIndex = 0;
        String targetColumnData = "与小鸟对接口号";
        int commandDescriptionColumnOffset = -2;
        int maxCommandDescriptionColumnLength = 4;
        boolean isContainCurrentColumn = false;

        List<Command> data = getCommandInfo(excelFile, sheetIndex, targetColumnData, commandDescriptionColumnOffset, maxCommandDescriptionColumnLength, isContainCurrentColumn);
        Map<String, String> cache = new HashMap<>();
        for (Command command : data) {
            String value = command.getValue();
            List<String> description = command.getDescription();
            String commandKey = getCommandKey(description);
            cache.put(commandKey, value);
        }
        writeObject2File(cache, commandCacheFile);
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
            ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(Paths.get(commandCacheFile)));
            data = (Map<String, String>) inputStream.readObject();
            log.info("指令数量：{}\n{}", data.size(), data);
        } catch (Exception e) {
            log.error("指令配置加载异常", e);
        }
        return data;
    }

    /**
     * 更新指令缓存数据
     * @param requestCommandConfigFile 请求指令文件
     * @param receiveCommandConfigFile 接收指令文件
     */
    public static void initCache(String requestCommandConfigFile, String receiveCommandConfigFile) {
        try {
            REQUEST_COMMAND_CACHE.clear();
            REQUEST_COMMAND_CACHE.putAll(loadData(requestCommandConfigFile));
            RECEIVE_COMMAND_CACHE.clear();
            RECEIVE_COMMAND_CACHE.putAll(loadData(receiveCommandConfigFile));
        } catch (Exception e) {
            log.error("加载指令数据异常", e);
        }
    }

    /**
     * 通过解析 excel 文件，生成指令缓存文件
     * @param excelFile excel 文件
     * @param commandCacheFile 缓存文件
     */
    public static void generateCommandReceiveCacheFile(String excelFile, String commandCacheFile) throws IOException {
//        Map<String, String> data = new HashMap<>();
//        try {
//            FileInputStream fis = new FileInputStream(excelFile);
//            Workbook workbook = WorkbookFactory.create(fis);
//            Sheet sheet = workbook.getSheetAt(2);
//            for (Row row : sheet) {
//                for (Cell cell : row) {
//                    String cellValue = cell.toString();
//                    if ("反馈指令".equals(cellValue)) {
//                        int startRowNum = cell.getRowIndex();
//                        int startCellNum = cell.getColumnIndex();
//                        log.info("反馈指令 row: " + startRowNum + ", 指令 cell: " + startCellNum);
//                        for (int i = (startRowNum+1); i <= sheet.getLastRowNum(); i ++) {
//                            Row commandRow = sheet.getRow(i);
//                            Cell commandColumn = commandRow.getCell(startCellNum);
//                            if (!StringUtils.hasText(commandColumn.toString())) {
//                                log.info("\n=========================================================================================================");
//                                break;
//                            }
//                            int commandValueColumnIndex = commandColumn.getColumnIndex();
//                            String commandKey = getColumnValue(sheet, i, commandValueColumnIndex, i);
//                            // 去除小数
//                            commandKey = commandKey.replaceFirst("\\.\\d*", "");
//                            int commandDesColumnOffset = -2;
//                            int commandDesColumnLength = 3;
//                            boolean isContainCurrentColumn = false;
//                            List<String> commandNameList = getCommandDescription(commandColumn, sheet, commandDesColumnOffset, commandDesColumnLength, isContainCurrentColumn);
//                            Collections.reverse(commandNameList);
//                            String commandValue = commandNameList.stream().collect(Collectors.joining(""));
//                            System.out.println("rowIndex: " + i + " - 指令描述：" + commandValue + "， 指令值：" + commandKey);
//                        }
//                    }
//                }
//            }
//            workbook.close();
//            FileOutputStream fileOutputStream = new FileOutputStream(commandCacheFile);
//            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
//            outputStream.writeObject(data);
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        int sheetIndex = 2;
        String targetColumnData = "反馈指令";
        int commandDescriptionColumnOffset = -2;
        int maxCommandDescriptionColumnLength = 3;
        boolean isContainCurrentColumn = false;

        List<Command> data = getCommandInfo(excelFile, sheetIndex, targetColumnData, commandDescriptionColumnOffset, maxCommandDescriptionColumnLength, isContainCurrentColumn);
//        List<Command> data = getCommandInfo(excelFile, 2, "反馈指令", -2, 3, false);
        Map<String, String> cache = new HashMap<>();
        for (Command command : data) {
            String value = command.getValue();
            List<String> description = command.getDescription();
            Collections.reverse(description);
            String commandDescription = String.join("", description);
            cache.put(value, commandDescription);
        }
        writeObject2File(cache, commandCacheFile);
    }

    private static List<Command> getCommandInfo(String excelFile, Integer sheetIndex, String targetColumnData, Integer commandDesColumnOffset, Integer commandDesColumnLength, Boolean isContainCurrentColumn) {
        List<Command> data = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(excelFile);
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = cell.toString();
                    if (targetColumnData.equals(cellValue)) {
                        int startRowNum = cell.getRowIndex();
                        int startCellNum = cell.getColumnIndex();
                        log.info(targetColumnData + " row: " + startRowNum + ", column: " + startCellNum);
                        for (int i = (startRowNum+1); i <= sheet.getLastRowNum(); i ++) {
                            Row commandRow = sheet.getRow(i);
                            Cell commandColumn = commandRow.getCell(startCellNum);
                            if (!StringUtils.hasText(commandColumn.toString())) {
                                log.warn("遇到空白数据列，退出（row：{}，column：{}）", commandColumn.getRowIndex(), commandColumn.getColumnIndex());
                                break;
                            }
                            int commandValueColumnIndex = commandColumn.getColumnIndex();
                            String command = getColumnValue(sheet, i, commandValueColumnIndex, i);
                            // 去除小数
                            command = command.replaceFirst("\\.\\d*", "");
//                            int commandDesColumnOffset = -2;
//                            int commandDesColumnLength = 3;
//                            boolean isContainCurrentColumn = false;
                            List<String> commandDescription = getCommandDescription(commandColumn, sheet, commandDesColumnOffset, commandDesColumnLength, isContainCurrentColumn);
//                            Collections.reverse(commandNameList);
//                            String commandValue = commandNameList.stream().collect(Collectors.joining(""));
                            data.add(new Command(command, commandDescription));
                            System.out.println("rowIndex: " + i + " - 指令描述：" + commandDescription + "， 指令值：" + command);
                        }
                        log.info("\n=========================================================================================================");
                    }
                }
            }
            workbook.close();
//            FileOutputStream fileOutputStream = new FileOutputStream(commandCacheFile);
//            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
//            outputStream.writeObject(column);
//            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
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

//    /**
//     * 获取指令名称
//     * <p>指令名称在 excel 文件中分多列存储，需要分别提取</p>
//     * @param commandCell 指令所在 Cell
//     * @param sheet 工作簿
//     * @return 指令名称集合
//     */
//    private static List<String> getCommandName(Cell commandCell, Sheet sheet) {
//        List<String> commandName = new ArrayList<>();
//        commandName.add(commandCell.toString());
//        for (int i = 1; i <= 2; i++) {
//            int columnIndex = commandCell.getColumnIndex() - i;
//            String cellValue = getColumnValue(sheet, commandCell.getRowIndex(), columnIndex, 0);
//            if (!StringUtils.hasText(cellValue)) {
//                break;
//            }
//            // 判读指令名称是否重复，重复则丢弃
//            if (!commandName.stream().filter(e -> e.contains(cellValue)).findFirst().isPresent()) {
//                commandName.add(cellValue);
//            }
//        }
//        return commandName;
//    }

    /**
     * 获取反馈指令描述
     * <p>指令描述在 excel 文件中分多列存储，需要分别提取</p>
     * @param commandCell 指令所在 Cell
     * @param sheet 工作簿
     * @return 指令描述集合
     */
    private static List<String> getCommandDescription(Cell commandCell, Sheet sheet, Integer startOffset, Integer maxColumnNum, Boolean isContainThis) {
        List<String> commandDes = new ArrayList<>();
        if (isContainThis) {
            commandDes.add(commandCell.toString());
        }
        for (int i = 0; i < maxColumnNum; i++) {
            int columnIndex = commandCell.getColumnIndex() + startOffset;
            String cellValue = getColumnValue(sheet, commandCell.getRowIndex(), columnIndex, 0);
            if (!StringUtils.hasText(cellValue)) {
                break;
            }
            // 判读指令名称是否重复，重复则丢弃
            if (!commandDes.stream().anyMatch(e -> e.contains(cellValue))) {
                commandDes.add(cellValue);
            }
            startOffset--;
        }
//        for (int i = offset; i <= maxColumnNum; i++) {
//            int columnIndex = commandCell.getColumnIndex() - i;
//            String cellValue = getColumnValue(sheet, commandCell.getRowIndex(), columnIndex, 0);
//            if (!StringUtils.hasText(cellValue)) {
//                break;
//            }
//            // 判读指令名称是否重复，重复则丢弃
//            if (!commandDes.stream().filter(e -> e.contains(cellValue)).findFirst().isPresent()) {
//                commandDes.add(cellValue);
//            }
//        }
        return commandDes;
    }

    /**
     * 获取指令存入 map 中的 key
     * @param commandList 指令名称集合
     * @return 指令名称集合对应的 key
     */
    private static String getCommandKey(List<String> commandList) {
        List<String> commandKeyList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(commandList)) {
            int commandListSize = commandList.size();
            if (commandListSize == 4) {
                commandKeyList.add(commandList.get(0) + commandList.get(1) + commandList.get(2) + commandList.get(3));
                commandKeyList.add(commandList.get(3) + commandList.get(2) + commandList.get(1) + commandList.get(0));
            } else if (commandListSize == 3) {
                commandKeyList.add(commandList.get(0) + commandList.get(1) + commandList.get(2));
                commandKeyList.add(commandList.get(2) + commandList.get(1) + commandList.get(0));
            } else if (commandListSize == 2) {
                commandKeyList.add(commandList.get(0) + commandList.get(1));
                commandKeyList.add(commandList.get(1) + commandList.get(0));
            }
        }
        return String.join("|", commandKeyList);
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

    /**
     * 将对象写入文件
     * @param obj 待写入的对象
     * @param file 待写入的文件
     * @throws IOException 将对象写入文件报错时，会抛出此异常
     */
    private static void writeObject2File(Object obj, String file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
        outputStream.writeObject(obj);
        outputStream.close();
    }

    @Data
    @AllArgsConstructor
    private static class Command {

        /**
         * 指令值
         */
        String value;

        /**
         * 指令描述
         */
        List<String> description;
    }
}
