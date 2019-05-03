all: DistanceVector

DistanceVector: DistanceVector.java	Router.java
	javac	*.java

run: DistanceVector
	java	DistanceVector