TestVTMGraphVertex : UnitTest {
	test_equality{
		var results, funcs;
		var valClasses = [
			Integer,
			Float,
			String,
			Symbol,
			Dictionary,
			IdentityDictionary,
			Event,
			Array,
			List,
			Set
		];
		var classCombos = all {:[x,y], 
		x <- (0 .. (valClasses.size - 1)), y <- (0 .. (valClasses.size - 1)) };
		classCombos = classCombos.collect({arg item;
			var i, j;
			#i, j = item;
			[valClasses[i], valClasses[j]];
		});
		funcs = [
			{|aa, bb| aa == aa;},
			{|aa, bb| aa != bb;},
			{|aa, bb| Set[aa] == Set[aa];},
			{|aa, bb| Set[aa] != Set[bb];},
		];
		funcs.do({arg expr;
				classCombos.do({arg classes;
					var classA, classB;
					var aa, bb;
					#classA, classB = classes;
					aa = VTMGraphVertex(classA.generateRandom);
					bb = VTMGraphVertex(classB.generateRandom);
					this.assert(
						expr.value(aa, bb),
						"All comparsions were correct\n\texpr: %\n\taa: %[%]\n\tbb: %[%]".format(
							expr.def.sourceCode, aa, aa.class, bb, bb.class
						);
					);
				});
			});
	}
}
