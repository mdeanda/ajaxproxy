package com.thedeanda.ajaxproxy.db;

import com.thedeanda.ajaxproxy.config.model.ServerConfig;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public interface ServerConfigDao {

    // @RegisterBeanMapper(User.class)
    @SqlQuery("select name from server_config where id = :id")
    List<ServerConfig> findAll();

    @SqlQuery("select name from server_config where id = :id")
    String findById(@Bind("id") long id);

    @SqlUpdate("insert into server_config (name, description) values " +
            "(:name, :description)")
    @GetGeneratedKeys(columnName = "id")
    long insert(@Bind("name") String name, @Bind("description") String desc);


}
