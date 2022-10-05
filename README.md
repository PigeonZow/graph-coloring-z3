# graph-coloring-z3

A Java 

To modify input, change input.txt with the following format:

```
N    M
v1   w1
v2   w2

...  

vn   wn
```

Where N = # of vertices {1, ..., N}, M = # of colors {1, ..., M}, and edges are represented by (v, w)


Sample Input:
```
4    3
1    2
1    3
1    4
2    4
3    4
```

![a graph of the sample input](https://github.com/PigeonZow/graph-coloring-z3/blob/master/input.PNG)


The output is an assignment of colors to vertices:

```
v1    c1
v2    c2

...

vn    cn
```

After running, output.txt should contain:
```
1    3
4    2
2    1
3    1
```
