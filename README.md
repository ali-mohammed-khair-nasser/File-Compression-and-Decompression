# File Compression & Decompression
### Introduction
To reduce the time needed for files to be transmitted over a network, Compression and Decompression techniques are very useful. Developers prefer to write code to compress files before sending them out to the network for a file upload process. Web applications get the most benefit out of it. The .NET Framework provides the System.IO.Compression namespace, which contains the compressing and decompressing libraries and streams. Developers can use these types to read and modify the contents of a compressed file.

# Project Requirements
### Java  programming language
Java is the number 1 programming language and development platform. It reduces costs, shortens development timeframes, drives innovation, and improves application services. With millions of developers running more than 51 billion Java Virtual Machines worldwide, Java continues to be the development platform of choice for enterprises and developers. You sholud have basic knowledge of coding and syntax formats of java for this project. And you should have JDK version 8 or newer installed on your machine.
- Install JDK from [oracle.com](https://www.oracle.com/java/technologies/javase-downloads.html)
- Download NetBeans IDE from [netbeans.org](https://netbeans.org/images_www/v6/download/community/8.2/)

### JavaFX GUI
<img src="https://github.com/ali-mohamed-nasser/File-Compression/blob/main/images/ui.svg" width="1200">

For the user interface I used JavaFX which is a set of graphics and media packages that enables developers to design, create, test, debug, and deploy rich client applications that operate consistently across diverse platforms. You can use your favorite editor or any integrated development environment (IDE) that supports the Java language (such as NetBeans, Eclipse, or IntelliJ IDEA) to create and edit JavaFX applications or you can use third-party application to edit ``` .fxml ``` file like Scenebuilder which is a visual layout tool that lets users quickly design JavaFX application user interfaces, without coding.
- Download Scene builder from [oracle.com](https://www.oracle.com/java/technologies/javafxscenebuilder-1x-archive-downloads.html)

# Usage
To run this project make sure you have the Java installed on your device then follow the steps given below:
- Clone or download this repository ``` https://github.com/ali-mohamed-nasser/File-Compression.git ```
- Create new javaFX project in your IDE.
- Copy ```src``` folder to your project then run the file ``` MultimediaProject.java ```

# How does it work?
### LZW Algorithm
The LZW algorithm is a very common compression technique. This algorithm is typically used in GIF and optionally in PDF and TIFF. Unix’s ‘compress’ command, among other uses. It is lossless, meaning no data is lost when compressing. The algorithm is simple to implement and has the potential for very high throughput in hardware implementations. It is the algorithm of the widely used Unix file compression utility compress, and is used in the GIF image format.

The main idea behind this algorithm is that it looks for repeated patterns of data (character sequence, bit sequences, etc), and replaces the pattern with a code (in case of images, it will replace that pattern with a value between 0 and 255). A dictionary holds the mapping between a data sequence and a corresponding code, so when a pattern is seen later in the data, we can check to see if it has been encountered already, and if so, replace it with the corresponding code from the dictionary.

### Compression Example
- The input string: **BABAABAAA**.
- The steps involved are systematically shown in the diagram below.
<img src="https://github.com/ali-mohamed-nasser/File-Compression/blob/main/images/lzw-compress.png" width="1200">

### LZW Decompression
The LZW decompressor creates the same string table during decompression. It starts with the first 256 table entries initialized to single characters. The string table is updated for each character in the input stream, except the first one.Decoding achieved by reading codes and translating them through the code table being built.

### Shannon Fano Algorithm
The Shannon Fano Algorithm is an entropy encoding technique for lossless data compression of multimedia. Named after Claude Shannon and Robert Fano, it assigns a code to each symbol based on their probabilities of occurrence. It is a variable length encoding scheme, that is, the codes assigned to the symbols will be of varying length. The steps of the algorithm are as follows:
- Create a frequency count list for given set of symbols so that the relative frequency of each symbol is known.
- Sort the list of symbols in decreasing order, the most frequency ones to the left and least frequency to the right.
- Split the list into two parts, with the total probability of both the parts being as close to each other as possible.
- The splitting stopped when each symbol is separated.
- Assign the value 0 to the left part and 1 to the right part.
- Repeat the steps 3 and 4 for each part, until all the symbols are split into individual subgroups.
- After getting the Shannon-fano tree then calculate the Shannon codes for each symbol. 
<img src="https://github.com/ali-mohamed-nasser/File-Compression/blob/main/images/fannon-shano.png" width="1200">

### JPEG images compression
In this algorithm we work with images only so the input data is only the pixels of this image. After get image apply the following steps:
- split our image into the blocks of 8x8 blocks. It forms 64 blocks in which each block is referred to as 1 pixel. 
- Color Space Transform from RBG to YCbCr model.
- Apply DCT on each block. DCT represents an image as a sum of sinusoids of varying magnitudes and frequencies. 
- Quantize image data using the quantization table. 
- Apply the zig-zag scanning pattern to exploit redundancy. 
- Apply DPCM on DC elements. DC elements are used to define the strength of colors.

*Note: This algorithm is lossy algorithm so there is no go back to original input image.
# References
Here is a list of sources I used to build this project:
- [Shannon-Fano Algorithm for Data Compression](https://www.geeksforgeeks.org/shannon-fano-algorithm-for-data-compression/)
- [LZW (Lempel–Ziv–Welch) Compression](https://www.geeksforgeeks.org/lzw-lempel-ziv-welch-compression-technique/)
- Java and javaFX tutorials on Youtube.
