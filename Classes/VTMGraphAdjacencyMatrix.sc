VTMGraphAdjacencyMatrix : VTMGraphData{
	var <matrix;//TEMP getter

	init{arg vertices, edges;
		super.init(vertices, edges);
		matrix = Matrix.newClear(vertices.size, vertices.size);
		
		//mark adjacents in matrix
		if(edges.notNil, {
			edges.do({arg edge;
				var fromIndex, toIndex;
				fromIndex = vertexMappings[edge.from];
				toIndex = vertexMappings[edge.to];
				matrix.put(fromIndex, toIndex, 1);
			});
		});
	}

	adjacencyList{
		var result;
		result = Dictionary.new;
		matrix.rows.do({arg rowIndex, i;
			var key = vertexMappings.getID(rowIndex);
			var val;
			matrix.getRow(rowIndex).do({arg jtem, j;
				if(jtem > 0.0, {
					val = val.add(vertexMappings.getID(j));
				});
			});
			result.put(key, val);
		});
		^result;
	}

	adjacencyMatrix{
		^matrix.copy;
	}

	adjacentsTo{arg vertex;
		var result = Set.new, rowIndex;
		rowIndex = vertexMappings[vertex];
		//return empty array if not found
		if(rowIndex.isNil, { ^result; });
		matrix.doRow(rowIndex, {arg item, i;
			//comparing GT in to support weighted edges
			if(item > 0.0, {
				result = result.add(vertexMappings.getID(i));
			});
		});
		^result;
	}

	getUnconnectedEdges{arg allowSelfLoops = false, edgeClass;
		var result = [];
		matrix.doMatrix({arg item, row, col;
			if(item == 0.0, {
				if(allowSelfLoops, {
					result = result.add(
						edgeClass.new(
							vertexMappings.getID(row),
							vertexMappings.getID(col)
						)
					);
				}, {
					if(row != col, {
						result = result.add(
							edgeClass.new(
								vertexMappings.getID(row),
								vertexMappings.getID(col)
							)
						);
					});
				});
			});
		});
		^result.as(Set);
	}

	printItemsOn{arg stream;
		var verticeNames = this.adjacencyEnum;
		verticeNames.do({arg vertexName, i;
			//if(stream.atLimit, {^this;});
			stream.tab;
			stream << matrix.getRow(i);
			stream << " <-- " << vertexName;
			stream << "\n";
		});
	}

}
