VTMAbstractVertex{
	var <obj;//holds the object that the vertex represents

	*new{arg obj;
		^super.newCopyArgs(obj);
	}

	=={arg what;
		^what.obj == obj.value;
	}

	hash{
		^obj.hash;
	}
}
