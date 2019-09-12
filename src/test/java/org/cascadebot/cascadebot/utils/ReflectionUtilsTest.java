package org.cascadebot.cascadebot.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest {

    @Test
    void assignMapToObjectNormal() throws NoSuchFieldException, IllegalAccessException {
        Map<String, Object> assignMap = Map.of("one", 2, "two", 3, "three", 1);
        TestObject original = new TestObject();
        TestObject modified = ReflectionUtils.assignMapToObject(original, assignMap, true);
        assertTrue(modified.one.equals(2));
        assertTrue(modified.two.equals(3));
        assertTrue(modified.three.equals(1));
    }

    @Test
    void assignMapToObjectEmptyMap() throws NoSuchFieldException, IllegalAccessException {
        Map<String, Object> assignMap = Map.of();
        TestObject original = new TestObject();
        TestObject modified = ReflectionUtils.assignMapToObject(original, assignMap, true);
        assertEquals(original, modified);
    }

    @Test
    void assignMapToObjectInvalidKeysIgnore() throws NoSuchFieldException, IllegalAccessException {
        Map<String, Object> assignMap = Map.of("hello", 2, "test", 6, "thingy", 4);
        TestObject original = new TestObject();
        TestObject modified = ReflectionUtils.assignMapToObject(original, assignMap, true);
        assertEquals(original, modified);
    }

    @Test
    void assignMapToObjectInvalidKeysThrows() {
        Map<String, Object> assignMap = Map.of("hello", 2, "test", 6, "thingy", 4);
        TestObject original = new TestObject();
        assertThrows(NoSuchFieldException.class, () -> ReflectionUtils.assignMapToObject(original, assignMap, false));
    }

    @Test
    void partiallyUpdateObjectNormal() throws IllegalAccessException {
        TestObject original = new TestObject();
        TestObject changed = new TestObject(3, 1, null);
        TestObject updated = ReflectionUtils.partiallyUpdateObject(original, changed);
        assertTrue(updated.one.equals(3));
        assertTrue(updated.two.equals(1));
        assertTrue(updated.three.equals(3));
    }

    @Test
    void partiallyUpdateObjectEmpty() throws IllegalAccessException {
        TestObject original = new TestObject();
        TestObject changed = null;
        TestObject updated = ReflectionUtils.partiallyUpdateObject(original, changed);
        assertTrue(updated.one.equals(1));
        assertTrue(updated.two.equals(2));
        assertTrue(updated.three.equals(3));

        changed = new TestObject(null, null, null);
        updated = ReflectionUtils.partiallyUpdateObject(original, changed);
        assertTrue(updated.one.equals(1));
        assertTrue(updated.two.equals(2));
        assertTrue(updated.three.equals(3));
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    private class TestObject {
        private Integer one = 1;
        private Integer two = 2;
        private Integer three = 3;
    }

}