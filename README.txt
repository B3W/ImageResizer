Berg Weston weberg

Image Processing (Resizing) Based on Pixel Importance

The input to the program is an image represented in the form of a 2D matrix of pixels. The pixels
consist of RGB values. The program will then construct a graph of the pixel images with which the
image width can be reduced. The minimum cost vertical cut of the image gets removed each time the
image is reduced in width by one. This minimum cost cut is found using an implementation of
Djikstra's shortest path algorithm. Cost is based on the importance of pixels. Pixel importance is
based of the RGB values of the pixels surrounding it.