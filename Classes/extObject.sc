+ Object {
	directedEdgeTo{arg what; ^VTMDirectedEdge.new(this, what); }

	undirectedEdgeTo{arg what; ^VTMUndirectedEdge.new(this, what); }

	--> {arg what; ^this.directedEdgeTo(what); }

	--- {arg what; ^this.undirectedEdgeTo(what); }

}
