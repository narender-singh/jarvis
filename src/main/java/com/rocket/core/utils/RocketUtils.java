package com.rocket.core.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public final class RocketUtils {

	private static final Logger LOG = LoggerFactory.getLogger(RocketUtils.class);

	private RocketUtils() {
	}

	public static void writeAbreviatedClassName(String className, Appendable app) throws IOException {
		int lx = className.lastIndexOf('.');
		if (lx < 0) {
			app.append(className);
			return;
		}
		char curr = className.charAt(0);
		app.append(curr).append('.');
		for (int i = 1; i < lx; i++) {
			curr = className.charAt(i);
			if (curr == '.')
				app.append(className.charAt(++i)).append('.');
		}
		app.append(className, lx + 1, className.length());
	}

	public static boolean isNonRecoverableError(Throwable e) {
		LOG.error("Error occured in service", e);
		if (e instanceof Error)
			return true;
		else if (e instanceof RuntimeException)
			return false;
		return true;
	}

	public static <T> T readPojoFromXml(String xml, Class<T> type)
			throws JAXBException, SAXException, IOException, ParserConfigurationException {
		return readPojoFromXml(new ByteArrayInputStream(xml.getBytes()), type);
	}

	public static <T> T readPojoFromXml(InputStream stream, Class<T> type)
			throws JAXBException, SAXException, IOException, ParserConfigurationException {
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = docBuilder.parse(stream);
		Element element = document.getDocumentElement();
		JAXBContext context = JAXBContext.newInstance(type);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		JAXBElement<T> loader = unmarshaller.unmarshal(element, type);
		return loader.getValue();
	}
}
