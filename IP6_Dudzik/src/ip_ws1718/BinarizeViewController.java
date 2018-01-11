// IP Ue5 WS2017/18
//
// Date: 2017-12-10

package ip_ws1718;

import java.awt.Point;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ip_ws1718.RasterImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;

public class BinarizeViewController {

	private static final String initialFileName = "w2.png";
	private static File fileOpenPath = new File(".");
	private static File loadedPicture = new File(initialFileName);
	private static DecimalFormat format = new DecimalFormat("0.0");

	private int zoom = 1;
	private int imageWidth = 0;
	private int imageHeight = 0;
	private double minalpha = 0.0;
	private double factor = 0.3;
	private double max = 1.0;
	ArrayList<Kontur> regions;
	ArrayList<Kontur> polygons;

	@FXML
	private Slider slider;
	
	@FXML
	private Slider alphaslider;
	
	@FXML
	private Slider factorslider;
	
	@FXML
	private Slider maxslider;

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
	private Label alphaLabel;
	
	@FXML
	private Label factorLabel;
	
	@FXML
	private Label maxLabel;
	
	@FXML
	private CheckBox cbfill;

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

		cbfill.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov,
								Boolean old_val, Boolean new_val) {
				processImage(); // TODO
				showImageZoomed();
			}
		});

		alphaslider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				minalpha = new_val.doubleValue();
				processImage(); // TODO
				showImageZoomed();
			}
		});
		factorslider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				factor = new_val.doubleValue();
				processImage(); // TODO
				showImageZoomed();
			}
		});
		maxslider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				max = new_val.doubleValue();
				processImage(); // TODO
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
		slider.setValue(zoom);
		alphaslider.setValue(minalpha);
		maxslider.setValue(max);
		factorslider.setValue(factor);
		messageLabel.setText("Zoom: " + zoom);
		alphaLabel.setText("Alpha: " + format.format(new Double(minalpha)));
		factorLabel.setText("Factor: " + format.format(new Double(factor)));
		maxLabel.setText("Max Value: " + format.format(new Double(max)));
		
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
		// int count = polygons.stream().map(polygon-> polygon.getVertices().size()-1)
		//				 .reduce(0, (Integer a, Integer b) -> Integer.sum(a, b));
		// System.out.println(count);
	}
	
	private void drawOverlays() {
		//drawRegion();
		drawGrid();
		drawPolygon();
	}

	class PPoint {
		double x;
		double y;
		double zoom = 1;

		PPoint (double x, double y) {
			this.x = x;
			this.y = y;
		}

		PPoint (Point p) {
			this.x = (double) p.x;
			this.y = (double) p.y;
		}

		PPoint middle (PPoint point) {
			double x = (this.x + point.x)/2;
			double y = (this.y + point.y)/2;
			return new PPoint(x, y);
		}

		PPoint zoom (int zoom) {
			double x = this.x * zoom;
			double y = this.y * zoom;
			double nZoom = this.zoom * zoom;
			PPoint p = new PPoint(x, y);
			p.zoom = nZoom;
			return p;
		}

		PPoint subtract (PPoint p) {
			double x = this.x - p.x;
			double y = this.y - p.y;
			return new PPoint(x, y);
		}

		PPoint anteil (PPoint p, double alpha) {
			double x = alpha * this.x + (1-alpha) * p.x;
			double y = alpha * this.y + (1-alpha) * p.y;

			return new PPoint(x, y);
		}

		double distance (PPoint start, PPoint end) {
			PPoint aa = start;
			PPoint bb = end;
			PPoint v = this;

			double x = bb.x-aa.x;
			double y = -1 * (bb.x-aa.x);

			double sx = x/Math.abs(x);
			double sy = y/Math.abs(y);

			if (Double.isNaN(sx)) {
    			sx = x;
			}
			if (Double.isNaN(sy)) {
    			sy = y;
			}

			PPoint s = new PPoint(sx, sy);
			PPoint va = v.subtract(aa);

			return s.scalar(va);
		}

		double scalar (PPoint p) {
			return this.x * p.x + this.y * p.y;
		}
	}

	private int mod(int x, int y) {
		int result = x % y;
		return result < 0? result + y : result;
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
			if (polygon.getType().compareTo(Kontur.Contourtype.internal) == 0)
				gc.setFill(Color.BLUE);
			else
				gc.setFill(Color.RED);

			gc.beginPath();
			for (int i = 0; i < n; i++) {
				PPoint a = new PPoint(points.get(mod((i-1),   (n-1))));
				PPoint b = new PPoint(points.get(mod( i,      (n-1))));
				PPoint c = new PPoint(points.get(mod((i + 1), (n-1))));

				PPoint startPoint = b.middle(a);
				PPoint endPoint   = b.middle(c);

				double distance = b.distance(startPoint, endPoint);

				double alpha1 = factor * ((distance - 0.5) / distance);
				double alpha = (alpha1 < minalpha) ? minalpha : alpha1;

				/*
				gc.setFill(Color.PALEGREEN);
				gc.fillOval(controlPoint1.x * zoom,controlPoint1.y * zoom,5,5);
				gc.setFill(Color.VIOLET);
				gc.fillOval(controlPoint2.x * zoom,controlPoint2.y * zoom,5,5);
				gc.setFill(Color.RED);
				gc.fillOval(startPoint.x * zoom,startPoint.y * zoom,5,5);
				gc.setFill(Color.BLUE);
				gc.fillOval(b.x * zoom,b.y * zoom,5,5);
				*/

				if (i == 0) {
					gc.moveTo(startPoint.x * zoom, startPoint.y * zoom);
				}
				if (alpha > max) {
					gc.lineTo(b.x * zoom, b.y * zoom);
					gc.lineTo(endPoint.x * zoom, endPoint.y * zoom);
				} else {
					PPoint controlPoint1 = b.anteil(startPoint, alpha);
					PPoint controlPoint2 = b.anteil(endPoint, alpha);
					
					gc.bezierCurveTo(controlPoint1.x * zoom, controlPoint1.y * zoom,
							controlPoint2.x * zoom, controlPoint2.y * zoom,
							endPoint.x * zoom, endPoint.y * zoom);
				}
				if (i == n-1) {
					gc.moveTo(endPoint.x * zoom, endPoint.y * zoom);
				}
			}
			gc.closePath();
			if (cbfill.isSelected())
				gc.fill();
			else
				gc.stroke();
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
