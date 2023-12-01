package it.unibo.mvc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import it.unibo.mvc.Configuration;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private static final int MIN = 0;
    private static final int MAX = 100;
    private static final int ATTEMPTS = 10;

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        Configuration.Builder cb = new Configuration.Builder();
        try {
            for(String line : Files.readAllLines(Path.of("/Users/bagarozzi/uni/oop/oop-lab10/102-advanced-mvc/src/main/resources/config.yml"))){
                if(line.contains("min")){
                    cb.setMin(Integer.parseInt(line.substring(line.indexOf(":") + 1, line.length())));
                }
                else if(line.contains("max")){
                    cb.setMax(Integer.parseInt(line.substring(line.indexOf(":") + 1, line.length())));
                }
                else {
                    cb.setAttempts((Integer.parseInt(line.substring(line.indexOf(":") + 1, line.length()))));
                }
            }

        }catch(IOException e){
            System.out.println("Error opening the file");
        }

        this.model = new DrawNumberImpl(cb.build());
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(new DrawNumberViewImpl());
    }

}
