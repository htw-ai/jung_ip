// IP Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-08-18

package ip_ws1718;

import java.io.File;

import ip_ws1718.RasterImage;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class BinarizeViewController {
	
	public enum MethodeType { 
		COPY("Copy Image"), 
		THRESHOLD("Threshold"), 
		ISODATA("ISO Data");
		
		private final String name;       
	    private MethodeType(String s) { name = s; }
	    public String toString() { return this.name; }
	};

	private static final String initialFileName = "tools1.png";
	private static File fileOpenPath = new File(".");

    @FXML
    private ImageView originalImageView;

    @FXML
    private ImageView binarizedImageView;

    @FXML
    private ComboBox<MethodeType> methodeSelection;
    
    @FXML
    private CheckBox outline;
    
    // TODO: add a Slider for threshold adjustment, add it to the GIU as well

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
    	outline.setDisable(methodeSelection.getValue() == MethodeType.COPY);
    	processImage();
    }
	
    @FXML
    void outlineChanged() {
    	processImage();
    }
	private void processImage() {
		if(originalImageView.getImage() == null)
			return; // no image: nothing to do
		
		long startTime = System.currentTimeMillis();
		
		RasterImage origImg = new RasterImage(originalImageView); 
		RasterImage binImg = new RasterImage(origImg); // create a clone of origImg
		
		int threshold = 128; // TODO: take value from added threshold slider
		
		switch(methodeSelection.getValue()) {
		case THRESHOLD:
			binImg.binarizeWithThreshold(threshold);
			break;
		case ISODATA:
			threshold = binImg.binarizeWithIsoData();
			break;
		default:
			break;
		}
		
		if(outline.isSelected() && methodeSelection.getValue() != MethodeType.COPY) {
			RasterImage outlineImg = new RasterImage(binImg.width, binImg.height);
			Filter.outline(binImg, outlineImg);
			outlineImg.setToView(binarizedImageView);			
		} else {
			binImg.setToView(binarizedImageView);
		}
		
	   	messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms, threshold = " + threshold);
	}
	

}
