package ReflectionUtils;

import java.lang.reflect.Field;

public class AdvancedGetter {

    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Field not found or inaccessible", e);
        }
    }
}
