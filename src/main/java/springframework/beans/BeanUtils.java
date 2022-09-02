package springframework.beans;

import java.lang.reflect.Method;

public class BeanUtils {


    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return paramTypes == null ? clazz.getDeclaredMethod(methodName) : clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            for (Class<?> clazzInterface : clazz.getInterfaces()) {
                Method method = findDeclaredMethod(clazzInterface, methodName, paramTypes);
                if (method != null) {
                    return method;
                }
            }
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            } else {
                return null;
            }
        }
    }

    public static Method findDeclaredMethod(Class<?> clazz, String methodName) {
        return findDeclaredMethod(clazz, methodName, null);
    }


}
