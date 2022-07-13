package com.tung.marc.service;

import com.tung.marc.constant.BaseParamConstant;
import com.tung.marc.dto.MarcDTO;
import com.tung.marc.util.HttpClientUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MarcServiceImpl {

    /**
     * 时间格式
     */
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

   
    public List<MarcDTO> getMarc(String key,String value,String parsing,String json,int size) {
        List<MarcDTO> resultArray = null;
        try {
            Map<String, Object> params = new HashMap<>(6);
            //请求参数
            params.put("uid", BaseParamConstant.UID);
            params.put("pwd",BaseParamConstant.PWD);
            params.put("key",key);
            params.put("value",value);
            params.put("dbcode",BaseParamConstant.DBCODE);
            params.put("parsing",parsing);
            params.put("json",json);
            params.put("size",size);
            // 执行请求
            resultArray = HttpClientUtil.getResultArray(BaseParamConstant.URL, params, MarcDTO.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultArray;
    }
}
