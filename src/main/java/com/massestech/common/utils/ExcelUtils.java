package com.massestech.common.utils;

import com.massestech.common.utils.excel.ExcelFuntion;
import com.massestech.common.utils.excel.ExcelSimpleObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * excel 工具类
 */
public class ExcelUtils {

    /**
     * 下载excel
     * @param data 需要填充的excel数据
     * @param filePath excel模板的文件相对路径名
     * @param downLoadFileName exlce下载的文件名,ex:   某某.xlsx
     */
    public static ResponseEntity<byte[]> getResponseEntity(Map<String,Object> data, String filePath, String downLoadFileName) throws IOException {
        return getResponseEntity(data, filePath, downLoadFileName, null);
    }

    /**
     * 下载excel
     * @param data 需要填充的excel数据
     * @param filePath excel模板的文件相对路径名
     * @param downLoadFileName exlce下载的文件名,ex:   某某.xlsx
     * @param fcData 自定义的功能函数
     */
    public static ResponseEntity<byte[]> getResponseEntity(Map<String,Object> data, String filePath, String downLoadFileName, Map<String,Object> fcData) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = getInputStreamByPath(filePath);

        Context context = PoiTransformer.createInitialContext();
        if (data != null) {
            for (String key : data.keySet()) {
                context.putVar(key, data.get(key));
            }
        }
        JxlsHelper jxlsHelper = JxlsHelper.getInstance();
        Transformer transformer  = jxlsHelper.createTransformer(in, out);
        //获得配置
        JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator)transformer.getTransformationConfig().getExpressionEvaluator();
        //函数强制，自定义功能
        Map<String, Object> funcs = new HashMap<>();
        funcs.put("fc", new ExcelFuntion());    //添加自定义功能
        if (fcData != null) {
            for (String key : fcData.keySet()) {
                funcs.put(key, fcData.get(key));
            }
        }
        evaluator.getJexlEngine().setFunctions(funcs);
        //必须要这个，否者表格函数统计会错乱
        jxlsHelper.setUseFastFormulaProcessor(false).processTemplate(context, transformer);

        return getResponseEntity(downLoadFileName, out);
    }

    private static ResponseEntity<byte[]> getResponseEntity(String downLoadFileName, ByteArrayOutputStream out) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        downLoadFileName = new String(downLoadFileName.getBytes("UTF-8"),"iso-8859-1");//为了解决中文名称乱码问题
        headers.setContentDispositionFormData("attachment", downLoadFileName);
        //以下载的方式打开链接
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.CREATED);
    }

    /**
     * 将excel文件转换成model
     * @param in excel输入流
     * @param clazz 需要转化的excelmodel
     * @param sheetAt excel页数
     * @param jumpNum 需要跳过的行数
     * @return
     */
    public static <T> List<T> excel2Model(InputStream in, Class<T> clazz, int sheetAt, int jumpNum) throws Exception {
        List<String[]> dataList = getDataList(in, sheetAt, jumpNum);
        return getSimpleObjectListForExcel(dataList, clazz);
    }

    private static InputStream getInputStreamByPath(String filePath) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        InputStream inputStream = classPathResource.getInputStream();

        return inputStream;
    }

    /**
     * 根据解析后的excel数据以及class类型,解析excel
     * @param rowsList 解析后的excel数据
     * @param clazz 需要解析的excel类型
     * @return 解析后的结果
     */
    public static <T> List<T> getSimpleObjectListForExcel(List<String[]> rowsList, Class<T> clazz) throws Exception {
        Map<String, ExcelSimpleObject> excelObjectMap = getExcelSimpleObjectMap(clazz);
        List<T> excelList = new LinkedList<>();
        // 迭代行
        for (int i = 0; i < rowsList.size(); i++) {
            T entity = clazz.newInstance();
            String[] rows = rowsList.get(i);
            String nextFileName = null;
            // 这里需要判断是否需要每一行都创建一个clazz对应的object对象(迭代列)
            // 这里要多一个判断j < excelObjectMap.size(),因为要避免列那里有空格.
            for (int j = 0; j < rows.length && j < excelObjectMap.size(); j++) {
                String value = rows[j]; // 得到对应行当前列的属性
                ExcelSimpleObject excelObject = getExcelSimpleObject(excelObjectMap, nextFileName, j); // 得到当前的行对应的属性
                nextFileName = excelObject.getNextFieldName(); // 下个field的名称
                ReflectionUtils.setField(excelObject.getField(),entity, value); // 设置值.
                // 如果是最后一列,那么就将model扔到list里面
                if (j == rows.length - 1 || j == excelObjectMap.size() - 1) {
                    excelList.add(entity);
                    entity = null;
                }
            }
        }
        return excelList;
    }

    public static Sheet getSheet(InputStream in, int sheetAt) throws Exception {
        Workbook wb = WorkbookFactory.create(in);
        Sheet sheet = wb.getSheetAt(sheetAt);
        return sheet;
    }

    /**
     * 得到将excel转换为数据
     * @param file 文件
     * @param sheetAt 第几个sheet页,从0开始计算
     * @param jumpNum 需要跳过的行数
     * @return
     * @throws FileNotFoundException
     */
    public static List<String[]> getDataList(File file, int sheetAt, int jumpNum) throws FileNotFoundException {
        return getDataList(new FileInputStream(file), sheetAt, jumpNum);
    }

    /**
     * 获取excel里面的数据
     */
    public static List<String[]> getDataList(InputStream inputStream, int sheetAt, int jumpNum) {
        List<String[]> dataList = new LinkedList<>();
        try {
            Sheet sheet = ExcelUtils.getSheet(inputStream, sheetAt);
            // 获取列数
            int columnNum = sheet.getRow(0).getLastCellNum();

            // 遍历sheet,但是跳过前面4行,从第5行开始
            for (int i = jumpNum; i <= sheet.getLastRowNum(); i ++) {
                Row row = sheet.getRow(i);
                // 如果row是空的的话,那么就跳过当前行.
                if (row == null) {
                    continue;
                }
                String[] singleRow = new String[columnNum];

                boolean isNotNullFlag = false; // 定义标识用于判断是否是空的,默认列是空的.
                for(int j = 0; j < columnNum; j++){
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    // 获取cell里面的值
                    singleRow[j] = ExcelUtils.getRowValue(cell);
                    // 如果不为空那么就改变标识
                    if (StringUtils.isNotBlank(singleRow[j])) {
                        isNotNullFlag = true;
                    }
                }
                // 如果列有数据,那么就将其放到数组里面去.
                if (isNotNullFlag) {
                    dataList.add(singleRow);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("excel读取失败......", e);
        }
        return dataList;
    }

    /**
     * 根据cell获取cell里面的值
     */
    private static String getRowValue(Cell cell) {
        String rowValue = "";
        // 根据单元格类型,将单元格转换为对应的数据
        switch(cell.getCellType()){
            case Cell.CELL_TYPE_BLANK:
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                rowValue = Boolean.toString(cell.getBooleanCellValue());
                break;
            //数值
            case Cell.CELL_TYPE_NUMERIC:
                if(DateUtil.isCellDateFormatted(cell)){
                    rowValue = String.valueOf(cell.getDateCellValue().getTime());
                }else{
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String temp = cell.getStringCellValue();
                    //判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
                    if(temp.indexOf(".")>-1){
                        rowValue = String.valueOf(new Double(temp)).trim();
                    }else{
                        rowValue = temp.trim();
                    }
                }
                break;
            case Cell.CELL_TYPE_STRING:
                rowValue = cell.getStringCellValue().trim();
                if (rowValue.equals("N/A")) {
                    rowValue = null;
                } else if (rowValue.equals("N/T")) {
                    rowValue = null;
                } else if (rowValue.equals("N//A")) {
                    rowValue = null;
                }
                break;
            case Cell.CELL_TYPE_ERROR:
                break;
            case Cell.CELL_TYPE_FORMULA:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                rowValue = cell.getStringCellValue();
                if(rowValue!=null){
                    rowValue = rowValue.replaceAll("#N/A","").trim();
                }
                break;
            default:
                break;
        }
        return rowValue;
    }



    /**
     * 根据class的类型,获取excel的注解,得到一个类似链表结构的map
     */
    private static <T> Map<String, ExcelSimpleObject> getExcelSimpleObjectMap(Class<T> clazz) {
        Map<String, ExcelSimpleObject> annotationMap = new HashMap<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            ExcelSimpleObject excelObject = new ExcelSimpleObject();
            Field field = declaredFields[i];
            field.setAccessible(true); // 设置字段可以访问.
            excelObject.setField(field);
            excelObject.setFieldName(field.getName());
            if (i != declaredFields.length -1) {
                excelObject.setNextFieldName(declaredFields[i+1].getName());
            }
            if (annotationMap.size() == 0) {
                annotationMap.put("begin", excelObject);
            } else {
                annotationMap.put(field.getName(), excelObject);
            }
        }

        return annotationMap;
    }

    private static ExcelSimpleObject getExcelSimpleObject(Map<String, ExcelSimpleObject> excelObjectMap, String nextFileName, int j) {
        ExcelSimpleObject excelObject;
        if (j == 0) {
            excelObject = excelObjectMap.get("begin");
        } else {
            excelObject = excelObjectMap.get(nextFileName);
        }
        return excelObject;
    }

}
