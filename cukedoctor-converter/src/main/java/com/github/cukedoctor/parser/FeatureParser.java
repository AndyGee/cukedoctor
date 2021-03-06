package com.github.cukedoctor.parser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cukedoctor.api.model.Feature;
import com.github.cukedoctor.util.FileUtil;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pestano on 04/06/15.
 */
public class FeatureParser {
	static final Logger log = Logger.getLogger(FeatureParser.class.getName());

	/**
	 *
	 * @param json absolute path to cucumber json output file
	 * @return list of cucumber features found in json output files
	 */
	public static List<Feature> parse(String json) {
		InputStreamReader is = null;
		List<Feature> features = null;
		try {
			is = new InputStreamReader(new FileInputStream(json),"UTF-8");
			features = new ObjectMapper().readValue(is, new TypeReference<List<Feature>>() {
			});
			Iterator<Feature> it = features.iterator();
			while (it.hasNext()) {
				Feature feature = it.next();
				if (!feature.isCucumberFeature()) {
					log.warning("json:" + json + " is NOT a Cucumber feature result file and will be ignored");
					it.remove();
				}else{
					feature.initScenarios();
					feature.processSteps();
				}
			}

		} catch (FileNotFoundException e) {
			log.warning("Could not find json file:" + json);
		} catch (JsonMappingException e) {
			log.warning("Could map json file:" + json);
		} catch (JsonParseException e) {
			log.warning("Could not parse json file:" + json);
		} catch (IOException e) {
			log.warning("Could not read json file:" + json);
		}
		if(features != null){
			sortFeatures(features);
		}

		return features;
	}

	private static void sortFeatures(List<Feature> features) {
		if(features != null) {
			Collections.sort(features, new Comparator<Feature>() {
				@Override
				public int compare(Feature f1, Feature f2) {
					return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
				}
			});
		}
	}

	/**
	 *
	 * @param paths absolute paths to cucumber json output files
	 * @return list of cucumber features found in json output files
	 */
	public static List<Feature> parse(List<String> paths) {
		List<Feature> features = new ArrayList<>();

		for (String path : paths) {
			List<Feature> result = parse(path);
			if (result != null) {
				features.addAll(result);
			}
		}
		sortFeatures(features);
		return features;
	}

	/**
	 *
	 * @param paths absolute paths to cucumber json output files
	 * @return list of cucumber features found in json output files
	 */
	public static List<Feature> parse(String... paths) {
		List<Feature> features = new ArrayList<>();

		for (String path : paths) {
			List<Feature> result = parse(path);
			if (result != null) {
				features.addAll(result);
			}
		}
		sortFeatures(features);

		return features;
	}

	/**
	 *
	 * @param basePath relative or absolute path to a directory containing cucumber json output files
	 * @return list of cucumber features found in json output files
	 */
	public static List<Feature> findAndParse(String basePath){
		List<String> paths = FileUtil.findJsonFiles(basePath);
		return parse(paths);
	}
}
