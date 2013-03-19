package search;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.*;
import org.apache.lucene.util.*;


public class Search {
	public Search() {
		
	}
	public String basicSearch(String searchString) {
		return searchString;

	}
}