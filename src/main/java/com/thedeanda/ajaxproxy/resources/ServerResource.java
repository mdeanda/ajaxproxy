package com.thedeanda.ajaxproxy.resources;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("/api/server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerResource {

    @GET
    public void list() {
        log.info("list");
    }

    @GET
    @Path("/{id}")
    public void get(@PathParam("id") long id) {
        log.info("get {}", id);
    }
}
