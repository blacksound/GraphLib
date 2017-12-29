VTMGraphAdjacencyList : VTMGraphData {
	var <list;//TEMP getter

	init{arg vertices_, edges_;
		var vertices = vertices_.copy;
		var edges = edges_.copy;
		super.init(vertices, edges);
		list = [];
		vertexMappings.size.do({arg i;
			var vertexLinkedList = LinkedList.new;
			var vertex = vertexMappings.getID(i);
			//add adjacent vertices from this vertex
			vertexLinkedList.addAll(
				vertexMappings.atAll(
					edges.select({arg item; 
						item.from == vertex;
					}).collect({arg it;
						it.to
					})
				)
			);
			list = list.add(vertexLinkedList);
		});
	}

	adjacentsTo{arg vertex;
		var result;
		result = vertexMappings[vertex];
		//return empty array if not found
		if(result.isNil, { ^[]; });
		result = list[result].collect(
			vertexMappings.getID(_)
		).as(Set);
		^result;
	}

	adjacencyList{
		var result;
		result = Dictionary.new;
		list.do({arg item, i;
			var key = vertexMappings.getID(i);
			result.put(key, item.collect({arg jtem; vertexMappings.getID(jtem);}).asArray);
		});
		^result;
	}

	adjacencyMatrix{
		var result = Matrix.newClear(vertexMappings.size, vertexMappings.size);
		list.do({arg item, i;
			item.do({arg jtem, j;
				result.put(i, jtem, 1);
			});
		});
		^result;
	}

	getUnconnectedEdges{arg allowSelfLoops = false, edgeClass;
		var result;
		var connected, all;
		connected = list.collect({arg it; it.as(Array)});
		all = Array.series(list.size) ! list.size;
		if(allowSelfLoops.not, {
			all = all.collect({arg it, i; 
				it.remove(i);
				it;
			});
		});
		result = all.collect({arg it, i; it.removeAll(connected[i])});
		result = result.collect({arg item, i;
			item.collect({arg jtem;
				edgeClass.new(
					vertexMappings.getID(i), 
					vertexMappings.getID(jtem)
				)
			});
		}).flatten;

		^result.as(Set);
	}

	printItemsOn{arg stream;
		var verticeNames = this.adjacencyEnum;
		verticeNames.do({arg vertexName, i;
			if(stream.atLimit, {^this;});
			stream.tab;
			stream << vertexName << " " << VTMDirectedEdge.opSymbol << " ";
			stream << list[i].asArray.collect({arg it; vertexMappings.getID(it); });
			stream << "\n";
		});
	}

}
