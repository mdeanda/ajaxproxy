package com.thedeanda.ajaxproxy.resources;

import com.thedeanda.ajaxproxy.api.ProxyConfigDto;
import com.thedeanda.ajaxproxy.api.ProxyConfigRequestDto;
import com.thedeanda.ajaxproxy.service.ServerConfigService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Slf4j
@Path("/config/server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerProxyResource {
    private final ServerConfigService serverConfigService;

    public ServerProxyResource(ServerConfigService serverConfigService) {
        this.serverConfigService = serverConfigService;
    }

    @GET
    @Path("/{id}/proxy")
    public List<ProxyConfigDto> listProxies(@PathParam("id") int id) {
        return serverConfigService.listProxies(id);
    }

    @GET
    @Path("/{id}/proxy/{pid}")
    public ProxyConfigDto getProxy(@PathParam("id") int id, @PathParam("pid") int proxyId) {
        return serverConfigService.getProxy(id, proxyId);
    }

    @PUT
    @Path("/{id}/proxy/{pid}/type/request")
    public void update(@PathParam("id") int id, @PathParam("pid") int proxyId, ProxyConfigRequestDto dto) {
        //TODO verify id's match
        if (dto.getId() != proxyId) {
            throw new IllegalArgumentException("Invalid Proxy ID: " + proxyId);
        }
        serverConfigService.updateProxy(id, proxyId, dto);
        log.warn("dto: {}", dto);
    }

    /*
    @POST
    public void create(ServerConfigDto config) {
        //ServerConfig it = ServerConfigMapper.INSTANCE.fromDto(config);

        //serverConfigService.save(it);

        log.info("config: {}", config);
    }
    */


}
