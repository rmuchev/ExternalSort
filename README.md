ExternalSort
============

Java External Sort

Algorithm is fast, customizable, uses minimal amount of memory and it is usable for large big data.
Objects are not sorted in memory, but on disk using merging of files. 

How it works:

- The algorithm starts reading the Java Comparable objects from all files in input folder and its subfolders.
- Every ExternalSortThread.MAX_OBJECTS_IN_FILE = 10000 comparable objects are sorted in memory and put in new file until all objects are read.
- Merge two from the new files in one new file, by comparing object by object from each of the two files and put "the smaller" object in the new file. Delete the two files. Repeat until only final sorted file remains.

More about External sorting:
http://en.wikipedia.org/wiki/External_sorting