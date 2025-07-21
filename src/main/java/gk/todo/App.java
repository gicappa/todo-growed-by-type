package gk.todo;

/**
 * Starting point of the application contaning the
 * main function.
 */
public class App implements Runnable {

    private final Greeter domain;

    public App(Greeter domain) {
        this.domain = domain;
    }

    @Override
    public void run() {
        System.out.println(domain.message());
    }

    public static void main(String[] args) {
        var app = new App(new Greeter());

        app.run();
    }
}
