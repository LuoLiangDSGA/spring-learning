package org.boot.dubbo.provider.common.serialization;

import com.alibaba.dubbo.common.serialize.support.SerializationOptimizer;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author luoliang
 * @date 2018/7/19
 */
public class SerializationOptimizerImpl implements SerializationOptimizer {
    @Override
    public Collection<Class> getSerializableClasses() {
        List<Class> classes = new LinkedList<>();
        classes.add(InputStream.class);

        return classes;
    }
}
