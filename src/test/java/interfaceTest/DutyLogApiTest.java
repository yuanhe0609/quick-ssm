package interfaceTest;

import com.alibaba.fastjson2.JSON;
import com.company.project.entity.TotalDutyLogEntity;
import com.company.project.utils.HttpClientUtil;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/16
 */

public class DutyLogApiTest {
    @Test
    public void Test() {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String resultGet = httpClientUtil.doHttpGet("http://localhost:8080/springMVC/calculate/2024/10");
        List<TotalDutyLogEntity> list =  JSON.parseObject(resultGet, List.class);
        String filePath = "C:\\Users\\33502\\Desktop\\result.txt"; // 文件路径

        Class DutyLogController = DutyLogController.class;
        Annotation[] annotations = (Annotation[]) DutyLogController.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation);
        }
        RestController restController = (RestController) DutyLogController.getDeclaredAnnotation(RestController.class);
        System.out.println(restController.value());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for(int i=0;i<list.size();i++){
                writer.write(JSON.toJSONString(list.get(i)));
                writer.newLine();
            }
            System.out.println("文件创建成功并且内容已写入。");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("文件创建或写入失败。");
        }
    }
}
