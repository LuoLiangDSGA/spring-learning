package org.boot.dubbo.provider.common.serialization;

import org.apache.dubbo.common.serialize.support.SerializationOptimizer;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author luoliang
 * @date 2018/7/19
 */
public class SerializationOptimizerImpl implements SerializationOptimizer {
    @Override
    public Collection<Class<?>> getSerializableClasses() {
        Collection<Class<?>> classes = new LinkedList<>();
        classes.add(InputStream.class);

        return classes;
    }
}
