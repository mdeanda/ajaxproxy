package com.thedeanda.ajaxproxy;

import com.thedeanda.ajaxproxy.config.ConfigFileService;
import com.thedeanda.ajaxproxy.health.SampleHealthCheck;
import com.thedeanda.ajaxproxy.mapper.ServerConfigMapper;
import com.thedeanda.ajaxproxy.resources.AjaxProxyResource;
import com.thedeanda.ajaxproxy.resources.ServerProxyResource;
import com.thedeanda.ajaxproxy.resources.ServerResource;
import com.thedeanda.ajaxproxy.service.AjaxProxyService;
import com.thedeanda.ajaxproxy.service.ServerConfigService;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.mapstruct.factory.Mappers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AjaxProxyApplication extends Application<AjaxProxyConfiguration> {
    private final String configFile;

    public static void main(String[] args) throws Exception {
        args = new String[]{"server", "config.yml"};
        new AjaxProxyApplication("ui_config.json").run(args);
    }

    public AjaxProxyApplication(String configFile) {
        super();
        this.configFile = configFile;
    }

    @Override
    public String getName() {
        return "AjaxProxy";
    }

    @Override
    public void initialize(final Bootstrap<AjaxProxyConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));

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


        ConfigFileService configFileService;
        try {
            configFileService = new ConfigFileService(new File(configFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final ServerConfigMapper serverConfigMapper = Mappers.getMapper(ServerConfigMapper.class);

        //services
        final ServerConfigService serverConfigService = new ServerConfigService(configFileService, serverConfigMapper);
        final AjaxProxyService ajaxProxyService = new AjaxProxyService(configFileService);


        //resources
        final List<Object> resources = new ArrayList<>();
        resources.add(new ServerResource(serverConfigService));
        resources.add(new ServerProxyResource(serverConfigService));
        resources.add(new AjaxProxyResource(ajaxProxyService));

        environment.jersey().setUrlPattern("/api/*");

        environment.healthChecks().register("sample", new SampleHealthCheck());
        resources.forEach(service ->
                environment.jersey().register(service)
        );

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
