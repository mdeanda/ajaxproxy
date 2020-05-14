package com.thedeanda.ajaxproxy.resources;

import com.thedeanda.ajaxproxy.api.AjaxProxyStatus;
import com.thedeanda.ajaxproxy.service.AjaxProxyService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
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

    @GET
    @Path("/status")
    public AjaxProxyStatus getStatus() {
        return ajaxProxyService.getStatus();
    }


}
