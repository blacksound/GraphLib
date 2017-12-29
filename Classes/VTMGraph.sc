VTMGraph{
	var <vertices;
	var <edges;
	var <allowSelfLoops;
	var <dataStructure;
	var <data;//TEMP getter
	var dataDirty;
	var delegate;

	*new{arg vertices, edges, allowSelfLoops = false, dataStructure = \list;
		^super.newCopyArgs(vertices, edges, allowSelfLoops, dataStructure).init;
	}

	*newFromAdjacencyList{arg list, allowSelfLoops = false, dataStructure = \list;
		var edges;
		var result;
		edges = this.edgeClass.expandAdjacencyList(list);
		result = this.new(nil, nil, allowSelfLoops, dataStructure);
		edges.do({arg item;
			result.addEdge(this.edgeClass.new(item.from, item.to), true);
		});
		^result;
	}

	*edgeClass{ this.subclassResponsibility(thisMethod); }
	*itemsClass{ ^Set; }

	*star{arg n; }
	*cycle{arg n; }
	*path{arg n; }
	*starWith{arg items; }
	*cycleWith{arg items; }
	*pathWith{arg items; }

	init{
		vertices = vertices.as(this.class.itemsClass);
		edges = edges.collect({arg edge; edge.as(this.class.edgeClass)}).as(
			this.class.itemsClass
		);
		this.buildData;
	}

	buildData{
		var dataClass;
		var buildEdges = this.prBuildEdges;
		switch(this.dataStructure,
			\list, {
				data = VTMGraphAdjacencyList.new(vertices, buildEdges);
			},
			\matrix, {
				data = VTMGraphAdjacencyMatrix.new(vertices, buildEdges);
			}
		);
		dataDirty = false;
	}

	prBuildEdges{ this.subclassResponsibility(thisMethod); }

	order{ ^vertices.size; }

	size{ ^edges.size; }

	isEmpty{ ^edges.isEmpty; }

	density{
		var o2 = this.order.squared;
		^1 - ((o2 - this.size) / o2);
	}

	hasSelfLoops{ ^edges.any(_.isLoop); }

	allowSelfLoops_{arg aBool;
		allowSelfLoops = aBool;
		if(allowSelfLoops.not, {
			//Remove self looping edges if any
			edges.select({arg item;
				item.isLoop;
			}).do({arg loopingEdge;
				this.removeEdge(loopingEdge);
			});
		});
	}

	adjacencyList{ ^data.adjacencyList; }

	adjacencyMatrix{ ^data.adjacencyMatrix; }

	adjacencyEnum{ ^data.adjacencyEnum; }

	laplacianMatrix{}

	addVertex{arg obj;
		vertices.add(obj);
		dataDirty = true;
		this.changed(\vertexAdded);
	}

	removeVertex{arg obj, preserveConnectivity = false;
		if(preserveConnectivity, {
			//TODO: implementation
			//connect vertex predecessors to successors
		});
		vertices.remove(obj);
		dataDirty = true;
		this.changed(\vertexRemoved);
	}

	//adding new vertices if not already in vertices set
	addEdge{arg anEdge, addNewVertices = false;
		if(allowSelfLoops.not, {
			if(anEdge.isLoop, {
				//fail silently if is loop edge
				^this;
			});
		});
		if(addNewVertices, {
			this.addVertex(anEdge.from);
			this.addVertex(anEdge.to);
		}, {
			//vertex must exists in order to add edge
			if(vertices.includesAll([anEdge.from, anEdge.to]).not, {
				//exit silently if vertex not found
				^this;
			});
		});
		edges.add(anEdge);
		dataDirty = true;
		this.changed(\edgeAdded);
	}

	removeEdge{arg from, to;
		var toBeRemoved = edges.detect({arg item;
			item.from == from and: {item.to == to};
		});
		edges.remove(toBeRemoved);
		dataDirty = true;
		this.changed(\edgeRemoved);
	}

	getEdge{arg from, to;

	}

	setEdgeWeight{arg from, to, val;
	}

	labelVertex{arg vertex, label;
		data.setVertexLabel(vertex, label);
	}

	removeEdgeLabelled{arg name; }

	includesEdgeLabelled{arg name; }

	includesVertexLabelled{arg name; }

	clear{
		this.clearVertices;
	}

	clearVertices{
		edges.clear;
		vertices.clear;
		dataDirty = true;
	}

	clearEdges{
		edges.clear;
		dataDirty = true;
	}

	*findVerticesFromEdges{arg edges;
		var result = IdentitySet.new;
		edges.do({arg item;
			result.add(item.from);
			result.add(item.to);
		});
		^result;
	}

	successorsOf{arg vertex;}
	predecessorsOf{arg vertex;}
	getAllPossibleEdges{ this.subclassResponsibility(thisMethod); }

	*absoluteMaxSize{arg order, allowSelfLoops = false;
		this.subclassResponsibility(thisMethod);
	}

	absoluteMaxSize{
		^this.class.absoluteMaxSize(this.order, allowSelfLoops);
	}

	//need to decide on whteher to use
	//objects to compare with or the edge
	//arguments. As object creation usually is expensive,
	//the latter seems better.?
	includesEdge{arg anEdge;
		^edges.includes(anEdge);
	}

	includesVertex{arg obj;
		^vertices.includes(obj);
	}

	dfs{arg onVertexDiscovery, onVertexFinish;
		if(dataDirty, {this.buildData;});
		^data.dfs;
	}

	bfs{}

	isRegular{}
	isDirected{}
	isUndirected{}
	isMixed{}
	isAllowingMultipleEdges{}
	isAllowingSelfLoops{}
	isAllowingCycles{}
	isWeighted{}

	isSimple{
		//will test for multiple edges in multi graph
		//subclasses.
		^this.hasSelfLoops;
	}

	isPseudograph{}
	isMultigraph{}
	asDirected{}
	asUndirected{}
	asMixed{}
	asUnweighted{}
	asWeighted{}
	asModifiable{}
	asUnmodifiable{}
	stronglyConnectedVertices{ /*Kosaraju*/}
	reachableVertices{}
	unreachableVertices{}
	level{arg anInteger;}
	minimumSpanningTree{}
	maximumIndependentVertexSets{}
	diameter{ /*dfs().longest or: GraphMeasurer.jav jtgraph*/ }
	radius{
	}
	girth{}
	graphCenter{
		//GraphMeasurer.getGraphCenter; jrgrpht/java
	}
	graphPerifery{
		//GraphMeasurer.getGraphPerifery; jrgrpht/java
	}
	prComputeEccentricityMap{}
	averagePathLength{}
	shortestPath{arg fromVertex, toVertex;}
	degreeDistribution{}
	averageDegree{}
	isForest{
		//has no cycles
	}
	trees{
		//if this.isForeste then find trees
	}
	isTree{
		//has no cycles
		//is connected
	}
	findTrees{
		//if this is forest
		//find all acyclic connected subgraphs
	}

	isBipartite{
		if(this.isEmpty, { ^true; });
		//TODO: implement the rest.
	}

	isCubic{
		^vertices.every({arg item;
			this.degreeOf(item) == 3;
		});
	}

	findLeafs{}

	*maxNumberNonIsomorphicTrees{arg order;

	}

	adjacentsTo{arg vertex;
		if(dataDirty, {this.buildData});
		^data.adjacentsTo(vertex);
	}

	isCyclic{
		this.subclassResponsibility(thisMethod);
	}

	findCycles{
	}

	getUnconnectedEdges{
		^data.getUnconnectedEdges(allowSelfLoops, this.class.edgeClass);
	}

	getUnconnectedVertices{
		^vertices - this.getConnectedVertices;
	}

	isConnected{
		this.getUnconnectedVertices.size == 0;
	}

	isEulerian{
	}

	neighboursOf{arg vertex;
		^data.neighboursOf(vertex);
	}

	hasSubgraph{arg subgraph;
		^data.hasSubgraph(subgraph);
	}

	degreeOf{arg vertex;
		^data.degreeOf(vertex);
	}

	isPath{
		//is the graph a path, i.e. a row of vertices with at most two edges
		//for vertices between start and end?
	}

	isStar{}
	isCycle{}
	union{arg aGraph;}
	disjointUnion{arg aGraph;}
	cartesianProduct{}
	compose{arg aGraph;}
	complement{arg aGraph;}

	edgesOf{arg vertex;
		//All edges touching the vertex
	}
	incomingEdgesOf{arg vertex;}
	outgoingEdgesOf{arg vertex;}

	inDegreeOf{arg vertex;
		//different for directed and undirected graphs..
	}

	outDegreeOf{arg vertex;
		//see this.inDegreeOf..
	}

	getConnectedVertices{
		var result = Set.new;
		edges.do({arg item;
			result.addAll([item.to, item.from]);
		});
		^result;
	}

	numSelfLoops{
		^this.selfLoopingEdges.size;
	}

	selfLoopingEdges{
		^edges.select(_.isLoop);
	}

	printOn{ arg stream;
		if(stream.atLimit, {^this;});
		stream << "a " << this.class.name;
		data.printOn(stream);
	}

	=={arg what;
		if(what.class != this.class, { ^false; });
		if(what.vertices != vertices, { ^false; });
		if(what.edges != edges, { ^false; });
		^true;
	}

	hash{
		^this.instVarHash([\vertices, \edges, \allowSelfLoops]);
	}

	isComplete{
		^this.getAllPossibleEdges == edges;
	}

}


/*
TODOs:
Implement factory class/functions for generating graphs, vertices and edges
  - e.g. GraphGenerator, VertexGenerator, EdgeGenerator

Look at listener classes in jgraph/src/main/java/org/jgrapht/event. They have interesting listening semantics.

Exporter and importer classes of jgrapht library are also interesting.

Implement the concept of returning a context from a vertex.
   also seleting/reject/detect for searching contexts. i.e. filtering. Akin to fgl Haskell library

Iterator methods, e.g. contextsDo, verticesDo, edgesDo. Or, contexts will of course return a colletion that can be used as any collection, thus supporting filters, and iterating.
*/
