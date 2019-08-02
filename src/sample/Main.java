package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pj.Pyjama;
import pj.compiler.PyjamaToJavaCompiler;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        //launch(args);

        //#omp parallel for
        for(int i = 0; i < 10; i++){
            System.out.println("Hello world from parallel code, executed by " + Pyjama.omp_get_thread_num());
        }

        System.out.println("Finished Execution");
    }
}
