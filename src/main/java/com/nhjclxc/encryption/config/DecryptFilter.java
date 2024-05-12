package com.nhjclxc.encryption.config;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.FilterConfig;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Component
public class DecryptFilter implements Filter {

    @Value("${request.encrypt.key}")
    private String KEY;
    @Value("${request.encrypt.encryptFlag}")
    private String ENCRYPT_FLAG;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        SymmetricCrypto sm4 = null;
        boolean encryptFlag = Boolean.parseBoolean(request.getHeader(ENCRYPT_FLAG));

        // params传参解密
        if (encryptFlag) {
            sm4 = new SM4(Mode.ECB, Padding.ISO10126Padding, KEY.getBytes(StandardCharsets.UTF_8));
            for (String k : parameterMap.keySet()) {
                String[] originalParameter = parameterMap.get(k);
                String[] decryptArr = new String[originalParameter.length];
                for (int i = 0; i < originalParameter.length; i++) {
                    decryptArr[i] = sm4.decryptStr(originalParameter[i], StandardCharsets.UTF_8);
                }
                parameterMap.put(k, decryptArr);
            }
        }
        RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) servletRequest, parameterMap);
        ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);

        // body传参解密
        if (encryptFlag) {
            String requestWrapperBody = requestWrapper.getBody();
            if (requestWrapper.isHasBody() && StringUtils.hasText(requestWrapperBody)) {
                String body = sm4.decryptStr(requestWrapperBody);
                requestWrapper.setBody(body);
                requestWrapper.setBytes(body.getBytes());
            }
        }
        filterChain.doFilter(requestWrapper, responseWrapper);
        if (responseWrapper.getContentType() != null && responseWrapper.getContentType().contains(MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
            flushResponse(servletResponse, responseWrapper, responseWrapper.toByteArray());
        } else {
            String response = new String(responseWrapper.toByteArray());
            if (encryptFlag) {
                response = sm4.encryptBase64(response);
                responseWrapper.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                responseWrapper.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ENCRYPT_FLAG);
                responseWrapper.setHeader(ENCRYPT_FLAG, URLEncoder.encode("true", StandardCharsets.UTF_8.name()));
            }
            flushResponse(servletResponse, responseWrapper, response.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void flushResponse(ServletResponse servletResponse, ResponseWrapper responseWrapper, byte[] responseByte) throws IOException {
        responseWrapper.setContentLength(responseByte.length);
        try (ServletOutputStream outputStream = servletResponse.getOutputStream()) {
            outputStream.write(responseByte);
            outputStream.flush();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
