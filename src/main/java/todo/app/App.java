package todo.app;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import todo.domain.Greeter;

/**
 * Starting point of the application contaning the
 * main function.
 */
public class App implements Runnable {

    public App() {

    }

    @Override
    public void run() {
        var app = Javalin.create()
            .post("/boards", this::handleRequest)
            .start(8080);
    }

    private void handleRequest(Context ctx) {
        ctx.status(201);
        ctx.header("Location", "/boards/5b9fabe4-4810-443c-85bd-d9b34f3aeaaf");
    }

    public static void main(String[] args) {
        var app = new App();

        app.run();
    }
}
