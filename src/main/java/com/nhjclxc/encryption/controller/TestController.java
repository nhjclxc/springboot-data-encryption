package com.nhjclxc.encryption.controller;

import com.nhjclxc.encryption.utils.JsonResult;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author LuoXianchao
 * @since 2024/05/12 21:01
 */
@RestController
@RequestMapping("/encryption")
public class TestController {

    // http://localhost:8080/encryption/get?str=strstrstrstr&num=66666
    // http://localhost:8080/encryption/get?str=REp8NItAHJdw47c3idDYgA==&num=IY6sHz4hIiNKdSzlhmYHTA==
    // AwFQEEnGx5nHBlJpGmyVJSIbzD1Geu5Lc0KmdaWQ36PDoc9FTo0uoqm2+MdA7W+1r8Ki/f81fLdga1eNQtwQeUWK9QSHHhv8c47vmcWaeQw=
    @GetMapping("/get")
    public JsonResult<Object> get(String str, Integer num){
        System.out.println("str = " + str);
        System.out.println("num = " + num);

        return JsonResult.success(str + " - " + num);
    }
    /*

{"consultContent":"wertgnygdfsvcaasedrthyumhg","date":"2024-05-12","dateTime":"2024-05-12 22:48:35","id":1234567898765,"time":"22:48:35","type":5}

CD9xAKahck2P89x0aDmPQnH+GJi/LNtKeMgV+vpQ6UDd9/T0nCYgMrYvSLZoXGBxsALTGz53VdcJNx4slxKBHDVgOLfCxtsJ5ITL/BaYdfRvz7FlZBUiIy0GZHaLYrDqWxypj7lWWEMswnACMSOxvEUC5FY/Nz746xY3DM1YTXIIHEaOhGbDchHVVusZS8DUraO8TD4hM/bZGCMsrT3fLw==
AwFQEEnGx5nHBlJpGmyVJSIbzD1Geu5Lc0KmdaWQ36PrxWnmWao3/4XocHJ8LPXQ46tlBQ4JAW+CpHUs3f5GQafrPBXRhRTuTcdis0D7askhUAJnwObGUj4J5cPwzdGodDuAG9EH/SEGSk98PK2mT10ts2k/S3BJLVNL3Tdqa7IR38H1BqPC2PLgIATf3UHHnDVViklZ1U6msfa5DFbTkVw0SCadIaleTKHXtKvEW9cTcMHhmCYQ+Ip5hhYpwN75956SWhmADrDx1PWG6aSHdQ==

     */
    // http://localhost:8080/encryption/post
    @PostMapping("/post")
    public JsonResult<Object> post(@RequestBody TestClass testClass){
        System.out.println("testClass = " + testClass);

        return JsonResult.success(testClass);
    }


    @PostMapping("/postFile")
    public void postFile(MultipartFile file) {

        try (FileOutputStream outputStream = new FileOutputStream("post.jpg")){
            IOUtils.copy(file.getInputStream(), outputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("文件上传成功");
    }

    @GetMapping("/getFile")
    public void getFile(HttpServletResponse response) throws IOException {

        setResponse(response, "post.jpg");

        try (FileInputStream inputStream = new FileInputStream("post.jpg")){
            IOUtils.copy(inputStream, response.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("文件下载成功");
    }

    /**
     * 设置响应流
     */
    public static void setResponse(HttpServletResponse response, String fileName) throws IOException {
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        response.setContentType("application/octet-stream; charset=UTF-8");
    }


}
