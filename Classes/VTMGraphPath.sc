VTMGraphPath{
	var <edges;

	*new{arg edges; ^super.new.init(edges); }

	*newFromVertices{arg vertices;
		var edges;
		vertices.doAdjacentPairs({arg it, jt;
			edges = edges.add(it --> jt);
		});
		^this.new(edges);
	}

	directedEdgeTo{arg what; ^VTMGraphPath.new(edges ++ (edges.last.to --> what)); }

	undirectedEdgeTo{arg what; ^VTMGraphPath.new(edges ++ (edges.last.to --- what)); }

	edgeClass{ ^edges.first.class; }

	++{arg what;
		var newEdges;
		case
		{what.isKindOf(VTMGraphEdge)} {
			newEdges = edges ++ this.edgeClass.new(
				this.end, what.from
			);
			newEdges = newEdges ++ what;
		}
		{what.isKindOf(VTMGraphPath)} {
			newEdges = edges ++ this.edgeClass.new(
				this.end, what.start
			);
			newEdges = newEdges ++ what.edges;
		}
		{
			newEdges = edges ++ this.edgeClass.new(
				this.end, what
			)
		};
		^this.class.new(newEdges);
	}

	hash{ ^this.instVarHash; }

	init{arg edges_; edges = edges_; }

	=={arg what;
		if(what.isKindOf(VTMGraphPath).not, {
			^false;
		});
		^edges == what.edges;
	}

	distance{ ^this.vertices.size; }

	start{ ^edges.first.from; }

	end{ ^edges.last.to; }

	reverse{ ^this.class.new(edges.reverse.collect(_.reverse)); }

	rotate{arg n; ^this.class.newFromVertices(this.vertices.rotate(n)); }

	vertices{ ^edges.collect({arg item; item.from}) ++ this.end; }

	asPattern{arg repeats = 1;
		^Prout({arg ...args;
			var start, end, items, str;
			var val, prev, step = 0;
			items = this.vertices;
			str = Pseq(items, repeats).asStream;
			start = items.first;
			end = items.last;
			loop{
				val = str.next;
				// "Parent thread: %".format(thisThread.parent).postln;
				if(val.notNil, {
					if( prev.isNil, {
						thisThread.changed(\start, val);
					}, {
						step = step + 1;
						thisThread.changed(\transition, prev, val, step);
					});
				}, {
					if(prev.notNil, {
						thisThread.changed(\end, prev);
					});
				});
				prev = val;
				val.yield;
			}
		});
	}

	asStream{
		^this.asPattern(1).asStream;
	}

	storeArgs { arg stream;
		^edges.collect(_.storeArgs);
	}

	printOn { arg stream;
		stream << "[";
		edges.do({arg edge;
			stream << edge.from << " " << edge.class.opSymbol << " ";
		});
		stream << this.end << "]";
	}

	storeOn { arg stream;
		stream << this.class.name << "(" <<<* this.storeArgs << ")"
	}
}
