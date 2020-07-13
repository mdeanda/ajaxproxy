package com.thedeanda.ajaxproxy;

import com.thedeanda.ajaxproxy.config.ConfigFileService;
import com.thedeanda.ajaxproxy.health.SampleHealthCheck;
import com.thedeanda.ajaxproxy.mapper.RequestMapper;
import com.thedeanda.ajaxproxy.mapper.ServerConfigMapper;
import com.thedeanda.ajaxproxy.mapper.VariableMapper;
import com.thedeanda.ajaxproxy.resources.*;
import com.thedeanda.ajaxproxy.service.*;
import com.thedeanda.ajaxproxy.ui.ConfigService;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.mapstruct.factory.Mappers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AjaxProxyApplication extends Application<AjaxProxyConfiguration> {
    private static final int CACHE_SIZE = 50;

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

        ConfigFileService configFileService;
        try {
            configFileService = new ConfigFileService(new File(configFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final ServerConfigMapper serverConfigMapper = Mappers.getMapper(ServerConfigMapper.class);
        final RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);
        final VariableMapper variableMapper = Mappers.getMapper(VariableMapper.class);

        //services
        File dbFile = ConfigService.get().getResourceHistoryDb();
        ResourceService resourceService = new ResourceService(CACHE_SIZE, dbFile);


        final ServerConfigService serverConfigService = new ServerConfigService(configFileService, serverConfigMapper);
        final AjaxProxyService ajaxProxyService = new AjaxProxyService(configFileService, resourceService);
        final RequestService requestService = new RequestService(resourceService, requestMapper);
        final VariableService variableService = new VariableService(configFileService, variableMapper);


        //resources
        final List<Object> resources = new ArrayList<>();
        resources.add(new ServerResource(serverConfigService));
        resources.add(new ServerProxyResource(serverConfigService));
        resources.add(new AjaxProxyResource(ajaxProxyService));
        resources.add(new RequestResource(requestService));
        resources.add(new VariableResource(variableService));

        environment.jersey().setUrlPattern("/api/*");

        environment.healthChecks().register("sample", new SampleHealthCheck());
        resources.forEach(service ->
                environment.jersey().register(service)
        );

    }

}
