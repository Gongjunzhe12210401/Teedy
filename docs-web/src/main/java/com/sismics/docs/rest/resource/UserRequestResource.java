package com.sismics.docs.rest.resource;

import com.sismics.docs.core.model.jpa.UserRequest;
import com.sismics.docs.core.service.UserRequestService;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.util.ValidationUtil;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * User request REST resource.
 */
@Path("/user-request")
public class UserRequestResource extends BaseResource {

    private final UserRequestService userRequestService = new UserRequestService();

    /**
     * Guest submits a new request via form.
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitRequest(@FormParam("username") String username,
                                  @FormParam("email") String email,
                                  @FormParam("reason") String reason) {
        try {
            // 参数校验
            username = ValidationUtil.validateLength(username, "username", 1, 255);
            ValidationUtil.validateEmail(email, "email"); // ✅ 正确
            reason = ValidationUtil.validateLength(reason, "reason", 0, 1000, true);

            userRequestService.submitRequest(username, email, reason);
            JsonObjectBuilder response = Json.createObjectBuilder()
                    .add("status", "ok");
            return Response.ok(response.build()).build();
        } catch (ClientException e) {
            JsonObjectBuilder error = Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error.build()).build();
        } catch (Exception e) {
            JsonObjectBuilder error = Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "服务器内部错误: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error.build()).build();
        }
    }

    /**
     * Admin fetches all registration requests.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRequests() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        List<UserRequest> requests = userRequestService.getAllRequests();
        JsonArrayBuilder items = Json.createArrayBuilder();
        for (UserRequest r : requests) {
            items.add(Json.createObjectBuilder()
                    .add("id", r.getId())
                    .add("username", r.getUsername())
                    .add("email", r.getEmail())
                    .add("reason", r.getReason() != null ? r.getReason() : "")
                    .add("status", r.getStatus())
                    .add("createDate", r.getCreateDate().toString())
            );
        }

        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("requests", items);
        return Response.ok(response.build()).build();
    }

    /**
     * Admin approves a request.
     */
    @POST
    @Path("/{id}/approve")
    @Produces(MediaType.APPLICATION_JSON)
    public Response approveRequest(@PathParam("id") Long id) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        try {
            userRequestService.approveRequest(id);
            JsonObjectBuilder response = Json.createObjectBuilder()
                    .add("status", "approved");
            return Response.ok(response.build()).build();
        } catch (Exception e) {
            JsonObjectBuilder error = Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error.build()).build();
        }
    }

    /**
     * Admin rejects a request.
     */
    @POST
    @Path("/{id}/reject")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rejectRequest(@PathParam("id") Long id) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        try {
            userRequestService.rejectRequest(id);
            JsonObjectBuilder response = Json.createObjectBuilder()
                    .add("status", "rejected");
            return Response.ok(response.build()).build();
        } catch (Exception e) {
            JsonObjectBuilder error = Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error.build()).build();
        }
    }
}
