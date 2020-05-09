package com.thedeanda.ajaxproxy.db;

import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

public interface ServerConfigDao {

    @SqlQuery("select name from something where id = :id")
    String findNameById(@Bind("id") int id);

}
