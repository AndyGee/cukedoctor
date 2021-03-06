package com.github.cukedoctor.converter;

import com.github.cukedoctor.api.*;
import com.github.cukedoctor.api.model.*;
import com.github.cukedoctor.config.CukedoctorConfig;
import com.github.cukedoctor.util.FileUtil;
import com.github.cukedoctor.util.Formatter;

import java.io.File;
import java.util.List;

import static com.github.cukedoctor.util.Assert.*;
import static com.github.cukedoctor.util.Constants.Atributes.*;
import static com.github.cukedoctor.util.Constants.Markup.*;
import static com.github.cukedoctor.util.Constants.newLine;


/**
 * Created by pestano on 02/06/15.
 */
public class CukedoctorConverterImpl implements CukedoctorConverter {


	private List<Feature> features;
	private DocumentAttributes documentAttributes;
	private DocWriter<StringBuilder> writer;
	private String filename;

	public CukedoctorConverterImpl(List<Feature> features, DocumentAttributes attrs, DocWriter<StringBuilder> writer) {
		this.features = features;
		this.documentAttributes = attrs;
		this.writer = writer;
	}


	public DocumentAttributes getDocumentAttributes() {
		return documentAttributes;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public StringBuilder getDocumentation() {
		return writer.getCurrentDoc();
	}

	/**
	 * @return a String representation of generated documentation
	 */
	public String renderDocumentation() {
		writer.clear();
		renderAttributes();
		writer.write(newLine());
		writer.write(H1(bold(getDocumentationTitle())), newLine(), newLine());
		renderSummary();
		writer.write(newLine(), newLine());
		writer.write(H2(bold("Features")), newLine(), newLine());
		renderFeatures(features);
		//generateDocInfo();
		//generatePdfTheme();

		return writer.getCurrentDoc().toString();
	}



	public String getDocumentationTitle() {
		if(documentAttributes != null && hasText(documentAttributes.getDocTitle())){
			return documentAttributes.getDocTitle();
		} else {
			return CukedoctorConfig.DOCUMENT_TITLE;
		}

	}

	public CukedoctorConverter renderAttributes() {
		if(documentAttributes != null){
			writer.write(toc(documentAttributes.getToc()), newLine()).
					write(backend(documentAttributes.getBackend()), newLine()).
					write(docTitle(documentAttributes.getDocTitle()), newLine()).
					write(docType(documentAttributes.getDocType()), newLine()).
					write(icons(documentAttributes.getIcons()), newLine()).
					write(numbered(documentAttributes.isNumbered()), newLine()).
					write(linkcss(documentAttributes.isLinkCss()), newLine()).
					write(sectAnchors(documentAttributes.isSectAnchors()), newLine()).
					write(sectLink(documentAttributes.isSectLink()), newLine()).
					write(docInfo(documentAttributes.isDocInfo()), newLine()).
					write(tocLevels(documentAttributes.getTocLevels()), newLine());
		}
		return this;
	}

	public CukedoctorConverter generateDocInfo() {
		if (notNull(documentAttributes) && documentAttributes.isDocInfo()) {
			//name must be filename-docinfo.html
			String docInfoName = filename.substring(0, filename.lastIndexOf(".")) + "-docinfo.html";
			File savedFile = FileUtil.copyFile("docinfo.html", docInfoName);
			//docinfo depends on cukedoctor.js and cukedoctor.css
			//save js and css file in same dir as docinfo
			String basePath = "";
			if (contains(filename, "/")) {
				basePath = filename.substring(0, filename.lastIndexOf("/"));
			}
			FileUtil.copyFile("cukedoctor.js", basePath + "/cukedoctor.js");
			FileUtil.copyFile("cukedoctor.css", basePath + "/cukedoctor.css");

		}

		return this;
	}

	public CukedoctorConverter generatePdfTheme() {
		if (notNull(documentAttributes) && documentAttributes.isPdfTheme()) {
			//name must be filename-theme.yml
			String pdfThemeName = filename.substring(0, filename.lastIndexOf(".")) + "-theme.yml";
			FileUtil.copyFile("theme.yml", pdfThemeName);
		}
		return this;
	}

	public CukedoctorConverterImpl renderSummary() {
		writer.write(H2(bold("Summary"))).write(newLine());
		writer.write("[cols=\"12*^m\", options=\"header,footer\"]" + newLine() +
				"|===" + newLine() +
				"3+|Scenarios 7+|Steps 2+|Features: " + features.size() +
				"" + newLine() + newLine() +
				"|[green]#*Passed*#" + newLine() +
				"|[red]#*Failed*#" + newLine() +
				"|Total" + newLine() +
				"|[green]#*Passed*#" + newLine() +
				"|[red]#*Failed*#" + newLine() +
				"|[purple]#*Skipped*#" + newLine() +
				"|[maroon]#*Pending*#" + newLine() +
				"|[yellow]#*Undefined*#" + newLine() +
				"|[blue]#*Missing*#" + newLine() +
				"|Total" + newLine() +
				"|Duration" + newLine() +
				"|Status" + newLine());

		for (Feature feature : features) {
			writer.write(newLine());
			writer.write("12+^" + tableCol(), "*<<", feature.getName().replaceAll(",", "").replaceAll(" ", "-"), ">>*", newLine());
			StepResults stepResults = feature.getStepResults();
			ScenarioResults scenarioResults = feature.getScenarioResults();

			writer.write(tableCol(), scenarioResults.getNumberOfScenariosPassed(), newLine());
			writer.write(tableCol(), scenarioResults.getNumberOfScenariosFailed(), newLine());
			writer.write(tableCol(), scenarioResults.getNumberOfScenarios(), newLine());
			writer.write(tableCol(), stepResults.getNumberOfPasses(), newLine());
			writer.write(tableCol(), stepResults.getNumberOfFailures(), newLine());
			writer.write(tableCol(), stepResults.getNumberOfSkipped(), newLine());
			writer.write(tableCol(), stepResults.getNumberOfPending(), newLine());
			writer.write(tableCol(), stepResults.getNumberOfUndefined(), newLine());
			writer.write(tableCol(), stepResults.getNumberOfMissing(), newLine());
			writer.write(tableCol(), stepResults.getNumberOfSteps(), newLine());
			writer.write(tableCol(), stepResults.getTotalDurationAsString(), newLine());
			writer.write(tableCol(), Status.getStatusColor(feature.getStatus()), newLine());
		}
		renderTotalsRow();
		writer.write(table());
		return this;
	}

	//should be only called inside renderSummary()
	public CukedoctorConverterImpl renderTotalsRow() {
		int totalPassedScenarios = 0;
		int totalFailedScenarios = 0;
		int totalScenarios = 0;
		int totalPassedSteps = 0;
		int totalFailedSteps = 0;
		int totalSkippedSteps = 0;
		int totalPendingSteps = 0;
		int totalMissingSteps = 0;
		int totalUndefinedSteps = 0;
		int totalSteps = 0;
		long totalDuration = 0;
		for (Feature feature : features) {
			totalPassedScenarios += feature.getScenarioResults().getNumberOfScenariosPassed();
			totalFailedScenarios += feature.getScenarioResults().getNumberOfScenariosFailed();
			totalScenarios += feature.getNumberOfScenarios();
			totalPassedSteps += feature.getStepResults().getNumberOfPasses();
			totalFailedSteps += feature.getStepResults().getNumberOfFailures();
			totalSkippedSteps += feature.getStepResults().getNumberOfSkipped();
			totalPendingSteps += feature.getStepResults().getNumberOfPending();
			totalMissingSteps += feature.getStepResults().getNumberOfMissing();
			totalUndefinedSteps += feature.getStepResults().getNumberOfUndefined();
			totalSteps += feature.getNumberOfSteps();
			totalDuration += feature.getStepResults().getTotalDuration();
		}
		writer.write("12+^|*Totals*", newLine()).
				write(tableCol(), totalPassedScenarios, tableCol(), totalFailedScenarios).
				write(tableCol(), totalScenarios).
				write(tableCol(), totalPassedSteps, tableCol(), totalFailedSteps).
				write(tableCol(), totalSkippedSteps, tableCol(), totalPendingSteps).
				write(tableCol(), totalUndefinedSteps, tableCol(), totalMissingSteps).
				write(tableCol(), totalSteps, " 2+", tableCol(), Formatter.formatTime(totalDuration));
		writer.write(newLine());

		return this;
	}

	public CukedoctorConverter renderFeatures(List<Feature> features) {
		for (Feature feature : features) {
			renderFeature(feature);
		}
		return this;
	}


	public CukedoctorConverterImpl renderFeature(Feature feature) {
		if(feature.hasIgnoreDocsTag()){
			return this;
		}
		writer.write(renderFeatureSectionId(feature), newLine());
		writer.write(H3(bold(feature.getName())), newLine(),newLine());
		if(notNull(documentAttributes) && hasText(documentAttributes.getBackend()) && documentAttributes.getBackend().toLowerCase().contains("html")) {
			//used by minimax extension @see com.github.cukedoctor.extension.CukedoctorMinMaxExtension
			writer.write("minmax::", feature.getName().replaceAll(",", "").replaceAll(" ", "-"), "[]", newLine());
		}
		if (hasText(feature.getDescription())) {
			writer.write("****", newLine()).
					//feature description has \n to delimit new lines
							write(feature.getDescription().trim().replaceAll("\\n", " +" + newLine())).
					write(newLine(), "****", newLine(), newLine());
		}


		renderFeatureScenarios(feature);
		return this;
	}


	public String renderFeatureSectionId(Feature feature) {
		if (isNull(feature) || not(hasText(feature.getName()))) {
			return "";
		}
		//Anchor must not have blanks neither commas to work
		return "[[" + feature.getName().replaceAll(",", "").replaceAll(" ", "-") +
				", " + feature.getName() + "]]";
	}

	public CukedoctorConverterImpl renderFeatureScenarios(Feature feature) {
		for (Scenario scenario : feature.getScenarios()) {
			if(scenario.hasIgnoreDocsTag()){
				continue;
			}
			writer.write(H4(scenario.getKeyword()), ": ", scenario.getName(), newLine());
			if (feature.hasTags() || scenario.hasTags()) {
				renderScenarioTags(feature, scenario);
				writer.write(newLine(), newLine());
			}

			writer.write(scenario.getDescription(), newLine());

			if (scenario.hasExamples()) {
				renderScenarioExamples(scenario);
				continue;
			}

			if (scenario.hasSteps()) {
				renderScenarioSteps(scenario.getSteps());
			}

			writer.write(newLine());
		}
		return this;
	}

	public CukedoctorConverter renderScenarioTags(Feature feature, Scenario scenario) {
		writer.write("[small]#tags: ");
		StringBuilder tags = new StringBuilder();
		if (feature.hasTags()) {
			for (Tag featureTag : feature.getTags()) {
				tags.append(featureTag.getName()).append(",");
			}
		}
		if (scenario.hasTags()) {
			for (Tag scenarioTag : scenario.getTags()) {
				tags.append(scenarioTag.getName()).append(",");
			}
		}
		if (tags.indexOf(",") != -1) {//delete last comma
			tags.deleteCharAt(tags.lastIndexOf(","));
		}
		writer.write(tags.toString());
		writer.write("#");

		return this;
	}

	public CukedoctorConverterImpl renderScenarioSteps(List<Step> steps) {
		writer.write("****", newLine());
		for (Step step : steps) {
			writer.write(step.getKeyword(), "::", newLine());
			writer.write(step.getName() + " ", Status.getStatusIcon(step.getStatus()));
			writer.write(renderStepTime(step.getResult())).write(newLine());

			renderStepTable(step);

			if(notNull(step.getDocString()) && hasText(step.getDocString().getValue())){
				writer.write(listing(),newLine(),newLine());
				writer.write(step.getDocString().getValue().replaceAll("\\n",newLine()));
				writer.write(newLine(),newLine(),listing(),newLine());
			}

			if (step.getResult() != null && !Status.passed.equals(step.getStatus())) {
				if (step.getResult().getErrorMessage() != null) {
					writer.write(newLine(), "IMPORTANT: ", step.getResult().getErrorMessage(), newLine());
				}
			}
		}
		writer.write("****", newLine());
		return this;
	}

	public CukedoctorConverter renderStepTable(Step step) {
		if (notEmpty(step.getRows())) {
			writer.write(newLine(),newLine(),"[cols=\"" + step.getRows()[0].getCells().length + "*\", options=\"header\"]", newLine());
			writer.write(table(), newLine());
			Row header = step.getRows()[0];
			for (String col : header.getCells()) {
				writer.write(tableCol(), col, newLine());
			}

			for (int i = 1; i < step.getRows().length; i++) {
				for (String cell : step.getRows()[i].getCells()) {
					writer.write(tableCol(), cell, newLine());
				}
			}
			writer.write(table(),newLine(),newLine());
		}

		return this;
	}

	public String renderStepTime(Result result) {
		if (result == null || result.getDuration() == null) {
			return "";
		}
		return " [small right]#(" + Formatter.formatTime(result.getDuration()) + ")#";
	}


	@Override
	public CukedoctorConverter setFilename(String filename) {
		if (filename == null) {
			filename = getDocumentationTitle();
		}
		if (!filename.contains(".")) {
			filename = filename + ".adoc";
		}
		filename = filename.replaceAll(" ", "_");//remove blank spaces with underline

		if (!FileUtil.ADOC_FILE_EXTENSION.matcher(filename).matches()) {
			throw new RuntimeException("Invalid filename extension for file: " + filename + ". Valid formats are: ad, adoc, asciidoc and asc");
		}


		this.filename = filename;
		return this;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	public CukedoctorConverter saveDocumentation() {
		FileUtil.saveFile(filename, renderDocumentation());
		return this;
	}

	@Override
	public CukedoctorConverter renderScenarioExamples(Scenario scenario) {
		if (scenario.hasExamples()) {
			writer.write(newLine());
			for (Example example : scenario.getExamples()) {
				writer.write("." + (example.getName() == null || "".equals(example.getName()) ? "Example" : example.getName()), newLine());
				writer.write("[cols=\"" + example.getRows()[0].getCells().length + "*\", options=\"header\"]", newLine());
				writer.write(table(), newLine());
				Row header = example.getRows()[0];
				for (String col : header.getCells()) {
					writer.write(tableCol(), col, newLine());
				}

				for (int i = 1; i < example.getRows().length; i++) {
					for (String cell : example.getRows()[i].getCells()) {
						writer.write(tableCol(), cell, newLine());
					}
				}
				writer.write(table(), newLine(), newLine());
			}
		}
		return this;
	}


}
