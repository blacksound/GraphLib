VTMAbstractEdge {
	var <from;
	var <to;
	var <>weight;

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

	isLink{
		^this.isLoop.not;
	}

	reversed{
		^this.class.new(to, from);
	}
}
