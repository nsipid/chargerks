package com.michaelgrenon.chargerks.dataspace;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.text.MessageFormat;
import org.jsoup.Connection;

public class UahClassIterable implements Iterable<String[]> {
    private String baseUrl = "http://www.uah.edu";
    private String indexUrl;

    public UahClassIterable(String semester, String dir) {
        this.indexUrl = MessageFormat.format("{3}/cgi-bin/schedule.pl?file={0}.html&dir={2}&segment={1}",
                new Object[]{semester, "NDX", dir, baseUrl});
    }

    public UahClassIterable(String indexUrl) {
        this.indexUrl = indexUrl;
    }

    private class UahClassListIterator implements Iterator<String[]> {
        
        String indexUrl;

        Document index; 
        Iterator<String> links = Collections.emptyIterator();
        Iterator<String[]> records = Collections.emptyIterator();

        public UahClassListIterator(String indexUrl) {
            this.indexUrl = indexUrl;
        }
 
        @Override
        public boolean hasNext() {
            try
            {
                initIndex();
                if (records.hasNext()) {
                    return true;
                } else if(links.hasNext()) {
                    String url = MessageFormat.format("{0}/{1}", new Object[]{baseUrl, links.next()});
                    Document page = Jsoup.connect(url).get();
                    Element pre = page.select("pre").first();
                    BorderedTable table = new BorderedTable(pre.text(), '-');
                    records = table.parseLines().iterator();
                    return records.hasNext();
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public String[] next() {
            if (hasNext()) {
                return records.next();
            } else {
                throw new NoSuchElementException();
            }
        }

        private void initIndex() throws IOException {
            if (this.index == null) {
                Connection connection = Jsoup.connect(this.indexUrl);
                connection.timeout(5000);
                this.index = connection.get();

                this.links = index.select("a[href]").eachAttr("href").iterator();
            }
        }
    }

	@Override
	public Iterator<String[]> iterator() {
		return new UahClassListIterator(indexUrl);
	}
}