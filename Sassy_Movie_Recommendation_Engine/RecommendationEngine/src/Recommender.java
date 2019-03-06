import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import sun.security.jca.GetInstance;

public class Recommender extends MovieDBuilder {
	
	private static ArrayList<Integer> moviesByID, genresByID, keywordsByID, actorsByID, directorsByID;
	private static Movie[] movies;
	private static Has_This[] genres, keywords, actors, directors;
	private static int maxVotes = 0;

	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("________Movie Recommendation Engine________\n");
		// check if DB needs to be constructed
		System.out.print("Build movie database?(y/n)\n-> ");
		String input = scan.nextLine();
		if(input.toLowerCase().charAt(0) == 'y') {
			System.out.println();
			buildDB();
		} else {
			System.out.println("Skipping DB construction\n");
		}
		
		System.out.println("Starting engine...");
		Connection conn = null;
		try {
			// Initialize Connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS); 
			String sql;
			Statement stmt = conn.createStatement();
			ResultSet rs;
			int rsCount, rsSize;
			
			//Use Database
			sql = "USE " + DB_NAME;
			stmt.executeUpdate(sql);
			
			System.out.print("discovering The Matrix: ");
			// BUILD DATA ARRAYS/OBJECTS
			// movies
			sql = "SELECT COUNT(*) FROM movies";
			rs = stmt.executeQuery(sql);
			rs.next();
			rsSize = rs.getInt(1);
			sql = "SELECT * FROM movies";
			rs = stmt.executeQuery(sql);
			rsCount = 0;
			moviesByID = new ArrayList<Integer>();
			movies = new Movie[rsSize];
			while (rs.next()) {
				movies[rsCount] = new Movie(rs.getInt(1), rs.getString(2), rs.getDate(3), 
						rs.getDouble(4), rs.getDouble(5), rs.getInt(6));
				rsCount++;
				/*if(rs.getInt(6) > maxVotes) {
					maxVotes = rs.getInt(6);
				}*/
			}
			for (int i = 0; i < movies.length; i++) {
				moviesByID.add(new Integer(movies[i].getId()));
			}
			// genres
			sql = "SELECT COUNT(*) FROM genres";
			rs = stmt.executeQuery(sql);
			rs.next();
			rsSize = rs.getInt(1);
			sql = "SELECT * FROM genres";
			rs = stmt.executeQuery(sql);
			rsCount = 0;
			genresByID = new ArrayList<Integer>();
			genres = new Has_This[rsSize];
			while (rs.next()) {
				genres[rsCount] = new Has_This(rs.getInt(1), rs.getString(2));
				rsCount++;
			}
			for (int i = 0; i < genres.length; i++) {
				genresByID.add(new Integer(genres[i].getId()));
			}
			// has_genre
			sql = "SELECT COUNT(*) FROM has_genre";
			rs = stmt.executeQuery(sql);
			rs.next();
			rsSize = rs.getInt(1);
			sql = "SELECT * FROM has_genre";
			rs = stmt.executeQuery(sql);
			rsCount = 0;
			while (rs.next()) {
				movies[moviesByID.indexOf(rs.getInt(1))].genres.add(genres[genresByID.indexOf(rs.getInt(2))]);
				movies[moviesByID.indexOf(rs.getInt(1))].genreIDs.add(rs.getInt(2));
				genres[genresByID.indexOf(rs.getInt(2))].movies.add(movies[moviesByID.indexOf(rs.getInt(1))]);
				rsCount++;
			}
			// keywords
			sql = "SELECT COUNT(*) FROM keywords";
			rs = stmt.executeQuery(sql);
			rs.next();
			rsSize = rs.getInt(1);
			// System.out.println("# of keywords: " + rsSize);
			sql = "SELECT * FROM keywords";
			rs = stmt.executeQuery(sql);
			rsCount = 0;
			keywordsByID = new ArrayList<Integer>();
			keywords = new Has_This[rsSize];
			while (rs.next()) {
				keywords[rsCount] = new Has_This(rs.getInt(1), rs.getString(2));
				rsCount++;
			}
			for (int i = 0; i < keywords.length; i++) {
				keywordsByID.add(new Integer(keywords[i].getId()));
			}
			// has_keyword
			sql = "SELECT COUNT(*) FROM has_keyword";
			rs = stmt.executeQuery(sql);
			rs.next();
			rsSize = rs.getInt(1);
			sql = "SELECT * FROM has_keyword";
			rs = stmt.executeQuery(sql);
			rsCount = 0;
			while (rs.next()) {
				movies[moviesByID.indexOf(rs.getInt(1))].keywords.add(keywords[keywordsByID.indexOf(rs.getInt(2))]);
				movies[moviesByID.indexOf(rs.getInt(1))].keywordIDs.add(rs.getInt(2));
				keywords[keywordsByID.indexOf(rs.getInt(2))].movies.add(movies[moviesByID.indexOf(rs.getInt(1))]);
				rsCount++;
			}
			// actors
			sql = "SELECT COUNT(*) FROM actors";
			rs = stmt.executeQuery(sql);
			rs.next();
			rsSize = rs.getInt(1);
			sql = "SELECT * FROM actors";
			rs = stmt.executeQuery(sql);
			rsCount = 0;
			actorsByID = new ArrayList<Integer>();
			actors = new Has_This[rsSize];
			while (rs.next()) {
				actors[rsCount] = new Has_This(rs.getInt(1), rs.getString(2));
				rsCount++;
			}
			for (int i = 0; i < actors.length; i++) {
				actorsByID.add(new Integer(actors[i].getId()));
			}
			// has_actor
			sql = "SELECT COUNT(*) FROM has_actor";
			rs = stmt.executeQuery(sql);
			rs.next();
			rsSize = rs.getInt(1);
			sql = "SELECT * FROM has_actor";
			rs = stmt.executeQuery(sql);
			rsCount = 0;
			while (rs.next()) {
				movies[moviesByID.indexOf(rs.getInt(1))].actors.add(actors[actorsByID.indexOf(rs.getInt(2))]);
				movies[moviesByID.indexOf(rs.getInt(1))].actorIDs.add(rs.getInt(2));
				actors[actorsByID.indexOf(rs.getInt(2))].movies.add(movies[moviesByID.indexOf(rs.getInt(1))]);
				rsCount++;
			}
			// directors
			sql = "SELECT COUNT(*) FROM directors";
			rs = stmt.executeQuery(sql);
			rs.next();
			rsSize = rs.getInt(1);
			sql = "SELECT * FROM directors";
			rs = stmt.executeQuery(sql);
			rsCount = 0;
			directorsByID = new ArrayList<Integer>();
			directors = new Has_This[rsSize];
			while (rs.next()) {
				directors[rsCount] = new Has_This(rs.getInt(1), rs.getString(2));
				rsCount++;
			}
			for (int i = 0; i < directors.length; i++) {
				directorsByID.add(new Integer(directors[i].getId()));
			}
			// has_director
			sql = "SELECT COUNT(*) FROM has_director";
			rs = stmt.executeQuery(sql);
			rs.next();
			rsSize = rs.getInt(1);
			sql = "SELECT * FROM has_director";
			rs = stmt.executeQuery(sql);
			rsCount = 0;
			while (rs.next()) {
				movies[moviesByID.indexOf(rs.getInt(1))].directors.add(directors[directorsByID.indexOf(rs.getInt(2))]);
				movies[moviesByID.indexOf(rs.getInt(1))].directorIDs.add(rs.getInt(2));
				directors[directorsByID.indexOf(rs.getInt(2))].movies.add(movies[moviesByID.indexOf(rs.getInt(1))]);
				rsCount++;
			}
			System.out.println("Neo found");
			System.out.print("calculating distances: ");
			Thread.sleep(1500);
			System.out.println("displacements placed");
			System.out.println("Sassy Engine ready for movie recommending!");
			System.out.println("___________________________________________\n");
			
			// GET INPUT MOVIE
			ArrayList<String> rsRecords;
			Movie selectedMovie;
			while(true) {
				rsRecords = new ArrayList<String>();
				selectedMovie = null;
				fancyPrint("Enter all or part of a movie title you like: \n-> ");
				input = scan.nextLine();
				sql = "SELECT COUNT(*) "
						+ "FROM movies "
						+ "WHERE title LIKE '%" + input + "%';";
				rs = stmt.executeQuery(sql);
				rs.next();
				rsSize = rs.getInt(1);
				if(rsSize == 0) {
					fancyPrintln("I can't help you with that.\n");
					continue;
				} else if (rsSize > 9) {
					fancyPrintln("You're going to need to be more specific.\n");
					continue;
				} else {
					sql = "SELECT mID, title "
							+ "FROM movies "
							+ "WHERE title LIKE '%" + input + "%';";
					rs = stmt.executeQuery(sql);
					if(rsSize == 1) {
						rs.next();
						selectedMovie = movies[moviesByID.indexOf(rs.getInt(1))];
						fancyPrintln("\nI found \"" + selectedMovie.getTitle() + "\"");
					} else { // otherwise between 2-10 
						fancyPrintln("Did you mean one of these? ");
						rsCount = 0;
						while(rs.next()) {
							rsCount++;
							System.out.println(rsCount + ") " + rs.getString(2));
							rsRecords.add(rs.getString(1));
						}
						fancyPrint("Which one do you want?(enter #)\n-> ");
						input = scan.nextLine();
						selectedMovie = movies[moviesByID.indexOf(Integer.parseInt(rsRecords.get(Integer.parseInt(input)-1)))];
						fancyPrintln("\n\"" + selectedMovie.getTitle() + "\" eh?");
					}
				}
				
				// RECOMMENDATION
				if(selectedMovie != null) {
					int[][] sortedRecomendations = getRecomendationIDs(selectedMovie, 50); // [ID][count]
					
					// get longest title length
					int longestTitleLength = 0;
					for (int i = 0; i < 5; i++) {
						if (movies[moviesByID.indexOf(sortedRecomendations[i][0])].getTitle().length() > longestTitleLength) {
							longestTitleLength = movies[moviesByID.indexOf(sortedRecomendations[i][0])].getTitle().length();
						}
					}
					
					// sortByPopularity(recomendationIDs);
					
					fancyPrintln("I guess I'd recommend these: \n");
					for (int i = 0; i < sortedRecomendations.length; i++) {
						System.out.print((i+1) + " ");
						System.out.printf("%-" + longestTitleLength + "s%5d features in common\n", 
								movies[moviesByID.indexOf(sortedRecomendations[i][0])].getTitle(), 
								sortedRecomendations[i][1]);
						if( (i+1) % 5 == 0 ) {
							fancyPrint("\nDo you want to see more results?(y/n)\n-> ");
							input = scan.nextLine();
							if(input.toLowerCase().charAt(0) == 'y') {
								System.out.println();
								longestTitleLength = 0;
								for (int j = i+1; j < i+6; j++) {
									if (movies[moviesByID.indexOf(sortedRecomendations[j][0])].getTitle().length() > longestTitleLength) {
										longestTitleLength = movies[moviesByID.indexOf(sortedRecomendations[j][0])].getTitle().length();
									}
								}
								continue;
							} else {
								break;
							}
						}
					}
				}
				
				fancyPrint("\nOkay... ");
				Thread.sleep(750);
				
				// Ask user for another 'bout
				fancyPrint("\nYou want to try another movie, or what?(y/n)\n-> ");
				input = scan.nextLine();
				if(input.toLowerCase().charAt(0) == 'y') {
					fancyPrint("Ugh. ");
					Thread.sleep(800);
					fancyPrintln("Fine.\n");
					continue;
				} else {
					break;
				}
				
			} // end user input while(true)
			
			
		} catch(SQLException se) {
			se.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn!=null) {
					fancyPrint("\nWell...");
					// System.out.println("Closing connection...");
					conn.close();
					// System.out.println("Connection closed!\n");
				}
			} catch(SQLException se){
				System.out.println("Connection failed to close!\n");
				se.printStackTrace();
			} 
		} // end try/catch/finally
		
		fancyPrintln(" Goodbye, then.");

	} // end main()
	
	private static void fancyPrint(String inputString) {
		for (int i = 0; i < inputString.length(); i++) {
			System.out.print(inputString.charAt(i));
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void fancyPrintln(String inputString) {
		for (int i = 0; i < inputString.length(); i++) {
			System.out.print(inputString.charAt(i));
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println();
	}
	
	private static int[][] getRecomendationIDs(Movie userMovie, int resultSize) {
		
		ArrayList<Integer> commonMovieIDs = new ArrayList<Integer>();
		ArrayList<AtomicInteger> commonMovieCounts = new ArrayList<AtomicInteger>();
		int matrixSize = resultSize;
		
		// get counts for genres
		for(int i = 0; i < userMovie.genres.size(); i++) {
			for(int j = 0; j < userMovie.genres.get(i).movies.size(); j++)
			if(commonMovieIDs.indexOf(userMovie.genres.get(i).movies.get(j).getId()) == -1) { // if movie not seen yet
				commonMovieIDs.add(userMovie.genres.get(i).movies.get(j).getId()); // add to common movies
				commonMovieCounts.add(new AtomicInteger(1)); // start count at 1
			} else { // otherwise, movie already seen
				// increment count
				commonMovieCounts.get(commonMovieIDs.indexOf(userMovie.genres.get(i).movies.get(j).getId())).incrementAndGet();
			}
		}
		// get counts for keywords
		for(int i = 0; i < userMovie.keywords.size(); i++) {
			for(int j = 0; j < userMovie.keywords.get(i).movies.size(); j++)
			if(commonMovieIDs.indexOf(userMovie.keywords.get(i).movies.get(j).getId()) == -1) { // if movie not seen yet
				commonMovieIDs.add(userMovie.keywords.get(i).movies.get(j).getId()); // add to common movies
				commonMovieCounts.add(new AtomicInteger(1)); // start count at 1
			} else { // otherwise, movie already seen
				// increment count
				commonMovieCounts.get(commonMovieIDs.indexOf(userMovie.keywords.get(i).movies.get(j).getId())).incrementAndGet();
			}
		}
		// get counts for actors
		for(int i = 0; i < userMovie.actors.size(); i++) {
			for(int j = 0; j < userMovie.actors.get(i).movies.size(); j++)
			if(commonMovieIDs.indexOf(userMovie.actors.get(i).movies.get(j).getId()) == -1) { // if movie not seen yet
				commonMovieIDs.add(userMovie.actors.get(i).movies.get(j).getId()); // add to common movies
				commonMovieCounts.add(new AtomicInteger(1)); // start count at 1
			} else { // otherwise, movie already seen
				// increment count
				commonMovieCounts.get(commonMovieIDs.indexOf(userMovie.actors.get(i).movies.get(j).getId())).incrementAndGet();
			}
		}
		// get counts for directors
		for(int i = 0; i < userMovie.directors.size(); i++) {
			for(int j = 0; j < userMovie.directors.get(i).movies.size(); j++)
			if(commonMovieIDs.indexOf(userMovie.directors.get(i).movies.get(j).getId()) == -1) { // if movie not seen yet
				commonMovieIDs.add(userMovie.directors.get(i).movies.get(j).getId()); // add to common movies
				commonMovieCounts.add(new AtomicInteger(1)); // start count at 1
			} else { // otherwise, movie already seen
				// increment count
				commonMovieCounts.get(commonMovieIDs.indexOf(userMovie.directors.get(i).movies.get(j).getId())).incrementAndGet();
			}
		}
		
		// System.out.println("# of common movies: " + commonMovieIDs.size());
		// matrixSize = commonMovieIDs.size();
		
		// convert ArrayLists into arrays
		int[] mIDs = new int[commonMovieIDs.size()], mCounts = new int[commonMovieIDs.size()];
		for (int i = 0; i < commonMovieIDs.size(); i++) {
			mIDs[i] = commonMovieIDs.get(i).intValue();
			mCounts[i] = commonMovieCounts.get(i).get();
		}
		
		// sort arrays
		ArrayUtils.pairSort(mIDs, mCounts); // returns 0 1 2 3 etc...
		
		// convert to resorted 2D array
		int[][] mIDsByCount = new int[matrixSize][2];
		
		for(int si = commonMovieIDs.size()-2, mi = 0; si >= commonMovieIDs.size()-1-matrixSize; si--, mi++) { // grab top matches
			mIDsByCount[mi][0] = mIDs[si]; // holds 10 9 8 7 etc...
			mIDsByCount[mi][1] = mCounts[si]; // holds 10 9 8 7 etc...
		}
		
		return mIDsByCount;
		
		// resort ID array
		/*int[] mostSimilarByID = new int[matrixSize];
		for(int si = commonMovieIDs.size()-1, mi = 0; si >= mIDs.length-matrixSize; si--, mi++) { // grab top matches
			mostSimilarByID[mi] = mIDs[si]; // holds 10 9 8 7 etc...
		}*/
		
		// matrix it up
		/*int[][] similarityMatrix = new int[matrixSize][userMovie.genres.size() + // ordered by mostSimilarByID
		                                               userMovie.keywords.size() + 
		                                               userMovie.actors.size() + 
		                                               userMovie.directors.size()];
		for (int i = 0; i < similarityMatrix.length; i++) {
			// genres
			for (int j = 0; j < userMovie.genres.size(); j++) {
				if (movies[moviesByID.indexOf(mostSimilarByID[i])].genreIDs.indexOf(userMovie.genreIDs.get(j)) != -1) {
					similarityMatrix[i][j] = 1;
				} else {
					similarityMatrix[i][j] = 0;
				}
			}
			// keywords
			for (int j = 0; j < userMovie.keywords.size(); j++) {
				if (movies[moviesByID.indexOf(mostSimilarByID[i])].keywordIDs.indexOf(userMovie.keywordIDs.get(j)) != -1) {
					similarityMatrix[i][userMovie.genres.size() + j] = 1;
				} else {
					similarityMatrix[i][userMovie.genres.size() + j] = 0;
				}
			}
			// actors
			for (int j = 0; j < userMovie.actors.size(); j++) {
				if (movies[moviesByID.indexOf(mostSimilarByID[i])].actorIDs.indexOf(userMovie.actorIDs.get(j)) != -1) {
					similarityMatrix[i][userMovie.genres.size() + userMovie.keywords.size() + j] = 1;
				} else {
					similarityMatrix[i][userMovie.genres.size() + userMovie.keywords.size() + j] = 0;
				}
			}
			// directors
			for (int j = 0; j < userMovie.directors.size(); j++) {
				if (movies[moviesByID.indexOf(mostSimilarByID[i])].directorIDs.indexOf(userMovie.directorIDs.get(j)) != -1) {
					similarityMatrix[i][userMovie.genres.size() + userMovie.keywords.size() + userMovie.actors.size() + j] = 1;
				} else {
					similarityMatrix[i][userMovie.genres.size() + userMovie.keywords.size() + userMovie.actors.size() + j] = 0;
				}
			}
			
			if(movies[moviesByID.indexOf(mostSimilarByID[i])].getVote_count() > maxVotes) {
				maxVotes = movies[moviesByID.indexOf(mostSimilarByID[i])].getVote_count();
			}
			
		}*/
		
		// display similarity matrix
		/*for(int i = 0; i < similarityMatrix.length; i++) {
			for(int j = 0; j < similarityMatrix[i].length; j++) {
				System.out.printf("%d ", similarityMatrix[i][j]);
			}
			System.out.println();
		}*/
		
		// Calculate similarity scores
		/*int[] similarityScores = new int[matrixSize];
		for (int i = 0; i < matrixSize; i++) {
			int sumation = 0;
			for (int j = 0; j < similarityMatrix[i].length; j++) {
				sumation += Math.pow(similarityMatrix[0][j] - similarityMatrix[i][j], 2);
			}
			similarityScores[i] = (int) Math.sqrt(sumation);
		}
		
		ArrayUtils.pairSort(mostSimilarByID, similarityScores);
		
		
		return mostSimilarByID;*/
	}
	

	private static void sortByPopularity(int[] mIDs) {
		
		double[] popularityScores = new double[mIDs.length];
		
		for (int i = 0; i < popularityScores.length; i++) {
			popularityScores[i] = Math.pow(movies[moviesByID.indexOf(mIDs[i])].getVote_average(), 2) * 
					movies[moviesByID.indexOf(mIDs[i])].getVote_average() / maxVotes;
		}
		
		/*for (int i = 0; i < popularityScores.length; i++) {
			System.out.printf("%3f ", popularityScores[i]);
		}*/
		
		ArrayUtils.pairSort(mIDs, popularityScores);
		
	}
}

class Movie {
	
	int id;
	String title;
	Date release_date;
	double popularity;
	double vote_average;
	int vote_count;
	ArrayList<Has_This> genres, keywords, actors, directors;
	ArrayList<Integer> genreIDs, keywordIDs, actorIDs, directorIDs;
	
	Movie(int id, String title, Date release_date, double popularity, double vote_average, int vote_count) {
		this.id = id;
		this.title = title;
		this.release_date = release_date;
		this.popularity = popularity;
		this.vote_average = vote_average;
		this.vote_count = vote_count;
		this.genres = new ArrayList<Has_This>();
		this.genreIDs = new ArrayList<Integer>();
		this.keywords = new ArrayList<Has_This>();
		this.keywordIDs = new ArrayList<Integer>();
		this.actors = new ArrayList<Has_This>();
		this.actorIDs = new ArrayList<Integer>();
		this.directors = new ArrayList<Has_This>();
		this.directorIDs = new ArrayList<Integer>();
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Date getRelease_date() {
		return release_date;
	}

	public double getPopularity() {
		return popularity;
	}

	public double getVote_average() {
		return vote_average;
	}

	public int getVote_count() {
		return vote_count;
	}
	
}

class Has_This {
	int id;
	String name;
	ArrayList<Movie> movies;
	
	public Has_This(int id, String name) {
		this.id = id;
		this.name = name;
		this.movies = new ArrayList<Movie>();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
}

class ArrayUtils {
	
	public static void pairSort(int idArr[], int countArr[]) { // sorts 2 sister arrays
		recDoubleSort(idArr, countArr, 0, idArr.length-1);
	}
	
	public static void pairSort(int idArr[], double countArr[]) { // sorts 2 sister arrays
		recDoubleSort(idArr, countArr, 0, idArr.length-1);
	}
	
    private static int partition(int idArr[], int countArr[], int low, int high) { 
        int pivot = countArr[high];  
        int i = (low-1); // index of smaller element 
        for (int j=low; j<high; j++) 
        { 
            // If current element is smaller than or 
            // equal to pivot 
            if (countArr[j] <= pivot) 
            { 
                i++; 
  
                // swap countArr[i] and countArr[j] 
                int temp = countArr[i]; 
                countArr[i] = countArr[j]; 
                countArr[j] = temp;
                
                temp = idArr[i]; 
                idArr[i] = idArr[j]; 
                idArr[j] = temp; 
            } 
        } 
  
        // swap countArr[i+1] and countArr[high] (or pivot) 
        int temp = countArr[i+1]; 
        countArr[i+1] = countArr[high]; 
        countArr[high] = temp;
        
        temp = idArr[i+1]; 
        idArr[i+1] = idArr[high]; 
        idArr[high] = temp;
  
        return i+1; 
    } 
    
    private static int partition(int idArr[], double countArr[], int low, int high) { 
        double pivot = countArr[high];  
        int i = (low-1); // index of smaller element 
        for (int j=low; j<high; j++) 
        { 
            // If current element is smaller than or 
            // equal to pivot 
            if (countArr[j] <= pivot) 
            { 
                i++; 
  
                // swap countArr[i] and countArr[j] 
                double temp = countArr[i]; 
                countArr[i] = countArr[j]; 
                countArr[j] = temp;
                
                temp = idArr[i]; 
                idArr[i] = idArr[j]; 
                idArr[j] = (int) temp; 
            } 
        } 
  
        // swap countArr[i+1] and countArr[high] (or pivot) 
        double temp = countArr[i+1]; 
        countArr[i+1] = countArr[high]; 
        countArr[high] = temp;
        
        temp = idArr[i+1]; 
        idArr[i+1] = idArr[high]; 
        idArr[high] = (int) temp;
  
        return i+1; 
    } 
  
  
    /*countArr[] --> Array to be sorted, 
      low  --> Starting index, 
      high  --> Ending index */
    public static void recDoubleSort(int idArr[], int countArr[], int low, int high) { 
        if (low < high) 
        { 
            /* pi is partitioning index, countArr[pi] is  
              now at right place */
            int pi = partition(idArr, countArr, low, high); 
  
            // Recursively sort elements before 
            // partition and after partition 
            recDoubleSort(idArr, countArr, low, pi-1); 
            recDoubleSort(idArr, countArr, pi+1, high); 
        } 
    }
    
    public static void recDoubleSort(int idArr[], double countArr[], int low, int high) { 
        if (low < high) 
        { 
            /* pi is partitioning index, countArr[pi] is  
              now at right place */
            int pi = partition(idArr, countArr, low, high); 
  
            // Recursively sort elements before 
            // partition and after partition 
            recDoubleSort(idArr, countArr, low, pi-1); 
            recDoubleSort(idArr, countArr, pi+1, high); 
        } 
    }
}