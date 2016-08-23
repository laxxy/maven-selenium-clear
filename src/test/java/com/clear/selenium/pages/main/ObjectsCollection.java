package com.clear.selenium.pages.main;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ObjectsCollection<B> {

    private Map<Class<? extends B>, B> collection = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends B> T getInstance(Class<T> typeReference) {
        T result = (T) collection.get(typeReference);
        if (result == null) {
            try {
                Constructor<T> constructor = typeReference.getDeclaredConstructor();
                constructor.setAccessible(true);
                result = constructor.newInstance();
                collection.put(typeReference, result);
                return result;
            } catch (Exception e) {
                throw new AssertionError(String.format("Wasn't able to instantiate [%s] class from ObjectCollection. " +
                        "See inner exception for details.", typeReference.getTypeName()), e);
            }
        } else {
            return result;
        }
    }

    public void clear() {
        collection.clear();
    }
}
