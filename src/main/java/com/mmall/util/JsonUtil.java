package com.mmall.util;

import com.google.common.collect.Lists;
import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @Auther gongfukang
 * @Date 7/4 19:31
 * UserController 里是 User 对象，Redis 中是 String 需要序列化和反序列化
 * jackson jar包
 */
@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    // 初始化 objectMapper
    static {

        // 序列化

        // 对象的所有字段全部列入序列化
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        // 取消默认转换 timestamps 形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 忽略空 Bean 转 json 的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        // 所有的日期格式都统一样式，即：yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        // 反序列化

        // 忽略在 json 字符串中存在，但是在 java 对象中不存在对应属性的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error", e);
        }
        return null;
    }

    /**
     * 返回封装好的 json
     */
    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error", e);
        }
        return null;
    }

    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
        }
        return null;
    }

    /**
     * 泛型反序列化
     */
    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            return null;
        }
    }

    /**
     * 测试
     */
    public static void main(String[] args) {
        User u1 = new User();
        u1.setId(1);
        u1.setEmail("u1test@test.com");

        User u2 = new User();
        u2.setId(2);
        u2.setEmail("u1test2@test.com");

        String user1Json = JsonUtil.obj2String(u1);

        String user1JsonPretty = JsonUtil.obj2StringPretty(u1);

        log.info("user1Json:{}", user1Json);
        log.info("user1JsonPretty:{}", user1JsonPretty);

        User user = JsonUtil.string2Obj(user1Json, User.class);

        List<User> userList = Lists.newArrayList();
        userList.add(u1);
        userList.add(u2);

        String userListStr = JsonUtil.obj2StringPretty(userList);

        log.info("+++++++++++++++++++++++++++++++++++");
        log.info(userListStr);
        log.info("+++++++++++++++++++++++++++++++++++");

        List<User> userListObj1 = JsonUtil.string2Obj(userListStr, new TypeReference<List<User>>(){ });

        List<User> userListObj2 = JsonUtil.string2Obj(userListStr, List.class, User.class);

        System.out.println("end");
    }
}
