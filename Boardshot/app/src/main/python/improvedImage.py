import cv2
import numpy as np
from PIL import Image
import base64
import io


def convertScale(img, alpha, beta):

    new_img = img * alpha + beta
    new_img[new_img < 0] = 0
    new_img[new_img > 255] = 255
    return new_img.astype(np.uint8)

def automatic_brightness_and_contrast(file):
    #decoded_data= base64.b64decode(file)
    #np_data = np.fromstring(decoded_data,np.uint8)
    #img = cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)


    image = cv2.imread(file)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    clip_hist_percent=25
    # Calculate grayscale histogram
    hist = cv2.calcHist([gray],[0],None,[256],[0,256])
    hist_size = len(hist)

    # Calculate cumulative distribution from the histogram
    accumulator = []
    accumulator.append(float(hist[0]))
    for index in range(1, hist_size):
        accumulator.append(accumulator[index -1] + float(hist[index]))

    # Locate points to clip
    maximum = accumulator[-1]
    clip_hist_percent *= (maximum/100.0)
    clip_hist_percent /= 2.0

    # Locate left cut
    minimum_gray = 0
    while accumulator[minimum_gray] < clip_hist_percent:
        minimum_gray += 1

    # Locate right cut
    maximum_gray = hist_size -1
    while accumulator[maximum_gray] >= (maximum - clip_hist_percent):
        maximum_gray -= 1

    # Calculate alpha and beta values
    alpha = 255 / (maximum_gray - minimum_gray)
    beta = -minimum_gray * alpha

    auto_result = convertScale(image, alpha=alpha, beta=beta)
    #pil_im = Image.fromarray(auto_result)
    #buff = io.BytesIO()
    #pil_im.save(buff,format= "PNG")
    #img_str = base64.b64decode(buff.getvalue()).decode(errors='ignore')
    return [alpha,beta]
