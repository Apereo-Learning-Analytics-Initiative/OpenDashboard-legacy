package od.repository;

import od.model.Session;


public interface SessionRepositoryInterface {
    Session findOne(final String key);
    Session save(final Session session);
}
