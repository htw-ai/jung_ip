package ip_ws1718;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Test extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("BinarizeView.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Regions - Dudzik/Woelbeling");
		primaryStage.show();
	}

	public static void main(String[] args) {
		RasterImage origImg = new RasterImage(new File("tools.png"));
		
		//System.out.println("Sequential: " + measure(new SequentialFloodFiller(), origImg));
		//System.out.println("BreadthFirst: " + measure(new FloodFillerBreadth(), origImg));
		//System.out.println("DepthFirst: " + measure(new FloodFillerDepth(), origImg));
		System.out.println("BreadthFirst (red.): " + measure(new FloodFillerBreadthRestricted(), origImg));
		//System.out.println("DepthFirst (red.): " + measure(new FloodFillerDepthRestricted(), origImg));
	}
		
		public static double measure(FloodFilling ff, RasterImage origImg) {
			long startTime = System.currentTimeMillis();
			double time = 0;
			
			for (int i = -20; i < 80; i++) {
			RasterImage img = new RasterImage(origImg); // create a clone of origImg
			if (i >= 0)
				startTime = System.currentTimeMillis();
			ff.fillRegions(img.argb, img.height, img.width);
			if (i >= 0)
				time += (System.currentTimeMillis() - startTime) / 80.0;
			
		}
			return time;
			
			
			
		}

}
