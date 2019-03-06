import csv
import json


def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False


allMovies = []  # list of all movie IDs
allGenres = []
allKeywords = {}
allActors = {}
allDirectors = []

movies = []
genres = []
hasGenre = []
keywords = []
hasKeyword = []
actors = []
hasActor = []
directors = []
hasDirector = []
print "processing movie file..."
with open("tmdb_5000_movies.csv", "rb") as csv_file:
    csv_reader = csv.DictReader(csv_file)

    lineNum = 0
    for line in csv_reader:
        lineNum += 1
        # print line["keywords"]

        # handle movies
        if line["id"] not in allMovies:
            allMovies.append(line["id"])
        else:
            exit("duplicate movie ID: " + line["id"])
        movie = {"mID": line["id"],
                 "title": line["title"],
                 "release_date": line["release_date"].encode('ascii', 'replace'),
                 "popularity": line["popularity"].encode('ascii', 'replace'),
                 "vote_average": line["vote_average"].encode('ascii', 'replace'),
                 "vote_count": line["vote_count"].encode('ascii', 'replace')}
        movies.append(movie)

        # handle genres
        for g in json.loads(line["genres"]):  # convert json string to list of genres
            if g["name"] not in allGenres:  # if genre not seen before
                allGenres.append(g["name"])  # add to list of all genres
                genre = {"gID": g["id"],
                         "gName": g["name"].encode('ascii', 'replace')}
                genres.append(genre)
            hasGenre.append({"mID": movie["mID"], "gID": g["id"]})

        # handle keywords
        for k in json.loads(line["keywords"]):  # convert json string to list of genres
            if k["id"] not in allKeywords:  # if keyword not seen before
                allKeywords[k["id"]] = k
                allKeywords[k["id"]]["count"] = 1
                keyword = {"kID": k["id"],
                           "kName": k["name"].encode('ascii', 'replace')}
                keywords.append(keyword)
            else:
                allKeywords[k["id"]]["count"] += 1
            hasKeyword.append({"mID": movie["mID"], "kID": k["id"]})

        print "\r" + str(lineNum) + " records processed",
        # if lineNum == 50:  # 50 records for testing
        #     break
print "\nmovie file processed!\n"

print "processing credits file..."
with open("tmdb_5000_credits.csv", "rb") as csv_file:
    csv_reader = csv.DictReader(csv_file)

    lineNum = 0
    for line in csv_reader:
        lineNum += 1
        # print line["crew"]

        # handle actors
        for a in json.loads(line["cast"]):  # convert json string to list of actors
            if a["id"] not in allActors:  # if actor not seen before
                allActors[a["id"]] = a
                allActors[a["id"]]["count"] = 1
                actor = {"aID": a["id"],
                         "aName": a["name"].encode('ascii', 'replace')}
                actors.append(actor)
            else:
                allActors[a["id"]]["count"] += 1
            ha = {"mID": line["movie_id"], "aID": a["id"]}
            # if not any(d["mID"] == ha["mID"] and d["aID"] == ha["aID"] for d in hasActor):
            hasActor.append(ha)

        # handle directors
        for d in json.loads(line["crew"]):  # convert json string to list of crew members
            if d["job"] == "Director":
                if d["id"] not in allDirectors:  # if director not seen before
                    allDirectors.append(d["id"])  # add to list of all genres
                    director = {"dID": d["id"],
                                "dName": d["name"].encode('ascii', 'replace')}
                    directors.append(director)
                hd = {"mID": line["movie_id"], "dID": d["id"]}
                # if not any(d["mID"] == hd["mID"] and d["dID"] == hd["dID"] for d in hasDirector):
                hasDirector.append(hd)

        print "\r" + str(lineNum) + " records processed",
        # if lineNum == 50:  # 50 records for testing
        #     break
print "\ncredits file processed!\n"

# remove possible duplicates
print "removing duplicates..."
numChecked = 0
seen = set()
has_actor = []
for d in hasActor:
    t = tuple(d.items())
    if t not in seen:
        seen.add(t)
        has_actor.append(d)
    numChecked += 1
    print "\rchecked " + str(numChecked) + " of " + str(len(hasActor)) + " in has_actor: " + \
          str((float(numChecked) / float(len(hasActor))) * 100) + "%",
print ""

numChecked = 0
seen = set()
has_director = []
for d in hasDirector:
    t = tuple(d.items())
    if t not in seen:
        seen.add(t)
        has_director.append(d)
    numChecked += 1
    print "\rchecked " + str(numChecked) + " of " + str(len(hasDirector)) + " in has_director: " + \
          str((float(numChecked) / float(len(hasDirector))) * 100) + "%",
print "\nduplicates removed!\n"

# Write New CSV Files
# write movies.csv
table = "movies"
print "Writing " + table + ".csv..."
with open(table + ".csv", 'wb') as csvfile:
    fieldnames = ["mID", "title", "release_date", "popularity", "vote_average", "vote_count"]
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(movies)
print str(len(movies)) + " records written\n"

# write genres.csv
table = "genres"
print "Writing " + table + ".csv..."
with open(table + ".csv", 'wb') as csvfile:
    fieldnames = ["gID", "gName"]
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(genres)
print str(len(genres)) + " records written\n"

# write has_genre.csv
table = "has_genre"
print "Writing " + table + ".csv..."
with open(table + ".csv", 'wb') as csvfile:
    fieldnames = ["mID", "gID"]
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(hasGenre)
print str(len(hasGenre)) + " records written\n"

# write keywords.csv
table = "keywords"
print "Writing " + table + ".csv..."
with open(table + ".csv", 'wb') as csvfile:
    fieldnames = ["kID", "kName"]
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    count = 0
    for k in keywords:
        if allKeywords[k["kID"]]["count"] >= 5:
            writer.writerow(k)
            count += 1
print str(count) + " of " + str(len(keywords)) + " records written\n"

# write has_keyword.csv
table = "has_keyword"
print "Writing " + table + ".csv..."
with open(table + ".csv", 'wb') as csvfile:
    fieldnames = ["mID", "kID"]
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    count = 0
    for hk in hasKeyword:
        if allKeywords[hk["kID"]]["count"] >= 5:
            writer.writerow(hk)
            count += 1
print str(count) + " of " + str(len(hasKeyword)) + " records written\n"

# write actors.csv
table = "actors"
print "Writing " + table + ".csv..."
with open(table + ".csv", 'wb') as csvfile:
    fieldnames = ["aID", "aName"]
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    count = 0
    for a in actors:
        if allActors[a["aID"]]["count"] >= 5:
            writer.writerow(a)
            count += 1
print str(count) + " of " + str(len(actors)) + " records written\n"

# write has_actor.csv
table = "has_actor"
print "Writing " + table + ".csv..."
with open(table + ".csv", 'wb') as csvfile:
    fieldnames = ["mID", "aID"]
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    count = 0
    for ha in has_actor:
        if allActors[ha["aID"]]["count"] >= 5:
            writer.writerow(ha)
            count += 1
print str(count) + " of " + str(len(has_actor)) + " records written\n"

# write directors.csv
table = "directors"
print "Writing " + table + ".csv..."
with open(table + ".csv", 'wb') as csvfile:
    fieldnames = ["dID", "dName"]
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(directors)
print str(len(directors)) + " records written\n"

# write has_director.csv
table = "has_director"
print "Writing " + table + ".csv..."
with open(table + ".csv", 'wb') as csvfile:
    fieldnames = ["mID", "dID"]
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(has_director)
print str(len(has_director)) + " records written\n"

print "DONE!"
