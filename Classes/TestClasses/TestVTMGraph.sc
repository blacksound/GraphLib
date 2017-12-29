TestVTMGraph : UnitTest {

	*classesForTesting{
		^[
			VTMUndirectedGraph,
			VTMDirectedGraph
		];
	}

	*dataStructuresForTesting{
		^[
			\list, \matrix
		];
	}

	*makeRandomObject{arg params, arguments;
		var result, args, graphClass;
		graphClass = this.findTestedClass;
		args = this.generateArgs(params);
		args.putAll(arguments);
		result = graphClass.performWithEnvir(\new,
			args
		);
		^result;
	}

	*generateArgs{arg randomParams;
		var result = IdentityDictionary.new;
		var minOrder = 5, maxOrder = 20;
		var minDensity, maxDensity;
		var minSize = 0, maxSize;
		var order, size, density;
		var absoluteMaxSize;
		var	allowSelfLoops = 0.5.coin;
		var dataStructure = ['list', \matrix].choose;
		if(randomParams.notNil and: {randomParams.includesKey(\order)}, {
			minOrder = randomParams[\minOrder] ? minOrder;
			maxOrder = randomParams[\maxOrder] ? maxOrder;
			order = randomParams[\order];
		});

		if(order.isNil, {
			order = rrand(minOrder, maxOrder);
		});
		result.put(\vertices, {Symbol.generateRandom} ! order);

		if(randomParams.notNil, {
			minDensity = randomParams[\minDensity];
			maxDensity = randomParams[\maxDensity];
			if([minDensity, maxDensity].any(_.notNil), {
				density = rrand(minDensity, maxDensity);
			});
			minSize = randomParams[\minSize] ? minSize;
			maxSize = randomParams[\maxSize];
			allowSelfLoops = randomParams[\allowSelfLoops] ? allowSelfLoops;
			size = randomParams[\size];
			dataStructure = randomParams[\dataStructure] ? dataStructure;
		});

		result.put(\dataStructure, dataStructure);

		absoluteMaxSize = this.findTestedClass.absoluteMaxSize(
			order, allowSelfLoops
		);	

		if(size.isNil, {
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
				size = rrand(minSize, maxSize);
			}, {

				size = rrand(
					0,
					density * maxSize
				);
			});
		});
		size = size.asInteger;
		if(size > absoluteMaxSize, {
			"graph size is larger than absolute max size".warn;
			size = min(size, absoluteMaxSize);
		});
		result.put(\allowSelfLoops, allowSelfLoops);
		result.put(\edges, 
			this.generateEdges(
				result[\vertices],
				size,
				allowSelfLoops
			)
		);
		^result;
	}

	test_new{
		[
			this.class.classesForTesting,//the class
			[\list, \matrix], //data strucutre
			[true, false] // allowSelfLoops
		].allTuples.do({arg item;
			var dataStructure, allowSelfLoops, class;
			var obj;
			var testClass;

			#class, dataStructure, allowSelfLoops = item;
			testClass = this.class.findTestClass(class);
			try{
				var args;
				args = testClass.generateArgs((
					dataStructure: dataStructure, allowSelfLoops: allowSelfLoops
				));
				obj = class.performWithEnvir(\new, args);
				this.assert(
					obj.isKindOf(class),
					"Made '%' correclty".format(class)
				);

				this.assertEquals(
					obj.dataStructure, dataStructure,
					"Graph data structure was set correctly"
				);
				this.assertEquals(
					obj.allowSelfLoops, allowSelfLoops,
					"Graph allow self loops was set correctly"
				);
			} {
				this.failed(
					thisMethod,
					"Error when making '%'".format(class)
				);
			};
		});
	}

	test_addEdge{
		[
			this.class.classesForTesting,//the class
			[\list, \matrix], //data strucutre
			[true, false], // allowSelfLoops
			Object.randomGeneratorClasses
		].allTuples.do({arg item;
			var dataStructure, allowSelfLoops, class;
			var obj;
			var valClass;
			var testClass;
			var testVals;
			var testVertexSet = Set.new;
			var testEdgeSet = Set.new;
			var numEdges;

			#class, dataStructure, allowSelfLoops, valClass = item;
			testClass = this.class.findTestClass(class);

			obj = class.new(
				dataStructure: dataStructure,
				allowSelfLoops: allowSelfLoops
			);

			rrand(1,12).do({arg i;
				var vertex = valClass.generateRandom;
				testVertexSet.add(vertex);
				obj.addVertex(vertex);
			});

			this.assertEquals(
				obj.vertices, testVertexSet,
				"Added all vertices in addEdge test"
			);

			numEdges = rrand(0, obj.absoluteMaxSize).asInteger;
			obj.getAllPossibleEdges.asArray.scramble.keep(numEdges).do({arg randomEdge;
				randomEdge.obj_(Object.generateRandomObject);
				testEdgeSet.add(randomEdge);
				obj.addEdge(randomEdge);
			});

			this.assertEquals(
				obj.edges, testEdgeSet,
				"Added all edges correctly"
			);

			//Testing adding self loop edges
			obj.clearEdges;
			//Add self loops to all vertices
			obj.vertices.do({arg vertex;
				obj.addEdge(class.edgeClass.new(vertex, vertex));
			});
			if(allowSelfLoops, {
				this.assert(
					obj.edges.every(_.isLoop) and: {
						obj.size == obj.vertices.size
					},
					"Graph only all self loops correctly."
				);
			}, {
				this.assert(
					obj.size == 0,
					"Graph added no self loops correctly."
				);
			});

			//check if new vertices are added
			obj.clear;
			this.assert(
				obj.isEmpty and: {
					obj.vertices.isEmpty
				} and: {
					obj.edges.isEmpty
				},
				"Graphs cleared correctly"
			);
			testVertexSet = Set.new;
			rrand(2,15).do({arg i;
				testVertexSet.add(valClass.generateRandom);
			});

			testVertexSet.asArray.scramble.doAdjacentPairs({arg aa, bb;
				obj.addEdge(
					class.edgeClass.new(aa, bb),
					true // add new vertices
				);
			});

			this.assertEquals(
				obj.vertices, testVertexSet,
				"Non existing vertices were added correctly upon edge add."
			);

			//Dont add edge if vertex not found
			{
				var otherVertexSet = Set.new;
				var oldEdgeSet = obj.edges.copy;
				rrand(2,15).do({arg i;
					otherVertexSet.add(Object.generateRandomObject);
				});
				otherVertexSet.asArray.scramble.doAdjacentPairs({arg aa, bb;
					obj.addEdge(
						class.edgeClass.new(aa, bb),
						false // add new vertices
					);
				});
				this.assert(
					obj.vertices.sect(otherVertexSet).isEmpty and: {
						obj.edges == oldEdgeSet
					},
					"Graph added no edges without existing vertices"
				);
			}.value;
		});
	}

	test_absoluteMaxSize{
		[
			this.class.classesForTesting,//the class
			[\list, \matrix], //data strucutre
			[true, false] // allowSelfLoops
		].allTuples.do({arg item;
			var class, dataStructure, allowSelfLoops;
			var obj;
			#class, dataStructure, allowSelfLoops = item;
			obj = class.generateRandom((
				allowSelfLoops: allowSelfLoops,
				dataStructure: dataStructure
			));
			obj.clearEdges;
			this.assertEquals(
				obj.getAllPossibleEdges.size, obj.absoluteMaxSize,
				"All possible edges and absolute max size are equal"
			);
		});
	}

	test_removeEdge{
		[
			this.class.classesForTesting,//the class
			[\list, \matrix], //data strucutre
			[true, false] // allowSelfLoops
		].allTuples.do({arg item;
			var class, dataStructure, allowSelfLoops;
			var obj;
			#class, dataStructure, allowSelfLoops = item;
			obj = class.generateRandom((
				allowSelfLoops: allowSelfLoops,
				dataStructure: dataStructure
			));
			{
				var oldEdgeSet = obj.edges.copy;
				var newEdgeSet = oldEdgeSet.copy;
				newEdgeSet = newEdgeSet.asArray.scramble.keep(1, newEdgeSet.size - 1);
				newEdgeSet.do({arg item;
					obj.removeEdge(item.from, item.to);
				});
				newEdgeSet = oldEdgeSet - newEdgeSet.as(Set);
				this.assertEquals(
					obj.edges, newEdgeSet,
					"Edge removal was correct"
				);
			}.value;
		});
	}

	test_addVertex{}//tested in addEdge

	test_removeVertex{
		[
			this.class.classesForTesting,//the class
			[\list, \matrix], //data strucutre
			[true, false] // allowSelfLoops
		].allTuples.do({arg item;
			var class, dataStructure, allowSelfLoops;
			var obj;
			#class, dataStructure, allowSelfLoops = item;
			obj = class.generateRandom((
				allowSelfLoops: allowSelfLoops,
				dataStructure: dataStructure
			));
		//Removing vertices
		//Removing verticex should remove edges too
			{
				var oldVertexSet = obj.vertices.copy;
				var newVertexSet;
				var vertexToRemove = newVertexSet.asArray.choose;
				newVertexSet = oldVertexSet.copy;
				newVertexSet.remove(vertexToRemove);
				obj.removeVertex(vertexToRemove);
				this.assertEquals(
					obj.vertices, newVertexSet,
					"Graph remove vertex correctly"
				);
			}.value;
		});

	}

	test_order{}
	test_size{}
	test_density{}

	test_getUnconnectedEdges{
		[
			this.class.classesForTesting,//the class
			[\list, \matrix], //data strucutre
			[true, false] // allowSelfLoops
		].allTuples.do({arg item;
			var dataStructure, allowSelfLoops, class;
			var obj;
			var testClass;
			var edges, allPossibleEdges, unconnectedEdges;
			#class, dataStructure, allowSelfLoops = item;
			testClass = this.class.findTestClass(class);
			obj = testClass.makeRandomObject(
				arguments: (
					dataStructure: dataStructure,
					allowSelfLoops: allowSelfLoops
				)
			);
			edges = obj.edges;
			allPossibleEdges = obj.getAllPossibleEdges;
			unconnectedEdges = obj.getUnconnectedEdges;
			this.assertEquals(
				unconnectedEdges,
				allPossibleEdges - edges,
				"Unconnected edges returne correctly value:\n\torder: %, size: %, selfLoops: % data: %".format(
					obj.order, obj.size, obj.allowSelfLoops, obj.dataStructure
				)
			);
		});
	}

	test_dfs{}

	test_isCyclic{
		[
			[VTMDirectedGraph],
			[\list/*, \matrix*/] //data strucutre
		].allTuples.do({arg item;
			var dataStructure, class;
			var testEdges;
			var obj;
			var testClass;
			#class, dataStructure = item;
			testClass = this.class.findTestClass(class);
			obj = class.newFromAdjacencyList(
				[
					1 --- [2,3],
					2 --- [6],
					3 --- [4],
					4 --- [5,1],
					5 --- []
				],
				dataStructure: dataStructure
			);
			this.assert(
				obj.isCyclic,
				"Detected cyclic graph"
			);
			obj = class.newFromAdjacencyList(
				[
					1 --- [],
					2 --- [3],
					3 --- [],
					4 --- [5,6],
					5 --- [1],
					6 --- [2]
				],
				dataStructure: dataStructure
			);
			this.assert(
				obj.isCyclic.not,
				"Detected non-cyclic graph"
			);
		});
		
	}
}
