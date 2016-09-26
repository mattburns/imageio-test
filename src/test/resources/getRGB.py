from PIL import Image
import sys

filename = sys.argv[1]
x = int(sys.argv[2])
y = int(sys.argv[3])

im = Image.open(filename)
pix = im.load()
print pix[x, y]
