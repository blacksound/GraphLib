VTMGraph.test;
VTMGraphEdge.test;
VTMGraphVertex.test;
VTMGraphPath.test

a = VTMGraph.generateRandom;

(
	a = VTMDirectedGraph();
	[\u, \v, \w, \x, \y, \z].do{arg item;
		a.addVertex(item);
	};
	a.addEdge(\u --> \v);
	a.addEdge(\u --> \x);
	a.addEdge(\x --> \v);
	a.addEdge(\v --> \y);
	a.addEdge(\y --> \x);
	a.addEdge(\w --> \y);
	a.addEdge(\w --> \x);
)
a.vertices
a.edges
a.dfs.do({arg item; item.postln;})


a = \aaa --> [\bbb, \xxx] --> \ccc
b = a.asStream

b.next
