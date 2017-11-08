// IP Ue2 WS2017/18
//
// Date: 2017-11-05

package ip_ws1718;

import java.io.File;

import ip_ws1718.RasterImage;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    	processImage();
    }

	
	private void processImage() {
		if(originalImageView.getImage() == null)
			return; // no image: nothing to do
		messageLabel.setText("");
		long startTime = System.currentTimeMillis();
		
		RasterImage origImg = new RasterImage(originalImageView); 
		RasterImage binImg = new RasterImage(origImg); // create a clone of origImg
		
		switch(methodeSelection.getValue()) {
		case DEPTH:
			//binImg.depthFirst();
			fill(new FloodFillerDepth(), binImg, startTime);
			break;
		case DEPTH2:
			fill(new FloodFillerDepthRestricted(), binImg, startTime);
			break;
		case BREADTH:
			//binImg.breadthFirst();
			fill(new FloodFillerBreadth(), binImg, startTime);
			break;
		case BREADTH2:
			fill(new FloodFillerBreadthRestricted(), binImg, startTime);
			break;
		case SEQUENTIAL:
			fill(new SequentialFloodFiller(), binImg, startTime);
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
		
	}
	
	private void fill(FloodFilling ff, RasterImage binImg, long startTime) {
		ff.fillRegions(binImg.argb, binImg.height, binImg.width);
		messageLabel.setText("Processing time: " + (System.currentTimeMillis() - startTime) + " ms, Stack size: " + ff.getStackSize());
	}
	

}
