VTMGraphData {
	var <vertexMappings;//key/label to index mappings //TEMP getter
	var dfs;

	*new{arg vertices, edges;
		^super.new.init(vertices, edges);
	}

	init{arg vertices_, edges_;
		vertexMappings = TwoWayIdentityDictionary.new;
		vertices_.do({arg vertex, i;
			vertexMappings.put(vertex, i);
		});
	}

	dfs{
		if(dfs.isNil, {
			dfs = {
				var result = Dictionary.new;
				var time = 0;
				var visit;
				vertexMappings.keys.do({arg item;
					result.put(item, (color: \white, vertex: item));
				});
				visit = {arg item;
					var adj;
					var data = result[item];
					time = time + 1;
					data.put(\discovered, time);
					data.put(\color, \grey);
					adj = this.adjacentsTo(item);
					adj.do({arg jtem; 
						if(result[jtem][\color] == \white, {
							result[jtem].put(\pre, item);
							visit.value(jtem);
						});
					});
					data.put(\color, \black);
					time = time + 1;
					data.put(\finished, time);
				};
				result.keysValuesDo({arg key, val;
					if(val[\color] == \white, {
						visit.value(key);
					});
				});
				result;
			}.value;
		});
		^dfs;
	}

	adjacencyList{ this.subclassResponsibility(thisMethod); }
	adjacencyMatrix{ this.subclassResponsibility(thisMethod); }
	adjacencyEnum{
		var result = Order.new;
		vertexMappings.do({arg i;
			result.put(i, vertexMappings.getID(i));
		});
		^result.asArray;
	}

	adjacentsTo{arg vertex;
		this.subclassResponsibility(thisMethod);
	}

	getUnconnectedEdges{arg allowSelfLoops, edgeClass;
		this.subclassResponsibility(thisMethod);
	}

	hasSubgraph{arg subgraph;
		this.subclassResponsibility(thisMethod);
	}

	degreeFor{arg vertex;
		this.subclassResponsibility(thisMethod);
	}

	storeOn{arg stream; this.subclassResponsibility(thisMethod); }

	printOn{arg stream;
		if(stream.atLimit, {^this;});
		stream << " <" << this.class.name << ">\n";
		this.printItemsOn(stream);
	}

}
