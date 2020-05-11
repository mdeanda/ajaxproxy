package com.thedeanda.ajaxproxy.resources;

import com.thedeanda.ajaxproxy.api.ServerConfigDto;
import com.thedeanda.ajaxproxy.core.ServerConfig;
import com.thedeanda.ajaxproxy.mapper.ServerConfigMapper;
import com.thedeanda.ajaxproxy.service.ServerConfigService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("/api/server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerResource {
    private final ServerConfigService serverConfigService;

    public ServerResource(ServerConfigService serverConfigService) {
        this.serverConfigService = serverConfigService;
    }

    @GET
    public void list() {
        log.info("list");
    }

    @GET
    @Path("/{id}")
    public void get(@PathParam("id") long id) {
        log.info("get {}", id);
        //String config = serverConfigDao.findById(id);
        //log.info("config: {}", config);
    }

    @POST
    public void create(ServerConfigDto config) {
        ServerConfig it = ServerConfigMapper.INSTANCE.fromDto(config);

        serverConfigService.save(it);

        log.info("config: {}", config);
    }
}
