package com.michaelgrenon.chargerks.dataspace;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.TravelMode;

public class DistanceMatrixIterable implements Iterable<String[]> {
    private Supplier<DistanceMatrixIterator> supplier;

    public DistanceMatrixIterable(String apiKey, String[] locations, String[] addresses) {
        supplier = () -> new DistanceMatrixIterator(apiKey, locations, addresses);
    }

    private class DistanceMatrixIterator implements Iterator<String[]> {
        private DistanceMatrixRow[] rows;
        private DistanceMatrixRow row;
        private String[] locations;
        private int rowNum = 0;
        private int colNum = 0;
        private boolean initialized = false;
		private String[] addresses;
		private String apiKey;

        private DistanceMatrixIterator(String apiKey, String[] locations, String[] addresses) {
            this.locations = locations;
            this.addresses = addresses;
            this.apiKey = apiKey;
        }

        @Override
        public boolean hasNext() {
            init();

            if (row == null) return false;

            if (rowNum < rows.length || colNum < row.elements.length) {
                return true;
            }

            return false;
        }

        @Override
        public String[] next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            String origin = locations[rowNum];
            String dest = locations[colNum];
            String duration = Long.toString(row.elements[colNum].duration.inSeconds);
            String[] nextRet = {origin, dest, duration};

            if (colNum < row.elements.length) {
                colNum++;
            } else {
                colNum = 0;
                rowNum++;
            }

            return nextRet;
        }

        private void init() {
            if (this.initialized) {
                return;
            }

            this.initialized = true;

            try
            {
                GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
                DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(context);
                DistanceMatrix matrix = request.origins(addresses).destinations(addresses).mode(TravelMode.WALKING).await();

                this.rows = matrix.rows;
                if (matrix.rows.length > 0) {
                    this.row = rows[0];
                }
            } catch (Exception e) {
                throw new NoSuchElementException(e.getMessage());
            }
        }
    }

	@Override
	public Iterator<String[]> iterator() {
		return supplier.get();
	}
}