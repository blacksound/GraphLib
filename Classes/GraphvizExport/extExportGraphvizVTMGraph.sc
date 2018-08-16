+ VTMGraph {
	*dotPath{ ^"/usr/local/bin/dot";}

	asDotScript{
		var result;
		result = "% {".format(this.class.dotSym);
		this.edges.do({arg edge;
			result = result ++ "\n\t" ++ edge.dotString;
		});
		result = result ++ "\n}";
		^result;
	}

	writeDotScript{arg path;
		var file;
		file = File.new(path, "w");
		file.write(this.asDotScript);
		file.close;
	}

	showDotImage{
		var imgPath;
		var dotPath;
		var image;
		var imgFilename = Main.elapsedTime.asString.tr($., $_);
		imgFilename = "graph_" ++ imgFilename ++ imgFilename.hash.asString ++ ".dot";
		dotPath = PathName(Platform.defaultTempDir +/+ imgFilename);
		imgPath = PathName(dotPath.pathOnly ++ dotPath.fileNameWithoutExtension ++ ".pdf");

		this.writeDotScript(dotPath.fullPath);
		"Making dot script".postln;
		while({File.exists(dotPath.fullPath).not}, {
			".".post; 0.04.wait;
		});

		"% % -Tpdf -o %".format(
			this.class.dotPath,
			dotPath.fullPath,
			imgPath.fullPath
		).unixCmd({
			{
				File.delete(dotPath.fullPath);
				"open %".format(imgPath.fullPath).unixCmd({
					// File.delete(imgPath.fullPath);
				});
				// var image, plotWin;
				// image = Image.new(imgPath.fullPath);
				// plotWin = image.plot(freeOnClose: true);
				// plotWin.onClose_({
				// 	File.delete(dotPath.fullPath);
				// 	File.delete(imgPath.fullPath);
				// });
			}.defer;
		});
	}
}

+ Object{
	dotString{
		^this.asString.quote;
	}
}

+ VTMDirectedGraph{
	*dotSym{ ^"digraph"; }
}

+ VTMUndirectedGraph {
	*dotSym{ ^"graph"; }
}

+ VTMGraphEdge{
	dotString{
		^"% % %".format(this.from.dotString, this.class.dotSym, this.to.dotString);
	}
}

+ VTMDirectedEdge{
	*dotSym{ ^"->"; }
}

+ VTMUndirectedEdge{
	*dotSym{ ^"--"; }
}

+ VTMGraphVertex{
	dotString{ ^this.value; }
}

+ String {
	surround{arg surroundChars = "[]";
		var ss = Pseq(surroundChars, inf).asStream;
		^(ss.next ++ this ++ ss.next);
	}
}