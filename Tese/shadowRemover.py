import cv2
import numpy as np
from PIL import Image
import matplotlib.pyplot as plt
import matplotlib.colors as colors
from skimage import io
from webcolors import rgb_to_name

image = cv2.imread("downloaded.jpg")
cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
imageCrop = Image.open(r"downloaded.jpg")


crop = imageCrop.crop((1289, 1028, 1513, 1208))


crop.save('myphoto.jpg', 'JPEG')
img = io.imread('myphoto.jpg')


average = img.mean(axis=0).mean(axis=0)
pixels = np.float32(img.reshape(-1, 3))

n_colors = 5
criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 200, .1)
flags = cv2.KMEANS_RANDOM_CENTERS

_, labels, palette = cv2.kmeans(pixels, n_colors, None, criteria, 10, flags)
_, counts = np.unique(labels, return_counts=True)

dominant = palette[np.argmax(counts)]
a = int(dominant[0])
b = int(dominant[1])
c = int(dominant[2])

if (a >= b) and (a >= c):
    largest = 'red'

elif (b >= a) and (b >= c):
    largest = 'green'
else:
    largest = 'blue'

tuple = (a,b,c)

avg_patch = np.ones(shape=img.shape, dtype=np.uint8)*np.uint8(average)
indices = np.argsort(counts)[::-1]
freqs = np.cumsum(np.hstack([[0], counts[indices]/counts.sum()]))
rows = np.int_(img.shape[0]*freqs)

dom_patch = np.zeros(shape=img.shape, dtype=np.uint8)
for i in range(len(rows) - 1):
    dom_patch[rows[i]:rows[i + 1], :, :] += np.uint8(palette[indices[i]])

cv2.imshow("mat",image)
fig, (ax0, ax1) = plt.subplots(1, 2, figsize=(12,6))
ax0.imshow(avg_patch)
ax0.set_title('Average color')
ax0.axis('off')
ax1.imshow(dom_patch)
ax1.set_title('Dominant colors')
ax1.axis('off')
plt.show(fig)

