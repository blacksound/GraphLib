TestVTMGraphEdge : UnitTest {
	test_new{
		this.class.findTestedClass.allSubclasses.do({arg class;
			var obj;
			var testFrom, testTo, testLabel;

			testFrom = \aa;
			testTo = \bb;
			testLabel = \myLabel;
			obj = class.new(testFrom, testTo, testLabel);
			this.assertEquals(
				[obj.from, obj.to, obj.label],
				[testFrom, testTo, testLabel],
				"Edge data fields init correctly"
			);
		});

	}

	test_newOperator{
		this.class.findTestedClass.allSubclasses.do({arg class;
			[
				[\aa, \bb],
				[11, 22],
				[\cc, 33],
				[\44, \dd]
			].do({arg item;
				var testFrom, testTo;
				var obj;
				#testFrom, testTo = item;
				obj = testFrom.perform(class.opSymbol, testTo);
				this.assert(obj.notNil and: 
					{obj.class == class},
					"% Symbol construction operator worked".format(class)
				);

				this.assertEquals(
					[obj.from, obj.to],
					[testFrom, testTo],
					"Data field were set correctly"
				);
			});
		});
	}

	test_isLoop{
		this.class.findTestedClass.allSubclasses.do({arg class;
			this.assert(
				class.new(\aa, \bb).isLoop.not,
				"Non looping edge was not loop"
			);

			this.assert(
				class.new(\aa, \aa).isLoop,
				"Looping edge was loop"
			);
		});
	}

	test_setLabel{
		this.class.findTestedClass.allSubclasses.do({arg class;
			var obj = class.new(\aa, \bb);
			var testVal = \myLabel;
			var callbackArgs;
			var callback = {arg ...args;
				callbackArgs = args;
			};
			obj.addDependant(callback);
			obj.label_(testVal);

			this.assertEquals(
				obj.label, testVal,
				"% changed label correctly".format(class)
			);

			this.assertEquals(
				callbackArgs, [obj, \label],
				"% notfied label change correctly.".format(class)
			);
			obj.release;
		});
	}

	test_equality{
		this.class.findTestedClass.allSubclasses.do({arg class;
			var objA, objB;
			objA = class.new(\aa, \bb);
			objB = class.new(\dd, \ee);

			//Should not be equal
			this.assert(
				objA != objB,
				"Inequal object found"
			);
			objB = class.new(\aa, \bb);

			this.assert(
				objA == objB,
				"Equal objects found"
			);

			objB.label = \aLabel;

			this.assert(
				objA == objB,
				"Equal objects found, even if labels differ."
			);
		});

		{
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
				{|aa, bb| (aa --- bb) == (bb --- aa)},
				{|aa, bb| (aa --- bb) == (aa --- bb)},
				{|aa, bb| (aa --> bb) == (aa --> bb)},
				{|aa, bb| (aa --> bb) != (bb --> aa)},
				{|aa, bb| (aa --> bb) != (aa --> bb).reverse},
				{|aa, bb| (aa --> bb) == (bb --> aa).reverse},
				{|aa, bb| (aa --- bb) == (aa --- bb).reverse},
				{|aa, bb| (aa --- bb) == (bb --- aa).reverse},
				{|aa, bb| (aa --- bb) != (bb --> aa)},
				{|aa, bb| (aa --- bb) != (aa --> bb)},
				{|aa, bb| (aa --> bb) != (aa --- bb)},
				{|aa, bb| (aa --> bb) != (bb --- aa)},
				{|aa, bb| Set[aa --- bb] == Set[aa --- bb]},
				{|aa, bb| Set[aa --> bb] == Set[aa --> bb]},
				{|aa, bb| Set[aa --- bb] == Set[bb --- aa]},
				{|aa, bb| Set[aa --> bb] != Set[bb --> aa]},
				{|aa, bb| Set[aa --- bb] != Set[aa --> bb]},
				{|aa, bb| Set[aa --> bb] != Set[aa --- bb]},
				{|aa, bb| Set[aa --- bb] != Set[bb --> aa]},
				{|aa, bb| Set[aa --> bb] != Set[bb --- aa]},
				{|aa, bb| (aa --- aa) == (aa --- aa)},
				{|aa, bb| (aa --- aa) != (aa --- bb)},
				{|aa, bb| (aa --> aa) == (aa --> aa)},
				{|aa, bb| (aa --> aa) != (aa --> bb)},
				{|aa, bb| (aa --- aa) != (bb --- bb)},
				{|aa, bb| (aa --> aa) != (bb --> bb)},
			];
			funcs.do({arg expr;
				classCombos.do({arg classes;
					var classA, classB;
					var aa, bb;
					#classA, classB = classes;
					aa = classA.generateRandom;
					bb = classB.generateRandom;
					this.assert(
						expr.value(aa, bb),
						"All comparsions were correct\n\texpr: %\n\taa: %[%]\n\tbb: %[%]".format(
							expr.def.sourceCode, aa, aa.class, bb, bb.class
						);
					);
				});
			});
		}.value;
	}
}
