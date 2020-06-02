import numpy as np
import cv2
from PIL import Image
import os
import firebase_admin
from firebase_admin import credentials
from firebase_admin import storage
#from urllib3.util import Retry

from firebase_admin import db
from pydub.playback import play
from pydub import AudioSegment
import sched, time
from gtts import gTTS
from pyrebase.pyrebase import Firebase

def getBiggerBlackLine(blackLines):

    key_list = list(blackLines.keys())
    val_list = list(blackLines.values())
    print(val_list)
    sorted_val_list = sorted(val_list)
    print(sorted_val_list)
    return key_list[val_list.index(sorted_val_list[len(sorted_val_list)-1])]




def adjust_gamma(image, gamma=1.0):

   invGamma = 1.0 / gamma
   table = np.array([((i / 255.0) ** invGamma) * 255
      for i in np.arange(0, 256)]).astype("uint8")

   return cv2.LUT(image, table)


def getGreenCounts(image):
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    lower_green, upper_green = np.array([36, 25, 25]), np.array([70, 255, 255])
    mask = cv2.inRange(hsv, lower_green, upper_green)
    greencnts = cv2.findContours(mask.copy(),
                                 cv2.RETR_EXTERNAL,
                                 cv2.CHAIN_APPROX_SIMPLE)[-2]

    area = -1
    if len(greencnts) > 0:
     green_area = max(greencnts, key=cv2.contourArea)
     (xg, yg, wg, hg) = cv2.boundingRect(green_area)
     tuple1 = (xg, yg)
     tuple2 = (xg + wg, yg + hg)
     area = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
     #cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (0, 255, 0), 9)
    return area


def getBlueCounts(image):
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    lower_blue = np.array([100, 50, 50])
    upper_blue = np.array([130, 255, 255])
    mask = cv2.inRange(hsv, lower_blue, upper_blue)
    bluecnts = cv2.findContours(mask.copy(),
                                cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)[-2]


    area = -1
    if len(bluecnts) > 0:
        blue_area = max(bluecnts, key=cv2.contourArea)
        (xg, yg, wg, hg) = cv2.boundingRect(blue_area)
        tuple1 = (xg, yg)
        tuple2 = (xg + wg, yg + hg)
        area = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
        #cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (255, 0, 0), 9)

    return area


def getRedCounts(image):
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    lower_red1, upper_red1 = np.array([0, 50, 20]), np.array([5, 255, 255])
    lower_red2, upper_red2 = np.array([175, 50, 20]), np.array([180, 255, 255])

    mask1 = cv2.inRange(hsv, lower_red1, upper_red1)
    mask2 = cv2.inRange(hsv, lower_red2, upper_red2)
    mask_red = mask1 + mask2
    redcnts = cv2.findContours(mask_red.copy(),
                               cv2.RETR_EXTERNAL,
                               cv2.CHAIN_APPROX_SIMPLE)[-2]

    area = -1
    if len(redcnts) > 0:
        red_area = max(redcnts, key=cv2.contourArea)
        (xg, yg, wg, hg) = cv2.boundingRect(red_area)
        tuple1 = (xg, yg)
        tuple2 = (xg + wg, yg + hg)
        area = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
        #cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (0, 0,255), 9)

    return area

def getBlackCounts(image):
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    black_lower, black_upper = np.array([0, 0, 0]), np.array([180, 255, 50])
    mask_black = cv2.inRange(hsv, black_lower, black_upper)
    # mask2 = cv2.inRange (hsv, black_upper,black_upper)
    # mask3 = mask1+mask2

    blackcnts = cv2.findContours(mask_black.copy(),
                                 cv2.RETR_EXTERNAL,
                                 cv2.CHAIN_APPROX_SIMPLE)[-2]

    areaBlacks = -1
    if len(blackcnts) > 0:
        black_area = max(blackcnts, key=cv2.contourArea)
        (xg, yg, wg, hg) = cv2.boundingRect(black_area)
        tuple1 = (xg, yg)
        tuple2 = (xg + wg, yg + hg)
        areaBlacks = abs(tuple1[0] - tuple2[0]) * abs(tuple1[1] - tuple2[1])
        cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (0, 0, 0), 9)
    return areaBlacks

def getRotateInstructions(currentMovement,nextMovement):

    rotacao = ""

    if currentMovement == "D":

        if nextMovement == "B":
            rotacao = "    rotate(-90,50) \n"
        elif nextMovement == "C":
            rotacao = "    rotate(90,50) \n"
        elif nextMovement == "E":
            rotacao = "    rotate(90,50) \n    rotate(90,50) \n"


    elif currentMovement == "E":

        if nextMovement == "B":
            rotacao = "    rotate(90,50) \n"
        elif nextMovement == "C":
            rotacao = "    rotate(-90,50) \n"
        elif nextMovement == "D":
            rotacao = "    rotate(90,50) \n    rotate(90,50) \n"


    elif currentMovement == "C":

        if nextMovement == "B":
            rotacao = "    rotate(90,50) \n    rotate(90,50) \n"
        elif nextMovement == "D":
            rotacao = "    rotate(-90,50) \n"
        elif nextMovement == "E":
            rotacao = "    rotate(90,50) \n"


    elif currentMovement == "B":

        if nextMovement == "C":
            rotacao = "    rotate(90,50) \n    rotate(90,50) \n"
        elif nextMovement == "D":
            rotacao = "    rotate(90,50) \n"
        elif nextMovement == "E":
            rotacao = "    rotate(-90,50) \n"


    return rotacao


def writeInstructionFile(movimentos):
    f = open("main.ozopy", "a")
    f.write("def f(): \n")
    mov = movimentos.split("_")
    i = 0
    while mov[i] != "F":
        if mov[i] == "E":
            f.write("\t move(10,30) \n")
            if i > 0:
                f.write(getRotateInstructions(mov[i-1],"E"))
        elif mov[i] == "D":
            f.write("\t move(10,30) \n")
            if i > 0:
                f.write(getRotateInstructions(mov[i-1],"D"))
        elif mov[i] == "C":
            f.write("\t move(10,30) \n")
            if i > 0:
                f.write(getRotateInstructions(mov[i-1],"C"))
        elif mov[i] == "B":
            f.write("\t move(10,30) \n")
            if i > 0:
                f.write(getRotateInstructions(mov[i-1],"B"))
        i+=1
    f.write("\t wheels(0, 0) \n")
    f.write("\n")
    f.write("while True: \n")
    f.write("\t f() \n")
    f.close()


def getFourCorners(tuple1,tuple2):
    #TOP,BOTTOM
    print("tuple1 = "+str(tuple1))
    print("tuple2 = "+str(tuple2))
    return [tuple1,(tuple2[0],tuple1[1]),(tuple1[0],tuple2[1]),tuple2]

def hasObstacle(currentPosition,board):
    return board[currentPosition] == "X"




def movimentsNormal(sequence,currentPosition,f):


    if sequence == "E":
        currentPosition = (currentPosition[0], currentPosition[1] - 1)
        print("vamos para a esquerda")
        play = "vamos para a esquerda"
        f.write("    move(30,30) \n")


    if sequence == "D":

        currentPosition = (currentPosition[0], currentPosition[1] + 1)
        print("vamos para a direita")
        play = "vamos para a direita"
        f.write("    move(30,30) \n")


    if sequence == "C":

        currentPosition = (currentPosition[0] - 1, currentPosition[1])
        print("vamos para Cima")
        play = "vamos para Cima"
        f.write("    move(30,30) \n")


    if sequence == "B":

        currentPosition = (currentPosition[0] + 1, currentPosition[1])
        print("vamos para Baixo")
        play = "vamos para Baixo"
        f.write("    move(30,30) \n")


    return currentPosition,play


def movimentsLoop(sequence,currentPosition,f):


    if sequence == "E":
        currentPosition = (currentPosition[0], currentPosition[1] - 1)
        print("vamos para a esquerda")
        play = "vamos para a esquerda"
        f.write("    move(30,30) \n")

    if sequence == "D":

        currentPosition = (currentPosition[0], currentPosition[1] + 1)
        print("vamos para a direita")
        play = "vamos para a direita"
        f.write("    move(30,30) \n")


    if sequence == "C":

        currentPosition = (currentPosition[0] - 1, currentPosition[1])
        print("vamos para Cima")
        play = "vamos para Cima"
        f.write("    move(30,30) \n")


    if sequence == "B":

        currentPosition = (currentPosition[0] + 1, currentPosition[1])
        print("vamos para Baixo")
        play = "vamos para Baixo"
        f.write("    move(30,30) \n")


    return currentPosition,play




def moviments(sequence,currentPosition,nextPosition,f,i,j,loopSequence):


    if sequence == "E":
        currentPosition = (currentPosition[0], currentPosition[1] - 1)
        print("vamos para a esquerda..")
        f.write("    move(30,30) \n")
        if i != int(loopSequence[1]) - 1 or j != len(loopSequence) - 2:
            f.write(getRotateInstructions(sequence,nextPosition))
        audio = AudioSegment.from_file('esquerda.m4a')
        # play(audio)

    if sequence == "D":

        currentPosition = (currentPosition[0], currentPosition[1] + 1)
        print("vamos para a direita..")
        f.write("    move(30,30) \n")
        if i != int(loopSequence[1]) - 1 or j != len(loopSequence) - 2:
            f.write(getRotateInstructions(sequence,nextPosition))
        audio = AudioSegment.from_file('direita.m4a')
        # play(audio)

    if sequence == "C":

        currentPosition = (currentPosition[0] - 1, currentPosition[1])
        print("vamos para a Cima..")
        f.write("    move(30,30) \n")
        if i != int(loopSequence[1]) - 1 or j != len(loopSequence) - 2:
            f.write(getRotateInstructions(sequence,nextPosition))
        audio = AudioSegment.from_file('frente.m4a')
        # play(audio)

    if sequence == "B":

        currentPosition = (currentPosition[0] + 1, currentPosition[1])
        print("vamos para a Baixo..")
        f.write("    move(30,30) \n")
        if i != int(loopSequence[1]) -1 or j != len(loopSequence) - 2:
            f.write(getRotateInstructions(sequence,nextPosition))
        audio = AudioSegment.from_file('tras.m4a')
        # play(audio)

    return currentPosition


def loopMoviments(sequence,index):
    sequencia = ""
    while sequence[index] != "LE":
        sequencia+=sequence[index]+"_"
        index+=1
    sequencia+="F"

    return sequencia

def getSequenciaLoop(index,sequencia):
    seq = ""
    while sequencia[index] != "LE":
        seq += sequencia[index]
        index+=1

    return seq


def computation(start,board,sequencia):
    playInstructions = []
    obstacleCicle= 0
    currentPosition = start
    arraySequencia = sequencia.split("_")
    index = 0
    f = open("main.ozopy", "a")
    f.write("def f(): \n")
    #board[currentPosition] != "F"
    while arraySequencia[index] != "F" or board[currentPosition] != "F" or board[currentPosition] != "X":

        if(arraySequencia[index] == "LB"):
            count = int(arraySequencia[(index + 1)])
            arraySequenciaLoop = getSequenciaLoop(index + 2,arraySequencia)

            for i in range(count):
                for j in range(len(arraySequenciaLoop)):
                    currentPosition,play = movimentsLoop(arraySequenciaLoop[j], currentPosition,f)
                    playInstructions +=[play]

                    if i != count - 1 or j != len(arraySequenciaLoop) - 1:
                        f.write(getRotateInstructions(arraySequenciaLoop[j],  arraySequenciaLoop[(j+1) % len(arraySequenciaLoop)]))

                    if board[currentPosition] == "F":
                        print("chegou ao objectivo")
                        playInstructions += ["chegou ao objectivo"]
                        obstacleCicle = 1
                        break

                    if board[currentPosition] == "X":
                        print("bateu num obstaculo")
                        obstacleCicle = 1
                        playInstructions += ["bateu num obstaculo"]
                        break

                    if arraySequencia[index] == "F":
                        print("sequencia chegou ao fim")
                        obstacleCicle = 1
                        playInstructions += ["sequencia chegou ao fim"]
                        break

            index+= len(arraySequenciaLoop) + 2
        else:

            if arraySequencia[index + 1] != "LB":

                if arraySequencia[index] == "LE":

                    f.write(getRotateInstructions(arraySequencia[index - 1],arraySequencia[index + 1]))

                else:

                    currentPosition,play = movimentsNormal(arraySequencia[index],currentPosition,f)
                    playInstructions += [play]
                    f.write(getRotateInstructions(arraySequencia[index],arraySequencia[index + 1]))

                index += 1
            else:

               currentPosition,play = movimentsNormal(arraySequencia[index],currentPosition,f)
               playInstructions += [play]
               f.write(getRotateInstructions(arraySequencia[index], arraySequencia[index+3]))

               index += 1

        if board[currentPosition] == "F":
            print("chegou ao objectivo")
            if obstacleCicle == 0:
                playInstructions += ["chegou ao objectivo"]
            break

        if board[currentPosition] == "X":
            print("bateu num obstaculo")
            if obstacleCicle == 0:
                playInstructions += ["bateu num obstaculo"]
            break

        if arraySequencia[index] == "F":
            print("sequencia chegou ao fim")
            if obstacleCicle == 0:
                playInstructions += ["sequencia chegou ao fim"]
            break

    f.write("    wheels(0, 0) \n")
    f.write("\n")
    f.write("i = 0 \n")
    f.write("while i < 1:\n")
    f.write("    f() \n")
    f.write("    i = i + 1")
    f.close()

    os.system('python compilerTest.py')
    #comp.runTest("main.ozopy",board)
    os.remove("main.ozopy")
    return playInstructions


def getKeyByValue(board):

    key_list = list(board.keys())
    val_list = list(board.values())
    return key_list[val_list.index("R")]


def playAudioDirectionChanged(currentBoard,previousBoard):

    currentCoordenates = getKeyByValue(currentBoard)
    previousCoordenates = getKeyByValue(previousBoard)

    xdiff = currentCoordenates[0] - previousCoordenates[0]
    ydiff = currentCoordenates[1] - previousCoordenates[1]

    if xdiff == 0 and ydiff > 0:
        print("andou para a direita")

    elif xdiff == 0 and ydiff < 0:
        print("andou para a esquerda")

    elif xdiff > 0 and ydiff == 0:
        print("andou para a baixo")

    elif xdiff < 0 and ydiff == 0:
        print("andou para a cima")


def playAudio(array):

    for i in array:
        audio = AudioSegment.from_file(i+".mp3")
        play(audio)
        time.sleep(1)

def getBoard():
    return board

cred = credentials.Certificate("/Users/goncalocardoso/PycharmProjects/OpenCV/tese-3ed7d-firebase-adminsdk-4xm5b-3176ad9835.json");
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://tese-3ed7d.firebaseio.com/'
});

config = {
    "apiKey":"AIzaSyCbwjD-zqfeO2-5hsHIhF6jHoFTnzFuGQg",
   "authDomain": "project-1085473150640.firebaseapp.com",
   "serviceAccount":"/Users/goncalocardoso/PycharmProjects/OpenCV/tese-3ed7d-firebase-adminsdk-4xm5b-3176ad9835.json",
  "databaseURL": "https://tese-3ed7d.firebaseio.com/",
  "storageBucket": "tese-3ed7d.appspot.com"
}


currentBoard = ""
previousBoard = ""


firebase = Firebase(config)
storage = firebase.storage()
a = storage.list_files()
numFiles = 0
for i in a:
    numFiles += 1

    if numFiles > 0:

        storage.child("images").download("downloaded.jpg")


        ref = db.reference('/');

        image = cv2.imread("downloaded.jpg")

        #img_rotate_90_clockwise = cv2.rotate(image, cv2.cv2.ROTATE_180)
        #cv2.imwrite('90_clockwise.jpg', img_rotate_90_clockwise)
        #image = cv2.imread("90_clockwise.jpg")

        #blurred = cv2.blur(image, (3,3))

        #cv2.imshow("blur", blurred)

        #canny = cv2.Canny(blurred, 50, 200)

       # cv2.imshow("canny", canny)

        ## find the non-zero min-max coords of canny
        #pts = np.argwhere(canny > 0)
        #y1,x1 = pts.min(0)
        #y2,x2 = pts.max(0)

        ## crop the region
        #cropped = image[y1:y2, x1:x2]
        #cv2.imshow("cropped", cropped)

        #tagged = cv2.rectangle(image.copy(), (x1,y1), (x2,y2), (0,0,0), 9, cv2.LINE_AA)
        hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
        # define range of blue color in HSV
        # lower_blue = np.array([100,50,50])
        # upper_blue = np.array([130,255,255])
        # Threshold the HSV image to get only blue colors
        mask = cv2.inRange(hsv, np.array([36, 25, 25]), np.array([70, 255, 255]))
        bluecnts = cv2.findContours(mask.copy(),
                                    cv2.RETR_EXTERNAL,
                                    cv2.CHAIN_APPROX_SIMPLE)[-2]

        if len(bluecnts) > 0:
            blue_area = max(bluecnts, key=cv2.contourArea)
            (xg, yg, wg, hg) = cv2.boundingRect(blue_area)
            cv2.rectangle(image, (xg, yg), (xg + wg, yg + hg), (0, 255, 0), 2)

        print((xg,yg))
        print((xg+wg,yg+hg))
        #ORDER FROM TOP TO BOTTOM,STARTING FROM LEFT
        fourCorners = getFourCorners((xg, yg),(xg + wg, yg + hg))
        print(fourCorners)


        #HORIZONTAL LINES

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

        #VERTICAL LINES

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
        board = {}
        lines = len(matrix)
        columns = len(matrix[0])
        i = 0
        j = 0
        end = 0
        start = 0
        blackLines = {}

        gamma = 2  # change the value here to get different result
        adjusted = adjust_gamma(image, gamma=gamma)
        cv2.imwrite("adjusted.jpg", adjusted)
        image = cv2.imread("adjusted.jpg")
        #break
        imageCrop = Image.open(r"adjusted.jpg")
        for index in range(len(squares)):
            if j == columns-1:
                i += 1
                j = 0

            tuples = (squares[index][0][0],squares[index][0][1],squares[index][3][0],squares[index][3][1])
            #print(str((i,j))+" "+str(tuples))
            #box_area1 = (23,236, 215,437)
            # crop = imageCrop.crop(tuples)
            #
            # crop.save('myphoto.jpg', 'JPEG')
            #
            # img = cv2.imread("myphoto.jpg")
            # height, width, _ = np.shape(img)
            # # calculate the average color of each row of our image
            # avg_color_per_row = np.average(img, 0)
            #
            # # calculate the averages of our rows
            # avg_colors = np.average(avg_color_per_row, 0)
            #
            # # avg_color is a tuple in BGR order of the average colors
            # # but as float values
            # #print(f'avg_colors: {avg_colors}')
            #
            # # so, convert that array to integers
            # int_averages = np.array(avg_colors, np.uint8)
            # #print(f'int_averages: {int_averages}')
            #
            # # create a new image of the same height/width as the original
            # average_image = np.zeros((height, width, 3), np.uint8)
            # # and fill its pixels with our average color
            # average_image[:] = int_averages
            #
            # # finally, show it side-by-side with the original
            # #cv2.imshow("Avg Color", np.hstack([img, average_image]))
            #
            # cv2.imwrite("color_patch_solid.png", average_image)
            #
            # imageForColorRecognition = Image.open('color_patch_solid.png')
            # pixel = (2,2)
            # rgb = imageForColorRecognition.getpixel(pixel) #rgb variable stores the R,G,B values
            #
            # #Now we will convert the array to a list to extract
            # #individual R,G,b values and use them to compare
            # rgb = list(imageForColorRecognition.getpixel(pixel))
            # #rgb variable stores the R,G,B values)
            #
            # a = int(rgb[0])
            # b = int(rgb[1])
            # c = int(rgb[2])

            #bluecnts = getBlueCounts(img)
            #greencnts = getGreenCounts(img)
            #redcnts = getRedCounts(img)
            #blackcnts = getBlackCounts(img)
            # print((redcnts,greencnts,bluecnts))

                #imagemR = cv2.resize(img, (500, 500))
                #cv2.imshow("image", imagemR)
                #cv2.waitKey(0)

            crop2 = imageCrop.crop(tuples)
            crop2.save('myphoto2.jpg', 'JPEG')
            img2 = cv2.imread("myphoto2.jpg")
            bluecnts = getBlueCounts(img2)
            greencnts = getGreenCounts(img2)
            redcnts = getRedCounts(img2)
            blackcnts = getBlackCounts(img2)
            print((i, j))
            print(tuples)
            print(blackcnts)
            blackLines[(i,j)] = blackcnts



            height, width, _ = np.shape(img2)
            # calculate the average color of each row of our image
            avg_color_per_row = np.average(img2, 0)

            # calculate the averages of our rows
            avg_colors = np.average(avg_color_per_row, 0)

            # avg_color is a tuple in BGR order of the average colors
            # but as float values
            # print(f'avg_colors: {avg_colors}')

            # so, convert that array to integers
            int_averages = np.array(avg_colors, np.uint8)
            # print(f'int_averages: {int_averages}')

            # create a new image of the same height/width as the original
            average_image = np.zeros((height, width, 3), np.uint8)
            # and fill its pixels with our average color
            average_image[:] = int_averages

            # finally, show it side-by-side with the original
            # cv2.imshow("Avg Color", np.hstack([img, average_image]))

            cv2.imwrite("color_patch_solid2.png", average_image)

            imageForColorRecognition = Image.open('color_patch_solid2.png')
            pixel = (2, 2)
            rgb = imageForColorRecognition.getpixel(pixel)  # rgb variable stores the R,G,B values

            # Now we will convert the array to a list to extract
            # individual R,G,b values and use them to compare
            rgb = list(imageForColorRecognition.getpixel(pixel))
            # rgb variable stores the R,G,B values)

            a = int(rgb[0])
            b = int(rgb[1])
            c = int(rgb[2])
            print("RGB="+str((a,b,c)))

            if a >= b and a >= c:
                board[(i, j)] = "X"

            elif b >= a and b >= c:
                board[(i, j)] = "O"

            elif c >= a and c >= b:
                board[(i, j)] = "F"
                end = (i, j)

            # if blackcnts > redcnts and blackcnts > bluecnts and blackcnts > greencnts\
            #         and abs(a - b) <= 20 and abs(b - c) <= 20 and abs(a - c) <= 20:
            #     board[(i, j)] = "R"
            #     start = (i, j)
            #     print(start)
            #     print((bluecnts, greencnts, redcnts, blackcnts))
            #     print((a,b,c))
            #     print(tuples)




            #
            #
            # if blackcnts > redcnts and blackcnts > bluecnts and blackcnts > greencnts\
            #     and abs(a - b) <= 15 and abs(b - c) <= 15 and abs(a - c) <= 15:
            #     largest = "Robo"
            #     board[(i,j)] = "R"
            #     start = (i,j)
            #
            # else:
            #     gamma = 1.8  # change the value here to get different result
            #     adjusted = adjust_gamma(img, gamma=gamma)
            #     bluecnts = getBlueCounts(adjusted)
            #     greencnts = getGreenCounts(adjusted)
            #     redcnts = getRedCounts(adjusted)
            #     blackcnts = getBlackCounts(adjusted)
            #
            #     if i == 5 and j == 4:
            #         print((redcnts,greencnts,bluecnts))
            #         print((a,b,c))
            #         imagemR = cv2.resize(adjusted, (500, 500))
            #         cv2.imshow("image", imagemR)
            #         cv2.waitKey(0)




                #
                # if (redcnts > bluecnts and redcnts > greencnts and (a >= b) and (a >= c)):
                #         #or (abs(redcnts-greencnts) >= 500  and (a >= b) and (a >= c)) \
                #         #or (abs(redcnts-bluecnts) >= 500  and (a >= b) and (a >= c)):
                #             largest = 'red'
                #             board[(i, j)] = "X"
                #
                # elif (bluecnts > greencnts and bluecnts > redcnts and (c >= a) and (c >= b)):
                #           #or (abs(bluecnts-greencnts) >= 500 and (c >= a) and (c >= b))\
                #           #or (abs(bluecnts-redcnts) >= 500 and (c >= a) and (c >= b)):
                #
                #             largest = 'blue'
                #             board[(i, j)] = "F"
                #             end = (i, j)
                #
                # elif (greencnts > bluecnts and greencnts > redcnts and (b >= a) and (b >= c)):
                #          #or (abs(greencnts-bluecnts >= 500) and (b >= a) and (b >= c))\
                #          #or (abs(greencnts-redcnts >= 500) and (b >= a) and (b >= c)):
                #
                #             largest = 'green'
                #             board[(i, j)] = "O"



                #
                # if redcnts > bluecnts and redcnts > greencnts \
                #      and (a >= b) and (a >= c):
                #     largest = 'red'
                #     board[(i, j)] = "X"
                #
                # elif bluecnts > greencnts and bluecnts > redcnts \
                #             and (((b >= a) and (b >= c)) or ((c >= a) and (c >= b))):
                #         largest = 'blue'
                #         board[(i, j)] = "F"
                #         end = (i,j)
                #
                # elif greencnts > bluecnts and greencnts > redcnts \
                #             and (((b >= a) and (b >= c)) or ((c >= a) and (c >= b))):
                #         largest = 'green'
                #         board[(i, j)] = "O"


            # if abs(a - b) <= 20 and abs(b - c) <= 20 and abs(a - c) <= 20:
            #     largest= "Robo"
            #     board[(i,j)] = "R"
            #     start = (i,j)
            # elif (a >= b) and (a >= c):
            #     largest = 'red'
            #     print(tuples)
            #     board[(i, j)] = "X"
            # elif (b >= a) and (b >= c):
            #     largest = 'green'
            #     board[(i, j)] = "O"
            # else:
            #     largest = 'blue'
            #     board[(i, j)] = "F"
            #     end = (i,j)
            j+=1

            os.remove("color_patch_solid2.png")
            os.remove("myphoto2.jpg")
        #os.remove("adjusted.jpg")
        board[getBiggerBlackLine(blackLines)] = "R"
        start = getBiggerBlackLine(blackLines)

        line = ""
        for i in range(0, lines-1):
            for j in range(0, columns-1):
                line += board[(i, j)]+" "
            print(line)
            line = ""
       # break
        #os.system("python SpeechRecognition.py")
        snapshot = ref.get()["sequencia"]
        instructions = computation(start, board, snapshot)
        playAudio(instructions)
        if "chegou ao objectivo" in instructions:
            break


imagemR = cv2.resize(image, (500, 500))
cv2.imshow("image", imagemR)
cv2.waitKey(0)
cv2.destroyAllWindows()