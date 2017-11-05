// IP Ue1 WS2017/18 
//
// Date: 2017-10-12

package ip_ws1718;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("BinarizeView.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Binarize - Dudzik/Woelbeling");
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
