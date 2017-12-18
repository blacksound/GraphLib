VTMAbstractEdge {
	var <from;
	var <to;

	*new{arg from, to;
		^super.new.init(from, to);
	}

	init{arg from_, to_;
		if(from_.isKindOf(VTMAbstractVertex).not, {
			from = VTMAbstractVertex(from_);
		});
		if(to.isKindOf(VTMAbstractVertex).not, {
			to = VTMAbstractVertex(to_);
		});
	}

	isLoop{
		^from == to;
	}
}
