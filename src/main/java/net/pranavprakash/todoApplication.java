package net.pranavprakash;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.pranavprakash.resources.TodoResource;
import net.pranavprakash.core.TodoService;
import net.pranavprakash.health.TodoHealthCheck;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class todoApplication extends Application<todoConfiguration> {

    private static final String SQL = "sql";
    private static final String TODO_SERVICE = "todo";

    public static void main(final String[] args) throws Exception {
        new todoApplication().run(args);
    }

    @Override
    public String getName() {
        return "todo";
    }

    @Override
    public void initialize(final Bootstrap<todoConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final todoConfiguration configuration,
                    final Environment environment) {
        // Data source configuration
        final DataSource dataSource =
                configuration.getDataSourceFactory().build(environment.metrics(), SQL);
        DBI dbi = new DBI(dataSource);
        // Register Health Check
        TodoHealthCheck healthCheck =
                new TodoHealthCheck(dbi.onDemand(TodoService.class));
        environment.healthChecks().register(TODO_SERVICE, healthCheck);
        environment.jersey().register(new TodoResource(dbi.onDemand(TodoService.class)));
    }

}
