package gk.todo;

/**
 * Starting point of the application contaning the
 * main function.
 */
public class App implements Runnable {

    private final Domain domain;

    public App(Domain domain) {
        this.domain = domain;
    }

    @Override
    public void run() {
        domain.greetMessage();
    }

    public static void main(String[] args) {
        var app = new App(new Domain());

        app.run();
    }
}
