package com.thedeanda.ajaxproxy.resources;

import com.thedeanda.ajaxproxy.api.RequestDto;
import com.thedeanda.ajaxproxy.api.RequestDtoListItem;
import com.thedeanda.ajaxproxy.service.RequestService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Slf4j
@Path("/requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RequestResource {
    private final RequestService requestService;

    public RequestResource(RequestService requestService) {
        this.requestService = requestService;
    }

    @GET
    public List<RequestDtoListItem> find() {
        return requestService.find();
    }

    @GET
    @Path("/{id}")
    public RequestDto get(@PathParam("id") String id) {
        return requestService.get(id);
    }
}
