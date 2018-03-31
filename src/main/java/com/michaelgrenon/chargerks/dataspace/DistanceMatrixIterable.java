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
        private String[] locations;
        private int rowNum = 0;
        private int colNum = 0;
		private String[] addresses;
		private String apiKey;

        private DistanceMatrixIterator(String apiKey, String[] locations, String[] addresses) {
            this.locations = locations;
            this.addresses = addresses;
            this.apiKey = apiKey;
            
        }

        @Override
        public boolean hasNext() {
            if (rowNum < addresses.length && colNum < addresses.length) {
                return true;
            }

            return false;
        }

        @Override
        public String[] next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            try
            {
                Thread.sleep(100);
                GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
                String origin = locations[rowNum];
                String dest = locations[colNum];

                
                DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(context);
                DistanceMatrix matrix = request.origins(addresses[rowNum]).destinations(addresses[colNum]).mode(TravelMode.WALKING).await();

                String duration = Long.toString(matrix.rows[0].elements[0].duration.inSeconds);

                String[] nextRet = {origin, dest, duration};

                if (colNum < addresses.length - 1) {
                    colNum++;
                } else {
                    colNum = 0;
                    rowNum++;
                }
    
                return nextRet;

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