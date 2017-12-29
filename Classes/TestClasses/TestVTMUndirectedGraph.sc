TestVTMUndirectedGraph : TestVTMGraph {

	*generateEdges{arg vertices, size, allowSelfLoops = false;
		var result, edgeIndexes;
		var num = vertices.size - 1;
		if(allowSelfLoops, {
			edgeIndexes = all {:[x,y],
				x <- (0 .. num),
				y <- (x .. num)
			};
		}, {
			edgeIndexes = all {:[x,y],
				x <- (0 .. num),
				y <- (x .. num),
				x != y
			};
		});
		edgeIndexes = edgeIndexes.as(Bag);
		result = size.collect({arg item;
			var i, j;
			#i, j = edgeIndexes.take;
			VTMUndirectedEdge(vertices[i], vertices[j]);
		});
		^result;
	}
}
