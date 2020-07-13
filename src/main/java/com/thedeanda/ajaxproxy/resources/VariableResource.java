package com.thedeanda.ajaxproxy.resources;

import com.thedeanda.ajaxproxy.api.VariableDto;
import com.thedeanda.ajaxproxy.service.VariableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Slf4j
@Path("/variable")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VariableResource {
    private final VariableService variableService;

    public VariableResource(VariableService variableService) {
        this.variableService = variableService;
    }

    @GET
    public List<VariableDto> get() {

        return variableService.getVariables();
    }

    @DELETE
    @Path("/{key}")
    public void remove(@PathParam("key") String key) {
        log.info("remove variable [{}]", key);
    }

    @POST
    @Path("/{key}")
    public void create(VariableDto variable) {
        log.info("create variable {}", variable);

        variableService.create(variable);
    }

    @PUT
    @Path("/{key}")
    public void update(@PathParam("key") String key, VariableDto variable) {
        log.info("update variable [{}] with {}", key, variable);

        if (!StringUtils.equals(variable.getKey(), key) || StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Invalid variable: " + key);
        }

        variableService.update(variable);
    }
}
