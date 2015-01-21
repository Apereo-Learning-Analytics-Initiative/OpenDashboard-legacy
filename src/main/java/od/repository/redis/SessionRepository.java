/**
 *
 */
package od.repository.redis;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import lti.LaunchRequest;
import od.model.Session;
import od.repository.SessionRepositoryInterface;
import od.repository.redis.model.RedisLaunchRequest;
import od.repository.redis.utils.KeyUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.DecoratingStringHashMapper;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.JacksonHashMapper;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.data.redis.support.collections.RedisMap;
import org.springframework.stereotype.Repository;
/**
 * @author ggilbert
 *
 */
@Repository
@Profile("redis")
public class SessionRepository implements SessionRepositoryInterface {
    private static final Logger logger = LoggerFactory.getLogger(SessionRepository.class);

    private String[] fieldsToNotCopy = new String[] {"custom","ext","extra"};
    private RedisAtomicLong sessionIdCounter;
    private final HashMapper<RedisLaunchRequest, String, String> launchRequestMapper = new DecoratingStringHashMapper<RedisLaunchRequest>(
            new JacksonHashMapper<RedisLaunchRequest>(RedisLaunchRequest.class));

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private KeyUtils keyUtils;

    @PostConstruct
    public void setup() {
        sessionIdCounter = new RedisAtomicLong(keyUtils.globalSessionId(), redisTemplate.getConnectionFactory());
    }

    @Override
    public Session findOne(String sessionId) {
        Session session = new Session();
        session.setId(sessionId);

        BoundHashOperations<String, String, String> sessionOps = redisTemplate.boundHashOps(keyUtils.session(sessionId));
        Long timestamp = Long.valueOf(sessionOps.get("timeStamp"));
        session.setTimestamp(timestamp);

        RedisLaunchRequest redisLaunchRequest = this.getLaunchRequest(sessionId);
        if (redisLaunchRequest == null) {
            return null;
        }
        Map<String, LaunchRequest> data = new LinkedHashMap<>();
        LaunchRequest launchRequest = new LaunchRequest();
        BeanUtils.copyProperties(redisLaunchRequest, launchRequest, fieldsToNotCopy);
        launchRequest.setExt(ltiLaunchRequestExt(session.getId()));
        launchRequest.setExtra(ltiLaunchRequestExtra(session.getId()));
        launchRequest.setCustom(ltiLaunchRequestCustom(session.getId()));
        data.put("lti", launchRequest);
        session.setData(data);
        return session;
    }

    @Override
    public Session save(Session session) {
        if (StringUtils.isEmpty(session.getId())) {
            session.setId(String.valueOf(sessionIdCounter.incrementAndGet()));
        }

        // add session timestamp
        BoundHashOperations<String, String, String> sessionOps = redisTemplate.boundHashOps(keyUtils.session(session.getId()));
        sessionOps.delete("timeStamp");
        sessionOps.put("timeStamp", String.valueOf(session.getTimestamp()));
        logger.info("adding session timestamp:{}, {}", session.getId(), session.getTimestamp());

        // add Launch Request
        RedisLaunchRequest redisLaunchRequest = new RedisLaunchRequest();
        LaunchRequest launchRequest = session.getData().get("lti");
        BeanUtils.copyProperties(launchRequest, redisLaunchRequest, fieldsToNotCopy);

     // Wiping any existing data allows save to be update or create
        this.clearExistingSession(session);

        logger.info("adding launch request:{}, {}", session.getId(), redisLaunchRequest);
        this.ltiLaunchRequest(session.getId()).putAll(launchRequestMapper.toHash(redisLaunchRequest));

        logger.info("adding launch request Ext:{}, {}", session.getId(), launchRequest.getExt());
        this.ltiLaunchRequestExt(session.getId()).putAll(launchRequest.getExt());

        logger.info("adding launch request Extra:{}, {}", session.getId(), launchRequest.getExtra());
        this.ltiLaunchRequestExtra(session.getId()).putAll(launchRequest.getExtra());

        logger.info("adding launch request Custom:{}, {}", session.getId(), launchRequest.getCustom());
        this.ltiLaunchRequestCustom(session.getId()).putAll(launchRequest.getCustom());

        return session;
    }

    private void clearExistingSession(Session session) {
        this.ltiLaunchRequest(session.getId()).clear();
        this.ltiLaunchRequestExt(session.getId()).clear();
        this.ltiLaunchRequestExtra(session.getId()).clear();
        this.ltiLaunchRequestCustom(session.getId()).clear();
    }

    public RedisLaunchRequest getLaunchRequest(String sessionId) {
        return this.convertLtiLaunchRequest(ltiLaunchRequest(sessionId));
    }

    private RedisMap<String, String> ltiLaunchRequest(String sessionId) {
        return new DefaultRedisMap<String, String>(keyUtils.ltiLaunchRequest(sessionId), redisTemplate);
    }

    private RedisLaunchRequest convertLtiLaunchRequest(RedisMap<String, String> hash) {
        logger.info("Map: {}", hash);
        return launchRequestMapper.fromHash(hash);
    }

    private RedisMap<String, String> ltiLaunchRequestExt(String sessionId) {
        return new DefaultRedisMap<String, String>(keyUtils.ltiLaunchRequestExt(sessionId), redisTemplate);
    }

    private RedisMap<String, String> ltiLaunchRequestExtra(String sessionId) {
        return new DefaultRedisMap<String, String>(keyUtils.ltiLaunchRequestExtra(sessionId), redisTemplate);
    }

    private RedisMap<String, String> ltiLaunchRequestCustom(String sessionId) {
        return new DefaultRedisMap<String, String>(keyUtils.ltiLaunchRequestCustom(sessionId), redisTemplate);
    }
}
