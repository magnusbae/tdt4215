package search;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.no.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.ontology.OntModel;

import datatypes.Atc;
import datatypes.ICD10;
import datatypes.NLH;

import parser.AtcParser;
import parser.ICD10parser;
import parser.NLHParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class IndexFiles {

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public ArrayList<ICD10> getIcd10s() {
		return icd10s;
	}

	public void setIcd10s(ArrayList<ICD10> icd10s) {
		this.icd10s = icd10s;
	}

	public IndexWriter getIndexer() {
		return indexer;
	}

	public void setIndexer(IndexWriter indexer) {
		this.indexer = indexer;
	}

	Analyzer analyzer;
	Directory dirAtc, dirNLH, dirICD;

	ArrayList<ICD10> icd10s;
	ArrayList<Atc> atcs;
	ArrayList<NLH> NLHs;
	OntModel atcModel;
	OntModel icd10Model;

	IndexWriter indexer;


	public IndexFiles(Directory dir, Directory dirAtc, Directory dirNLH, Analyzer ana) {
		this.dirAtc = dirAtc;
		this.dirNLH = dirNLH;
		this.dirICD = dir;
		this.analyzer = ana;
	}

	public void index(){
		try {
			FileUtils.deleteDirectory(new File("Index/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		indexICD10();
		indexAtc();
		indexNLH();
	}
	public void indexICD10(){
		try {
			ICD10parser parser = new ICD10parser("Data/icd10no.owl");
			icd10s = parser.getParsedICDs();
			icd10Model = parser.getOnto();
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
			indexer = new IndexWriter(dirICD, iwc);
			for(ICD10 i:icd10s){
				Document doc = new Document();
				if(i.getICDCode()!=null)
					doc.add(new StringField("ICDCode", i.getICDCode(), Field.Store.YES));
				if(i.getLabel()!=null)
					doc.add(new TextField("label", i.getLabel(), Field.Store.YES));
				if(i.getSynonyms() != null){
					doc.add(new TextField("synonyms", i.getSynonyms(), Field.Store.YES));
				}
				indexer.addDocument(doc);
			}
			indexer.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void indexNLH(){
		try {
			NLHParser parser = new NLHParser();
			NLHs = parser.getParsedNLHs();
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
			indexer = new IndexWriter(dirNLH, iwc);
			for(NLH i:NLHs){
				Document doc = new Document();
				if(i.getChapter()!=null)
					doc.add(new StringField("Chapter", i.getChapter(), Field.Store.YES));
				if(i.getText()!=null)
					doc.add(new TextField("label", i.getText(), Field.Store.YES));
				if(i.getText()!=null)
					doc.add(new TextField("Name", i.getName(), Field.Store.YES));
				if(i.getSynonyms() != null){
					String syn = i.getSynonyms();
					syn += findSyn(i.getSynonyms());
					TextField synonymField = new TextField("synonyms", syn, Field.Store.YES);
//					synonymField.setBoost(1.3f);
					doc.add(synonymField);
				}
				indexer.addDocument(doc);
			}
			indexer.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private String findSyn(String synonyms) throws IOException {
		String syn = "";
		if(synonyms.length() < 3)
			return "";
		if(synonyms.contains("itt")){
			syn += " " +synonyms.replace("itt", "ene");
			syn += " " +synonyms.replace("itt", "er");
			
		}
		synonyms = synonyms.trim();
		if(synonyms.length() < 3)
			return "";
		
		try {
			QueryParser q = new MultiFieldQueryParser(Version.LUCENE_CURRENT
					, new String[] {"label","synonyms"},
					analyzer);

			int hitsPerPage = 3;
			DirectoryReader reader = DirectoryReader.open(dirAtc);
//			IndexReader reader = IndexReader.open(dirAtc);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
			synonyms = synonyms.replaceAll("[^\\w\\s]"," ");
			searcher.search(q.parse(QueryParser.escape(synonyms)), collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			if(hits.length != 0){
				int docId = hits[0].doc;
				Document d = searcher.doc(docId);
				syn += " " + d.get("Atccode") + " " +  d.get("label")+ " ";
			}
			collector = TopScoreDocCollector.create(hitsPerPage, true);
			reader.close();
			reader = DirectoryReader.open(dirICD);
			searcher = new IndexSearcher(reader);
			synonyms = synonyms.replaceAll("[^\\w\\s]"," ");
			searcher.search(q.parse(QueryParser.escape(synonyms)), collector);
			hits = collector.topDocs().scoreDocs;
			
			if(hits.length != 0){
				int docId = hits[0].doc;
				Document d = searcher.doc(docId);
				syn += " " + d.get("ICDCode") + d.get("synonyms") + " " +  d.get("label")+ " ";
				reader.close();
				return syn;
			}
			reader.close();
			return null;
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void indexAtc(){
		try {
			AtcParser parser = new AtcParser("Data/atc.owl");
			atcs = parser.getParsedAtcs();
			atcModel = parser.getOnto();
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
			indexer = new IndexWriter(dirAtc, iwc);
			for(Atc i:atcs){
				Document doc = new Document();
				if(i.getAtcCode()!=null)
					doc.add(new StringField("Atccode", i.getAtcCode(), Field.Store.YES));
				if(i.getLabel()!=null)
					doc.add(new TextField("label", i.getLabel(), Field.Store.YES));
				indexer.addDocument(doc);
			}
			indexer.close();
		} catch (Exception e) {
		}
	}
}
