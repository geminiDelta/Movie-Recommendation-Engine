with open("movies" + ".csv", 'rb') as csvfile:
    lines = csvfile.readlines()
    for line in lines:
        print repr(line)
