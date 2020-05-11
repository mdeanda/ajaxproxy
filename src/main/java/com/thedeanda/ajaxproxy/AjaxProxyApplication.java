package com.thedeanda.ajaxproxy;

import com.j256.ormlite.dao.DaoManager;
import com.thedeanda.ajaxproxy.config.ConfigFileService;
import com.thedeanda.ajaxproxy.health.SampleHealthCheck;
import com.thedeanda.ajaxproxy.resources.ServerResource;
import com.thedeanda.ajaxproxy.service.ServerConfigService;
import com.thedeanda.javajson.JsonException;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class AjaxProxyApplication extends Application<AjaxProxyConfiguration> {

    public static void main(String[] args) throws Exception {
        args = new String[]{"server", "config.yml"};
        new AjaxProxyApplication().run(args);
    }

    @Override
    public String getName() {
        return "AjaxProxy";
    }

    @Override
    public void initialize(final Bootstrap<AjaxProxyConfiguration> bootstrap) {


    }

    @Override
    public void run(final AjaxProxyConfiguration config,
                    final Environment environment) {
        // TODO: implement application

        /*
        try {
            runLiquibase(config, environment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //*/

        //final DBIFactory factory = new DBIFactory();
        //final DBI jdbi = factory.build(environment, config.getDataSourceFactory(), "datasource");
        // jdbi.installPlugin(new SqlObjectPlugin());


        ConfigFileService configFileService = null;
        try {
            configFileService = new ConfigFileService(new File("sample_config.json"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //services
        final ServerConfigService serverConfigService = new ServerConfigService();

        //resources
        final ServerResource resource = new ServerResource(serverConfigService);



        environment.healthChecks().register("sample", new SampleHealthCheck());
        environment.jersey().register(resource);
    }

    /*
    private void runLiquibase(final AjaxProxyConfiguration config, final Environment environment) throws Exception {
        DataSource dataSource = config.getDataSourceFactory().build(environment.metrics(), "mydatasource");

        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
        Liquibase liquibase = new liquibase.Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
        liquibase.close();

    }//*/
}
