Program:	Sassy Movie Recomendation Engine
Course:		COP 4710 - Data Modeling
Student:	Austin Green
Instructor:	Dr. Ayan Dutta
Date Due:	12/04/2018

CONTENTS:
0.0	-	Submission Notes
1.0	-	How to Run
2.0	-	Example Output
3.0	-	Developer Notes


0.0	-	Submission Notes
- The JAR file is inside the Eclipse RecomendationEngine project file.
- The program accesses the CSV files directly from the DataParser project directory.


1.0	-	How to Run
1.1	-	Eclipse Envirionment Execution
	0. Open Eclipse
	1. Using Eclipse, open the 'RecomendationEngine' project folder and allow project to load
	2. Run the Recomender.java file
	3. Follow the sassy prompts in the console


3.0	-	Example Output
Selected Movie: avatar
Results:
1 Star Trek Into Darkness    9 features in common
2 The Fifth Element          7 features in common
3 Aliens                     7 features in common
4 Predators                  6 features in common
5 AlienÂ³                    6 features in common

Selected Movie: The Terminator
Results:
1 Terminator 3: Rise of the Machines   10 features in common
2 Terminator Salvation                  9 features in common
3 Terminator Genisys                    9 features in common
4 Terminator 2: Judgment Day            8 features in common
5 Aliens                                7 features in common
6 The Matrix Revolutions    7 features in common
7 The Matrix Reloaded       6 features in common
8 The Matrix                5 features in common
9 RoboCop 3                 5 features in common
10 Children of Men           5 features in common

Selected Movie: Frozen
Results:
1 Wreck-It Ralph          11 features in common
2 Big Hero 6               8 features in common
3 Aladdin                  7 features in common
4 Mr. Peabody & Sherman    7 features in common
5 The Angry Birds Movie    6 features in common

Selected Movie: Gamer
Results:
1 Ghost Rider: Spirit of Vengeance     5 features in common
2 Final Fantasy: The Spirits Within    5 features in common
3 They Live                            5 features in common
4 Repo Men                             5 features in common
5 Pitch Black                          5 features in common

Selected Movie: The Curious Case of Benjamin Button
Results:
1 The Mortal Instruments: City of Bones    5 features in common
2 The Good German                          5 features in common
3 The Time Traveler's Wife                 5 features in common
4 Zodiac                                   5 features in common
5 The Red Violin                           5 features in common


3.0	-	Developer Notes
3.1	-	Summary
This movie recomendation engine identifies recomendations by counting the total 
number of common features shared between a user-selected movie any all other 
movies and sorting those movies by the highest count.

3.2	-	Strengths
- This engine excells at matching movies based on similarity.
- This engine gives power to the user by providing a scroll feature which allows 
users to scroll through up to 50 movies from a set of recomendations.

3.3	-	 Weaknesses
- This engine could be improved by sorting movies with the same common feature counts by popularity.

