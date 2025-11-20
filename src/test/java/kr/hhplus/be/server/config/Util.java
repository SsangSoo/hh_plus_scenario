package kr.hhplus.be.server.config;

import java.lang.reflect.Field;

public class Util {

    /**
     * Test에서만 사용할 id 주입을 위한 메서드
     * @param entity
     * @param id
     * @return
     * @param <T>
     */
    public static <T> T setId(T entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
}
