# File Compression & Decompression
### Introduction
To reduce the time needed for files to be transmitted over a network, Compression and Decompression techniques are very useful. Developers prefer to write code to compress files before sending them out to the network for a file upload process. Web applications get the most benefit out of it. The .NET Framework provides the System.IO.Compression namespace, which contains the compressing and decompressing libraries and streams. Developers can use these types to read and modify the contents of a compressed file.

# Project Requirements
### Java  programming language
Java is the number 1 programming language and development platform. It reduces costs, shortens development timeframes, drives innovation, and improves application services. With millions of developers running more than 51 billion Java Virtual Machines worldwide, Java continues to be the development platform of choice for enterprises and developers. You sholud have basic knowledge of coding and syntax formats of java for this project. And you should have JDK version 8 or newer installed on your machine.

For the GUI I used JavaFX which is a set of graphics and media packages that enables developers to design, create, test, debug, and deploy rich client applications that operate consistently across diverse platforms. You can use your favorite editor or any integrated development environment (IDE) that supports the Java language (such as NetBeans, Eclipse, or IntelliJ IDEA) to create and edit JavaFX applications or you can use third-party application to edit ``` .fxml ``` file like Scenebuilder which is a visual layout tool that lets users quickly design JavaFX application user interfaces, without coding.
- Install JDK from [oracle.com](https://www.oracle.com/java/technologies/javase-downloads.html)
- Download Scene builder from [oracle.com/javafx](https://www.oracle.com/java/technologies/javafxscenebuilder-1x-archive-downloads.html)

# Usage
To run this project make sure you have the Java installed on your device then follow the steps given below:
- Clone or download this repository ``` https://github.com/ali-mohamed-nasser/File-Compression.git ```
- Create new javaFX project in your IDE.
- Copy ```src``` folder to your project then run the file ``` MultimediaProject.java ```

# How this algorithms work?
### LZW-compression
The main idea behind this algorithm is that it looks for repeated patterns of data (character sequence, bit sequences, ...etc), and replaces the pattern with a code (in case of images, it will replace that pattern with a value between 0 and 255). A dictionary holds the mapping between a data sequence and a corresponding code, so when a pattern is seen later in the data, we can check to see if it has been encountered already, and if so, replace it with the corresponding code from the dictionary.
