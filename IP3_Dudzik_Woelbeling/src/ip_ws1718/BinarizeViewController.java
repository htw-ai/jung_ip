// IP Ue3 WS2017/18
//
// Date: 2017-11-15

package ip_ws1718;

import java.io.File;
import java.util.ArrayList;

import ip_ws1718.RasterImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class BinarizeViewController {

	//private static final String initialFileName = "tools.png";
	private static final String initialFileName = "testkleinu.png";
	private static File fileOpenPath = new File(".");
	
	private int zoom = 1;
	private int imageWidth = 0;
	private int imageHeight = 0;

@FXML
private Slider slider;

@FXML
private Canvas canvas;

    @FXML
    private ImageView binarizedImageView;


    @FXML
    private Label messageLabel;

	@FXML
	public void initialize() {
		
		// set slider
				slider.valueProperty().addListener(new ChangeListener<Number>() {
		            public void changed(ObservableValue<? extends Number> ov,
		                Number old_val, Number new_val) {
		            	
		            	zoom = new_val.intValue();
		            	zoomChanged();
		            	
		            }
		        });
		
		// load and process default image
		new RasterImage(new File(initialFileName)).setToView(binarizedImageView);
		processImage();
		messageLabel.setText(zoom + "");
	}
	
    @FXML
    void openImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(fileOpenPath); 
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images (*.jpg, *.png, *.gif)", "*.jpeg", "*.jpg", "*.png", "*.gif"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if(selectedFile != null) {
			fileOpenPath = selectedFile.getParentFile();
			new RasterImage(selectedFile).setToView(binarizedImageView);
	    	processImage();
	    	messageLabel.getScene().getWindow().sizeToScene();
		}
    }

	
	private void processImage() {
		if(binarizedImageView.getImage() == null)
			return; // no image: nothing to do
		
		
		RasterImage origImg = new RasterImage(binarizedImageView); 
		RasterImage binImg = new RasterImage(origImg); // create a clone of origImg
		
		imageWidth = origImg.width;
		imageHeight = origImg.height;
		binarizedImageView.setFitWidth(imageWidth);
		binarizedImageView.setFitHeight(imageHeight);

		ArrayList<Kontur> regions = new Potracer(binImg).scan();
		drawOverlay();
		
	}
	
	private void zoomChanged() {
		messageLabel.setText(zoom + "");
		double zoomedWidth = Math. ceil (zoom * imageWidth);
		double zoomedHeight = Math. ceil (zoom * imageHeight);
		binarizedImageView.setFitWidth(zoomedWidth);
		binarizedImageView.setFitHeight(zoomedHeight);
		drawOverlay();
	}

	private void drawOverlay() {
		double zoomedWidth = Math. ceil (zoom * imageWidth);
		double zoomedHeight = Math. ceil (zoom * imageHeight);
		canvas.setWidth(zoomedWidth);
		canvas.setHeight(zoomedHeight);

		// overlay example: draw a grit
		int gritPixelDistance = 16;
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, zoomedWidth, zoomedHeight);
		gc.setStroke(Color. RED);
		gc.setLineWidth(1);
		double gritSpacing = zoom * gritPixelDistance;
		for(double y = 0; y <= zoomedHeight; y += gritSpacing) {
			gc.strokeLine(0, y, zoomedWidth, y);
		}
		for(double x = 0; x <= zoomedWidth; x += gritSpacing) {
			gc.strokeLine(x, 0, x, zoomedHeight);
		}
	}

}
