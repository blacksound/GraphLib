VTMDirectedGraph : VTMGraph {
	*edgeClass{ ^VTMDirectedEdge; }

	getAllPossibleEdges{
		var result;
		if(vertices.size == 0, {^Set.new});
		if(vertices.size == 0, {^Set.new});
		if(allowSelfLoops, {
			result = all {:[x,y],
				x <- vertices,
				y <- vertices
			};
		}, {
			result = all {:[x,y],
				x <- vertices,
				y <- vertices,
				x != y
			};
		});
		result = result.collect({arg it;
			this.class.edgeClass.new(it[0], it[1]);
		});
		^result.as(Set);
	}

	prBuildEdges{ ^edges; }

	edgesReversed{
		^edges.collect({arg edge;
			edge.as(VTMDirectedEdge).reverse;
		})
	}

	isCyclic{
		var visited = Set.new;
		var visit;
		visit = {arg v;
			visited.add(v);
			this.adjacentsTo(v).do({arg item;
				if(visited.includes(item), {
					"XXX vertex: %".format(item).postln;
					^true;
				}, {
					"YYY vertex: %".format(item).postln;
					visit.value(item);
				});
			});
		};
		"AAA".postln;

		if(allowSelfLoops, {
			if(edges.any({arg item; item.isLoop; }), {
				"BBB".postln;
				^true;
			});
		});
		
		vertices.do({arg vertex;
			if(visited.includes(vertex), {
				"CCC: vertex: %".format(vertex).postln;
				^true;
			}, {
				"DDD: vertex: %".format(vertex).postln;
				visit.value(vertex);
			});
		});

		^false;
	}

	outEdges{}
	inEdges{}
	inDegreeOf{arg vertex;}
	outDegreeOf{arg vertex;}
	successorsOf{arg vertex;}
	predecessorsOf{arg vertex;}

	tranposer{}
	topologicalSort{}
	
	*absoluteMaxSize{arg order, allowSelfLoops = false;
		var result;
		result = (order * (order - 1));
		//if simple add self loop number
		if(allowSelfLoops, {
			result = result + order;
		});
		^result;
	}
}
