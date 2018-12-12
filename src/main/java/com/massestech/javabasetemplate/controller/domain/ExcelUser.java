package com.massestech.javabasetemplate.controller.domain;

import lombok.Data;

/**
 * 接收解析的excel数据
 */
@Data
public class ExcelUser {

    /** 第1列 序号*/
    private String column1;

    /** excel第二列,姓名 */
    private String userName;

    /** excel第三列,年龄 */
    private String age;

    /** excel第四列,性别(男&女) */
    private String sexStatus;

    public int getSexStatus() {
        // 对于excel的数据进行转换.
        if ("男".equals(sexStatus)) {
            return 0;
        } else {
            return 1;
        }
    }

}
