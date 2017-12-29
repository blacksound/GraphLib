+ VTMGraph {
	*generateRandom{arg params;
		var result, vertices, edges, dataStructure, allowSelfLoops;
		if(this == VTMGraph, {
			^this.allSubclasses.choose.generateRandom(params);
		});
		dataStructure = [\list, \matrix].choose;
		allowSelfLoops = 0.5.coin;
		if(params.notNil, {
			dataStructure = params[\dataStructure] ? dataStructure;
			allowSelfLoops = params[\allowSelfLoops] ? allowSelfLoops;
		});
		vertices = this.generateRandomVertices(params);
		edges = this.generateRandomEdges(params, vertices, allowSelfLoops);
		result = this.new(vertices, edges, allowSelfLoops, dataStructure);
		^result;
	}

	*generateRandomVertices{arg params;
		var result;
		var minOrder = 5, maxOrder = 20;
		var order, verticeClass, verticeParams;
		if(params.notNil and: {params.includesKey(\order)}, {
			minOrder = params[\minOrder] ? minOrder;
			maxOrder = params[\maxOrder] ? maxOrder;
			order = params[\order];
			verticeClass = params[\verticeClass];
			verticeParams = params[\verticeParams];
		});

		if(verticeClass.isNil, {
			verticeClass = this.randomGeneratorClasses.choose;
		});
		if(order.isNil, {
			order = rrand(minOrder, maxOrder);
		});

		result = {verticeClass.generateRandom(verticeParams);} ! order;
		^result;
	}

	*generateRandomSize{arg params, vertices, allowSelfLoops=false;
		var density, minDensity, maxDensity, order;
		var minSize = 0, maxSize;
		var result;
		var absoluteMaxSize;
		order = vertices.size;
		if(params.notNil, {
			minDensity = params[\minDensity];
			maxDensity = params[\maxDensity];
			if([minDensity, maxDensity].any(_.notNil), {
				density = rrand(minDensity, maxDensity);
			});
			density = params[\density] ? density;
			minSize = params[\minSize] ? minSize;
			maxSize = params[\maxSize];
		});

		absoluteMaxSize = this.absoluteMaxSize(
			order, allowSelfLoops
		);	

		maxSize = maxSize ? absoluteMaxSize;
		if( maxSize > absoluteMaxSize, {
			"Max graph size is larger than absolute max size".warn;
			maxSize = min(maxSize, absoluteMaxSize);
		});
		if( minSize > absoluteMaxSize, {
			"Min graph size is larger than absolute max size".warn;
			minSize = min(minSize, absoluteMaxSize);
		});

		//Density overrides min and max size random params
		if(density.isNil, {
			result = rrand(minSize, maxSize);
		}, {
			result = (density * absoluteMaxSize).ceil;
		});

		result = result.asInteger;
		if(result > absoluteMaxSize, {
			"graph size is larger than absolute max size".warn;
			result = min(result, absoluteMaxSize);
		});
		^result;
	}
}

+ VTMDirectedGraph {
	*generateRandomEdges{arg params, vertices, allowSelfLoops = false;
		var result, edgeIndexes;
		var num;
		var absoluteMaxSize;
		var size;
		if(vertices.isNil, {
			vertices = this.generateRandomVertices(params);
		});
		num = vertices.size - 1;
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
		absoluteMaxSize = this.absoluteMaxSize(
			vertices.size, allowSelfLoops
		);
		size = this.generateRandomSize(params, vertices, allowSelfLoops);
		if(size > absoluteMaxSize, {
			size = min(size, absoluteMaxSize);
		});
		result = size.collect({arg item;
			var i, j;
			#i, j = edgeIndexes.take;
			this.edgeClass.new(vertices[i], vertices[j]);
		});
		^result;
	}
}

+ VTMUndirectedGraph {
	*generateRandomEdges{arg params, vertices, allowSelfLoops = false;
		var result, edgeIndexes;
		var num;
		var absoluteMaxSize;
		var size;
		if(vertices.isNil, {
			vertices = this.generateRandomVertices(params);
		});
		num = vertices.size - 1;
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
		absoluteMaxSize = this.absoluteMaxSize(
			vertices.size, allowSelfLoops
		);
		size = this.generateRandomSize(params, vertices, allowSelfLoops);
		if(size > absoluteMaxSize, {
			size = min(size, absoluteMaxSize);
		});
		result = size.collect({arg item;
			var i, j;
			#i, j = edgeIndexes.take;
			this.edgeClass.new(vertices[i], vertices[j]);
		});
		^result;
	}
}

