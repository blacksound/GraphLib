TestVTMDirectedGraph : TestVTMGraph {
	*generateEdges{arg vertices, size, allowSelfLoops = false;
		var result, edgeIndexes;
		var num = vertices.size - 1;
		var absoluteMaxSize;
		if(allowSelfLoops, {
			edgeIndexes = all {:[x,y],
				x <- (0 .. num),
				y <- (0 .. num)
			};
		}, {
			edgeIndexes = all {:[x,y],
				x <- (0 .. num),
				y <- (0 .. num),
				x != y
			};
		});
		edgeIndexes = edgeIndexes.as(Bag);
		absoluteMaxSize = this.findTestedClass.absoluteMaxSize(
			vertices.size, allowSelfLoops
		);
		if(size > absoluteMaxSize, {
			"Edge size is larger than absolute max graph size".warn;
			size = min(size, absoluteMaxSize);
		});
		result = size.collect({arg item;
			var i, j;
			#i, j = edgeIndexes.take;
			VTMUndirectedEdge(vertices[i], vertices[j]);
		});
		^result;
	}

}
