TestVTMGraphPath : UnitTest{

	test_equality{
		var exprs = [
			{(111 --> 222 --> 333) == (111 --> 222 --> 333)},
			{(111 --> 222 --> 333) != (333 --> 222 --> 111)},
			{(111 --> 222 --> 333) == (333 --> 222 --> 111).reverse},
			{
				(111 --> 222 --> 333).reverse.reverse == 
				(333 --> 222 --> 111).reverse
			},
			{
				Set[(111 --> 222 --> 333), (333 --> 222 --> 111)] ==
				Set[(111 --> 222 --> 333), (333 --> 222 --> 111)]
			},
			{
				Set[(111 --> 222 --> 333), (444 --> 555 --> 666)] !=
				Set[(111 --> 222 --> 333), (333 --> 222 --> 111)]
			},
			{
				([11,22] --> [33,44] --> [55,66]) ==
				([11,22] --> [33,44] --> [55,66])
			},
			{
				([11,22] --> [33,44] --> [55,66]) !=
				([22,11] --> [44,33] --> [66,55])
			},
			{
				([11,22] --> [33,44] --> [55,66]) !=
				([55,66] --> [33,44] --> [11,22])
			},
			//undirected operators
			{(111 --- 222 --- 333) == (111 --- 222 --- 333)},
			{(111 --- 222 --- 333) != (333 --- 222 --- 111)},
			{(111 --- 222 --- 333) == (333 --- 222 --- 111).reverse},
			{
				(111 --- 222 --- 333).reverse.reverse == 
				(333 --- 222 --- 111).reverse
			},
			{
				Set[(111 --- 222 --- 333), (333 --- 222 --- 111)] ==
				Set[(111 --- 222 --- 333), (333 --- 222 --- 111)]
			},
			{
				Set[(111 --- 222 --- 333), (444 --- 555 --- 666)] !=
				Set[(111 --- 222 --- 333), (333 --- 222 --- 111)]
			},
			{
				([11,22] --- [33,44] --- [55,66]) ==
				([11,22] --- [33,44] --- [55,66])
			},
			{
				([11,22] --- [33,44] --- [55,66]) !=
				([22,11] --- [44,33] --- [66,55])
			},
			{
				([11,22] --- [33,44] --- [55,66]) !=
				([55,66] --- [33,44] --- [11,22])
			},
			{
				([11,22] --- [33,44] --- [55,66]) ==
				([55,66] --- [33,44] --- [11,22]).reverse
			},
		];
		exprs.do({arg expr;
			var val = expr.value;
			this.assert(
				val,
				"expr:'%' - '%'".format(val, expr.def.sourceCode)
			)
		});
		
	}

	test_newFromVertices{
		var obj;
		var vertices;
		vertices = [11,22,33,44];
		try{
			obj = VTMGraphPath.newFromVertices(vertices);
			this.assert( obj.notNil && obj.class == VTMGraphPath, 
				"Made graph path from vertices"
			);
			this.assertEquals(
				obj.vertices, vertices,
				"Matching vertices"
			);
		}{
			this.failed(thisMethod, "Error making a GraphPath from vertices");
		};
		
	}

	test_reverse{
		var obj = 111 --> 222 --> 333;
		this.assertEquals(
			obj, obj.reverse.reverse,
			"GraphPath intact after double reversal."
		);

	}

	test_rotate{
		var obj = 111 --> 222 --> 333 --> 444;
		var vertices = obj.vertices.deepCopy;
		var results;
		(vertices.size * 4 - (vertices.size * 2)).do({arg n;
			var aa, bb;
			aa = vertices.rotate(n);
			bb = obj.rotate(n).vertices;
			results = results.add( aa == bb );
		});
		this.assert(
			results.every({arg it; it;}),
			"Rotating GraphPath correct"
		);
	}

	test_concatenate{
		var path = 111 --> 222 --> 333;
		var other;
		other = 444;
		path = path ++ other;
		this.assertEquals(
			path, 111 --> 222 --> 333 --> 444,
			"Extended path with single value"
		);

		this.assertEquals(
			path.vertices, [111,222,333,444],
			"Extended path with single value changed vertices"
		);
		path = 111 --> 222 --> 333;
		other = 444 --> 555;
		path = path ++ other;
		this.assertEquals(
			path, 111 --> 222 --> 333 --> 444 --> 555,
			"Extended path with edge"
		);

		this.assertEquals(
			path.vertices, [111,222,333,444,555],
			"Extended path with edge changed vertices"
		);

		path = 111 --> 222 --> 333;
		other = 444 --> 555 --> 666;
		path = path ++ other;
		this.assertEquals(
			path, 111 --> 222 --> 333 --> 444 --> 555 --> 666,
			"Extended path with path"
		);

		this.assertEquals(
			path.vertices, [111,222,333,444,555,666],
			"Extended path with path changed vertices"
		);
	}
}
