import cv2
import numpy as np


def getFourCorners(tuple1,tuple2):
    #TOP,BOTTOM
    return [tuple1,(tuple2[0],tuple1[1]),(tuple1[0],tuple2[1]),tuple2]



def boardRecognition(file):
    image = cv2.imread(file)
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    mask = cv2.inRange(hsv, np.array([36, 25, 25]), np.array([70, 255, 255]))
    bluecnts = cv2.findContours(mask.copy(),
                                cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)[-2]
    if len(bluecnts) > 0:
        blue_area = max(bluecnts, key=cv2.contourArea)
        (xg, yg, wg, hg) = cv2.boundingRect(blue_area)
        cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (0, 255, 0), 2)


    fourCorners = getFourCorners((xg, yg),(xg + wg, yg + hg))


    ###############HORIZONTAL LINES##########################

    x1 = fourCorners[0][0]
    y1 = fourCorners[0][1]

    x2 = fourCorners[1][0]
    y2 = fourCorners[1][1]
    horizontalLines = []
    horizontalLines+=[fourCorners[0][1]]
    cv2.line(image,fourCorners[0],fourCorners[1], (0, 255, 0), 9)
    cv2.line(image,fourCorners[2],fourCorners[3], (0, 255, 0), 9)
    diference1 = 0
    diference2 = 0
    count = 0
    i = 1
    while y1 < fourCorners[2][1] and y2 < fourCorners[3][1]:
        y1 += 175
        y2 += 175

        if count < 7:
            cv2.line(image,(x1,y1),(x2,y2), (0, 255, 0), 9)
            horizontalLines += [y1]
        count+=1
        i+=1
    horizontalLines+=[fourCorners[2][1]]


    return horizontalLines



def verticalLines(file):
    image = cv2.imread(file)
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    mask = cv2.inRange(hsv, np.array([36, 25, 25]), np.array([70, 255, 255]))
    bluecnts = cv2.findContours(mask.copy(),
                                cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)[-2]
    if len(bluecnts) > 0:
        blue_area = max(bluecnts, key=cv2.contourArea)
        (xg, yg, wg, hg) = cv2.boundingRect(blue_area)
        cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (0, 255, 0), 2)


    fourCorners = getFourCorners((xg, yg),(xg + wg, yg + hg))

    x1 = fourCorners[0][0]
    y1 = fourCorners[0][1]

    x2 = fourCorners[2][0]
    y2 = fourCorners[2][1]
    verticalLines = []
    verticalLines+=[fourCorners[0][0]]
    cv2.line(image,fourCorners[0],fourCorners[2], (0, 255, 0), 9)
    cv2.line(image,fourCorners[1],fourCorners[3], (0, 255, 0), 9)

    diference1 = 0
    diference2 = 0
    count2 = 0
    j = 1
    while x1 < fourCorners[1][0] and x2 < fourCorners[3][0]:

        x1 += 175
        x2 += 175


        if count2 < 5:
            cv2.line(image, (x1, y1), (x2, y2), (0, 255, 0), 9)
            verticalLines += [x1]
        count2+=1
        j+= 1

    verticalLines+= [fourCorners[1][0]]

    return verticalLines



def squares(file):
    image = cv2.imread(file)
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    mask = cv2.inRange(hsv, np.array([36, 25, 25]), np.array([70, 255, 255]))
    bluecnts = cv2.findContours(mask.copy(),
                                cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)[-2]
    if len(bluecnts) > 0:
        blue_area = max(bluecnts, key=cv2.contourArea)
        (xg, yg, wg, hg) = cv2.boundingRect(blue_area)
        cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (0, 255, 0), 2)


    fourCorners = getFourCorners((xg, yg),(xg + wg, yg + hg))


    ###############HORIZONTAL LINES##########################

    x1 = fourCorners[0][0]
    y1 = fourCorners[0][1]

    x2 = fourCorners[1][0]
    y2 = fourCorners[1][1]
    horizontalLines = []
    horizontalLines+=[fourCorners[0][1]]
    cv2.line(image,fourCorners[0],fourCorners[1], (0, 255, 0), 9)
    cv2.line(image,fourCorners[2],fourCorners[3], (0, 255, 0), 9)
    diference1 = 0
    diference2 = 0
    count = 0
    i = 1
    while y1 < fourCorners[2][1] and y2 < fourCorners[3][1]:
        y1 += 143
        y2 += 143

        if count < 11:
            cv2.line(image,(x1,y1),(x2,y2), (0, 255, 0), 9)
            horizontalLines += [y1]
        count+=1
        i+=1
    horizontalLines+=[fourCorners[2][1]]

    x1 = fourCorners[0][0]
    y1 = fourCorners[0][1]

    x2 = fourCorners[2][0]
    y2 = fourCorners[2][1]
    verticalLines = []
    verticalLines+=[fourCorners[0][0]]
    cv2.line(image,fourCorners[0],fourCorners[2], (0, 255, 0), 9)
    cv2.line(image,fourCorners[1],fourCorners[3], (0, 255, 0), 9)

    diference1 = 0
    diference2 = 0
    count2 = 0
    j = 1
    while x1 < fourCorners[1][0] and x2 < fourCorners[3][0]:

        x1 += 143
        x2 += 143


        if count2 < 11:
            cv2.line(image, (x1, y1), (x2, y2), (0, 255, 0), 9)
            verticalLines += [x1]
        count2+=1
        j+= 1

    verticalLines+= [fourCorners[1][0]]

    hsize = len(horizontalLines)
    vsize = len(verticalLines)
    matrix = np.zeros((hsize, vsize), tuple)

    for h in range(hsize):
        for v in range(vsize):
            y = horizontalLines[h]
            x = verticalLines[v]
            matrix[h][v] = (int(x), int(y))

    squares = []
    for i in range(0,hsize-1):
        for j in range(0,vsize-1):
            squares += [(matrix[i][j], matrix[i][j + 1], matrix[i + 1][j], matrix[i + 1][j + 1])]

    tuples = []
    lines = len(matrix)
    columns = len(matrix[0])
    for index in range(len(squares)):

            tuples += [(squares[index][0][0],squares[index][0][1],squares[index][3][0],squares[index][3][1])]

    return tuples

