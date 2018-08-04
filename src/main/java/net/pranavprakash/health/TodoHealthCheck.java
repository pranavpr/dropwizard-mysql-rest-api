package net.pranavprakash.health;

import com.codahale.metrics.health.HealthCheck;
import net.pranavprakash.core.TodoService;

public class TodoHealthCheck extends HealthCheck {
    private static final String HEALTHY_MESSAGE = "The Todo Service is healthy for read and write";
    private static final String UNHEALTHY_MESSAGE = "The Todo Service is not healthy. ";

    private final TodoService todoService;

    public TodoHealthCheck(TodoService todoService) {
        this.todoService = todoService;
    }

    @Override
    public Result check() throws Exception {
        String mySqlHealthStatus = todoService.performHealthCheck();

        if (mySqlHealthStatus == null) {
            return Result.healthy(HEALTHY_MESSAGE);
        } else {
            return Result.unhealthy(UNHEALTHY_MESSAGE , mySqlHealthStatus);
        }
    }
}
