package net.pranavprakash.core;

import net.pranavprakash.db.TodoDao;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import java.util.List;
import java.util.Objects;

public abstract class TodoService {

    private static final String TODO_NOT_FOUND = "Todo id %s not found.";
    private static final String SUCCESS = "Success";
    private static final String UNEXPECTED_DELETE_ERROR = "An unexpected error occurred while deleting todo.";

    private static final String DATABASE_ACCESS_ERROR =
            "Could not reach the MySQL database. The database may be down or there may be network connectivity issues. Details: ";
    private static final String DATABASE_CONNECTION_ERROR =
            "Could not create a connection to the MySQL database. The database configurations are likely incorrect. Details: ";
    private static final String UNEXPECTED_DATABASE_ERROR =
            "Unexpected error occurred while attempting to reach the database. Details: ";

    @CreateSqlObject
    abstract TodoDao todoDao();

    public List<Todo> getTodos () {
        return todoDao().getTodos();
    }

    public Todo getTodo(Integer id) {
        Todo todo = todoDao().getTodo(id);
        if(Objects.isNull(todo)) {
            throw new WebApplicationException(String.format(TODO_NOT_FOUND, id), Status.NOT_FOUND);
        }
        return todo;
    }

    public Todo createTodo(Todo todo) {
        todoDao().createTodo(todo);
        return todoDao().getTodo(todoDao().lastInsertId());
    }

    public Todo editTodo(Todo todo) {
        if(Objects.isNull(todoDao().getTodo(todo.getId()))) {
            throw new WebApplicationException(String.format(TODO_NOT_FOUND, todo.getId()), Status.NOT_FOUND);
        }
        todoDao().editTodo(todo);
        return todoDao().getTodo(todo.getId());
    }

    public String deleteTodo(final Integer id) {
        Integer result = todoDao().deleteTodo(id);
        switch (result) {
            case 1:
                return SUCCESS;
            case 0:
                throw new WebApplicationException(String.format(TODO_NOT_FOUND, id), Status.NOT_FOUND);
            default:
                throw new WebApplicationException(String.format(UNEXPECTED_DELETE_ERROR, id), Status.INTERNAL_SERVER_ERROR);
        }
    }

    public String performHealthCheck() {
        try {
            todoDao().getTodos();
        } catch (UnableToObtainConnectionException ex) {
            return checkUnableToObtainConnectionException(ex);
        } catch (UnableToExecuteStatementException ex) {
            return checkUnableToExecuteStatementException(ex);
        } catch (Exception ex) {
            return UNEXPECTED_DATABASE_ERROR + ex.getCause().getLocalizedMessage();
        }
        return null;
    }

    private String checkUnableToObtainConnectionException(UnableToObtainConnectionException ex) {
        if (ex.getCause() instanceof java.sql.SQLNonTransientConnectionException) {
            return DATABASE_ACCESS_ERROR + ex.getCause().getLocalizedMessage();
        } else if (ex.getCause() instanceof java.sql.SQLException) {
            return DATABASE_CONNECTION_ERROR + ex.getCause().getLocalizedMessage();
        } else {
            return UNEXPECTED_DATABASE_ERROR + ex.getCause().getLocalizedMessage();
        }
    }

    private String checkUnableToExecuteStatementException(UnableToExecuteStatementException ex) {
        if (ex.getCause() instanceof java.sql.SQLSyntaxErrorException) {
            return DATABASE_CONNECTION_ERROR + ex.getCause().getLocalizedMessage();
        } else {
            return UNEXPECTED_DATABASE_ERROR + ex.getCause().getLocalizedMessage();
        }
    }

}
