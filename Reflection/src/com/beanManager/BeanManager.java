package com.beanManager;

import com.annotation.Inject;
import com.annotation.Provides;
import com.provider.H2ConnectionProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BeanManager {
	// singleton fake
	private static BeanManager instance = new BeanManager();
	private Map<Class<?>, Supplier<?>> registry = new HashMap<>();

	public static BeanManager getInstance() {
		return instance;
	}

	private BeanManager() {
		List<Class<?>> connectionClasses = List.of(H2ConnectionProvider.class);
		for (Class<?> connectionClass : connectionClasses) {
			Method[] declaredMethods =  connectionClass.getDeclaredMethods();
			for (Method method : declaredMethods) {
				Provides provides = method.getAnnotation(Provides.class);
				if (provides != null) {
					Class<?> returnType = method.getReturnType();
					Supplier<?> supplier = () -> {
						try {
							if (!Modifier.isStatic(method.getModifiers())) {
								Object object = connectionClass.getConstructor().newInstance();
								return method.invoke(object);
							} else {
								return method.invoke(null);
							}
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					};
					registry.put(returnType, supplier);
				}
			}
		}
	}

	public <T> T getInstance(Class<T> managerClass) {
		try {
			T t = managerClass.getConstructor().newInstance();
			Field[] fields = managerClass.getDeclaredFields();
			for (Field field : fields) {
				Inject inject = field.getAnnotation(Inject.class);
				if (inject != null) {
					Class<?> injectedFieldType = field.getType();
					Supplier<?> supplier = registry.get(injectedFieldType);
					Object objectToInject = supplier.get();
					field.setAccessible(true);
					field.set(t, objectToInject);
				}
			}

			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
