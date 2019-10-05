//An undirected edge
VTMUndirectedEdge : VTMGraphEdge {
	*opSymbol{ ^'---'; }

	//labels are not tested for equality.
	=={arg what;
		if(what.class != this.class, {^false;});
		^this.asSet == what.asSet;
	}

	hash{
		^this.asSet.hash;
	}
}
