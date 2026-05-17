import javafx.application.Application;
import javafx.stage.Stage;
import ui.MainUI;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MainUI ui = new MainUI();
        ui.show(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}