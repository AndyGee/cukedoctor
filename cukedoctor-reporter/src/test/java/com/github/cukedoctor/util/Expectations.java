package com.github.cukedoctor.util;

import static com.github.cukedoctor.util.Constants.newLine;

/**
 * Created by pestano on 07/06/15.
 */
public interface Expectations {


	 String SUMMARY_ONE_FEATURE = "= Title" + newLine() + newLine() +
			".Summary" + newLine() +
			"[cols=\"2h,12*^1\", options=\"header,footer\"]" + newLine() +
			"|===" + newLine() +
			"|{empty} 3+|Scenarios 7+|Steps |{empty}|{empty}" + newLine() +
			"" + newLine() +
			"|Feature" + newLine() +
			"|Passed" + newLine() +
			"|Failed" + newLine() +
			"|Total" + newLine() +
			"|Passed" + newLine() +
			"|Failed" + newLine() +
			"|Skipped" + newLine() +
			"|Pending" + newLine() +
			"|Undefined" + newLine() +
			"|Missing" + newLine() +
			"|Total" + newLine() +
			"|Duration" + newLine() +
			"|Status" + newLine() +
			"" + newLine() +
			"<|One passing scenario, one failing scenario" + newLine() +
			"|1" + newLine() +
			"|1" + newLine() +
			"|2" + newLine() +
			"|1" + newLine() +
			"|1" + newLine() +
			"|0" + newLine() +
			"|0" + newLine() +
			"|0" + newLine() +
			"|0" + newLine() +
			"|2" + newLine() +
			"|010ms" + newLine() +
			"|failed" + newLine() +
			"|===" + newLine();


	String SUMMARY_MULTIPLE_FEATURES = "= Title"+newLine() +
			""+newLine() +
			".Summary"+newLine() +
			"[cols=\"2h,12*^1\", options=\"header,footer\"]"+newLine() +
			"|==="+newLine() +
			"|{empty} 3+|Scenarios 7+|Steps |{empty}|{empty}"+newLine() +
			""+newLine() +
			"|Feature"+newLine() +
			"|Passed"+newLine() +
			"|Failed"+newLine() +
			"|Total"+newLine() +
			"|Passed"+newLine() +
			"|Failed"+newLine() +
			"|Skipped"+newLine() +
			"|Pending"+newLine() +
			"|Undefined"+newLine() +
			"|Missing"+newLine() +
			"|Total"+newLine() +
			"|Duration"+newLine() +
			"|Status"+newLine() +
			""+newLine() +
			"<|One passing scenario, one failing scenario"+newLine() +
			"|1"+newLine() +
			"|1"+newLine() +
			"|2"+newLine() +
			"|1"+newLine() +
			"|1"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|2"+newLine() +
			"|010ms"+newLine() +
			"|failed"+newLine() +
			""+newLine() +
			"<|An embed data directly feature"+newLine() +
			"|3"+newLine() +
			"|0"+newLine() +
			"|3"+newLine() +
			"|3"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|3"+newLine() +
			"|000ms"+newLine() +
			"|passed"+newLine() +
			""+newLine() +
			"<|An outline feature"+newLine() +
			"|0"+newLine() +
			"|1"+newLine() +
			"|1"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|0"+newLine() +
			"|1"+newLine() +
			"|1"+newLine() +
			"|000ms"+newLine() +
			"|failed"+newLine() +
			"|==="+newLine();
}
