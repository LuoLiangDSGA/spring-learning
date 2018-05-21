##Redis在SpringBoot中的使用
> Redis是一个高性能的内存数据库，在日常开发中运用非常的广泛，主要用作缓存。Redis提供了非常丰富的数据结构，有String，List，Set，ZSet，Hash，
Redis为这些数据结构提供了丰富的原子性操作。弥补了其他NoSQL如Memcached的不足。在SpringBoot中，由于Boot提供了强大的AutoConfiguration，
集成Redis变得非常简单。本文将介绍Redis在SpringBoot中的应用，包括手动使用RedisTemplate进行操作，和使用注解（@Cacheable等）把业务数据缓存到Redis中。

### 开始

### RedisTemplate


### 使用Annotation缓存数据
- @CacheConfig：主要用于配置该类中会用到的一些共用的缓存配置。在这里@CacheConfig(cacheNames = "users")：配置了该数据访问对象中返回的
内容将存储于名为users的缓存对象中，我们也可以不使用该注解，直接通过@Cacheable自己配置缓存集的名字来定义。
- @Cacheable：配置了findByName函数的返回值将被加入缓存。同时在查询时，会先从缓存中获取，若不存在才再发起对数据库的访问。该注解主要有下面几个参数：
1. value、cacheNames：两个等同的参数（cacheNames为Spring 4新增，作为value的别名），用于指定缓存存储的集合名。由于Spring 4中新增了@CacheConfig，
因此在Spring 3中原本必须有的value属性，也成为非必需项了
2. key：缓存对象存储在Map集合中的key值，非必需，缺省按照函数的所有参数组合作为key值，若自己配置需使用SpEL表达式，比如：@Cacheable(key = "#p0")：
使用函数第一个参数作为缓存的key值，更多关于SpEL表达式的详细内容可参考官方文档
3. condition：缓存对象的条件，非必需，也需使用SpEL表达式，只有满足表达式条件的内容才会被缓存，比如：@Cacheable(key = "#p0", condition = "#p0.length() < 3")，
表示只有当第一个参数的长度小于3的时候才会被缓存，若做此配置上面的AAA用户就不会被缓存，读者可自行实验尝试。
4. unless：另外一个缓存条件参数，非必需，需使用SpEL表达式。它不同于condition参数的地方在于它的判断时机，该条件是在函数被调用之后才做判断的，
所以它可以通过对result进行判断。
5. keyGenerator：用于指定key生成器，非必需。若需要指定一个自定义的key生成器，我们需要去实现org.springframework.cache.interceptor.KeyGenerator接口，
并使用该参数来指定。需要注意的是：该参数与key是互斥的
6. cacheManager：用于指定使用哪个缓存管理器，非必需。只有当有多个时才需要使用
7. cacheResolver：用于指定使用那个缓存解析器，非必需。需通过org.springframework.cache.interceptor.CacheResolver接口来实现自己的
缓存解析器，并用该参数指定。
- @CachePut：配置于函数上，能够根据参数定义条件来进行缓存，它与@Cacheable不同的是，它每次都会真实调用函数，所以主要用于数据新增和修改操作上
。它的参数与@Cacheable类似，具体功能可参考上面对@Cacheable参数的解析。
- @CacheEvict：配置于函数上，通常用在删除方法上，用来从缓存中移除相应数据。除了同@Cacheable一样的参数之外，它还有下面两个参数：
1. allEntries：非必需，默认为false。当为true时，会移除所有数据
2. beforeInvocation：非必需，默认为false，会在调用方法之后移除数据。当为true时，会在调用方法之前移除数据。
