##Redis在SpringBoot中的使用
> Redis是一个高性能的内存数据库，在日常开发中运用非常的广泛，主要用作缓存。Redis提供了非常丰富的数据结构，有String，List，Set，ZSet，Hash，
Redis为这些数据结构提供了丰富的原子性操作。弥补了其他NoSQL如Memcached的不足。在SpringBoot中，由于Boot提供了强大的AutoConfiguration，
集成Redis变得非常简单。本文将介绍Redis在SpringBoot中的应用，包括手动使用RedisTemplate进行操作，和使用注解（@Cacheable等）把业务数据缓存到Redis中。

### 开始

### RedisTemplate


### 使用Annotation缓存数据