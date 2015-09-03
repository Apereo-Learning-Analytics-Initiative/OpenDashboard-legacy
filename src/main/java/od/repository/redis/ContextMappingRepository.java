/**
 *
 */
package od.repository.redis;

import java.util.Set;

import javax.annotation.PostConstruct;

import od.framework.model.ContextMapping;
import od.framework.model.Dashboard;
import od.repository.ContextMappingRepositoryInterface;
import od.repository.redis.model.RedisContextMapping;
import od.repository.redis.utils.KeyUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.DecoratingStringHashMapper;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.JacksonHashMapper;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.data.redis.support.collections.DefaultRedisSet;
import org.springframework.data.redis.support.collections.RedisMap;
import org.springframework.data.redis.support.collections.RedisSet;
import org.springframework.stereotype.Repository;

/**
 * @author ggilbert
 *
 */
@Repository
@Profile("redis")
public class ContextMappingRepository implements ContextMappingRepositoryInterface {
    private static final Logger logger = LoggerFactory.getLogger(ContextMappingRepository.class);

    private String[] fieldsToNotCopy = new String[] {"dashboards"};
    private RedisAtomicLong contextMappingIdCounter;
    private final HashMapper<RedisContextMapping, String, String> contextMappingMapper = new DecoratingStringHashMapper<RedisContextMapping>(
            new JacksonHashMapper<RedisContextMapping>(RedisContextMapping.class));

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisTemplate<String, Dashboard> redisSetTemplate;
    @Autowired
    private KeyUtils keyUtils;


    @PostConstruct
    public void setup() {
        contextMappingIdCounter = new RedisAtomicLong(keyUtils.globalContextMappingId(), redisTemplate.getConnectionFactory());
    }

    @Override
    public ContextMapping findByKeyAndContext(final String key, final String context) {
        ContextMapping contextMapping = new ContextMapping();
        RedisContextMapping redisContextMapping = this.getContextMappingByKeyAndContext(key, context);
        BeanUtils.copyProperties(redisContextMapping, contextMapping, fieldsToNotCopy);
        Set<Dashboard> dashboards = this.contextMappingDashboardsByKeyAndContext(key, context);
        contextMapping.setDashboards(dashboards);
        return contextMapping;
    }

    @Override
    public ContextMapping findOne(String contextMappingId) {
        ContextMapping contextMapping = new ContextMapping();
        RedisContextMapping redisContextMapping = this.getContextMapping(contextMappingId);
        BeanUtils.copyProperties(redisContextMapping, contextMapping, fieldsToNotCopy);
        Set<Dashboard> dashboards = this.contextMappingDashboards(contextMappingId);
        contextMapping.setDashboards(dashboards);
        return contextMapping;
    }

    @Override
    public ContextMapping save(ContextMapping contextMapping) {
        if (StringUtils.isEmpty(contextMapping.getId())) {
            contextMapping.setId(String.valueOf(contextMappingIdCounter.incrementAndGet()));
        }
        RedisContextMapping redisContextMapping = new RedisContextMapping();
        BeanUtils.copyProperties(contextMapping, redisContextMapping, fieldsToNotCopy);

        // Wiping any existing data allows save to be update or create
        this.clearExistingContextMapping(redisContextMapping);

        this.contextMapping(redisContextMapping.getId()).putAll(contextMappingMapper.toHash(redisContextMapping));
        this.contextMappingByKeyAndContext(redisContextMapping.getKey(), redisContextMapping.getContext()).putAll(contextMappingMapper.toHash(redisContextMapping));

        Set<Dashboard> dashboards = contextMapping.getDashboards();
        if (dashboards != null && !dashboards.isEmpty()) {
            this.contextMappingDashboards(redisContextMapping.getId()).addAll(contextMapping.getDashboards());
            this.contextMappingDashboardsByKeyAndContext(redisContextMapping.getKey(), redisContextMapping.getContext()).addAll(contextMapping.getDashboards());
        }
        return contextMapping;
    }

    private void clearExistingContextMapping(RedisContextMapping redisContextMapping) {
        this.contextMapping(redisContextMapping.getId()).clear();
        this.contextMappingByKeyAndContext(redisContextMapping.getKey(), redisContextMapping.getContext()).clear();
        this.contextMappingDashboards(redisContextMapping.getId()).clear();
        this.contextMappingDashboardsByKeyAndContext(redisContextMapping.getKey(), redisContextMapping.getContext()).clear();
    }

    public RedisContextMapping getContextMapping(String contextMappingId) {
        return this.convertContextMapping(this.contextMapping(contextMappingId));
    }

    public RedisContextMapping getContextMappingByKeyAndContext(String key, String context) {
        return this.convertContextMapping(this.contextMappingByKeyAndContext(key, context));
    }

    private RedisMap<String, String> contextMapping(String contextMappingId) {
        return new DefaultRedisMap<String, String>(keyUtils.contextMapping(contextMappingId), redisTemplate);
    }

    private RedisMap<String, String> contextMappingByKeyAndContext(String key, String context) {
        return new DefaultRedisMap<String, String>(keyUtils.contextMappingByKeyAndContext(key, context), redisTemplate);
    }

    private RedisSet<Dashboard> contextMappingDashboards(String contextMappingId) {
        return new DefaultRedisSet<Dashboard>(keyUtils.contextMappingDashboards(contextMappingId), redisSetTemplate);
    }

    private RedisSet<Dashboard> contextMappingDashboardsByKeyAndContext(String key, String context) {
        return new DefaultRedisSet<Dashboard>(keyUtils.contextMappingByKeyAndContextDashboards(key, context), redisSetTemplate);
    }

    private RedisContextMapping convertContextMapping(RedisMap<String, String> hash) {
        logger.info("Map: {}", hash);
        return contextMappingMapper.fromHash(hash);
    }
}
