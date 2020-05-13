package com.thedeanda.ajaxproxy.resources;

import com.thedeanda.ajaxproxy.api.ServerConfigDto;
import com.thedeanda.ajaxproxy.service.ServerConfigService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Slf4j
@Path("/config/server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerResource {
    private final ServerConfigService serverConfigService;

    public ServerResource(ServerConfigService serverConfigService) {
        this.serverConfigService = serverConfigService;
    }

    @GET
    public List<ServerConfigDto> list() {
        return serverConfigService.list();
    }

    @GET
    @Path("/{id}")
    public ServerConfigDto get(@PathParam("id") int id) {
        return serverConfigService.get(id);
    }

    @POST
    public void create(ServerConfigDto config) {
        //ServerConfig it = ServerConfigMapper.INSTANCE.fromDto(config);

        //serverConfigService.save(it);

        log.info("config: {}", config);
    }
}
