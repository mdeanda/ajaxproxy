package com.thedeanda.ajaxproxy.resources;

import com.thedeanda.ajaxproxy.service.AjaxProxyService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("/server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AjaxProxyResource {
    private final AjaxProxyService ajaxProxyService;

    public AjaxProxyResource(AjaxProxyService ajaxProxyService) {
        this.ajaxProxyService = ajaxProxyService;
    }

    @POST
    @Path("/start")
    public void startProxy() throws Exception {
        ajaxProxyService.startServer();
    }

    @POST
    @Path("/stop")
    public void stopProxy() {
        ajaxProxyService.stopServer();
    }


}
