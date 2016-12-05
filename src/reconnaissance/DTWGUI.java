package reconnaissance;

/**
 * @author <a href="mailto:gery.casiez@univ-lille1.fr">Gery Casiez</a>
 */

import java.util.Vector;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sun.applet.Main;

public class DTWGUI extends Application{
	GraphicsContext gc;
	Canvas canvas;
	Vector<Point2D> userGesture = new Vector<Point2D>();
	
	//q4
	Template recognizedGesture = null;
	TemplateManager refs = new TemplateManager();
	//--
	
	public void start(Stage stage) {
		VBox root = new VBox();
		canvas = new Canvas (600, 700);
		gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);
		
		//q4
		refs.loadFile(Main.class.getResourceAsStream("/gestures.xml"));
		//--
		
		canvas.setOnMousePressed(e -> {
			userGesture.clear();
			redrawMyCanvas();			
		});
		
		canvas.setOnMouseDragged(e -> {
			userGesture.add(new Point2D(e.getX(), e.getY()));
			redrawMyCanvas();
		});
		
		canvas.setOnMouseReleased(e -> {
			recognizeGesture();
			redrawMyCanvas();
		});

		Scene scene = new Scene(root);
		stage.setTitle("Université Lille 1 - M2 IVI - NIHM - Dynamic Time Warping - G. Casiez");
		stage.setScene(scene);
		stage.show();
	}
	
	//q4	
	private void recognizeGesture() {
		double minDist = Double.POSITIVE_INFINITY;

		Vector<Point2D> normalizedTestGesture = normalizeGesture(userGesture);
		int m = normalizedTestGesture.size();
		
		for (Template ref : refs.getTemplates()) {			
			Vector<Point2D> normalizedRefGesture = normalizeGesture(ref.getPoints());
			
			int n = normalizedRefGesture.size();
			
			Matrix d = DTW.computeDistanceMatrix(normalizedRefGesture, normalizedTestGesture);
						
			double dist = d.items[n-1][m-1]; 
			if(dist < minDist) {
				minDist = dist;
				recognizedGesture = ref;
			}
		}
	}

	private Vector<Point2D> normalizeGesture(Vector<Point2D> gesture) {
		Vector<Point2D> normalizedGesture = new Vector<Point2D>(gesture.size());
		Point2D pt = gesture.get(0);
		Point2D ptNew;
		
		double xMin, xMax, yMin, yMax;
		xMin = xMax = pt.getX();
		yMin = yMax = pt.getY();
		
		for(int i = 1; i < gesture.size(); i++) {
			pt = gesture.get(i);
			xMin = Math.min(pt.getX(), xMin);
			xMax = Math.max(pt.getX(), xMax);
			yMin = Math.min(pt.getY(), yMin);
			yMax = Math.max(pt.getY(), yMax);
		}

		double width = xMax - xMin;
		double height = yMax - yMin;
				
		for(int i = 0; i < gesture.size(); i++) {
			pt = gesture.get(i);
			ptNew = new Point2D((pt.getX() - xMin) / width, (pt.getY() - yMin) / height); 
			normalizedGesture.add(ptNew);
		}
		
		return normalizedGesture;
	}
	//--

	public void redrawMyCanvas() {
		double r = 5.0;
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		for (int i=1; i<userGesture.size(); i++) {
			gc.setStroke(Color.BLACK);
			gc.strokeLine(userGesture.elementAt(i-1).getX(), userGesture.elementAt(i-1).getY(),
					userGesture.elementAt(i).getX(), userGesture.elementAt(i).getY());
			gc.strokeOval(userGesture.elementAt(i-1).getX() - r, userGesture.elementAt(i-1).getY() - r, 2*r, 2*r);
		}
		
		if(recognizedGesture != null) {
			Vector<Point2D> recognizedGesturePoints = recognizedGesture.getPoints();
			for (int i=1; i<recognizedGesturePoints.size(); i++) {
				gc.setStroke(Color.ORANGE);
				gc.strokeLine(recognizedGesturePoints.elementAt(i-1).getX(), recognizedGesturePoints.elementAt(i-1).getY(),
						recognizedGesturePoints.elementAt(i).getX(), recognizedGesturePoints.elementAt(i).getY());
				gc.strokeOval(recognizedGesturePoints.elementAt(i-1).getX() - r, recognizedGesturePoints.elementAt(i-1).getY() - r, 2*r, 2*r);
			}
			gc.fillText(recognizedGesture.getName(), 10, 15);
		}
		
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
