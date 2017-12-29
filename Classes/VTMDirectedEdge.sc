VTMDirectedEdge : VTMGraphEdge {

	*opSymbol{ ^'-->'; }

	//labels are not tested for equality.
	=={arg what; 
		if(what.class != this.class, {^false;});
		^from == what.from and: {to == what.to;};
   	}

	hash{ ^this.instVarHash([\from, \to]); }

}
