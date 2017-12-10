// IP Ue4 WS2017/18
//
// Date: 2017-11-15

package ip_ws1718;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import ip_ws1718.RasterImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;

public class BinarizeViewController {

	private static final String initialFileName = "head.png";
	private static File fileOpenPath = new File(".");
	private static File loadedPicture = new File(initialFileName);

	private int zoom = 1;
	private int imageWidth = 0;
	private int imageHeight = 0;
	ArrayList<Kontur> regions;
	ArrayList<Kontur> polygons;

	@FXML
	private Slider slider;

	@FXML
	private Canvas canvas;
	
	@FXML
	private Canvas canvas2;

	@FXML
	private ImageView binarizedImageView;

	@FXML
	private Rectangle2D screenBounds;	// optimal window size

	@FXML
	private Label messageLabel;

	@FXML
	public void initialize() {

		// set slider
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {

				zoom = new_val.intValue();
				showImageZoomed();
			}
		});
		
		screenBounds = Screen.getPrimary().getVisualBounds();

		// load and process default image
		loadedPicture = new File(initialFileName);
		loadImage();

		processImage();
		showImageZoomed();
	}

	@FXML
	void openImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(fileOpenPath); 
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images (*.jpg, *.png, *.gif)", "*.jpeg", "*.jpg", "*.png", "*.gif"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if(selectedFile != null) {
			fileOpenPath = selectedFile.getParentFile();
			loadedPicture = new File(selectedFile.getAbsolutePath());
			loadImage();
			processImage();
			showImageZoomed();
		}
	}
	
	private void showImageZoomed() {
		RasterImage image = new RasterImage(loadedPicture);
		double zoomedWidth  = Math.ceil (zoom * image.width);
		double zoomedHeight = Math.ceil (zoom * image.height);
		Image img = new Image(loadedPicture.toURI().toString(), zoomedWidth, zoomedHeight, true, false);
		binarizedImageView.setImage(img);
		binarizedImageView.setFitWidth(zoomedWidth);
		binarizedImageView.setFitHeight(zoomedHeight);
		slider.setValue(zoom);		// TODO: Zirkelbezug entfernen! (Eventlistener ruft diese Methode wieder auf)
		messageLabel.setText(zoom + "");
		
		drawOverlays();
	}
	
	private void loadImage() {
		RasterImage image = new RasterImage(loadedPicture);
		imageWidth = image.width;
		imageHeight = image.height;
		Image img = new Image(loadedPicture.toURI().toString(), imageWidth, imageHeight, true, false);
		binarizedImageView.setImage(img);
		
		int optZoomX = (int) Math.floor(screenBounds.getWidth() * 0.8 / imageWidth);
		int optZoomY = (int) Math.floor(screenBounds.getHeight() * 0.6 / imageHeight);
		zoom = (optZoomX < optZoomY) ? optZoomX : optZoomY;
		if (zoom > slider.getMax()) 
			zoom = (int) slider.getMax();
		if (zoom < slider.getMin())
			zoom = (int) slider.getMin();
	}

	private void processImage() {
		if(binarizedImageView.getImage() == null)
			return; // no image: nothing to do

		RasterImage origImg = new RasterImage(loadedPicture); 

		regions = new Contourfinder(origImg).scan();
		polygons = Potracer.getPolygons(regions);

	}
	
	private void drawOverlays() {
		//drawRegion();
		drawGrid();
		drawPolygon();
	}

	private void drawRegion() {
		double zoomedWidth  = Math.ceil (zoom * imageWidth);
		double zoomedHeight = Math.ceil (zoom * imageHeight);
		canvas.setWidth(zoomedWidth);
		canvas.setHeight(zoomedHeight);

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, zoomedWidth, zoomedHeight);
		gc.setLineWidth(1);
		for (Kontur region : regions) {
			Color color = (region.getType() == Kontur.Contourtype.internal) ? Color.BLUE : Color.RED;
			gc.setStroke(color);
			ArrayList<Point> points = region.getVertices();
			int pointSize = points.size() -1;
			double[] xs = new double[pointSize];
			double[] ys = new double[pointSize];
			for (int i = 0; i < pointSize; i++) {
				xs[i] = ((double) points.get(i).x) * zoom;
				ys[i] = ((double) points.get(i).y) * zoom;
			}
			gc.strokePolygon(xs, ys, pointSize);
		}
	}
	
	private void drawPolygon() {
		double zoomedWidth  = Math.ceil (zoom * imageWidth);
		double zoomedHeight = Math.ceil (zoom * imageHeight);
		canvas.setWidth(zoomedWidth);
		canvas.setHeight(zoomedHeight);

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, zoomedWidth, zoomedHeight);
		gc.setLineWidth(2);
		gc.setStroke(Color.BLUE);
		gc.setFill(Color.BLUE);
		for (Kontur polygon : polygons) {

			ArrayList<Point> points = polygon.getVertices();
			int n = points.size();
			double[] xs = new double[n];
			double[] ys = new double[n];
			for (int i = 0; i < points.size(); i++) {
				xs[i] = ((double) points.get(i).x) * zoom;
				ys[i] = ((double) points.get(i).y) * zoom;
				gc.fillOval((((double) points.get(i).x) - 0.1) * zoom, (((double) points.get(i).y) - 0.1) * zoom, 0.2 * zoom, 0.2 * zoom);
			}
			gc.strokePolyline(xs,  ys, n);
		}
	}
	
	private void drawGrid() {
		double zoomedWidth  = Math.ceil (zoom * imageWidth);
		double zoomedHeight = Math.ceil (zoom * imageHeight);
		canvas2.setWidth(zoomedWidth);
		canvas2.setHeight(zoomedHeight);
		GraphicsContext gc2 = canvas2.getGraphicsContext2D();
		gc2.clearRect(0, 0, zoomedWidth, zoomedHeight);

		if (zoom >= 15) {
			int gridPixelDistance = 1;
			gc2.setLineWidth(1);
			gc2.setLineDashes(2);
			gc2.setStroke(Color.LIGHTGRAY);
			double gritSpacing = zoom * gridPixelDistance;
			for(double y = 0; y <= zoomedHeight; y += gritSpacing) {
				gc2.strokeLine(0, y, zoomedWidth, y);
			}
			for(double x = 0; x <= zoomedWidth; x += gritSpacing) {
				gc2.strokeLine(x, 0, x, zoomedHeight);
			}
		}
		canvas.toFront();
	}

}
