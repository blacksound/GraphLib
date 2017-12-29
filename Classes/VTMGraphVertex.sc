VTMGraphVertex {
	var <obj;
	var <>label;
	
	*new{arg obj, label;
		^super.newCopyArgs(Ref(obj), label);
	}

	*newFrom{arg what;
		if(what.isKindOf(VTMGraphVertex), {^what.copy;});
		^this.new(what);
	}

	=={arg what;
		if(what.class != this.class, {^false;});
		^what.obj.value == obj.value;
	}

	hash{
		^obj.value.hash;
	}

	printOn{arg stream;
		stream << "VTMGraphVertex<" << obj.value << ">";
	}

	value{
		^obj.value;
	}

}
