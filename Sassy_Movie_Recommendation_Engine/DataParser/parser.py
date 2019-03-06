import csv
import json

# open csv
# fPath = raw_input('Enter the file name: ')

# headerList = []
allGenres = []
allKeywords = []
movies = []
hasGenre = []
hasKeyword = []

print 'processing source file...'
with open('tmdb_5000_movies.csv', 'r') as csv_file:
    csv_reader = csv.reader(csv_file)

    lineNum = 0
    for line in csv_reader:
        # print line
        if lineNum == 0:
            headerList = line
        else:
            # title, release date, vote average, genres, keywords
            movie = {'mID': len(movies) + 1, 'title': line[17], 'release date': line[11], 'vote avg': line[18]}

            genres = json.loads(line[1])  # convert json string to list of genres
            for genre in genres:
                if genre['name'] not in allGenres:  # if genre not seen before
                    allGenres.append(genre['name'])  # add to list of all genres
                    # allGenres.append({'gID': len(allGenres) + 1, 'gName': genre['name']})  # add to list of all genres
                hasGenre.append({'mID': movie['mID'], 'gID': allGenres.index(genre['name']) + 1})

            keywords = json.loads(line[4])  # convert json string to list of genres
            for keyword in keywords:
                # if u'\xa0' in keyword['name'] or u'\xe1' in keyword['name']:
                #     print keyword
                #     keyword['name'] = keyword['name'][0:-1]
                #     print keyword
                if keyword['name'] not in allKeywords:  # if keyword not seen before
                    allKeywords.append(keyword['name'].encode('ascii', 'replace'))  # add to list of all keywords
                hasKeyword.\
                    append({'mID': movie['mID'], 'kID': allKeywords.index(keyword['name'].
                                                                          encode('ascii', 'replace')) + 1})

            movies.append(movie)  # add movie to list of all movies

        # if lineNum == 5:
        #     break
        lineNum += 1

    # for movie in movies:
    #     print movie
    #
    # print hasGenre
    # print hasKeyword
    # print allGenres
    # print allKeywords

# Build csv files from lists #############
print 'building movies.csv...'
with open('movies.csv', 'w') as movie_csv:
    fields = ['mID', 'title', 'release date', 'vote avg']
    csv_writer = csv.DictWriter(movie_csv, fieldnames=fields, lineterminator='\n')
    csv_writer.writeheader()
    csv_writer.writerows(movies)

print 'building genres.csv...'
with open('genres.csv', 'w') as genre_csv:
    csv_writer = csv.writer(genre_csv, lineterminator='\n')
    csv_writer.writerow(['gID', 'gName'])
    for genre in allGenres:
        row = [allGenres.index(genre) + 1, genre]
        csv_writer.writerow(row)

print 'building keywords.csv...'
with open('keywords.csv', 'w') as keyword_csv:
    csv_writer = csv.writer(keyword_csv, lineterminator='\n')
    csv_writer.writerow(['kID', 'kName'])
    for keyword in allKeywords:
        # if u'\xa0' in keyword or u'\xe1' in keyword or u'\xe9' in keyword or u'\xed' in keyword:
        #     print keyword
        row = [allKeywords.index(keyword) + 1, keyword]
        csv_writer.writerow(row)

print 'building has_genre.csv...'
with open('has_genre.csv', 'w') as hasGenre_csv:
    fields = ['mID', 'gID']
    csv_writer = csv.DictWriter(hasGenre_csv, fieldnames=fields, lineterminator='\n')
    csv_writer.writeheader()
    csv_writer.writerows(hasGenre)

print 'building has_keyword.csv...'
with open('has_keyword.csv', 'w') as hasKeyword_csv:
    fields = ['mID', 'kID']
    csv_writer = csv.DictWriter(hasKeyword_csv, fieldnames=fields,  lineterminator='\n')
    csv_writer.writeheader()
    csv_writer.writerows(hasKeyword)

print 'DONE!'
