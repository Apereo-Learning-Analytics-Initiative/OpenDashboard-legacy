/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * https://github.com/Loki-Afro/multi-tenant-spring-mongodb/blob/master/src/main/java/com/github/zarathustra/mongo/MultiTenantMongoDbFactory.java
 * 
 * 
 */

package od.repository.mongo;

import java.util.HashMap;

import od.TenantService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver.IndexDefinitionHolder;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.util.Assert;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class MultiTenantMongoDbFactory extends SimpleMongoDbFactory {

  @Autowired
  private TenantService tenantService;

  private final String defaultName;
  private static final Logger logger = LoggerFactory.getLogger(MultiTenantMongoDbFactory.class);

  private static final HashMap<String, Object> databaseIndexMap = new HashMap<String, Object>();
  private MongoTemplate mongoTemplate = null;

  public MultiTenantMongoDbFactory(final Mongo mongo, final String defaultDatabaseName) {
    super(mongo, defaultDatabaseName);
    logger.debug("Instantiating " + MultiTenantMongoDbFactory.class.getName() + " with default database name: " + defaultDatabaseName);
    this.defaultName = defaultDatabaseName;
  }

  // dirty but ... what can I do?
  public void setMongoTemplate(final MongoTemplate mongoTemplate) {
    Assert.isNull(this.mongoTemplate, "You can set MongoTemplate just once");
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public DB getDb() {
    logger.debug("tenant service {}", tenantService.getTenant());
    final String tlName = tenantService.getTenant();
    final String dbToUse = (tlName != null ? tlName : this.defaultName);
    logger.debug("Acquiring database: " + dbToUse);
    createIndexIfNecessaryFor(dbToUse);
    return super.getDb(dbToUse);
  }

  private void createIndexIfNecessaryFor(final String database) {
    if (this.mongoTemplate == null) {
      logger.error("MongoTemplate is null, will not create any index.");
      return;
    }
    // sync and init once
    boolean needsToBeCreated = false;
    synchronized (MultiTenantMongoDbFactory.class) {
      final Object syncObj = databaseIndexMap.get(database);
      if (syncObj == null) {
        databaseIndexMap.put(database, new Object());
        needsToBeCreated = true;
      }
    }
    // make sure only one thread enters with needsToBeCreated = true
    synchronized (databaseIndexMap.get(database)) {
      if (needsToBeCreated) {
        logger.debug("Creating indices for database name=[" + database + "]");
        createIndexes();
        logger.debug("Done with creating indices for database name=[" + database + "]");
      }
    }
  }

  private void createIndexes() {
    final MongoMappingContext mappingContext = (MongoMappingContext) this.mongoTemplate.getConverter().getMappingContext();
    final MongoPersistentEntityIndexResolver indexResolver = new MongoPersistentEntityIndexResolver(mappingContext);
    for (BasicMongoPersistentEntity<?> persistentEntity : mappingContext.getPersistentEntities()) {
      checkForAndCreateIndexes(indexResolver, persistentEntity);
    }
  }

  private void checkForAndCreateIndexes(final MongoPersistentEntityIndexResolver indexResolver, final MongoPersistentEntity<?> entity) {
    // make sure its a root document
    if (entity.findAnnotation(Document.class) != null) {
      for (IndexDefinitionHolder indexDefinitionHolder : indexResolver.resolveIndexForClass(entity.getType())) {
        // work because of javas reentered lock feature
        this.mongoTemplate.indexOps(entity.getType()).ensureIndex(indexDefinitionHolder);
      }
    }
  }
}