// IP Ue2 WS2017/18
//
// Date: 2017-11-05

package ip_ws1718;

import java.io.File;

import ip_ws1718.RasterImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class BinarizeViewController {
	
	public enum MethodeType { 
		COPY("Copy"),
		DEPTH("Depth First"), 
		DEPTH2("Depth First (check check before adding)"), 
		BREADTH("Breadth First"), 
		BREADTH2("Breadth First (check before adding)"),
		SEQUENTIAL("Sequential");
		
		private final String name;       
	    private MethodeType(String s) { name = s; }
	    @Override
		public String toString() { return this.name; }
	};

	private static final String initialFileName = "tools.png";
	private static File fileOpenPath = new File(".");

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
		            	processImage();
		            	
		            }
		        });
		
		// load and process default image
		new RasterImage(new File(initialFileName)).setToView(binarizedImageView);
		processImage();
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
		
		int zoom = (int)slider.getValue();
		messageLabel.setText(zoom + "");
		
		RasterImage origImg = new RasterImage(binarizedImageView); 
		RasterImage binImg = new RasterImage(origImg); // create a clone of origImg
		
		binImg.setToView(binarizedImageView);
		
	}
	

}
