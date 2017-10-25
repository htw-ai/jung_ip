// IP Ue1 WS2017/18
//
// Date: 2017-10-12

package ip_ws1718;

import java.io.File;

import ip_ws1718.RasterImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class BinarizeViewController {
	
	public enum MethodeType { 
		COPY("Copy"),
		DEPTH("Depth First"), 
		BREADTH("Breadth First"), 
		SEQUENTIAL("Sequential");
		
		private final String name;       
	    private MethodeType(String s) { name = s; }
	    public String toString() { return this.name; }
	};

	private static final String initialFileName = "tools.png";
	private static File fileOpenPath = new File(".");

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView binarizedImageView;

    @FXML
    private ComboBox<MethodeType> methodeSelection;

    @FXML
    private Label messageLabel;

	@FXML
	public void initialize() {
		// set combo boxes items
		methodeSelection.getItems().addAll(MethodeType.values());
		methodeSelection.setValue(MethodeType.COPY);
		
		// initialize parameters
		methodeChanged();
		
		// load and process default image
		new RasterImage(new File(initialFileName)).setToView(originalImageView);
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
			new RasterImage(selectedFile).setToView(originalImageView);
	    	processImage();
	    	messageLabel.getScene().getWindow().sizeToScene();
		}
    }
    
    @FXML
    void methodeChanged() {
    	//outline.setDisable(methodeSelection.getValue() == MethodeType.COPY);
    	processImage();
    }

	
	private void processImage() {
		if(originalImageView.getImage() == null)
			return; // no image: nothing to do
		
		long startTime = System.currentTimeMillis();
		
		RasterImage origImg = new RasterImage(originalImageView); 
		RasterImage binImg = new RasterImage(origImg); // create a clone of origImg
		
		int threshold = 128;
		
		switch(methodeSelection.getValue()) {
		case DEPTH:
			//TODO
			break;
		case BREADTH:
			// TODO
			break;
		case SEQUENTIAL:
			//TODO
			break;
		default:
			break;
		}
		
		/*
		if(outline.isSelected() && methodeSelection.getValue() != MethodeType.COPY) {
			RasterImage outlineImg = new RasterImage(binImg.width, binImg.height);
			Filter.outline(binImg, outlineImg);
			outlineImg.setToView(binarizedImageView);			
		} else {
			binImg.setToView(binarizedImageView);
		}*/
		binImg.setToView(binarizedImageView);
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms, threshold = " + threshold);
	}
	

}
