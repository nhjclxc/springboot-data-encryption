package com.nhjclxc.encryption.controller;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
class TestClass implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer type;
    private String consultContent;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    private LocalDate date;
    private LocalTime time;



    public static void main(String[] args) {
        TestClass testClass = new TestClass();
        testClass.id = 1234567898765L;
        testClass.type = 5;
        testClass.consultContent = "wertgnygdfsvcaasedrthyumhg";
        testClass.dateTime = LocalDateTime.now();
        testClass.date = LocalDate.now();
        testClass.time = LocalTime.now();
        System.out.println(testClass);
        String jsonString = JSON.toJSONString(testClass);
        System.out.println(jsonString);


        String key = "ZAQ12WSXCDE34RFV"; //任意16位数字字母组合
        SymmetricCrypto sm4 = new SM4(Mode.ECB, Padding.ISO10126Padding,key.getBytes());
        String encryptBase64 = sm4.encryptBase64(jsonString);
        System.out.println("加密后：" + encryptBase64);
        String decryptStr = sm4.decryptStr(encryptBase64);
        System.out.println("解密：" + decryptStr);

    }
}
