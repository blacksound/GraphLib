VTMUndirectedGraph : VTMGraph{
	*edgeClass{ ^VTMUndirectedEdge; }

	getAllPossibleEdges{
		var result;
		var vertexArray, numVertices;
		if(vertices.size == 0, {^Set.new});
		vertexArray = vertices.asArray;
		numVertices = vertexArray.size - 1;
		if(allowSelfLoops, {
			result = all {:[x,y],
				x <- (0 .. numVertices),
				y <- (x .. numVertices)
			};
		}, {
			result = all {:[x,y],
				x <- (0 .. numVertices),
				y <- (x .. numVertices),
				x != y
			};
		});
		result = result.collect({arg it;
			var i, j;
			#i, j = it;
			this.class.edgeClass.new(vertexArray[i], vertexArray[j]);
		});
		^result.as(Set);
	}

	prBuildEdges{
		^edges.collect(_.as(VTMDirectedEdge)).addAll(
			edges.collect({arg edge; edge.as(VTMDirectedEdge).reverse;})
		);
	}

	isCyclic{

	}

	*absoluteMaxSize{arg order, allowSelfLoops	= false;
		var result;
		result = (order * (order-1)) / 2;
		//if simple add self loop number
		if(allowSelfLoops, {
			result = result + order;
		});
		^result;
	}
}
