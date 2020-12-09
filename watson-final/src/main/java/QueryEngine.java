import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import edu.stanford.nlp.simple.Sentence;

public class QueryEngine {
	
	// Handle index path's here
	// vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
	// This one is what will be used by the query functions
	// This is where the index will be placed if you call readWikiPages() to build a new index.
	// If you build a new index using the same path of a pre-existing index it will overwrite it!
	static String indexPath = "C:\\bestWikiPageIndex\\index.lucene";
	// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	
	static StandardAnalyzer analyzer = new StandardAnalyzer();	
	static IndexWriterConfig config = new IndexWriterConfig(analyzer);    
    static IndexWriter w;
    private static int answersCorrect;
	private static int totalQuestions;
	private static List<ResultClass> results;
    
    // I got this list of stopwords from an article on this website
 	// https://medium.com/@saitejaponugoti/stop-words-in-nlp-5b248dadad47#:~:text=In%20computing%2C%20stop%20words%20are,universal%20list%20of%20stop%20words.
 	// They are from the gensim library or something, I just copy pasted them for my purposes.
    static String[] stopWordList = {"her", "during", "among", "thereafter", "only", "hers", "in", "none", 
			"with", "un", "put", "hence", "each", "would", "have", "to", "itself", "that", "seeming", 
			"hereupon", "someone", "eight", "she", "forty", "much", "throughout", "less", "was", 
			"interest", "elsewhere", "already", "whatever", "or", "seem", "fire", "however", "keep", 
			"detail", "both", "yourselves", "indeed", "enough", "too", "us", "wherein", "himself", 
			"behind", "everything", "part", "made", "thereupon", "for", "nor", "before", "front", 
			"sincere", "really", "than", "alone", "doing", "amongst", "across", "him", "another", 
			"some", "whoever", "four", "other", "latterly", "off", "sometime", "above", "often", 
			"herein", "am", "whereby", "although", "who", "should", "amount", "anyway", "else", 
			"upon", "this", "when", "we", "few", "anywhere", "though", "being", "used", "full", 
			"thru", "call", "whereafter", "various", "has", "same", "former", "whereas", "what", 
			"had", "mostly", "onto", "go", "could", "yourself", "meanwhile", "beyond", "beside", 
			"burs", "side", "our", "five", "nobody", "herself", "is", "ever", "they", "here", 
			"eleven", "fifty", "therefore", "nothing", "not", "without", "whence", "get", 
			"whither", "then", "no", "own", "many", "anything", "etc", "make", "from", 
			"against", "ltd", "next", "afterwards", "unless", "while", "thin", "beforehand", 
			"by", "amoungst", "you", "third", "as", "those", "done", "becoming", "say", "either", 
			"doesn", "twenty", "his", "yet", "latter", "somehow", "are", "these", "mine", 
			"under", "take", "whose", "others", "over", "perhaps", "thence", "does", "where", 
			"two", "always", "your", "wherever", "became", "which", "about", "but", 
			"towards", "still", "rather", "quite", "whether", "somewhere", "might", "do", "bottom", 
			"until", "km", "yours", "serious", "find", "please", "hasnt", "otherwise", "six", "toward",
			"sometimes", "of", "fifteen", "eg", "just", "a", "me", "describe", "why", "an", "and", "may", 
			"within", "kg", "con", "re", "nevertheless", "through", "very", "anyhow", "down", "nowhere",
			"now", "it", "cant", "de", "move", "hereby", "how", "found", "whom", "were", "together", 
			"again", "moreover", "first", "never", "below", "between", "computer", "ten", "into", "see", 
			"everywhere", "there", "neither", "every", "couldnt", "up", "several", "the", "i", "becomes",
			"don", "ie", "been", "whereupon", "seemed", "most", "noone", "whole", "must", "cannot", "per", 
			"my", "thereby", "so", "he", "name", "co", "its", "everyone", "if", "become", "thick", "thus",
			"regarding", "didn", "give", "all", "show", "any", "using", "on", "further", "around", "back",
			"least", "since", "anyone", "once", "can", "hereafter", "be", "seems", "their", "myself", 
			"nine", "also", "system", "at", "more", "out", "twelve", "therein", "almost", "except", 
			"last", "did", "something", "besides", "via", "whenever", "formerly", "cry", "one", "hundred", 
			"sixty", "after", "well", "them", "namely", "empty", "three", "even", "along", "because", 
			"ourselves", "such", "top", "due", "inc", "themselves"};
	
    private static void addDoc(IndexWriter w, String docId, String docText) throws IOException {
  	  Document doc = new Document();
  	  doc.add(new StringField("docid", docId, Field.Store.YES));
  	  doc.add(new TextField("docText", docText, Field.Store.YES));
  	  w.addDocument(doc);
  	}
    
    /* Function : readWikiPages
     * 
     * Description : This is the big function that parses through all the wiki pages
     * and builds our index. 
     * 
     * It will iterate over every folder containing all 80 files.
     * 
     * For each of the 80 files, it will iterate over every line.
     * 
     * It identifies the START of each individual article in a file by
     * finding the "[[ ... ]"
     * 
     * Next we identify the END of each article by finding the start of the next article 
     * "[[ ... ]".
     * 
     * We save the article title, and the lemmatized article text into a lucene Document,
     * for our index.
     * 
     */
    
    
	public static void readWikiPages() throws IOException {
		FSDirectory index = FSDirectory.open(Paths.get(indexPath));
		w = new IndexWriter(index, config);
		ClassLoader classLoader = QueryEngine.class.getClassLoader();
		File dir = new File(classLoader.getResource("wikiPages").getPath());
		Map<String, String> wikiPages = new HashMap<>();
		File[] dirList = dir.listFiles();
		String title = "";
		Boolean offset = false;
		if(dirList != null) {
			// For every wiki file in our directory
			for (File child : dirList) {
				// Scan every line in that file
				try (Scanner inputScanner = new Scanner(child)) {
		            while (inputScanner.hasNextLine()) {
		            	// We do this weird offset thing because we have to access the nextline to find end of wiki page.
		            	// We set the title when we access it the previous time, but then go back to normal.
		            	if (!offset) {
		            		title = inputScanner.nextLine();
		            	}
		            	offset = false;
		            	if (title.length() >= 2 && title.length() <= 75) {
		            		// If the line is a title
		            		if (title.charAt(0) == '[' && title.charAt(1) == '[' && title.charAt(title.length() - 2) == ']') {
		            			title = title.replaceAll("\\p{Punct}", "");
		            			String pageText = "";

            					// Read through wiki article that was a match
            					while(inputScanner.hasNextLine()) {
            						String pageLine = inputScanner.nextLine();
            						pageText = pageText + " " + pageLine;
            						//System.out.println("Page text \n" + pageText);
            						// Look for next beginning of next wiki page
            						if (pageLine.length() >= 2 && pageLine.length() <= 75) {
            							if (pageLine.charAt(0) == '[' && pageLine.charAt(1) == '[' && pageLine.charAt(pageLine.length() - 2) == ']') {
            		            			//Lemmatize the text and add it to map.
            								pageText = pageText.replaceAll("'", "");
            								pageText = pageText.replaceAll("\\p{Punct}", " ");
            								pageText = removeStopWords(pageText);
            		            			Sentence sent = new Sentence(pageText.replaceAll("\\p{Punct}", " "));
            		            			addDoc(w, title, sent.lemmas().toString().replaceAll("\\p{Punct}", " "));
            		            			title = pageLine;
            		            			offset = true;
            		            			break;
            		            		} else {
            		            			pageText += pageLine;		
            		            		}
            						}
            					}		            			
			            	}
		            	}	
		            }
		            inputScanner.close();    
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
			try {
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/* Description : 
	 * This function runs the Lucene Default BM25 retrieval algorithm on a single 
	 * query, searching the entire index for the answer.
	 * 
	 * In the scope of this project, this is called once for every Jeopardy question.
	 * The Jeopardy Question itself undergoes some formatting before this function, and at the beginning
	 * of this function.
	 * 
	 * You as user have to indicate the Index you want to use, at the very top of 
	 * the program in the variable preBuiltIndexPath.
	 * 
	 */
	public static List<ResultClass> queryBasic(String querystr) throws java.io.FileNotFoundException,java.io.IOException {
    	ScoreDoc[] hits = null;
        List<ResultClass>  ans=new ArrayList<ResultClass>();
        try { 
        	Sentence querySent = new Sentence(querystr);
        	querystr = querySent.lemmas().toString().replaceAll("\\p{Punct}", "");


			Query q = new QueryParser("docText", analyzer).parse(querystr);

			int hitsPerPage = 1;
	        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
	        IndexSearcher searcher = new IndexSearcher(reader);
	        //searcher.setSimilarity(new ClassicSimilarity());
	        TopDocs docs = searcher.search(q,  hitsPerPage);
	        hits = docs.scoreDocs;

	        for (int i = 0; i < hits.length; ++i) {
	        	int docId = hits[i].doc;
	        	double docScore = hits[i].score;
	        	Document d = searcher.doc(docId);
	        	ResultClass result = new ResultClass();
	        	result.DocName = d;
	        	result.docScore = hits[i].score;
	        	ans.add(result);
	        }
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return ans;
    }
	
	
	/*
	 * Description : This functio extracts the answers and clues/questions from
	 * the file given containing Jeopardy questions and answers.
	 * 
	 * It can handle any number of Jeopardy questions but they must follow the
	 * 
	 * CATEGORY
	 * QUESTION
	 * ANSWER
	 * BLANK LINE
	 * 
	 * format.
	 */
	public static String[][] getAnswers(String filePath) {
		String[][] answers = new String[100][2];
		int i = 0;
		ClassLoader classLoader = QueryEngine.class.getClassLoader();
		File file = new File(classLoader.getResource(filePath).getFile());
		try (Scanner inputScanner = new Scanner(file)) {
            while (inputScanner.hasNextLine()) {
            	String title = inputScanner.nextLine();
            	String question = inputScanner.nextLine();
            	String answer = inputScanner.nextLine();
            	String gap = inputScanner.nextLine();
            	answers[i][0] = question;
            	answers[i][1] = answer;
            	i++;
            }
            inputScanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
		return answers;
	}
	
	public static String askUserToBuildIndex() {
		Scanner userInput = new Scanner(System.in);
		System.out.println("Do you want to build an index? Y/N");
		String input = userInput.nextLine();
		while (!input.toLowerCase().equals("y") && !input.toLowerCase().equals("n")){
			System.out.println("Please enter Y or N");
			System.out.println("Do you want to build an index? Y/N");
			input = userInput.nextLine();
		}
		return input;
	}
	
	public static String askUserToRunQuery() {
		Scanner userInput = new Scanner(System.in);
		System.out.println("Do you want to build run a query? Y/N");
		String input = userInput.nextLine();
		while (!input.toLowerCase().equals("y") && !input.toLowerCase().equals("n")){
			System.out.println("Please enter Y or N");
			System.out.println("Do you want to build run a query? Y/N");
			input = userInput.nextLine();
		}
		return input;
	}
	
	public static String getFilePath() {
		Scanner userInput = new Scanner(System.in);
		String input = userInput.nextLine();
		try {
			Paths.get(input);
		} catch (InvalidPathException ex) {
			System.out.println("That was an invalid path! \nPlease try again.");
			return getFilePath();
		}
		return input;
	}
	
	public static String removeStopWords(String str) {
		for (String stopWord : stopWordList) {
			if (str.contains(" " + stopWord + " ")) {
				str = str.replace(" " + stopWord + " ", " ");
			}
		}
		return str;
	}
	
	/* Description :
	 * This function runs the function queryBasic and then compares the result
	 * to the correct answer. It prints out the results and updates the total
	 * number of questions asked and correct answers.
	 * 
	 */
	public static void runMyQuery(String question, String answer) throws FileNotFoundException, IOException {
		String guess = "";
		question = removeStopWords(question);
		results = queryBasic(question);
		if (results.size() > 0) {
			totalQuestions++;
			guess = results.get(0).DocName.get("docid");
		} 
		
		// Multiple answers indicated with "|"
		if (answer.contains("|")) {
			String[] multipleAnswers = answer.split("\\|");
			for (String ans : multipleAnswers) {
				if (guess.equals(ans.replaceAll("\\p{Punct}", ""))) {
					answersCorrect++;
					System.out.println("\nRIGHT\nGuess was : " + guess);
					System.out.println("Answer was : " + ans.replaceAll("\\p{Punct}", ""));
					//Only want to get one right, should need this but a safety
					break;
				} else {
					System.out.println("\nWRONG\nGuess was : " + guess);
					System.out.println("Answer was : " + ans.replaceAll("\\p{Punct}", ""));
				}
			}
		// Only one answer
		} else {
			if (guess.equals(answer.replaceAll("\\p{Punct}", ""))) {
				answersCorrect++;
				System.out.println("\nRIGHT\nGuess was : " + guess);
				System.out.println("Answer was : " + answer.replaceAll("\\p{Punct}", ""));
			} else {
				System.out.println("\nWRONG\nGuess was : " + guess);
				System.out.println("Answer was : " + answer);
			}
		}
	}

	public static void main(String[] args ) throws FileNotFoundException, IOException {
		String userInputIndex = askUserToBuildIndex();
		String userInputQuery = askUserToRunQuery();

		String queryPath = "";
		boolean buildIndex;
		boolean runQuery;
		
		if (userInputIndex.equals("y")) {
			buildIndex = true;
		} else {
			buildIndex = false;
		}
		
		if (userInputQuery.equals("y")) {
			System.out.println("Please enter the file path to your query txt file");
			queryPath = getFilePath();
			runQuery = true;
		} else {
			runQuery = false;
		}
		
		if (buildIndex) {
			readWikiPages();
			System.out.println("Completed Index");
		}
		if (runQuery) {
			String[][] answers = getAnswers(queryPath);
			results = new ArrayList<>();
			answersCorrect = 0;
			totalQuestions = 0;
			int i;
			
			String question;
			String answer;
			
			for (i = 0; i < 100; i++) {
				question = answers[i][0];
				answer = answers[i][1];
				runMyQuery(question, answer);
			}
			System.out.println("\nAnswers correct : " + answersCorrect);
			System.out.println("Total questions : " + totalQuestions);
		}


	}
}
