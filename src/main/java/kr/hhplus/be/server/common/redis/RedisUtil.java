package kr.hhplus.be.server.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 단순 문자열 저장
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * TTL과 함께 저장
     */
    public void set(String key, String value, Duration duration) {
        stringRedisTemplate.opsForValue().set(key, value, duration);
    }

    /**
     * 조회
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 객체를 JSON으로 저장
     */
    public <T> void setObject(String key, T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object for key: {}", key, e);
            throw new RuntimeException("Redis serialization failed", e);
        }
    }

    /**
     * 객체를 JSON으로 저장 (TTL)
     */
    public <T> void setObject(String key, T value, Duration duration) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, duration);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object for key: {}", key, e);
            throw new RuntimeException("Redis serialization failed", e);
        }
    }

    /**
     * JSON을 객체로 조회
     */
    public <T> T getObject(String key, Class<T> type) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            return json != null ? objectMapper.readValue(json, type) : null;
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize object for key: {}", key, e);
            throw new RuntimeException("Redis deserialization failed", e);
        }
    }

    /**
     * 삭제
     */
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 여러 키 삭제
     */
    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    /**
     * 존재 여부
     */
    public Boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * TTL 설정
     */
    public Boolean expire(String key, Duration duration) {
        return stringRedisTemplate.expire(key, duration);
    }

    /**
     * TTL 조회
     */
    public Long getTTL(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 증가
     */
    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 증가 (델타)
     */
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 감소
     */
    public Long decrement(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    /**
     * 감소 (델타)
     */
    public Long decrement(String key, long delta) {
        return stringRedisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * Key가 없을 때만 설정 (NX)
     */
    public Boolean setIfAbsent(String key, String value, Duration duration) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, duration);
    }

    // ========== Set 연산 ==========

    public Long addToSet(String key, String... values) {
        return stringRedisTemplate.opsForSet().add(key, values);
    }

    public Boolean isMemberOfSet(String key, String value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    public Set<String> getSetMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    public Long removeFromSet(String key, String... values) {
        return stringRedisTemplate.opsForSet().remove(key, (Object[]) values);
    }

    public Long getSetSize(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    // ========== Hash 연산 ==========

    public void putHash(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    public String getHash(String key, String field) {
        Object value = stringRedisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : null;
    }

    public Map<Object, Object> getAllHash(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public Boolean hasHashKey(String key, String field) {
        return stringRedisTemplate.opsForHash().hasKey(key, field);
    }

    public Long deleteHashKeys(String key, String... fields) {
        return stringRedisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    // ========== List 연산 ==========

    public Long pushToList(String key, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, value);
    }

    public String popFromList(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    public List<String> getListRange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    public Long getListSize(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    // ========== Sorted Set 연산 ==========

    public Boolean addToZSet(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    public Double incrementZSetScore(String key, String value, double delta) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    public Set<String> getZSetRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    public Set<String> getZSetReverseRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    public Long getZSetRank(String key, String value) {
        return stringRedisTemplate.opsForZSet().rank(key, value);
    }

    public Double getZSetScore(String key, String value) {
        return stringRedisTemplate.opsForZSet().score(key, value);
    }



}
