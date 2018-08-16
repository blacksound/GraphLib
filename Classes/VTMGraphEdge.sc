VTMGraphEdge {
	var <from;
	var <to;
	var <label;
	var <>weight;
	var <>obj;

	//the edge type is set by the graph after dfs
	//Symbols: \tree, \back, \forward, \cross
	var <>type;

	*new{arg from, to, label;
		^super.newCopyArgs(from, to, label);
	}

	*newFrom{arg anEdge;
		^this.new(*anEdge.storeArgs);
	}

	*expandAdjacencyList{arg list;
		var result = [];
		list.do({arg item;
			if(item.to.isKindOf(SequenceableCollection), {
				item.to.do({arg jtem;
					result = result.add(this.new(item.from, jtem));
				});
			}, {
				result = result.add(this.new(item.from, item.to));
			});
		});
		^result;
	}

	*opSymbol{ this.subclassResponsibility(thisMethod); }

	directedEdgeTo{arg what;
		^VTMGraphPath.new([this, to --> what]);
	}

	undirectedEdgeTo{arg what;
		^VTMGraphPath.new([this, to --- what]);
	}

	isLoop{ ^this.isSelfEqual; }
	isSelfEqual{ ^from == to; }
	isSelfIdentical{ ^from === to; }

	storeArgs { arg stream; ^[from, to, label] }

	label_{arg sym;
		label = sym;
		this.changed(\label);
	}

	printOn { arg stream;
		if(label.notNil, {
			stream << "'%': ".format(label);
		});
		//stream << from << " <><> " << to;
		stream << "(" << from << " % ".format(this.class.opSymbol) << to << ")";
	}
	storeOn { arg stream;
		stream << this.class.name << "(" <<<* this.storeArgs << ")"
	}

	asSet{ ^Set[from, to]; }

	reverse{ ^this.class.new(to, from); }

	isBridge{}
}
