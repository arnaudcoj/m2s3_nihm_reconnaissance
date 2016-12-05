package reconnaissance;

import java.security.InvalidParameterException;
import java.util.Vector;

import javafx.geometry.Point2D;

public class DTW {
	
	public static Matrix computeDistanceMatrix(Vector<Point2D> ref, Vector<Point2D> test) {
		int n = ref.size();
		int m = test.size();
		
		Matrix d = new Matrix(n, m);
		
		//first elt
		d.couple[0][0] = new Couple(0,0);
		d.items[0][0] = 0;
		
		//edges
		for(int i = 1; i < n; i++) {
			d.couple[i][0] = new Couple(i-1,0);
			d.items[i][0] = d.items[i-1][0] + ref.get(i).distance(test.get(0));
		}
		for(int j = 1; j < m; j++) {
			d.couple[0][j] = new Couple(0,j-1);
			d.items[0][j] = d.items[0][j-1] + ref.get(0).distance(test.get(j));
		}
		
		//fill d
		for(int i = 1; i < n; i++) {
			for(int j = 1; j < m; j++) {
				
				d.items[i][j] = ref.get(i).distance(test.get(j));
				
				//peut faire plus élégant
				if(d.items[i-1][j] < d.items[i][j-1] && d.items[i-1][j] < d.items[i-1][j-1]) {
					d.couple[i][j] = new Couple(i-1,j);
					d.items[i][j] += d.items[i-1][j];
					
				} else if (d.items[i][j-1] < d.items[i-1][j] && d.items[i][j-1] < d.items[i-1][j-1]) {
					d.couple[i][j] = new Couple(i,j-1);
					d.items[i][j] += d.items[i][j-1];
					
				} else {
					d.couple[i][j] = new Couple(i-1,j-1);
					d.items[i][j] += d.items[i-1][j-1];
				}
			}
		}
		
		return d;
	}
}
