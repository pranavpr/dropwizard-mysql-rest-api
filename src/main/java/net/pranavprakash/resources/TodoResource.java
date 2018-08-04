package net.pranavprakash.resources;

import com.codahale.metrics.annotation.Timed;
import net.pranavprakash.core.Todo;
import net.pranavprakash.core.TodoService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {
    private final TodoService todoService;

    public TodoResource(TodoService todoService) {
        this.todoService = todoService;
    }

    @GET
    @Timed
    public Response getTodos() {
        return Response.ok(todoService.getTodos()).build();
    }

    @GET
    @Timed
    @Path("{id}")
    public Response getTodo(@PathParam("id") final Integer id) {
        return Response.ok(todoService.getTodo(id)).build();
    }

    @POST
    @Timed
    public Response createTodo(@NotNull @Valid final Todo todo) {
        Todo todoCreate = new Todo(todo.getName(), todo.getStatus());
        return Response.ok(todoService.createTodo(todoCreate)).build();
    }

    @PUT
    @Timed
    @Path("{id}")
    public Response updateTodo(@PathParam("id") final Integer id, @NotNull @Valid final Todo todo) {
        todo.setId(id);
        return Response.ok(todoService.editTodo(todo)).build();
    }

    @DELETE
    @Timed
    @Path("{id}")
    public Response deleteTodo(@PathParam("id") final Integer id) {
        Map<String, String> response = new HashMap<>();
        response.put("status", todoService.deleteTodo(id));
        return Response.ok(response).build();
    }
}
