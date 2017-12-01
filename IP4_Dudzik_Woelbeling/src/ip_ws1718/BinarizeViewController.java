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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.FileChooser;

public class BinarizeViewController {

	private static String initialFileName = "w2.png";
	private static File fileOpenPath = new File(".");

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
	private ImageView binarizedImageView;


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
				zoomChanged();

			}
		});

		// load and process default image
		File loadedPicture = new File(initialFileName);
		RasterImage image = new RasterImage(loadedPicture);
		Image img = new Image(loadedPicture.toURI().toString(), image.width, image.height, true, false);
		binarizedImageView.setImage(img);

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
			initialFileName = selectedFile.getAbsolutePath();
			fileOpenPath = selectedFile.getParentFile();

			File loadedPicture = new File(initialFileName);
			RasterImage image = new RasterImage(loadedPicture);
			Image img = new Image(loadedPicture.toURI().toString(), image.width, image.height, true, false);
			binarizedImageView.setImage(img);
			
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

		regions = new Contourfinder(binImg).scan();
		//ArrayList<Path> p = Potracer.getPaths(regions);	//TODO
		polygons = Potracer.getPolygons(regions);
		
		//drawRegion();
		drawPolygon();

	}

	private void zoomChanged() {
		messageLabel.setText(zoom + "");
		double zoomedWidth  = Math.ceil (zoom * imageWidth);
		double zoomedHeight = Math.ceil (zoom * imageHeight);

		Image img = new Image(new File(initialFileName).toURI().toString(), zoomedWidth, zoomedHeight, true, false);
		binarizedImageView.setImage(img);

		binarizedImageView.setFitWidth(zoomedWidth);
		binarizedImageView.setFitHeight(zoomedHeight);
		//drawRegion();
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

}
