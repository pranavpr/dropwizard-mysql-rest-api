package net.pranavprakash.resources;

import com.codahale.metrics.annotation.Timed;
import net.pranavprakash.api.Representation;
import net.pranavprakash.core.Todo;
import net.pranavprakash.core.TodoService;
import org.eclipse.jetty.http.HttpStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {
    private final TodoService todoService;

    public TodoResource(TodoService todoService) {
        this.todoService = todoService;
    }

    @GET
    @Timed
    public Representation<List<Todo>> getTodos() {
        return new Representation<List<Todo>>(HttpStatus.OK_200, todoService.getTodos());
    }

    @GET
    @Timed
    @Path("{id}")
    public Representation<Todo> getTodo(@PathParam("id") final Integer id) {
        return new Representation<Todo>(HttpStatus.OK_200, todoService.getTodo(id));
    }

    @POST
    @Timed
    public Representation<Todo> createTodo(@NotNull @Valid final Todo todo) {
        Todo todoCreate = new Todo(todo.getName(), todo.getStatus());
        return new Representation<Todo>(HttpStatus.OK_200, todoService.createTodo(todoCreate));
    }

    @PUT
    @Timed
    @Path("{id}")
    public Representation<Todo> updateTodo(@PathParam("id") final Integer id, @NotNull @Valid final Todo todo) {
        todo.setId(id);
        return new Representation<Todo>(HttpStatus.OK_200, todoService.editTodo(todo));
    }

    @DELETE
    @Timed
    @Path("{id}")
    public Representation<String> deleteTodo(@PathParam("id") final Integer id) {
        return new Representation<String>(HttpStatus.OK_200, todoService.deleteTodo(id));
    }
}
